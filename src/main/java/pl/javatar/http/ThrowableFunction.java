package pl.javatar.http;

@FunctionalInterface
public interface ThrowableFunction<T, R, Throw extends Throwable>
{
    R apply(T t) throws Throw;
}
