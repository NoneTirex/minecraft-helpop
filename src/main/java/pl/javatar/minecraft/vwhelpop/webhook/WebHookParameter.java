package pl.javatar.minecraft.vwhelpop.webhook;

import com.google.gson.JsonElement;

import java.util.function.Function;

public interface WebHookParameter
{
    JsonElement toJson(Function<String, String> function);
}
