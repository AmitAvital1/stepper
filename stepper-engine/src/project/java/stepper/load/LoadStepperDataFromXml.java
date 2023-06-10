package project.java.stepper.load;

import project.java.stepper.dd.api.DataDefinition;
import project.java.stepper.exceptions.*;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.FlowDefinitionImpl;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.definition.api.StepUsageDeclarationImpl;
import project.java.stepper.flow.execution.runner.FlowsExecutionManager;
import project.java.stepper.schema.generated.*;
import project.java.stepper.step.StepDefinitionRegistry;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class LoadStepperDataFromXml {
    /*
    This function load data from xml path. Using JAXB to get data from xml, and make deep copy to Flows in our system
     */
    private final static String JAXB_XML_PACKAGE_NAME = "project.java.stepper.schema.generated";

    public static List<FlowDefinition> load(String xmlPath, FlowsExecutionManager flowsExecutionManager) throws FileNotFoundException, JAXBException, StepperExeption {
        List<FlowDefinition> flowList = new ArrayList<>();
        InputStream inputStream = new FileInputStream(new File(xmlPath));
        STStepper genStepper = deserializeFrom(inputStream);
        if (firstCheckFlowValidate(genStepper.getSTFlows().getSTFlow())) {
            for (STFlow flow : genStepper.getSTFlows().getSTFlow()) {
                FlowDefinition systemFlow = cloneFlowDetails(flow);//Doing the deep copy
                systemFlow.validateFlowStructure();
                addInitialsInputs(flow,systemFlow);
                flowList.add(systemFlow);//Add the flow to the list
            }
        }
        addContinuations(flowList, genStepper);

        if(genStepper.getSTThreadPool() <= 0)
            throw new InvalidThreadPoolNumber("Invalid thread-pool number - must be non negative number.");

        flowsExecutionManager.setThreadExecutor(genStepper.getSTThreadPool());
        return flowList;
    }
    private static void addContinuations(List<FlowDefinition> flowList, STStepper genStepper) throws StepperExeption {
        int i = 0;
        for(STFlow stFlow : genStepper.getSTFlows().getSTFlow() ){
            if(Optional.ofNullable(stFlow.getSTContinuations()).isPresent()){
                List<STContinuation> continuationsFlow = stFlow.getSTContinuations().getSTContinuation();
                for(STContinuation flowContinuation : continuationsFlow){
                    boolean found = false;
                    Map<String,String> sourceToTarget= new HashMap<>();
                    for(FlowDefinition targetFlow : flowList) {
                        if (targetFlow.getName().equals(flowContinuation.getTargetFlow())) {
                            found = true;
                            for (STContinuationMapping data : flowContinuation.getSTContinuationMapping()) {
                                if(targetFlow.getFreeInputFinalNameToDD().containsKey(data.getTargetData())) {
                                    checkTheSameDD(flowList.get(i), targetFlow.getFreeInputFinalNameToDD().get(data.getTargetData()), data.getSourceData(), data.getTargetData());
                                    sourceToTarget.put(data.getSourceData(), data.getTargetData());//Else throw the exception
                                }
                                else
                                    throw new InvalidContinuationsData("In flow: " + flowList.get(i).getName() + " the target data in the continuation:"
                                        + data.getTargetData() + " does not input of the flow " + targetFlow.getName());
                            }
                                flowList.get(i).addContinuation(targetFlow, sourceToTarget);
                                break;
                            }
                        }
                     if(!found)
                         throw new InvalidContinuationsData("In flow: " + flowList.get(i).getName() + ", "+ flowContinuation.getTargetFlow() +" is not exist flow to do continuation.");
                    }
                }
            i++;
        }

        }

    private static boolean checkTheSameDD(FlowDefinition existFlow, DataDefinitionDeclaration targetDD, String sourceName,String targetName) throws InvalidContinuationsData {
        for(StepUsageDeclaration step : existFlow.getFlowSteps()){
            DataDefinition existOutPutDD = step.getFinalNamesOutputsToDD().get(sourceName);
            DataDefinition existInputDD = step.getFinalNamesInputsToDD().get(sourceName);
            if(existOutPutDD != null){
                if(targetDD.dataDefinition().getType() == existOutPutDD.getType())
                    return true;
                else
                    throw new InvalidContinuationsData("In flow: " + existFlow.getName() + " the target data in the continuation:"
                            + targetName + "[" + targetDD.dataDefinition().getType().getSimpleName() + "] is not the same type as the source " + sourceName + "["
                    + existOutPutDD.getType().getSimpleName() + "]");
            }
            else if(existInputDD != null){
                if(targetDD.dataDefinition().getType() == existInputDD.getType())
                    return true;
                else
                    throw new InvalidContinuationsData("In flow: " + existFlow.getName() + " the target data in the continuation:"
                            + targetName + "[" + targetDD.dataDefinition().getType().getSimpleName() + "] is not the same type as the source " + sourceName + "["
                            + existInputDD.getType().getSimpleName() + "]");
            }
        }
        return false;
    }

    private static FlowDefinition cloneFlowDetails(STFlow flow) throws StepperExeption{
        String flowName = flow.getName();
        String flowDescription = flow.getSTFlowDescription();
        FlowDefinition flow1 = new FlowDefinitionImpl(flowName, flowDescription);

        getFlowsStep(flow, flowName, flow1);
        makeStepAliasing(flow, flowName, flow1);
        getFormalOutputs(flow, flowName, flow1);

        if(Optional.ofNullable(flow.getSTCustomMappings()).isPresent())
            addCustomMapping(flow1,flow.getSTCustomMappings().getSTCustomMapping());

        return flow1;
    }

    private static void getFormalOutputs(STFlow flow, String flowName, FlowDefinition flow1) throws SyntaxErrorInXML {
        List<String> formalOutputs = new ArrayList<>();
        String[] splitString = flow.getSTFlowOutput().split(",");
        for (String word : splitString) {
            word = word.trim();
            if(word.length() > 0)
                formalOutputs.add(word.trim());
        }
        for(String flowOut : formalOutputs){
            String outPutRealName = "";
            boolean found = false;
            for(StepUsageDeclaration s : flow1.getFlowSteps()){
                if(Optional.ofNullable(s.getFinalNameToOutput().get(flowOut)).isPresent()) {
                    outPutRealName = s.getFinalNameToOutput().get(flowOut);
                    found = true;
                    break;
                }
            }
            Optional<DataDefinitionDeclaration> formalOutPut;
            if(found){
                List<StepUsageDeclaration> allSteps = flow1.getFlowSteps();
                for(StepUsageDeclaration step : allSteps){
                    String finalOutPutRealName = outPutRealName;
                    formalOutPut = step.getStepDefinition().outputs().stream().filter(output -> output.getName().equals(finalOutPutRealName)).findFirst();
                    if(formalOutPut.isPresent()) {
                        flow1.addFormalOutput(flowOut, formalOutPut.get());
                        break;
                    }
                }
            }
            else
                throw new SyntaxErrorInXML("In flow " + flowName + " given wrong flow formal output as the flow real output");
        }
    }

    private static void makeStepAliasing(STFlow flow, String flowName, FlowDefinition flow1) throws SyntaxErrorInXML {
        if(Optional.ofNullable(flow.getSTFlowLevelAliasing()).isPresent()) {
            for (STFlowLevelAlias flowAlias : flow.getSTFlowLevelAliasing().getSTFlowLevelAlias()) {
                Optional<StepUsageDeclaration> maybeStepToAlias = flow1.getFlowSteps().stream().filter(step -> flowAlias.getStep().equals(step.getFinalStepName())).findFirst();
                if (!maybeStepToAlias.isPresent())
                    throw new SyntaxErrorInXML("Error in FlowLevelAlias:" + "In flow " + flowName + " trying to flow level alias the step:" + flowAlias.getStep() + " but its not exist in the flow steps");
                if (!maybeStepToAlias.get().addLevelAlias(flowAlias.getSourceDataName(), flowAlias.getAlias()))
                    throw new SyntaxErrorInXML("Error in FlowLevelAlias:" + "In flow " + flowName + " trying to alias some data in step:" + flowAlias.getStep() + " called:" + flowAlias.getSourceDataName() + " but its not exist in the flow definition");
            }
        }
    }

    private static void getFlowsStep(STFlow flow, String flowName, FlowDefinition flow1) throws StepInFlowNotExist {
        for(STStepInFlow step : flow.getSTStepsInFlow().getSTStepInFlow()) {
            String stepFinalName = step.getName();;
            boolean ifFailing = false;

            if(!StepDefinitionRegistry.getStepRegistryByName(step.getName()).isPresent())
                throw new StepInFlowNotExist("In flow " + flowName + " The step " + stepFinalName + " does not exist step");

            StepDefinitionRegistry stepDefinition = StepDefinitionRegistry.getStepRegistryByName(step.getName()).get();

            if (Optional.ofNullable(step.getAlias()).isPresent())
                stepFinalName = step.getAlias();

            if (Optional.ofNullable(step.isContinueIfFailing()).isPresent())
                ifFailing = step.isContinueIfFailing();

            flow1.getFlowSteps().add(new StepUsageDeclarationImpl(stepDefinition.getStepDefinition(),ifFailing,stepFinalName));
            if(!stepDefinition.getStepDefinition().isReadonly())
                flow1.setReadOnly(false);
        }
    }

    private static void addCustomMapping(FlowDefinition flow, List<STCustomMapping> flowMapping) throws CustomeMappingInvalid {
        for(STCustomMapping custom : flowMapping){
            boolean hasNotFound = true;
            for(StepUsageDeclaration step : flow.getFlowSteps()){
                if(step.getFinalStepName().equals(custom.getTargetStep())){
                    hasNotFound = false;
                    if(step.getFinalNameToInput().get(custom.getTargetData()) == null)
                        throw new CustomeMappingInvalid("In custom mapping,the step " + step.getFinalStepName() + " does not have input-" + custom.getTargetData());
                    if(checkValidSyntaxOfStepsDetails(custom,flow)) {
                        step.addCustomeMapInput(custom.getTargetData(), custom.getSourceData());
                    }
                }
            }
            if(hasNotFound)
                throw new CustomeMappingInvalid("In custom mapping the step:" + custom.getTargetStep() + " not exist");
        }
    }

    private static boolean checkValidSyntaxOfStepsDetails(STCustomMapping custom,FlowDefinition flow) throws CustomeMappingInvalid {
        Optional<StepUsageDeclaration> opt = flow.getFlowSteps().stream()
                .filter(st -> st.getFinalStepName().equals(custom.getSourceStep()))
                .findFirst();

        if(!opt.isPresent())
            throw new CustomeMappingInvalid("Error in custom mapping: The step " + custom.getSourceStep() + " not exist");
        else
            if(opt.get().getFinalNameToOutput().get(custom.getSourceData()) == null)
                throw new CustomeMappingInvalid("Error in custom mapping: The source output " + custom.getSourceData() + " does not exist");

       return true;
    }
    private static void addInitialsInputs(STFlow flow, FlowDefinition systemFlow) throws InvalidInitialFlowValues {
        if(Optional.ofNullable(flow.getSTInitialInputValues()).isPresent()){
            for(STInitialInputValue initData: flow.getSTInitialInputValues().getSTInitialInputValue()){
                if(systemFlow.getFreeInputFinalNameToDD().containsKey(initData.getInputName())){
                    DataDefinitionDeclaration dd = systemFlow.getFreeInputFinalNameToDD().get(initData.getInputName());
                    try {
                        Object initValue = dd.dataDefinition().convertUserInputToDataType(initData.getInitialValue(), dd.dataDefinition().getType());
                        systemFlow.addInitialValue(initData.getInputName(),initValue);
                    }catch (Exception e){
                        throw new InvalidInitialFlowValues("Wrong initial value data to " + initData.getInputName() + ", the data have to be a " + dd.dataDefinition().getType().getSimpleName());
                    }
                }else {
                    throw new InvalidInitialFlowValues("In Initials Values:The initial value " + initData.getInputName() + ", is invalid. There are no input at this name.");
                }
            }
        }
    }

    private static STStepper deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (STStepper) u.unmarshal(in);
    }

    private static boolean firstCheckFlowValidate(List<STFlow> flows) throws DuplicateFlowName, StepInFlowNotExist {
        /*
        This function doing 'quick' check about the xml structure
         */

        List<String> FlowNames = flows.stream()
                .map(STFlow::getName)
                .collect(Collectors.toList());//Getting all the flows names

        boolean hasDuplicateNames = FlowNames.stream().anyMatch(element -> Collections.frequency(FlowNames, element) > 1);//Check if there is duplicate flow name

        if (hasDuplicateNames)
            throw new DuplicateFlowName("Invalid read the file. There are 2 flows with the same name");
        /*
        for (STFlow flow : flows) {//Check if the flows existing in our stepper
            List<String> names = flow.getSTStepsInFlow().getSTStepInFlow().stream()
                    .map(step -> step.getName())
                    .collect(Collectors.toList());
            boolean hasNoStepName = names.stream().anyMatch(element -> Arrays.stream(StepDefinitionRegistry.values()).anyMatch(step -> element.equals(step.getStepDefinition().name())));
            if (!hasNoStepName)
                throw new StepInFlowNotExist("In flow:" + flow.getName() + " have step that not exist.");
        }*/
        return true;
    }
}
