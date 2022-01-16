package sq2aql.model.aql;

import java.util.Objects;
import sq2aql.PrintContext;

public class CountFunctionCall implements AggregateFunctionCall {

  private IdentifiedPath identifiedPath;
  private boolean distinct;

  public CountFunctionCall() {
  }

  private CountFunctionCall(IdentifiedPath identifiedPath, boolean distinct) {
    this.identifiedPath = identifiedPath;
    this.distinct = distinct;
  }

  public static CountFunctionCall of(IdentifiedPath identifiedPath, boolean distinct) {
    return new CountFunctionCall(identifiedPath, distinct);
  }

  public String print(PrintContext printContext) {
    return "COUNT (" + (Objects.nonNull(identifiedPath) ?
        (distinct ? "DISTINCT " + identifiedPath.print(printContext)
            : identifiedPath.print(printContext)) : "*") +
        ")";
  }
}
