package sq2aql.model.aql;

import sq2aql.PrintContext;

public record ExistsExpr(IdentifiedPath identifiedPath) implements IdentifiedExpr {

  public static ExistsExpr of(IdentifiedPath identifiedPath) {
    return new ExistsExpr(identifiedPath);
  }

  @Override
  public String print(PrintContext printContext) {
    return "EXISTS " + identifiedPath.print(printContext);
  }
}
