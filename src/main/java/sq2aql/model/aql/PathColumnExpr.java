package sq2aql.model.aql;

import sq2aql.PrintContext;

public class PathColumnExpr implements ColumnExpr{
  private final IdentifiedPath identifiedPath;

  private PathColumnExpr(IdentifiedPath identifiedPath) {
    this.identifiedPath = identifiedPath;
  }

  public static PathColumnExpr of(IdentifiedPath identifiedPath) {
    return new PathColumnExpr(identifiedPath);
  }

  @Override
  public String print(PrintContext printContext) {
    return this.identifiedPath.print(printContext);
  }
}
