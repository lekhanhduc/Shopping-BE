package vn.khanhduc.shoppingbackendservice.service.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.khanhduc.shoppingbackendservice.entity.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductBuilder {

    private final List<SearchCriteria> param;

    public ProductBuilder() {
        this.param = new ArrayList<>();
    }

    public ProductBuilder buildWithOr(String key, String operation, String value, String prefix, String suffix) {
        return build(SearchOperation.OR_PREDICATE_FLAG, key, operation, value, prefix, suffix);
    }

    public ProductBuilder build(String key, String operation, String value, String prefix, String suffix) {
        return build(null, key, operation, value, prefix, suffix);
    }

    public ProductBuilder build(String orPredicate, String key, String operation, String value, String prefix, String suffix) {
        SearchOperation searchOperation = SearchOperation.getOperation(operation.charAt(0));

        boolean startWith = prefix != null && Objects.equals(prefix, SearchOperation.ZERO_OR_MORE_REGEX);
        boolean endWith = suffix != null && Objects.equals(suffix, SearchOperation.ZERO_OR_MORE_REGEX);

        if(startWith && endWith) {
            searchOperation = SearchOperation.CONTAINS;
        }
        else if(startWith) {
            searchOperation = SearchOperation.ENDS_WITH;
        }
        else if(endWith) {
            searchOperation = SearchOperation.START_WITH;
        }

        param.add(new SearchCriteria(orPredicate, key, searchOperation, value));
        return this;
    }

    public Specification<Product> build() {
        if(param.isEmpty()) {
            return null;
        }
        Specification<Product> specification = new ProductSpecification(param.getFirst());
        if(param.size() > 1) {
            for(int i = 1 ; i < param.size(); i++) {
                specification = param.get(i).getOrPredicate() ?
                        Specification.where(specification).or(new ProductSpecification(param.get(i)))
                        : Specification.where(specification).and(new ProductSpecification(param.get(i)));
            }
        }
        return specification;
    }
}
