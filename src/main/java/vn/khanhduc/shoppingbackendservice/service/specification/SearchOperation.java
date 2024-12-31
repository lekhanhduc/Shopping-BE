package vn.khanhduc.shoppingbackendservice.service.specification;

import lombok.Getter;

@Getter
public enum SearchOperation {

    EQUALITY, NEGATION, GREATER_THAN, LESS_THAN, LIKE, START_WITH, ENDS_WITH, CONTAINS;
    ;
    public static final String ZERO_OR_MORE_REGEX = "*"; // 0 hoặc nhiều ký tự
    public static final String OR_PREDICATE_FLAG= "'"; // Điều kiện or

    public static SearchOperation getOperation(char input) {
        return switch (input) {
            case ':' -> EQUALITY;
            case '!' -> NEGATION;
            case '~' -> LIKE;
            case '>' -> GREATER_THAN;
            case '<' -> LESS_THAN;
            default -> null;
        };
    }
}
