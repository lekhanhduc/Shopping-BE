package vn.khanhduc.shoppingbackendservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.khanhduc.shoppingbackendservice.common.Gender;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    private String userName;
    private String email;
    private Gender gender;
    private String phone;
    private LocalDate dob;
}
