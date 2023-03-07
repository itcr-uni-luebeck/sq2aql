package sq2aql.model.aql;

import sq2aql.PrintContext;

public record UriMatchesOperand(String uri) implements MatchesOperand {

  public static UriMatchesOperand of(String uri) {
    return new UriMatchesOperand(uri);
  }

  @Override
  public String print(PrintContext printContext) {
    return "{" + uri + "}";
  }
}
