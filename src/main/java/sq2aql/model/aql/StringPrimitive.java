package sq2aql.model.aql;

import java.util.Objects;
import sq2aql.PrintContext;

public record StringPrimitive(String value) implements Primitive {

  public StringPrimitive(String value) {
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

