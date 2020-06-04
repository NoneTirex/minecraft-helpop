package pl.javatar.http.resolver;

import pl.javatar.http.ThrowableFunction;

import java.io.InputStream;

@FunctionalInterface
public interface TemplateResolver<E, T extends Throwable>
        extends ThrowableFunction<InputStream, E, T>
{
}
