package com.test.fooapp.domain;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EchoRequest implements Serializable {

  private String message;

}
