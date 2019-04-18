package com.fooapp.echo.gateway.http;

import com.fooapp.echo.gateway.http.conf.AbstractHttpTest;
import com.fooapp.echo.gateway.http.jsons.ErrorResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class CustomExceptionHandlerTest extends AbstractHttpTest {

  private MockMvc mockMvc;

  @Before
  public void setup() {
    final CustomExceptionHandlerTestController controller =
        new CustomExceptionHandlerTestController();
    mockMvc = buildMockMvcWithBusinessExecptionHandler(controller);
  }

  @Test
  public void testMethodNotSupported() throws Exception {
    mockMvc
        .perform(post("/test/method-not-supported"))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(jsonPath("$.message").value(ErrorResponse.ERR_METHOD_NOT_SUPPORTED))
        .andExpect(jsonPath("$.description").value("Request method 'POST' not supported"));
  }

  @Test
  public void testInternalServerError() throws Exception {
    mockMvc
        .perform(get("/test/internal-server-error"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(ErrorResponse.ERR_INTERNAL_SERVER_ERROR))
        .andExpect(jsonPath("$.description").value("Internal server error"));
  }


  @Test
  public void testBadWordError() throws Exception {
    mockMvc
        .perform(get("/test/bad-word"))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value(ErrorResponse.ERR_UNPROCESSABLE_ENTITY))
        .andExpect(jsonPath("$.description").value("That is a bad word!"));
  }
}
