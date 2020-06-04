package pl.javatar.http;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.Proxy;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class HttpBuilder
        extends HttpContainer
{
    private CookieManager cookieManager;
    private Proxy         proxy = Proxy.NO_PROXY;

    public HttpBuilder(CookieManager cookieManager, Proxy proxy)
    {
        this.cookieManager = cookieManager;
        this.proxy = proxy;
    }

    public HttpBuilder(HttpBuilder httpBuilder)
    {
        super(httpBuilder);
        this.cookieManager = httpBuilder.cookieManager;
        this.proxy = httpBuilder.proxy;
    }

    public CookieManager getCookieManager()
    {
        return cookieManager;
    }

    public HttpConnection buildConnection(String url)
    {
        return new HttpConnection(url, this);
    }

    public Proxy getProxy()
    {
        return proxy;
    }

    public HttpBuilder setProxy(Proxy proxy)
    {
        this.proxy = proxy;
        return this;
    }

    public HttpCookie getCookie(URI uri, String name)
    {
        List<HttpCookie> cookies = this.cookieManager.getCookieStore().get(uri);
        for (HttpCookie cookie : cookies)
        {
            if (cookie.getName().equalsIgnoreCase(name))
            {
                return cookie;
            }
        }
        return null;
    }

    public void setCookie(URI uri, String name, String value)
    {
        try
        {
            this.cookieManager.put(uri, Collections.singletonMap("Set-Cookie", Collections.singletonList(name + "=" + value)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static HttpBuilder create()
    {
        return create(null);
    }

    public static HttpBuilder create(CookieManager cookieManager)
    {
        if (cookieManager == null)
        {
            cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        }
        return new HttpBuilder(cookieManager, Proxy.NO_PROXY);
    }
}
