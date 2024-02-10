package ro.george.postelnicu.geolibrary.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.function.Function;

import static java.util.Objects.nonNull;

public class NumericalFieldSpecification<T> implements Specification<T> {

    private final Number min;
    private final Number max;
    private final Function<Root<T>, Expression<String>> fieldExpressionProvider;

    private NumericalFieldSpecification(Number min, Number max,
                                        Function<Root<T>, Expression<String>> fieldExpressionProvider) {
        this.min = min;
        this.max = max;
        this.fieldExpressionProvider = fieldExpressionProvider;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (nonNull(min) && nonNull(max)) {
            return criteriaBuilder.between(fieldExpressionProvider.apply(root), min.toString(), max.toString());
        } else if (nonNull(min)) {
            return criteriaBuilder.greaterThanOrEqualTo(fieldExpressionProvider.apply(root), min.toString());
        } else if (nonNull(max)) {
            return criteriaBuilder.lessThanOrEqualTo(fieldExpressionProvider.apply(root), max.toString());
        } else {
            return criteriaBuilder.conjunction();
        }
    }

    public static <T> NumericalFieldSpecification<T> buildNumericalSpecification(Number min, Number max,
                                                                                 Function<Root<T>, Expression<String>> fieldExpressionProvider) {
        return new NumericalFieldSpecification<>(min, max, fieldExpressionProvider);
    }
}
