package sq2aql.model.aql;

import sq2aql.PrintContext;

public class UriMatchesOperand implements MatchesOperand{

  private final String uri;

  private UriMatchesOperand(String uri) {
    this.uri = uri;
  }

  public static UriMatchesOperand of(String uri) {
    return new UriMatchesOperand(uri);
  }

  @Override
  public String print(PrintContext printContext) {
    return "{" + uri + "}";
  }
}
