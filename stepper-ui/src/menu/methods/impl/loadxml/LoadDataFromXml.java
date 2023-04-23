package menu.methods.impl.loadxml;

import menu.methods.api.AbstractMenuMethod;
import menu.user.input.UserInputContext;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.FlowDefinitionImpl;
import project.java.stepper.flow.definition.api.StepUsageDeclarationImpl;
import project.java.stepper.step.StepDefinitionRegistry;

import java.util.ArrayList;
import java.util.List;

public class LoadDataFromXml {

    public static List<FlowDefinition> LoadData(String xmlNative) {
        System.out.println("Im in xml choise");
        List<FlowDefinition> flowList = new ArrayList<>();
        FlowDefinition flow1 = new FlowDefinitionImpl("File Renamer", "Renames all the files");
        flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.COLLECT_FILES_FOLDER_STEP.getStepDefinition()));
        flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.FILES_RENAMER_STEP.getStepDefinition()));
        flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.FILE_DUMPER_STEP.getStepDefinition()));
        flow1.validateFlowStructure();
        flowList.add(flow1);

        return flowList;
    }
}
