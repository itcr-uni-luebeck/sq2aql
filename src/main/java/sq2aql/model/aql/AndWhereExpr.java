package sq2aql.model.aql;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sq2aql.PrintContext;

public class AndWhereExpr implements BooleanWhereExpr{

  public static final int PRECEDENCE = 4;


  private final List<WhereExpr> expressions;

  private AndWhereExpr(List<WhereExpr> expressions) {
    this.expressions = expressions;
  }


  @Override
  public String print(PrintContext printContext) {
    return printContext.parenthesize(PRECEDENCE, expressions.stream()
        .map(e -> e.print(printContext.withPrecedence(PRECEDENCE))).collect(joining(" AND\n")));
  }

  public static AndWhereExpr of(WhereExpr e1, WhereExpr e2) {
    if (e1 instanceof AndWhereExpr) {
      return new AndWhereExpr(Stream.concat(((AndWhereExpr) e1).expressions.stream(),
          Stream.of(Objects.requireNonNull(e2))).collect(Collectors.toUnmodifiableList()));
    } else {
      return new AndWhereExpr(List.of(Objects.requireNonNull(e1), Objects.requireNonNull(e2)));
    }
  }
}
