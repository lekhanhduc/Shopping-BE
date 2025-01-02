package vn.khanhduc.shoppingbackendservice.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Map;

@Builder
@Getter
@Setter
public class EmailEvent implements Serializable {
    private String channel;
    private String recipient;
    private String templateCode;
    private String subject;
    private Map<String, Object> param;
}
