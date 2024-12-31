package vn.khanhduc.shoppingbackendservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.math.BigDecimal;

@Entity(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends AbstractEntity<Long>{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Long stockQuantity;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(name = "sold_quantity")
    @ColumnDefault(value = "0")
    private Integer soldQuantity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

}
