package ro.george.postelnicu.geolibrary.specification;

import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.function.Function;

import static java.util.Objects.isNull;

public class StringLikeFieldSpecification<T> implements Specification<T> {

  private final Object value;
  private final Function<Root<T>, Expression<String>> fieldExpressionProvider;

  private StringLikeFieldSpecification(Object value,
                                       Function<Root<T>, Expression<String>> fieldExpressionProvider) {
    this.value = value;
    this.fieldExpressionProvider = fieldExpressionProvider;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> q, CriteriaBuilder cb) {
    if (isNull(value)) {
      return cb.conjunction();
    }

    Expression<String> expression = fieldExpressionProvider.apply(root);
    String v = getValueWithWildcards(value.toString());
    if (v.contains("%")) {
      return cb.like(cb.lower(expression), v);
    } else {
      return cb.equal(cb.lower(expression), v);
    }
  }

  public static <T> StringLikeFieldSpecification<T> buildSpecification(Object value,
      Function<Root<T>, Expression<String>> fieldExpressionProvider) {
    return new StringLikeFieldSpecification<>(value, fieldExpressionProvider);
  }

  private String getValueWithWildcards(@NotNull String value) {
    return value.replace("*", "%").toLowerCase();
  }
}
