//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.05.28 at 02:45:57 PM IDT 
//


package project.java.stepper.schema.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the project.java.stepper.schema.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _STFlowOutput_QNAME = new QName("", "ST-FlowOutput");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: project.java.stepper.schema.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link STFlowLevelAlias }
     * 
     */
    public STFlowLevelAlias createSTFlowLevelAlias() {
        return new STFlowLevelAlias();
    }

    /**
     * Create an instance of {@link STContinuationMapping }
     * 
     */
    public STContinuationMapping createSTContinuationMapping() {
        return new STContinuationMapping();
    }

    /**
     * Create an instance of {@link STContinuation }
     * 
     */
    public STContinuation createSTContinuation() {
        return new STContinuation();
    }

    /**
     * Create an instance of {@link STInitialInputValue }
     * 
     */
    public STInitialInputValue createSTInitialInputValue() {
        return new STInitialInputValue();
    }

    /**
     * Create an instance of {@link STCustomMapping }
     * 
     */
    public STCustomMapping createSTCustomMapping() {
        return new STCustomMapping();
    }

    /**
     * Create an instance of {@link STCustomMappings }
     * 
     */
    public STCustomMappings createSTCustomMappings() {
        return new STCustomMappings();
    }

    /**
     * Create an instance of {@link STFlow }
     * 
     */
    public STFlow createSTFlow() {
        return new STFlow();
    }

    /**
     * Create an instance of {@link STFlowLevelAliasing }
     * 
     */
    public STFlowLevelAliasing createSTFlowLevelAliasing() {
        return new STFlowLevelAliasing();
    }

    /**
     * Create an instance of {@link STContinuations }
     * 
     */
    public STContinuations createSTContinuations() {
        return new STContinuations();
    }

    /**
     * Create an instance of {@link STInitialInputValues }
     * 
     */
    public STInitialInputValues createSTInitialInputValues() {
        return new STInitialInputValues();
    }

    /**
     * Create an instance of {@link STStepsInFlow }
     * 
     */
    public STStepsInFlow createSTStepsInFlow() {
        return new STStepsInFlow();
    }

    /**
     * Create an instance of {@link STStepInFlow }
     * 
     */
    public STStepInFlow createSTStepInFlow() {
        return new STStepInFlow();
    }

    /**
     * Create an instance of {@link STStepper }
     * 
     */
    public STStepper createSTStepper() {
        return new STStepper();
    }

    /**
     * Create an instance of {@link STFlows }
     * 
     */
    public STFlows createSTFlows() {
        return new STFlows();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ST-FlowOutput")
    public JAXBElement<String> createSTFlowOutput(String value) {
        return new JAXBElement<String>(_STFlowOutput_QNAME, String.class, null, value);
    }

}
