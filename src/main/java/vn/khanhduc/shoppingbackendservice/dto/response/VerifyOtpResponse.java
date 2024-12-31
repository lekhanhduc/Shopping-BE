package vn.khanhduc.shoppingbackendservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@Builder
public class VerifyOtpResponse implements Serializable {
    private String message;
    private Boolean success;
}
