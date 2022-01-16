package sq2aql.model.aql;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import sq2aql.PrintContext;

public class ValueListMatchesOperand implements MatchesOperand{
  private final List<ValueListItem> valueListItems;

  public ValueListMatchesOperand(List<ValueListItem> valueListItems) {
    if (valueListItems.isEmpty()) {
      throw new IllegalArgumentException("Empty lists are not allowed: " + valueListItems);
    }
    this.valueListItems = valueListItems;
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
