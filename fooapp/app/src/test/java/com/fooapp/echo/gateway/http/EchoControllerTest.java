package com.fooapp.echo.gateway.http;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooapp.echo.domain.Echo;
import com.fooapp.echo.gateway.http.jsons.EchoRequest;
import com.fooapp.echo.gateway.http.jsons.EchoResponse;
import com.fooapp.echo.usecase.ExecuteEcho;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class EchoControllerTest extends AbstractHttpTest {

    private MockMvc mockMvc;

    @Mock
    private ExecuteEcho executeEcho;

    @InjectMocks
    private EchoController echoController;


    @Before
    public void setup() {
        mockMvc = buildMockMvcWithBusinessExecptionHandler(echoController);
        FixtureFactoryLoader.loadTemplates("com.fooapp.echo.templates");
    }

    @Test
    public void returnOkWhenCoolRequest() throws Exception {
        //GIVEN an cool Request
        EchoRequest echoRequest = Fixture.from(EchoRequest.class).gimme("Cool Echo");

        //AND a cool echo
        Echo echo = Fixture.from(Echo.class).gimme("Cool Echo");
        when(executeEcho.execute(any())).thenReturn(echo);

        //WHEN the controller is called with these request params
        MvcResult result = mockMvc
            .perform(
                post("/api/v1/echo")
                        .content(asJsonString(echoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))


        //THEN no errors are thrown and the status returned is OK
            .andExpect(status().isOk())
            .andReturn();

        // AND
        EchoResponse orderResponse = new ObjectMapper().readValue(result.getResponse().getContentAsString(), EchoResponse.class);
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getEchoMessage()).isEqualTo(echoRequest.getMessage());
        assertThat(orderResponse.getEchoMessage()).isEqualTo(orderResponse.getOriginalMessage());
    }

    @Test
    public void returnOkWhenUncoolRequest() throws Exception {
        //GIVEN an cool Request
        EchoRequest echoRequest = Fixture.from(EchoRequest.class).gimme("Not Cool Echo");

        //AND a cool echo
        Echo echo = Fixture.from(Echo.class).gimme("Not Cool Echo");
        when(executeEcho.execute(any())).thenReturn(echo);

        //WHEN the controller is called with these request params
        MvcResult result = mockMvc
                .perform(
                        post("/api/v1/echo")
                                .content(asJsonString(echoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))


                //THEN no errors are thrown and the status returned is OK
                .andExpect(status().isOk())
                .andReturn();

        // AND
        EchoResponse orderResponse = new ObjectMapper().readValue(result.getResponse().getContentAsString(), EchoResponse.class);
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getEchoMessage()).isEqualTo(echoRequest.getMessage());
        assertThat(orderResponse.getEchoMessage()).isEqualTo(orderResponse.getOriginalMessage());
    }
}
