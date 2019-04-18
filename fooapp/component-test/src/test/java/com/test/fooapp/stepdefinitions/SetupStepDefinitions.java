package com.test.fooapp.stepdefinitions;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.test.fooapp.ApplicationConfiguration;
import cucumber.api.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest(classes = ApplicationConfiguration.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class)
public class SetupStepDefinitions {


    @Autowired
    public SetupStepDefinitions() {
    }

    @Before("@FixtureLoad")
    public void initializeFixtureTemplate() throws Throwable {
        FixtureFactoryLoader.loadTemplates("com.test.fooapp.templates");
    }
}
