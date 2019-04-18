package com.fooapp.echo.conf.log;

import com.google.common.base.CaseFormat;

public enum LogKey {
  ECHO_MESSAGE,
  CAUSE;


  @Override
  public String toString() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name().toLowerCase());
  }
}
