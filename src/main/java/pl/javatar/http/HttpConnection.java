package pl.javatar.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpConnection
        extends HttpContainer
{
    private static final Gson gson = new GsonBuilder().create();

    private final HttpBuilder     httpBuilder;
    private       String          url;
    private       HttpMethod      method      = HttpMethod.GET;
    private       HttpContentType contentType = HttpContentType.FORM_DATA;

    public HttpConnection(String url, HttpBuilder httpBuilder)
    {
        super(httpBuilder);
        this.url = url;
        this.httpBuilder = httpBuilder;
    }

    public HttpConnection(String url, HttpContainer httpContainer, HttpBuilder httpBuilder)
    {
        super(httpContainer);
        this.url = url;
        this.httpBuilder = httpBuilder;
    }

    public HttpBuilder getHttpBuilder()
    {
        return httpBuilder;
    }

    public String getFullUrl()
    {
        String url = this.url;

        List<String> parameters = this.getUrlParameters().entrySet().stream().map(entry ->
        {
            try
            {
                return entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            return entry.getKey() + "=" + entry.getValue();
        }).collect(Collectors.toList());

        if (parameters.size() > 0)
        {
            int index = url.indexOf('?');
            if (index >= 0)
            {
                int newIndex = url.lastIndexOf('&');
                if (newIndex >= 0)
                {
                    index = newIndex;
                }
            }
            if (index >= 0 && index + 1 != url.length())
            {
                url += "&";
            }
            else
            {
                url += "?";
            }
            url += String.join("&", parameters);
        }

        return url;
    }

    public String getUrl()
    {
        return this.url;
    }

    public HttpMethod getMethod()
    {
        return method;
    }

    public void setMethod(HttpMethod method)
    {
        this.method = method;
    }

    public void execute() throws IOException
    {
        this.execute(null);
    }

    public HttpContentType getContentType()
    {
        return contentType;
    }

    public void setContentType(HttpContentType contentType)
    {
        this.contentType = contentType;
    }

    private HttpURLConnection prepareConnection()
    {
        try
        {
            String url = this.getFullUrl();

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(this.httpBuilder.getProxy());

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.setRequestMethod(this.method.name());

            this.getHeaders().forEach(connection::setRequestProperty);

            Map<String, List<String>> requestProperties = this.httpBuilder.getCookieManager()
                                                                          .get(connection.getURL().toURI(),
                                                                                  connection.getRequestProperties());
            List<String> cookieList = requestProperties.get("Cookie");

            connection.setRequestProperty("Cookie", String.join("; ", cookieList));

            return connection;
        }
        catch (IOException | URISyntaxException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public <T, R extends Throwable> T execute(ThrowableFunction<InputStream, T, R> function) throws R, IOException
    {
        HttpURLConnection connection = this.prepareConnection();

        if (connection == null)
        {
            return null;
        }

        if (this.method == HttpMethod.POST)
        {
            connection.setDoOutput(true);

            if (this.contentType == HttpContentType.FORM_DATA)
            {
                connection.setRequestProperty("Content-Type", "multipart/form-data");
                List<String> parameters = this.getParameters().entrySet().stream()
                                              .map(entry -> entry.getKey() + "=" + entry.getValue())
                                              .collect(Collectors.toList());

                if (parameters.size() > 0)
                {
                    try (OutputStream outputStream = connection.getOutputStream())
                    {
                        outputStream.write(String.join("&", parameters).getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                    }
                }
            }
            else if (this.contentType == HttpContentType.JSON)
            {
                connection.setRequestProperty("Content-Type", "application/json");
                JsonElement jsonElement = null;

                Object rootParameter = this.getParameters().get(ROOT_PARAMETER);
                if (rootParameter instanceof JsonElement)
                {
                    jsonElement = (JsonElement) rootParameter;
                }
                if (jsonElement == null)
                {
                    JsonObject jsonObject = new JsonObject();

                    this.getParameters().forEach((key, value) ->
                    {
                        if (value instanceof JsonElement)
                        {
                            jsonObject.add(key, (JsonElement) value);
                        }
                        else if (value instanceof String)
                        {
                            jsonObject.addProperty(key, (String) value);
                        }
                    });
                    jsonElement = jsonObject;
                }

                try (OutputStream outputStream = connection.getOutputStream())
                {
                    outputStream.write(gson.toJson(jsonElement).getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                }
            }
        }

        int status = connection.getResponseCode();

        if (status == HttpStatus.MOVED_PERMANENTLY)
        {
            String location = connection.getHeaderField("Location");
            if (location != null && !location.isEmpty())
            {
                this.url = location;
                return this.execute(function);
            }
        }

        if (status / 100 != 2)
        {
            throw new HttpStatusException(status);
        }

        try
        {
            this.httpBuilder.getCookieManager().put(connection.getURL().toURI(), connection.getHeaderFields());
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        if (function == null)
        {
            return null;
        }

        try (InputStream inputStream = connection.getInputStream())
        {
            return function.apply(inputStream);
        }
    }
}
