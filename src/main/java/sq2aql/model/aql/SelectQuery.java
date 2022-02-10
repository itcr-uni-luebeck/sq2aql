package sq2aql.model.aql;

import java.util.Objects;
import sq2aql.PrintContext;

public class SelectQuery implements LogicalExpression{
  private final SelectClause selectClause;
  private final FromClause fromClause;
  private WhereClause whereClause;


  private SelectQuery(SelectClause selectClause, FromClause fromClause) {
    this.selectClause = selectClause;
    this.fromClause = fromClause;
  }

  public static SelectQuery of(SelectClause selectClause, FromClause fromClause) {
    return new SelectQuery(selectClause, fromClause);
  }

  private SelectQuery(SelectClause selectClause, FromClause fromClause,
      WhereClause whereClause) {
    this.selectClause = selectClause;
    this.fromClause = fromClause;
    this.whereClause = whereClause;
  }

  public static SelectQuery of(SelectClause selectClause, FromClause fromClause,
      WhereClause whereClaus) {
    return new SelectQuery(selectClause, fromClause, whereClaus);
  }

  public String print(PrintContext printContext) {
    return selectClause.print(printContext) + "\n" + fromClause.print(printContext) + "\n" +
        (Objects.nonNull(whereClause) ? whereClause.print(printContext) : "");
  }
}
