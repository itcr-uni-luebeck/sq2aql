package sq2aql.model.aql;

import sq2aql.PrintContext;

public class IntegerPrimitive implements Primitive{
  private final int integer;

  private IntegerPrimitive(int integer) {
    this.integer = integer;
  }

  public static IntegerPrimitive of(int integer){
    return new IntegerPrimitive(integer);
  }

  @Override
  public String print(PrintContext printContext) {
    return String.valueOf(integer);
  }
}
