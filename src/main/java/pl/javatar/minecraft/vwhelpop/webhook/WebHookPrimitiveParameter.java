package pl.javatar.minecraft.vwhelpop.webhook;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.Function;

public class WebHookPrimitiveParameter implements WebHookParameter
{
    private Object value;

    public WebHookPrimitiveParameter(Object value)
    {
        this.value = value;
    }

    @Override
    public JsonElement toJson(Function<String, String> function)
    {
        if (this.value instanceof Number)
        {
            return new JsonPrimitive((Number) this.value);
        }
        if (this.value instanceof String)
        {
            String value = (String) this.value;
            if (function != null)
            {
                value = function.apply((String) this.value);
            }
            return new JsonPrimitive(value);
        }
        if (this.value instanceof Boolean)
        {
            return new JsonPrimitive((Boolean) this.value);
        }
        if (this.value instanceof Character)
        {
            return new JsonPrimitive((Character) this.value);
        }
        return null;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("WebHookPrimitiveParameter{");
        sb.append("value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
