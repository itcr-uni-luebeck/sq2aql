package sq2aql.model.aql;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sq2aql.PrintContext;

public record Minus(List<LogicalExpression> expressions) implements LogicalExpression{


  public static Minus of(LogicalExpression e1, LogicalExpression e2) {
    if (e1 instanceof Minus) {
      return new Minus(Stream.concat(((Minus) e1).expressions.stream(),
          Stream.of(Objects.requireNonNull(e2))).collect(Collectors.toUnmodifiableList()));
    }
    else if (Objects.isNull(e1))
    {
      return new Minus(List.of(e2));

    }
    else if (Objects.isNull(e2))
    {
      return new Minus(List.of(e1));

    }
    else if (e1.equals(e2)){
      return new Minus(List.of(e1));
    }
    else {
      return new Minus(List.of(Objects.requireNonNull(e1), Objects.requireNonNull(e2)));
    }
  }

  public String print(PrintContext printContext) {
    return expressions.stream().map(e -> e.print(printContext)).collect(joining("\nMINUS\n"));
  }


}
