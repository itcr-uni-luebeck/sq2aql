package sq2aql.model.aql;

import java.util.Objects;
import sq2aql.PrintContext;

public class ContainsExpression implements BooleanContainsExpr {
  private final ClassExprOperand classExprOperand;
  private ContainsExpr containsExpr;

  private ContainsExpression(ClassExprOperand classExprOperand) {
    this.classExprOperand = classExprOperand;
  }

  public static ContainsExpression of(ClassExprOperand classExprOperand) {
    return new ContainsExpression(classExprOperand);
  }

  private ContainsExpression(ClassExprOperand classExprOperand,
      ContainsExpr containsExpr) {
    this.classExprOperand = classExprOperand;
    this.containsExpr = containsExpr;
  }

  public static ContainsExpression of(ClassExprOperand classExprOperand, ContainsExpr containsExpr) {
    return new ContainsExpression(classExprOperand, containsExpr);
  }

  public void setContainsExpr(ContainsExpr containsExpr) {
    this.containsExpr = containsExpr;
  }

  public ContainsExpr getContainsExpr() {
    return containsExpr;
  }

  @Override
  public String print(PrintContext printContext) {
      return classExprOperand.print(printContext) +
          (Objects.nonNull(containsExpr) ? ("\nCONTAINS " + containsExpr.print(printContext) ) : "");
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ContainsExpression that = (ContainsExpression) o;
    return classExprOperand.equals(that.classExprOperand) && Objects.equals(this.containsExpr, that.containsExpr);
  }

  public boolean sameClassExpr(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ContainsExpression that = (ContainsExpression) o;
    return classExprOperand.equals(that.classExprOperand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classExprOperand, containsExpr);
  }
}
