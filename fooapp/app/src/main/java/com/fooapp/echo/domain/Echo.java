package com.fooapp.echo.domain;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Echo {

    private Boolean cool;

    private String sentence;

    private String echoSentence;
}
