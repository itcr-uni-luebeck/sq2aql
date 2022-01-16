package sq2aql.model.aql;

import java.math.BigDecimal;
import sq2aql.PrintContext;

public class RealPrimitive  implements Primitive{
  private final BigDecimal real;

  private RealPrimitive(BigDecimal real) {
    this.real = real;
  }

  public static RealPrimitive of(BigDecimal real) {
    return new RealPrimitive(real);
  }

  @Override
  public String print(PrintContext printContext) {
    return String.valueOf(real);
  }
}
