package sq2aql.model.aql;

import sq2aql.PrintContext;

public interface LogicalExpression {

  /**
   * An expression that always evaluates to {@code true}.
   */
  LogicalExpression TRUE = printContext -> "true";

  /**
   * An expression that always evaluates to {@code false}.
   */
  LogicalExpression FALSE = printContext -> "false";

  String print(PrintContext printContext);
}
