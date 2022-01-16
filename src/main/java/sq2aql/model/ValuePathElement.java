package sq2aql.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record ValuePathElement(String openEhrType, String archetypeId) {

  @JsonCreator
  public static ValuePathElement of(@JsonProperty("openEhrType") String openEhrType,
      @JsonProperty("archetypeId") String archetypeId) {
    return new ValuePathElement(Objects.requireNonNull(openEhrType), archetypeId);
  }
}
