package vn.khanhduc.shoppingbackendservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Builder
public class ProductCreationRequest implements Serializable {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Price cannot be blank")
    @Min(value = 0, message = "Price must be at least 0")
    private BigDecimal price;

    @NotNull(message = "StockQuantity cannot be null")
    @Min(value = 0 , message = "StockQuantity must be at least 0")
    private Long stockQuantity;
}
