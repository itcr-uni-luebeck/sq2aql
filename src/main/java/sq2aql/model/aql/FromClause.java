package sq2aql.model.aql;

import sq2aql.PrintContext;

public record FromClause(FromExpr fromExpr) {

  public static FromClause of(FromExpr fromExpr) {
    return new FromClause(fromExpr);
  }

  public String print(PrintContext printContext) {
    return "FROM\n" + fromExpr.print(printContext);
  }
}
