package vn.khanhduc.shoppingbackendservice.dto.response;

import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ProductCreationResponse implements Serializable {
    private Long id;
    private String author;
    private String name;
    private String description;
    private BigDecimal price;
    private Long stockQuantity;
    private String thumbnail;
}
