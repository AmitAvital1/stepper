package dto.users;

public class UpdateManagerDTO {
    private String username;
    private boolean isManager;

    public UpdateManagerDTO(String username, boolean isManager) {
        this.username = username;
        this.isManager = isManager;
    }

    public boolean isManager() {
        return isManager;
    }

    public String getUsername() {
        return username;
    }
}
