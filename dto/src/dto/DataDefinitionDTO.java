package dto;

import project.java.stepper.dd.api.DataDefinition;

public class DataDefinitionDTO {

    private final String name;
    private final boolean userFriendly;
    private final String type;

    public DataDefinitionDTO(DataDefinition data) {
        this.name = data.getName();
        this.userFriendly = data.isUserFriendly();
        this.type = data.getType().getSimpleName();
    }

    public String getName() {
        return name;
    }

    public boolean isUserFriendly() {
        return userFriendly;
    }

    public String getType() {
        return type;
    }
}
