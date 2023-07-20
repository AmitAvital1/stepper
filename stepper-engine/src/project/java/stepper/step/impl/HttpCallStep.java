package project.java.stepper.step.impl;

import okhttp3.*;
import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.enumerator.MethodEnum;
import project.java.stepper.dd.impl.enumerator.ProtocolEnum;
import project.java.stepper.dd.impl.enumerator.ZipEnum;
import project.java.stepper.dd.impl.json.JsonData;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.*;

import java.io.IOException;
import java.util.Optional;

public class HttpCallStep extends AbstractStepDefinition {
    public HttpCallStep() {
        super("HTTP Call", false);

        addInput(new DataDefinitionDeclarationImpl("RESOURCE", DataNecessity.MANDATORY, "Resource Name (include query parameters)", DataDefinitionRegistry.STRING, UIDDPresent.NA));
        addInput(new DataDefinitionDeclarationImpl("ADDRESS", DataNecessity.MANDATORY, "Domain:Port", DataDefinitionRegistry.STRING, UIDDPresent.NA));
        addInput(new DataDefinitionDeclarationImpl("PROTOCOL", DataNecessity.MANDATORY, "Protocol", DataDefinitionRegistry.PROTOCOLENUMERATOR, UIDDPresent.ENUM));
        addInput(new DataDefinitionDeclarationImpl("METHOD", DataNecessity.OPTIONAL, "Method", DataDefinitionRegistry.METHODENUMERATOR, UIDDPresent.ENUM));
        addInput(new DataDefinitionDeclarationImpl("BODY", DataNecessity.OPTIONAL, "Request Body", DataDefinitionRegistry.JSON, UIDDPresent.NA));

        addOutput(new DataDefinitionDeclarationImpl("CODE", DataNecessity.NA, "Response code", DataDefinitionRegistry.INTEGER, UIDDPresent.NA));
        addOutput(new DataDefinitionDeclarationImpl("RESPONSE_BODY", DataNecessity.NA, "Response body", DataDefinitionRegistry.STRING, UIDDPresent.NA));
    }
    @Override
    public StepResult invoke(StepExecutionContext context) throws NoStepInput {
        String resource = context.getDataValue("RESOURCE", String.class);
        String address = context.getDataValue("ADDRESS", String.class);
        ProtocolEnum protocol = context.getDataValue("PROTOCOL", ProtocolEnum.class);
        Optional<MethodEnum> maybeMethod = Optional.ofNullable(context.getDataValue("METHOD", MethodEnum.class));
        Optional<JsonData> maybeBody = Optional.ofNullable(context.getDataValue("BODY", JsonData.class));

        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res;

        String url = buildUrl(protocol, address, resource);
        Request.Builder requestBuilder = new Request.Builder().url(url);

        MethodEnum method = maybeMethod.orElse(MethodEnum.GET);
        JsonData body = maybeBody.orElse(new JsonData("{}"));//Empty body

        switch (method) {
            case GET:
                requestBuilder.get();
                break;
            case POST:
                requestBuilder.post(RequestBody.create(MediaType.parse("application/json"), body.toString()));
                break;
            case PUT:
                requestBuilder.put(RequestBody.create(MediaType.parse("application/json"), body.toString()));
                break;
            case DELETE:
                requestBuilder.delete(RequestBody.create(MediaType.parse("application/json"), body.toString()));
                break;
            default:
                context.addStepSummaryLine("Step failed cause invalid method");
                res = StepResult.FAILURE;
        }
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(requestBuilder.build());
        try {
            logs.addLogLine("About to invoke http request: " + protocol + " | " + method + " | " + address + " | " + resource);
            Response response = call.execute();

            logs.addLogLine("Received Response. Status code: " + response.code());
            context.addStepSummaryLine("Received Response. Status code: " + response.code());

            context.storeDataValue("CODE", response.code());

            String responseBody = response.body() != null ? response.body().string() : "";
            context.storeDataValue("RESPONSE_BODY", responseBody);

            res = StepResult.SUCCESS;


        } catch (IOException e) {
            context.addStepSummaryLine("Step failed cause error occurred during the http call");
            res = StepResult.FAILURE;
        }


        context.addStepLog(logs);
        return res;

    }
    private String buildUrl(ProtocolEnum protocol, String domain, String resource) {
        StringBuilder builder = new StringBuilder();
        builder.append(protocol.toString())
                .append("://")
                .append(domain)
                .append(resource);
        return builder.toString();
    }
}
