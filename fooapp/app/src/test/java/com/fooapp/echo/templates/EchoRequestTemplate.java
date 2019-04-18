package com.fooapp.echo.templates;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.fooapp.echo.domain.Echo;
import com.fooapp.echo.gateway.http.jsons.EchoRequest;

import java.util.UUID;

public class EchoRequestTemplate implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(EchoRequest.class)
                .addTemplate("Cool Echo", new Rule() {
                    {
                        add("message", "some cool foo stuff");
                    }
                })
                .addTemplate("Not Cool Echo", new Rule() {
                    {
                        add("message", "some not cool stuff");
                    }
                });
    }
}
