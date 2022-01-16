package sq2aql.model.aql;

import sq2aql.PrintContext;

public class NodePredicate implements PathPredicate{

  @Override
  public String print(PrintContext printContext) {
    return "[" + "]";
  }
}
