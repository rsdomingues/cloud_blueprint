package com.test.fooapp.domain;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EchoResponse {

    private String originalMessage;

    private String echoMessage;
}
