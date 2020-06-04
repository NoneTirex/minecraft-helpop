package pl.javatar.minecraft.vwhelpop.webhook;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.function.Function;

public class WebHookMapParameter
        implements WebHookParameter
{
    private Map<String, WebHookParameter> value;

    public WebHookMapParameter(Map<String, WebHookParameter> value)
    {
        this.value = value;
    }

    @Override
    public JsonElement toJson(Function<String, String> function)
    {
        JsonObject object = new JsonObject();
        this.value.forEach((key, value) ->
        {
            object.add(key, value.toJson(function));
        });
        return object;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("WebHookMapParameter{");
        sb.append("value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
