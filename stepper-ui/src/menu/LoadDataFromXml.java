package menu;

import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.load.LoadStepperDataFromXml;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class LoadDataFromXml {

    public static List<FlowDefinition> LoadData(String xmlNative) {

        List<FlowDefinition> flowList = new ArrayList<>();
        if(xmlNative.equals("0"))
            return flowList;

        try {
                flowList = LoadStepperDataFromXml.load(xmlNative);
            System.out.println("XML load successfully");

        }catch(FileNotFoundException e) {
            System.out.println("Error: XML path not found");
        }catch (JAXBException e) {
            System.out.println("Error while trying get your data from XML");
        }catch (StepperExeption e){
            System.out.println("Error:" + e.getMessage());
        }
        return flowList;

    }
}

