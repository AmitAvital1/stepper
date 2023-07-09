package dto.execution;

public class FreeInputDTO {
    private final String UUID;
    private final String inputFinalName;
    private final String inputData;

    public FreeInputDTO(String inputFinalName, String inputData, String UUID) {
        this.UUID = UUID;
        this.inputFinalName = inputFinalName;
        this.inputData = inputData;
    }

    public String getInputFinalName() {
        return inputFinalName;
    }

    public String getInputData() {
        return inputData;
    }

    public String getUUID() {
        return UUID;
    }
}
