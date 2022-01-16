package sq2aql.model.aql;

import java.util.Objects;
import sq2aql.PrintContext;

public class IdentifiedPath {

  private final String identifier;
  private PathPredicate pathPredicate;
  private String objectPath;

  private IdentifiedPath(String identifier)
  {
    this.identifier = identifier;
  }

  public static IdentifiedPath of(String identifier) {
    return new IdentifiedPath(identifier);
  }

  private IdentifiedPath(String identifier, PathPredicate pathPredicate) {
    this.identifier = identifier;
    this.pathPredicate = pathPredicate;
  }

  public static IdentifiedPath of(String identifier, PathPredicate pathPredicate) {
    return new IdentifiedPath(identifier, pathPredicate);
  }

  private IdentifiedPath(String identifier, String objectPath) {
    this.identifier = identifier;
    this.objectPath = objectPath;
  }

  public static IdentifiedPath of(String identifier, String objectPath) {
    return new IdentifiedPath(identifier, objectPath);
  }

  private IdentifiedPath(String identifier, PathPredicate pathPredicate,
      String objectPath) {
    this.identifier = identifier;
    this.pathPredicate = pathPredicate;
    this.objectPath = objectPath;
  }

  public static IdentifiedPath of(String identifier, PathPredicate pathPredicate,
      String objectPath) {
    return new IdentifiedPath(identifier, pathPredicate, objectPath);
  }

  public String print(PrintContext printContext) {
    return this.identifier +
        (Objects.nonNull(this.pathPredicate) ? this.pathPredicate.print(printContext) : "" ) +
        (Objects.nonNull(this.objectPath) ? this.objectPath : "");
  }
}
