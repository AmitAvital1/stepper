package project.java.stepper.load;

import project.java.stepper.exceptions.*;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.FlowDefinitionImpl;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.definition.api.StepUsageDeclarationImpl;
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
    private final static String JAXB_XML_PACKAGE_NAME = "project.java.stepper.schema.generated";

    public static List<FlowDefinition> load(String xmlPath) throws FileNotFoundException, JAXBException, StepperExeption {
        List<FlowDefinition> flowList = new ArrayList<>();
        InputStream inputStream = new FileInputStream(new File(xmlPath));
        STStepper genStepper = deserializeFrom(inputStream);
        if (firstCheckFlowValidate(genStepper.getSTFlows().getSTFlow())) {
            for (STFlow flow : genStepper.getSTFlows().getSTFlow()) {
                FlowDefinition systemFlow = cloneFlowDetails(flow);
                systemFlow.validateFlowStructure();
                flowList.add(systemFlow);
            }
        }
        return flowList;
    }

    private static FlowDefinition cloneFlowDetails(STFlow flow) throws SyntaxErrorInXML{
        String flowName = flow.getName();
        String flowDescription = flow.getSTFlowDescription();
        FlowDefinition flow1 = new FlowDefinitionImpl(flowName, flowDescription);
        for(STStepInFlow step : flow.getSTStepsInFlow().getSTStepInFlow()) {

            String stepFinalName = step.getName();;
            boolean ifFailing = false;
            if (Optional.ofNullable(step.getAlias()).isPresent())
                stepFinalName = step.getAlias();

            if (Optional.ofNullable(step.isContinueIfFailing()).isPresent())
                ifFailing = step.isContinueIfFailing();

            flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.getStepRegistryByName(step.getName()).getStepDefinition(),ifFailing,stepFinalName));
            if(!StepDefinitionRegistry.getStepRegistryByName(step.getName()).getStepDefinition().isReadonly())
                flow1.setReadOnly(false);
        }
        if(Optional.ofNullable(flow.getSTFlowLevelAliasing()).isPresent()) {
            for (STFlowLevelAlias flowAlias : flow.getSTFlowLevelAliasing().getSTFlowLevelAlias()) {
                Optional<StepUsageDeclaration> maybeStepToAlias = flow1.getFlowSteps().stream().filter(step -> flowAlias.getStep().equals(step.getFinalStepName())).findFirst();
                if (!maybeStepToAlias.isPresent())
                    throw new SyntaxErrorInXML("Error in FlowLevelAlias: Trying to flow level alias the step:" + flowAlias.getStep() + " but its not exist in the flow steps");
                if (!maybeStepToAlias.get().addLevelAlias(flowAlias.getSourceDataName(), flowAlias.getAlias()))
                    throw new SyntaxErrorInXML("Error in FlowLevelAlias: Trying to alias some data in step:" + flowAlias.getStep() + " called:" + flowAlias.getSourceDataName() + " but its not exist in the flow definition");
            }
        }
        List<String> formalOutputs = new ArrayList<>();
        String[] splitString = flow.getSTFlowOutput().split(",");
        for (String word : splitString) {
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
                throw new SyntaxErrorInXML("In xml given wrong flow output");
        }
        if(Optional.ofNullable(flow.getSTCustomMappings()).isPresent())
            addCustomMapping(flow1,flow.getSTCustomMappings().getSTCustomMapping());
        return flow1;
    }
    private static void addCustomMapping(FlowDefinition flow, List<STCustomMapping> flowMapping){
        for(STCustomMapping custome : flowMapping){
            for(StepUsageDeclaration step : flow.getFlowSteps()){
                if(step.getFinalStepName().equals(custome.getTargetStep())){
                    step.addCustomeMapInput(custome.getTargetData(), custome.getSourceData());
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
        List<String> FlowNames = flows.stream()
                .map(STFlow::getName)
                .collect(Collectors.toList());

        boolean hasDuplicateNames = FlowNames.stream().anyMatch(element -> Collections.frequency(FlowNames, element) > 1);

        if (hasDuplicateNames)
            throw new DuplicateFlowName("Invalid read the file. There are 2 flows with the same name");

        for (STFlow flow : flows) {
            List<String> names = flow.getSTStepsInFlow().getSTStepInFlow().stream().map(step ->
                    {
                        if (Optional.ofNullable(step.getAlias()).isPresent())
                            return step.getAlias();
                        else
                            return step.getName();
                    })
                    .collect(Collectors.toList());
            boolean hasNoStepName = names.stream().anyMatch(element -> Arrays.stream(StepDefinitionRegistry.values()).anyMatch(step -> element.equals(step.getStepDefinition().name())));
            if (!hasNoStepName)
                throw new StepInFlowNotExist("In flow:" + flow.getName() + " have step that not exist.");
        }
        return true;
    }
}