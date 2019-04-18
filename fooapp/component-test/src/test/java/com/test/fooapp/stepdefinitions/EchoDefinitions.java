package com.test.fooapp.stepdefinitions;

import br.com.six2six.fixturefactory.Fixture;
import com.test.fooapp.ApplicationConfiguration;
import com.test.fooapp.domain.EchoRequest;
import com.test.fooapp.domain.EchoResponse;
import com.test.fooapp.gateways.feign.FooAppClient;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = ApplicationConfiguration.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class)
public class EchoDefinitions {

    private FooAppClient fooAppClient;
    private Exception exceptionReceived;

    private EchoRequest request;
    private EchoResponse response;


    @Autowired
    public EchoDefinitions(FooAppClient fooAppClient) {
        this.fooAppClient = fooAppClient;
    }

    @Given("^a \"([^\"]*)\" sentence$")
    public void aSentence(String order) throws Throwable {
        this.request = Fixture.from(EchoRequest.class).gimme(order);
    }

    @When("^I shout in the cave$")
    public void iShoutInTheCave() {
        try {
            this.response = this.fooAppClient.echo(this.request);
        } catch (Exception e) {
            this.exceptionReceived = e;
        }

    }

    @Then("^I hear it back$")
    public void iHearItBack() {
        assertThat(this.response).isNotNull();
        assertThat(this.response.getEchoMessage()).isNotBlank();
        assertThat(this.response.getEchoMessage()).isEqualTo(this.request.getMessage());
        assertThat(this.exceptionReceived).isNull();
    }

    @Then("^I dont hear anything$")
    public void iDontHearAnything() {
        assertThat(this.response).isNull();
        assertThat(this.exceptionReceived).isNotNull();
    }
}
