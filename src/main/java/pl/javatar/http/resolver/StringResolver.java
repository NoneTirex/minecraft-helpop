package pl.javatar.http.resolver;

import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class StringResolver implements TemplateResolver<String, IOException>
{
    @Override
    public String apply(InputStream inputStream) throws IOException
    {
        return new String(IOUtils.readFully(inputStream, Short.MAX_VALUE, false));
    }
}
