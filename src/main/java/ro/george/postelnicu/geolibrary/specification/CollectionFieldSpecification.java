package ro.george.postelnicu.geolibrary.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static java.util.Objects.isNull;

public class CollectionFieldSpecification<T> implements Specification<T> {

    private final Collection<String> names;
    private final Function<Root<T>, Expression<String>> fieldExpressionProvider;

    public CollectionFieldSpecification(Collection<String> names, Function<Root<T>, Expression<String>> fieldExpressionProvider) {
        this.names = names;
        this.fieldExpressionProvider = fieldExpressionProvider;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (isNull(names) || names.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        // Create a separate predicate for each author
        Set<Predicate> predicates = new HashSet<>();
        for (String authorName : names) {
            predicates.add(criteriaBuilder.equal(fieldExpressionProvider.apply(root), authorName));
        }

        // Combine all predicates using 'AND'
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    public static <T> CollectionFieldSpecification<T> buildCollectionsSpecification(Collection<String> names,
                                                                         Function<Root<T>, Expression<String>> fieldExpressionProvider) {
        return new CollectionFieldSpecification<>(names, fieldExpressionProvider);
    }
}
