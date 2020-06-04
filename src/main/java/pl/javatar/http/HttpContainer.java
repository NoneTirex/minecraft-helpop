package pl.javatar.http;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

public class HttpContainer
{
    public static final String ROOT_PARAMETER = "";

    private final Map<String, String> headers       = new HashMap<>();
    private final Map<String, String> urlParameters = new HashMap<>();
    private final Map<String, Object> parameters    = new HashMap<>();

    public HttpContainer()
    {
    }

    public HttpContainer(HttpContainer httpContainer)
    {
        this.headers.putAll(httpContainer.headers);
        this.urlParameters.putAll(httpContainer.urlParameters);
        this.parameters.putAll(httpContainer.parameters);
    }

    public HttpContainer setHeader(String name, String value)
    {
        this.headers.put(name, value);
        return this;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public HttpContainer setUrlParameter(String name, String value)
    {
        this.urlParameters.put(name, value);
        return this;
    }

    public HttpContainer setUrlParameter(String name, boolean value)
    {
        this.urlParameters.put(name, Integer.toString(value ? 1 : 0));
        return this;
    }

    public HttpContainer setUrlParameter(String name, int value)
    {
        this.urlParameters.put(name, Integer.toString(value));
        return this;
    }

    public HttpContainer setUrlParameter(String name, long value)
    {
        this.urlParameters.put(name, Long.toString(value));
        return this;
    }

    public HttpContainer setUrlParameter(String name, double value)
    {
        this.urlParameters.put(name, Double.toString(value));
        return this;
    }

    public Map<String, String> getUrlParameters()
    {
        return urlParameters;
    }

    public HttpContainer setParameter(String name, JsonElement value)
    {
        this.parameters.put(name, value);
        return this;
    }

    public HttpContainer setParameter(String name, String value)
    {
        this.parameters.put(name, value);
        return this;
    }

    public Map<String, Object> getParameters()
    {
        return parameters;
    }
}
