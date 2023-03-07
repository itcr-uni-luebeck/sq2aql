package sq2aql.model.aql;

import java.util.List;
import sq2aql.PrintContext;

import static java.util.stream.Collectors.joining;

public record SelectClause(boolean distinct,
                           List<SelectExpr> selectExpr) {

  public static SelectClause of(boolean distinct, List<SelectExpr> selectExpr) {
    return new SelectClause(distinct, selectExpr);
  }

  public String print(PrintContext printContext) {
    return "SELECT" + (distinct ? " DISTINCT\n" : "\n") + selectExpr.stream()
        .map(e -> e.print(printContext)).collect(joining(","));
  }
}
