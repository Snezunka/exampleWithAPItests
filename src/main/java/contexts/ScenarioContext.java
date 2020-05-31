package contexts;

import static java.util.Optional.ofNullable;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

  private static Map<Context, Object> context = new HashMap<>();

  public static <T> void setContext(Context key, T value) {
    context.put(key, value);
  }

  public static <T> T getContext(Context key) {
    return ofNullable(context.get(key)).map(obj -> (T) obj).orElse(null);
  }

  public enum Context {
    HTTP_RESPONSE
  }
}
