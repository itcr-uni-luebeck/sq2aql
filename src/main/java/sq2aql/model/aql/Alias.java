package sq2aql.model.aql;

import sq2aql.PrintContext;

public final class Alias {
  private final String identifier;

  private Alias(String alias) {
    identifier = alias;
  }

  public static Alias of(String alias) {
    return new Alias(alias);
  }

  public String print(PrintContext printContext) {
    return " as " + identifier;
  }
}
