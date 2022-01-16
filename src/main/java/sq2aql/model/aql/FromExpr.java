package sq2aql.model.aql;

import sq2aql.PrintContext;

public class FromExpr {
  private final ContainsExpr containsExpr;

  private FromExpr(ContainsExpr containsExpr) {
    this.containsExpr = containsExpr;
  }

  public static FromExpr of(ContainsExpr containsExpr) {
    return new FromExpr(containsExpr);
  }

  public String print(PrintContext printContext) {
    return containsExpr.print(printContext);
  }
}
