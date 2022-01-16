package sq2aql.model.aql;

import java.nio.file.Path;
import java.util.Objects;
import sq2aql.PrintContext;

public class ClassExprOperand {

  private final String classIdentifier;
  private String alias = "";
  private PathPredicate pathPredicate;

  public ClassExprOperand(String classIdentifier) {
    this.classIdentifier = classIdentifier;
  }

  private ClassExprOperand(String classIdentifier, String alias) {
    this.classIdentifier = classIdentifier;
    this.alias = alias;
  }

  public static ClassExprOperand of(String classIdentifier, String alias) {
    return new ClassExprOperand(classIdentifier, alias);
  }

  private ClassExprOperand(String classIdentifier, String alias,
      PathPredicate pathPredicate) {
    this.classIdentifier = classIdentifier;
    this.alias = alias;
    this.pathPredicate = pathPredicate;
  }


  public static ClassExprOperand of(String classIdentifier, String alias,
      PathPredicate pathPredicate) {
    return new ClassExprOperand(classIdentifier, alias, pathPredicate);
  }

  public static ClassExprOperand of(String classIdentifier, PathPredicate pathPredicate) {
    return new ClassExprOperand(classIdentifier, pathPredicate);
  }

  private ClassExprOperand(String classIdentifier, PathPredicate pathPredicate) {
    this.classIdentifier = classIdentifier;
    this.pathPredicate = pathPredicate;
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
