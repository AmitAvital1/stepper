package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.AbstractStepDefinition;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.StepResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class CommandLineStep extends AbstractStepDefinition{
    public CommandLineStep() {
        super("Command Line", false);

        addInput(new DataDefinitionDeclarationImpl("COMMAND", DataNecessity.MANDATORY, "Command", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("ARGUMENTS", DataNecessity.OPTIONAL, "Command arguments", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Command output", DataDefinitionRegistry.STRING));
    }
    @Override
    public synchronized StepResult invoke(StepExecutionContext context) throws NoStepInput {

        String cmd = context.getDataValue("COMMAND", String.class);
        Optional<String> maybeArg = Optional.ofNullable(context.getDataValue("ARGUMENTS", String.class));
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res = StepResult.SUCCESS;
        String cmdResult;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            logs.addLogLine("About to invoke " + cmd + " " + (maybeArg.isPresent() ? maybeArg.get() : ""));
            // Set the command
            processBuilder.command(cmd);

            // Add optional argument if present
            maybeArg.ifPresent(processBuilder::command);

            // Redirect standard output and error
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Read the output of the command
            String output = readOutput(process);

            // Wait for the process to complete
            int exitCode = process.waitFor();
            logs.addLogLine("Command executed. Exit code: " + exitCode);
            context.addStepSummaryLine("Command executed. Exit code: " + exitCode);
            cmdResult = output;
        } catch (IOException | InterruptedException e) {
            logs.addLogLine("Error occurred with running the command");
            context.addStepSummaryLine("Error occurred with running the command");
            cmdResult = "Error occurred with running the command";
        }
        context.storeDataValue("RESULT", cmdResult);
        context.addStepLog(logs);
        return res;
    }
    private static String readOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }
        return output.toString();
    }
}
