package vn.khanhduc.shoppingbackendservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@Builder
public class SignInRequest implements Serializable {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email incorrect format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Min(value = 6, message = "Password must be 6 characters")
    private String password;
}
