package com.fooapp.echo.usecase;

import com.fooapp.echo.domain.Echo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Component
public class ExecuteEcho {

    public ExecuteEcho() {
    }

    public Echo execute(String sentence) {
        checkArgument(sentence != null, "this is a bad word");
        checkArgument(!sentence.contains("bar"), "bar is a bad word");

        return Echo.builder()
                .sentence(sentence)
                .echoSentence(sentence)
                .cool(sentence.contains("foo"))
                .build();
    }
}
