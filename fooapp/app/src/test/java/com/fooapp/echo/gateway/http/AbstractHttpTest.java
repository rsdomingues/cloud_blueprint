package com.fooapp.echo.gateway.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringRunner.class)
@WebAppConfiguration
public abstract class AbstractHttpTest {

  protected MockMvc buildMockMvcWithBusinessExecptionHandler(final Object controller) {
    return MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new CustomExceptionHandler())
        .build();
  }

  protected ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
    final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.initialize();
    return taskExecutor;
  }

  protected String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String jsonContent = mapper.writeValueAsString(obj);
      return jsonContent;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
