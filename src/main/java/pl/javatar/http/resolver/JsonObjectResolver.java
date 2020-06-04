package pl.javatar.http.resolver;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class JsonObjectResolver<E>
        implements TemplateResolver<E, IOException>
{
    private final Type type;
    private final Gson gson;

    public JsonObjectResolver(Type type, Gson gson)
    {
        this.type = type;
        this.gson = gson;
    }

    @Override
    public E apply(InputStream inputStream) throws IOException
    {
        return this.gson.fromJson(new InputStreamReader(inputStream), this.type);
    }
}
