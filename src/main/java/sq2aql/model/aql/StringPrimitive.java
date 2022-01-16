package sq2aql.model.aql;

import java.util.Objects;
import sq2aql.PrintContext;

public class StringPrimitive implements  Primitive{
  private final String value;

  private StringPrimitive(String value) {
    this.value = Objects.requireNonNull(value);
  }

  public static StringPrimitive of(String value) {
    return new StringPrimitive(value);
  }

  @Override
  public String print(PrintContext printContext) {
    return "'%s'".formatted(value);
  }
}

