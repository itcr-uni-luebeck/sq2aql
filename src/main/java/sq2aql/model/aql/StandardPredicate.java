package sq2aql.model.aql;

import sq2aql.PrintContext;
import sq2aql.model.common.Comparator;

public record StandardPredicate(String objectPath,
                                Comparator comparator,
                                PathPredicateOperand operand) implements
    PathPredicate {

  public static StandardPredicate of(String objectPath, Comparator comparator,
      PathPredicateOperand operand) {
    return new StandardPredicate(objectPath, comparator, operand);
  }

  @Override
  public String print(PrintContext printContext) {
    return "[" + objectPath + comparator.toString() + operand.print(printContext) + "]";
  }
}
