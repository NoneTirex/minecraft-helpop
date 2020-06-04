package pl.javatar.minecraft.vwhelpop.webhook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class WebHookListParameter implements WebHookParameter
{
    private List<WebHookParameter> value = new ArrayList<>();

    public WebHookListParameter(List<WebHookParameter> value)
    {
        this.value = value;
    }

    @Override
    public JsonElement toJson(Function<String, String> function)
    {
        JsonArray jsonArray = new JsonArray();
        for (WebHookParameter parameter : this.value)
        {
            jsonArray.add(parameter.toJson(function));
        }
        return jsonArray;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("WebHookListParameter{");
        sb.append("value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
