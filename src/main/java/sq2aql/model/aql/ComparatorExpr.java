package sq2aql.model.aql;

import sq2aql.PrintContext;
import sq2aql.model.common.Comparator;

public final class ComparatorExpr implements IdentifiedExpr {
  private final IdentifiedPath identifiedPath;
  private final Comparator comparator;
  private final Terminal terminal;

  private ComparatorExpr(IdentifiedPath identifiedPath, Comparator comparator,
      Terminal terminal) {
    this.identifiedPath = identifiedPath;
    this.comparator = comparator;
    this.terminal = terminal;
  }

  public static ComparatorExpr of(IdentifiedPath identifiedPath, Comparator comparator,
      Terminal terminal) {
    return new ComparatorExpr(identifiedPath, comparator, terminal);
  }

  @Override
  public String print(PrintContext printContext) {
    return identifiedPath.print(printContext) + " " + comparator.toString()
        + " " + terminal.print(printContext);
  }
}
