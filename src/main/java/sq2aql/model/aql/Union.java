package sq2aql.model.aql;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sq2aql.PrintContext;

public record Union(List<LogicalExpression> expressions) implements LogicalExpression{

  public static Union of(LogicalExpression e1, LogicalExpression e2) {
    if (e1 instanceof Union) {
      return new Union(Stream.concat(((Union) e1).expressions.stream(),
          Stream.of(Objects.requireNonNull(e2))).collect(Collectors.toUnmodifiableList()));
    }
    else if (Objects.isNull(e1))
    {
      return new Union(List.of(e2));

    }
    else if (Objects.isNull(e2))
    {
      return new Union(List.of(e1));

    }
    else if (e1.equals(e2)){
      return new Union(List.of(e1));
    }
    else {
      return new Union(List.of(Objects.requireNonNull(e1), Objects.requireNonNull(e2)));
    }
  }

  public String print(PrintContext printContext) {
    return expressions.stream().map(e -> e.print(printContext)).collect(joining("\nUNION\n"));
  }
}
