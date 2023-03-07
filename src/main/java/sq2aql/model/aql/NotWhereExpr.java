package sq2aql.model.aql;

import sq2aql.PrintContext;

public record NotWhereExpr(WhereExpr whereExpr) implements BooleanWhereExpr {

  public static final int PRECEDENCE = 12;


  public static NotWhereExpr of(WhereExpr whereExpr) {
    return new NotWhereExpr(whereExpr);
  }

  @Override
  public String print(PrintContext printContext) {
    return printContext.parenthesize(PRECEDENCE,
        "NOT" + whereExpr.print(printContext.withPrecedence(PRECEDENCE)));
  }
}
