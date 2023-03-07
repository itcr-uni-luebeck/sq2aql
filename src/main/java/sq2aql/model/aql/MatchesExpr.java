package sq2aql.model.aql;

import sq2aql.PrintContext;

public record MatchesExpr(IdentifiedPath identifiedPath,
                          MatchesOperand matchesOperand) implements
    IdentifiedExpr {

  public static MatchesExpr of(IdentifiedPath identifiedPath, MatchesOperand matchesOperand) {
    return new MatchesExpr(identifiedPath, matchesOperand);
  }


  @Override
  public String print(PrintContext printContext) {
    return identifiedPath.print(printContext) + " MATCHES " + matchesOperand.print(printContext);
  }

}
