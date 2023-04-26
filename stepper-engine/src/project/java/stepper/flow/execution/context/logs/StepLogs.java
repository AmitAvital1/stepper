package project.java.stepper.flow.execution.context.logs;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;

import java.time.LocalTime;
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
        LocalTime time = LocalTime.now();
        String formattedTime = time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();
        logLines.add(formattedTime + "-" + logLine);
    }
    public List<String> getStepLogs() {
        return logLines;
    }
    public String getStepLogsName(String logLine) {
        return stepFinalName;
    }
}
