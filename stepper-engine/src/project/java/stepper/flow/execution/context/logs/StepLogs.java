package project.java.stepper.flow.execution.context.logs;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;

import java.util.ArrayList;
import java.util.List;

public class StepLogs {
    private String stepFinalName;
    private List<String> logLines;

    public StepLogs(String stepName) {
        stepFinalName = stepName;
        logLines = new ArrayList<>();
    }
    public void addLogLine(String logLine) {
        logLines.add(logLine);
    }
    public List<String> getStepLogs(String logLine) {
        return logLines;
    }
    public String getStepLogsName(String logLine) {
        return stepFinalName;
    }
}
