package sq2aql.model.aql;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sq2aql.PrintContext;

import static java.util.stream.Collectors.joining;


public class AndContainsExpr implements BooleanContainsExpr {
  private final List<ContainsExpr> expressions;

  public AndContainsExpr(List<ContainsExpr> containsExpressions) {
    expressions = containsExpressions;
  }

  @Override
  public String print(PrintContext printContext) {
    return expressions.stream().
        map(e -> e.print(printContext)).collect(joining(" AND\n "));
  }


  //TODO: FIX THIS!
  public static AndContainsExpr of(BooleanContainsExpr e1, BooleanContainsExpr e2) {
    if (e1 instanceof AndContainsExpr) {
      return new AndContainsExpr(Stream.concat(((AndContainsExpr) e1).expressions.stream(),
          Stream.of(Objects.requireNonNull(e2))).collect(Collectors.toUnmodifiableList()));
    }
    else if (Objects.isNull(e1))
    {
      return new AndContainsExpr(List.of(e2));

    }
    else if (Objects.isNull(e2))
    {
      return new AndContainsExpr(List.of(e1));

    }
    else if (e1.equals(e2)){
      return new AndContainsExpr(List.of(e1));
    }
    else {
      return new AndContainsExpr(List.of(Objects.requireNonNull(e1), Objects.requireNonNull(e2)));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AndContainsExpr that = (AndContainsExpr) o;
    return expressions.equals(that.expressions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expressions);
  }
}
