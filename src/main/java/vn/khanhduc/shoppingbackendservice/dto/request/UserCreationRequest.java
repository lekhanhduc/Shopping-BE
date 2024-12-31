package vn.khanhduc.shoppingbackendservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Setter
@Getter
@Builder
public class UserCreationRequest implements Serializable {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Incorrect Email Format")
    private String email;

    @NotBlank(message = "Email cannot be blank")
    @Min(value = 6, message = "Password must be 6 characters")
    private String password;

    @NotBlank(message = "User cannot be blank")
    private String username;
}
