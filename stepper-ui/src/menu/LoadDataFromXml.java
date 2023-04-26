package menu;

import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.FlowDefinitionImpl;
import project.java.stepper.flow.definition.api.StepUsageDeclarationImpl;
import project.java.stepper.load.LoadStepperDataFromXml;
import project.java.stepper.step.StepDefinitionRegistry;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class LoadDataFromXml {

    public static List<FlowDefinition> LoadData(String xmlNative) {
        List<FlowDefinition> flowList = new ArrayList<>();
        try {
                flowList = LoadStepperDataFromXml.load("C:\\Users\\USER\\Desktop\\checker\\ex1.xml");
        }catch(FileNotFoundException e) {
            System.out.println("XML path not found");
        }catch (JAXBException e) {
            System.out.println("Error while trying get your data from XML");
        }catch (StepperExeption e){
            System.out.println(e.getMessage());
        }
        return flowList;

    }
}

