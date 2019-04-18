package com.fooapp.echo.gateway.http;

import com.fooapp.echo.conf.log.LogKey;
import com.fooapp.echo.domain.Echo;
import com.fooapp.echo.gateway.http.jsons.EchoRequest;
import com.fooapp.echo.gateway.http.jsons.EchoResponse;
import com.fooapp.echo.usecase.ExecuteEcho;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Api(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class EchoController {

  private ExecuteEcho executeEcho;

  @Autowired
  public EchoController(ExecuteEcho executeEcho) {
    this.executeEcho = executeEcho;
  }

  @ApiOperation(value = "Echo request")
  @ApiResponses(
          value = {
                  @ApiResponse(code = 200, message = "Echo done successfully"),
                  @ApiResponse(code = 403, message = "Feature disabled"),
                  @ApiResponse(code = 408, message = "Request Timeout"),
                  @ApiResponse(code = 422, message = "That is a bad word!"),
                  @ApiResponse(code = 500, message = "Internal Server Error")
          }
  )
  @RequestMapping(path = "/echo", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public EchoResponse echo(@RequestBody EchoRequest request) {
    String message = request.getMessage();

    log.info("Requesting an Echo with message {}", value(LogKey.ECHO_MESSAGE.toString(), message));

    Echo echo = executeEcho.execute(message);

    return EchoResponse.builder()
            .originalMessage(echo.getSentence())
            .echoMessage(echo.getEchoSentence())
            .build();
  }
}
