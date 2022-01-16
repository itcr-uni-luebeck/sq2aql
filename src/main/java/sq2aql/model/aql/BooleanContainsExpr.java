package sq2aql.model.aql;

public interface BooleanContainsExpr extends ContainsExpr{
  /**
   * An expression that always evaluates to {@code true}.
   */
  BooleanContainsExpr TRUE = printContext -> "true";

  /**
   * An expression that always evaluates to {@code false}.
   */
  BooleanContainsExpr FALSE = printContext -> "false";

}
