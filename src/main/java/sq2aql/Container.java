package sq2aql;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import sq2aql.model.ValuePathElement;
import sq2aql.model.aql.AndWhereExpr;
import sq2aql.model.aql.BooleanContainsExpr;
import sq2aql.model.aql.BooleanWhereExpr;

import sq2aql.model.aql.OrContainsExpr;
import sq2aql.model.aql.OrWhereExpr;

/**
 * A container holds an expression together with all valuePathElements used in the ContainsExpr.
 * <p>
 * Containers can be {@link #combiner combined}, collecting all code system definitions the
 * individual contains use.
 * <p>
 * Instances are immutable.
 *
 * @author Alexander Kiel, Lorenzn Rosenau
 */
public final class Container<T> {

  private static final Container<?> EMPTY = new Container<>(null, Set.of());
  public static final BinaryOperator<Container<BooleanWhereExpr>> AND = combiner(
      AndWhereExpr::of);
  public static final BinaryOperator<Container<BooleanWhereExpr>> OR = combiner(OrWhereExpr::of);

  public static final BinaryOperator<Container<BooleanContainsExpr>> CONTAINS_OR = combiner(
      OrContainsExpr::of);
  private final T expression;

  private final Set<List<ValuePathElement>> valuePathElements;

  private Container(T expression, Set<List<ValuePathElement>> valuePathElements) {
    this.expression = expression;
    this.valuePathElements = valuePathElements;
  }

  /**
   * Returns the empty container that contains no expression and no code system definitions.
   * <p>
   * The empty container is the identity element of the binary {@link #combiner combine} operation.
   *
   * @param <T> the type of the expression
   * @return the empty container
   */
  public static <T> Container<T> empty() {
    @SuppressWarnings("unchecked")
    Container<T> empty = (Container<T>) EMPTY;
    return empty;
  }

  /**
   * Returns a container holding {@code expression} and {@code codeSystemDefinitions}.
   *
   * @param expression the expression being hold
   * @param <T>        the type of the expression
   * @return a container
   * @throws NullPointerException if {@code expression} is null
   */
  public static <T> Container<T> of(T expression, List<ValuePathElement>... valuePathElements) {
    return new Container<>(Objects.requireNonNull(expression), Set.of(valuePathElements));
  }

  /**
   * Returns a binary operator that combines expressions using {@code combiner} and the code system
   * definitions with set union.
   * <p>
   * Because the {@link #empty() empty conatainer} is the identity element of the returned operator
   * {@code op}, the following holds: {@code op.apply(empty, x) == x} and {@code op.apply(x, empty)
   * == x}.
   *
   * @param combiner the binary operator to combine the expressions of both containers
   * @param <T>      the type of both expressions
   * @return a container holding the combined expression and the union of the code system
   * definitions of both containers
   * @throws NullPointerException if {@code combiner} is null
   */
  public static <T> BinaryOperator<Container<T>> combiner(BinaryOperator<T> combiner) {
    Objects.requireNonNull(combiner);
    return (a, b) -> a == EMPTY
        ? b : b == EMPTY ? a : new Container<>(combiner.apply(a.expression, b.expression),
        Sets.union(a.valuePathElements, b.valuePathElements));
  }

  /**
   * Returns the expression the container holds.
   *
   * @return the expression or {@link Optional#empty()} iff this container is {@link #isEmpty()
   * empty}.
   */
  public Optional<T> getExpression() {
    return Optional.ofNullable(expression);
  }


  public Set<List<ValuePathElement>> getValuePathElements() {
    return valuePathElements;
  }


  /**
   * Returns {@code true} iff this container is empty.
   *
   * @return {@code true} iff this container is empty
   */
  public boolean isEmpty() {
    return expression == null;
  }

  public <U> Container<U> map(Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper);
    return isEmpty() ? empty()
        : new Container<>(Objects.requireNonNull(mapper.apply(expression)), valuePathElements);
  }

  public <U> Container<U> flatMap(Function<? super T, ? extends Container<? extends U>> mapper) {
    Objects.requireNonNull(mapper);
    if (isEmpty()) {
      return empty();
    }
    Container<? extends U> container = mapper.apply(expression);
    if (container.isEmpty()) {
      return empty();
    }
    assert container.getExpression().isPresent();
    return new Container<>(Objects.requireNonNull(container.getExpression().get()),
        container.getValuePathElements());
  }
}
