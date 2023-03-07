package sq2aql.model.aql;

import sq2aql.PrintContext;

public record FromExpr(ContainsExpr containsExpr) {

  public static FromExpr of(ContainsExpr containsExpr) {
    return new FromExpr(containsExpr);
  }

  public String print(PrintContext printContext) {
    return containsExpr.print(printContext);
  }
}
