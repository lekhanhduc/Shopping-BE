package vn.khanhduc.shoppingbackendservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ProductDetailResponse implements Serializable {
    private Long id;
    private String name;
    private String author;
    private String description;
    private BigDecimal price;
    private Long stockQuantity;
    private String thumbnail;
}
