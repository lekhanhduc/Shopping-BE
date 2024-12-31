package vn.khanhduc.shoppingbackendservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefreshTokenResponse {
    private String userId;
    private String accessToken;
    private String refreshToken;
}
