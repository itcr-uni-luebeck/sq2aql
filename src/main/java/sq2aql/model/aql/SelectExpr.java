package sq2aql.model.aql;

import java.util.Objects;
import sq2aql.PrintContext;

public class SelectExpr {
  private final PathColumnExpr columnExpr;
  private Alias alias;

  private SelectExpr(PathColumnExpr columnExpr) {
    this.columnExpr = columnExpr;
  }

  public static SelectExpr of(PathColumnExpr columnExpr) {
    return new SelectExpr(columnExpr);
  }


  private SelectExpr(PathColumnExpr columnExpr, Alias alias) {
    this.columnExpr = columnExpr;
    this.alias = alias;
  }

  public static SelectExpr of(PathColumnExpr columnExpr, Alias alias) {
    return new SelectExpr(columnExpr);
  }

  public String print(PrintContext printContext) {
    return columnExpr.print(printContext) +
        (Objects.nonNull(alias) ? alias.print(printContext) : "");
  }
}
