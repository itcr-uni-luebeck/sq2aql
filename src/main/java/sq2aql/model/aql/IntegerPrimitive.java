package sq2aql.model.aql;

import sq2aql.PrintContext;

public record IntegerPrimitive(int integer) implements Primitive {

  public static IntegerPrimitive of(int integer) {
    return new IntegerPrimitive(integer);
  }

  @Override
  public String print(PrintContext printContext) {
    return String.valueOf(integer);
  }
}
