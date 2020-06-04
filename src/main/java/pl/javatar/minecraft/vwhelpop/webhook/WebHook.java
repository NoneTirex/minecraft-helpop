package pl.javatar.minecraft.vwhelpop.webhook;

public class WebHook
{
    private String url;
    private WebHookParameter parameters;

    public WebHook(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public WebHookParameter getParameters()
    {
        return parameters;
    }

    public void setParameters(WebHookParameter parameters)
    {
        this.parameters = parameters;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("WebHook{");
        sb.append("url='").append(url).append('\'');
        sb.append(", parameters=").append(parameters);
        sb.append('}');
        return sb.toString();
    }
}
