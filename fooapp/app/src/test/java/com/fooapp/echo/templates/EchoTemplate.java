package com.fooapp.echo.templates;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.fooapp.echo.domain.Echo;
import com.fooapp.echo.gateway.http.jsons.EchoRequest;

public class EchoTemplate implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(Echo.class)
                .addTemplate("Cool Echo", new Rule() {
                    {
                        add("sentence", "some cool foo stuff");
                        add("echoSentence", "some cool foo stuff");
                        add("cool", true);
                    }
                })
                .addTemplate("Not Cool Echo", new Rule() {
                    {
                        add("sentence", "some not cool stuff");
                        add("echoSentence", "some not cool stuff");
                        add("cool", false);
                    }
                });
    }
}
