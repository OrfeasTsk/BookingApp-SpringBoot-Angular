package tedi.backend.model;

import lombok.Data;

@Data
public class PasswordDetails {
    private String currPassword;
    private String newPassword;
    private String passwordConfirm;
}
