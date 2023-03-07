package sq2aql.model.aql;

import java.util.Objects;
import sq2aql.PrintContext;

public record ClassExprOperand(String classIdentifier,
    String alias,
    PathPredicate pathPredicate) {


  public static ClassExprOperand of(String classIdentifier, String alias) {
    return new ClassExprOperand(classIdentifier, alias, null);
  }


  public static ClassExprOperand of(String classIdentifier, String alias,
      PathPredicate pathPredicate) {
    return new ClassExprOperand(classIdentifier, alias, pathPredicate);
  }

  public static ClassExprOperand of(String classIdentifier, PathPredicate pathPredicate) {
    return new ClassExprOperand(classIdentifier, null, pathPredicate);
  }


  public String print(PrintContext printContext) {
    return classIdentifier + " " + alias +
        (Objects.nonNull(pathPredicate) ? pathPredicate.print(printContext) : "");
  }

  @Override
  public int hashCode() {
    return Objects.hash(classIdentifier, pathPredicate);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ClassExprOperand that = (ClassExprOperand) o;
    return classIdentifier.equals(that.classIdentifier) && pathPredicate.equals(that.pathPredicate);
  }
}
