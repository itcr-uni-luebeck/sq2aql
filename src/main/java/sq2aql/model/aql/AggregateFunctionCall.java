package sq2aql.model.aql;

import sq2aql.PrintContext;

/**
 * @author Lorenz Rosenau
 */

public interface AggregateFunctionCall extends ColumnExpr{
  public String print(PrintContext printContext);

}
