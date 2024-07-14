package TeamJ.MUSt.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {
    @NotEmpty
    String username;

    @NotEmpty
    String password;
}
