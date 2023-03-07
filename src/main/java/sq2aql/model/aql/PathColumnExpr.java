package sq2aql.model.aql;

import sq2aql.PrintContext;

public record PathColumnExpr(IdentifiedPath identifiedPath) implements ColumnExpr {

  public static PathColumnExpr of(IdentifiedPath identifiedPath) {
    return new PathColumnExpr(identifiedPath);
  }

  @Override
  public String print(PrintContext printContext) {
    return this.identifiedPath.print(printContext);
  }
}
