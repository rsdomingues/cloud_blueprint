package com.fooapp.echo.gateway.http;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
public class CustomExceptionHandlerTestController {

    @GetMapping("/test/method-not-supported")
    public void methodNotSupported() {
    }

    @GetMapping("/test/internal-server-error")
    public void internalServerError() {
        throw new RuntimeException();
    }

    @GetMapping("/test/bad-word")
    public void badWordError() {
        throw new IllegalArgumentException("Bad word used");
    }

    @PostMapping("/test/method-argument")
    public void methodArgument(@Valid @RequestBody TestDTO testDTO) {
    }

    public static class TestDTO {

        @NotNull
        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }
}
