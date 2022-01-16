package sq2aql.model.structured_query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import sq2aql.model.common.TermCode;
import java.util.stream.Stream;

/**
 * @author Alexander Kiel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface Modifier {

    @JsonCreator
    static Modifier create(@JsonProperty("type") String type,
                           @JsonProperty("valuePath") String path,
                           @JsonProperty("value") JsonNode... values) {
        if (values == null) {
            throw new IllegalArgumentException("missing modifier values");
        }

        if (values.length == 0) {
            throw new IllegalArgumentException("empty modifier values");
        }

        if ("code".equals(type)) {
            return CodeModifier.of(path, Stream.of(values).map(JsonNode::asText).toArray(String[]::new));
        }
        if ("coding".equals(type)) {
            return CodingModifier.of(path, Stream.of(values).map(TermCode::fromJsonNode).toArray(TermCode[]::new));
        }
        throw new IllegalArgumentException("unknown type: " + type);
    }

//    Container<BooleanContainsExpr> expression(MappingContext mappingContext, Expression alias);
}
