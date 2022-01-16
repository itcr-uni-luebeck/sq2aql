package sq2aql.model.aql;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sq2aql.PrintContext;

import static java.util.stream.Collectors.joining;


public class OrContainsExpr implements BooleanContainsExpr {
  private final List<ContainsExpr> expressions;

  public OrContainsExpr(List<ContainsExpr> containsExpressions) {
    expressions = containsExpressions;
  }


  //FIXME!
  public static OrContainsExpr of(BooleanContainsExpr e1, BooleanContainsExpr e2) {
    if (e1 instanceof OrContainsExpr) {
      return new OrContainsExpr(Stream.concat(((OrContainsExpr) e1).expressions.stream(),
          Stream.of(Objects.requireNonNull(e2))).collect(Collectors.toUnmodifiableList()));
    }
    else if (Objects.isNull(e2))
    {
      return new OrContainsExpr(List.of(e1));

    }
    else if (Objects.isNull(e1))
    {
      return new OrContainsExpr(List.of(e2));
    }
    else if (e1.equals(e2)) {
      return new OrContainsExpr(List.of(e1));
    }
    else {
      return new OrContainsExpr(List.of(Objects.requireNonNull(e1), Objects.requireNonNull(e2)));
    }
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OrContainsExpr that = (OrContainsExpr) o;
    return expressions.equals(that.expressions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expressions);
  }

  @Override
  public String print(PrintContext printContext) {
    return expressions.stream().
        map(e -> e.print(printContext)).collect(joining(" OR\n"));
  }
}
