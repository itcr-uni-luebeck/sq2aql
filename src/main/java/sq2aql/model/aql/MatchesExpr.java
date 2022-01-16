package sq2aql.model.aql;

import sq2aql.PrintContext;

public class MatchesExpr implements IdentifiedExpr {
  private final IdentifiedPath identifiedPath;
  private final MatchesOperand matchesOperand;

  private MatchesExpr(IdentifiedPath identifiedPath, MatchesOperand matchesOperand) {
    this.identifiedPath = identifiedPath;
    this.matchesOperand = matchesOperand;
  }

  public static MatchesExpr of(IdentifiedPath identifiedPath, MatchesOperand matchesOperand) {
    return new MatchesExpr(identifiedPath, matchesOperand);
  }


  @Override
  public String print(PrintContext printContext) {
    return identifiedPath.print(printContext) + " MATCHES " + matchesOperand.print(printContext);
  }

}
