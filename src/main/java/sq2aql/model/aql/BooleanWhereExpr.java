package sq2aql.model.aql;

public interface BooleanWhereExpr extends WhereExpr{
  /**
   * An expression that always evaluates to {@code true}.
   */
  BooleanWhereExpr TRUE = printContext -> "true";

  /**
   * An expression that always evaluates to {@code false}.
   */
  BooleanWhereExpr FALSE = printContext -> "false";

}
