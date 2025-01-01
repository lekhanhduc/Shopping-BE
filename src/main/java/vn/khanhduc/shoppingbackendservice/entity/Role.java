package vn.khanhduc.shoppingbackendservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role extends AbstractEntity<Integer> {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

}
