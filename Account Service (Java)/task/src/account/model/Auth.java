package account.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public class Auth {
    @NotNull @NotBlank
    private String name;
    @NotNull @NotBlank
    private String lastname;
    @NotNull @NotBlank @Pattern(regexp =".+@acme.com")
    private String email;
    @NotNull
    private String password;

    public String getName() {
        return name;
    }
    public String getLastname() {
        return lastname;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}
