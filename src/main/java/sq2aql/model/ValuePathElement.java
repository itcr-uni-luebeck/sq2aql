package sq2aql.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record ValuePathElement(String openEhrType, String archetypeId, String alias) {

  @JsonCreator
  public static ValuePathElement of(@JsonProperty("openEhrType") String openEhrType,
      @JsonProperty("archetypeId") String archetypeId) {
    var archetype_split = archetypeId.split("\\.");
    var sub_string = "";
    if (archetype_split.length > 1) {
      sub_string = archetype_split[archetype_split.length - 2];
    }
    else {
      sub_string = archetypeId;
    }
    final String alias = openEhrType.substring(0,4) + sub_string;
    return new ValuePathElement(Objects.requireNonNull(openEhrType), archetypeId, alias);
  }
}
