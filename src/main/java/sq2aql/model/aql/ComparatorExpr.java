package sq2aql.model.aql;

import sq2aql.PrintContext;
import sq2aql.model.common.Comparator;

public record ComparatorExpr(IdentifiedPath identifiedPath,
                             Comparator comparator,
                             Terminal terminal) implements IdentifiedExpr {

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
