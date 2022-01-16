package sq2aql.model.aql;

import sq2aql.PrintContext;

public class FromClause {

  private FromClause(FromExpr fromExpr) {
    this.fromExpr = fromExpr;
  }

  public static FromClause of(FromExpr fromExpr) {
    return new FromClause(fromExpr);
  }

  private final FromExpr fromExpr;

  public String print(PrintContext printContext) {
    return "FROM\n" + fromExpr.print(printContext);
  }
}
