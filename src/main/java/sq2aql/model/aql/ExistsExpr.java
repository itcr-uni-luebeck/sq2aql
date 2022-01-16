package sq2aql.model.aql;

import sq2aql.PrintContext;

public final class ExistsExpr implements IdentifiedExpr{

  private final IdentifiedPath identifiedPath;

  private ExistsExpr(IdentifiedPath identifiedPath) {
    this.identifiedPath = identifiedPath;
  }

  public static ExistsExpr of(IdentifiedPath identifiedPath) {
    return new ExistsExpr(identifiedPath);
  }

  @Override
  public String print(PrintContext printContext) {
    return "EXISTS " + identifiedPath.print(printContext);
  }
}
