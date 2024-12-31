package vn.khanhduc.shoppingbackendservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Setter
@Getter
@Builder
public class UserCreationResponse implements Serializable {
    private String email;
    private String username;
    private String isEnabled;
}
