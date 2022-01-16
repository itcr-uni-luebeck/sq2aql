package sq2aql.model.aql;

import sq2aql.PrintContext;
import sq2aql.model.common.Comparator;

public class StandardPredicate implements PathPredicate {

  private final String objectPath;
  private final Comparator comparator;
  private final PathPredicateOperand operand;

  private StandardPredicate(String objectPath, Comparator comparator,
      PathPredicateOperand operand) {
    this.objectPath = objectPath;
    this.comparator = comparator;
    this.operand = operand;
  }

  public static StandardPredicate of(String objectPath, Comparator comparator,
      PathPredicateOperand operand) {
    return new StandardPredicate(objectPath, comparator, operand);
  }

  @Override
  public String print(PrintContext printContext) {
    return "[" + objectPath + comparator.toString() + operand.print(printContext) + "]";
  }
}
