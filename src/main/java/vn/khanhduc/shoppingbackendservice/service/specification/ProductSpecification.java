package vn.khanhduc.shoppingbackendservice.service.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import vn.khanhduc.shoppingbackendservice.entity.Product;

@RequiredArgsConstructor
public class ProductSpecification implements Specification<Product> {

    private final SearchCriteria criteria;

    @Override
    public Predicate toPredicate(@NonNull Root<Product> root,
                                 CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder criteriaBuilder) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case START_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), criteria.getValue().toString() + "%");
            case ENDS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()),"%" +criteria.getValue().toString());
            case CONTAINS -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
        };
    }
}
