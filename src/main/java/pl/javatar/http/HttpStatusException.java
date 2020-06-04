package pl.javatar.http;

import java.io.IOException;

public class HttpStatusException
        extends IOException
{
    private final int statusCode;

    public HttpStatusException(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public int getStatusCode()
    {
        return statusCode;
    }
}
