package com.test.fooapp.templates;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.test.fooapp.domain.EchoRequest;

public class EchoRequestTemplate implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(EchoRequest.class)
                .addTemplate("cool", new Rule() {
                    {
                        add("message", "some cool foo stuff");
                    }
                })
                .addTemplate("not cool", new Rule() {
                    {
                        add("message", "some not cool bar stuff");
                    }
                });
    }
}
