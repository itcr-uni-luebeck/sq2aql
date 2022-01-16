package sq2aql.model.aql;

import java.util.AbstractMap;
import java.util.Map;

public class OpenEhrDataTypes {
  public static final String DV_QUANTITY = "DV_QUANTITY";
  public static final String DV_DURATION = "DV_DURATION";

  public static final Map<String, String> UCUM_TO_ISO8601 = Map.ofEntries(
      new AbstractMap.SimpleEntry<String, String>("a", "Y"),
      new AbstractMap.SimpleEntry<String, String>("mo", "M"),
      new AbstractMap.SimpleEntry<String, String>("wk", "W"),
      new AbstractMap.SimpleEntry<String, String>("d", "D"),
      new AbstractMap.SimpleEntry<String, String>("h", "h"),
      new AbstractMap.SimpleEntry<String, String>("min", "m")
  );

  }
