package vn.khanhduc.shoppingbackendservice.service.specification;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    private String key;
    private SearchOperation operation;
    private Object value;
    private Boolean orPredicate;

    public SearchCriteria(String orPredicate, String key, SearchOperation operation, Object value) {
        this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_PREDICATE_FLAG);
        this.key = key;
        this.operation = operation;
        this.value =value;
    }

    public SearchCriteria(String key, String operation, String prefix, String value, String suffix) {
        SearchOperation searchOperation = SearchOperation.getOperation(operation.charAt(0));
        if (searchOperation != null) {
            if (searchOperation == SearchOperation.EQUALITY) { // the operation may be complex operation
                final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
                final boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

                if (startWithAsterisk && endWithAsterisk) {
                    searchOperation = SearchOperation.CONTAINS;
                } else if (startWithAsterisk) {
                    searchOperation = SearchOperation.ENDS_WITH;
                } else if (endWithAsterisk) {
                    searchOperation = SearchOperation.START_WITH;
                }
            }
        }
        this.key = key;
        this.operation = searchOperation;
        this.value = value;
    }
}
