package sq2aql.model.aql;

import sq2aql.PrintContext;

public record WhereClause(WhereExpr whereExpr) {

  public static WhereClause of(WhereExpr whereExpr) {
    return new WhereClause(whereExpr);
  }

  public String print(PrintContext printContext) {
    return "WHERE " + whereExpr.print(printContext);
  }
}
