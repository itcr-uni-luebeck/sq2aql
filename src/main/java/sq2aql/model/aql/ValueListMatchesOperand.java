package sq2aql.model.aql;

import static java.util.stream.Collectors.joining;

import java.util.List;
import sq2aql.PrintContext;

public record ValueListMatchesOperand(
    List<ValueListItem> valueListItems) implements MatchesOperand {

  public ValueListMatchesOperand {
    if (valueListItems.isEmpty()) {
      throw new IllegalArgumentException("Empty lists are not allowed: " + valueListItems);
    }
  }

  public static ValueListMatchesOperand of(List<ValueListItem> valueListItems) {
    return new ValueListMatchesOperand(valueListItems);
  }


  @Override
  public String print(PrintContext printContext) {
    return "{" + valueListItems.stream().
        map(e -> e.print(printContext)).collect(joining(", ")) + "}";
  }
}
