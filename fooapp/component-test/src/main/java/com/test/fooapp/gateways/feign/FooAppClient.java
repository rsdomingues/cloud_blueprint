package com.test.fooapp.gateways.feign;

import com.test.fooapp.domain.EchoRequest;
import com.test.fooapp.domain.EchoResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "fooapp")
public interface FooAppClient {

  @RequestMapping(method = RequestMethod.POST, value = "/api/v1/echo")
  EchoResponse echo(@RequestBody EchoRequest request);
}
