package com.fooapp.echo.gateway.http.jsons;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse implements Serializable {

  public static final String ERR_VALIDATION = "error.validation";
  public static final String ERR_METHOD_NOT_SUPPORTED = "error.methodNotSupported";
  public static final String ERR_INTERNAL_SERVER_ERROR = "error.internalServerError";
  public static final String ERR_FEATURE = "error.featureDisabled";
  public static final String ERR_TIMEOUT = "error.timeout";
  public static final String ERR_BAD_REQUEST = "error.badRequest";
  public static final String ERR_UNPROCESSABLE_ENTITY = "error.unprocessableEntity";
  public static final String ERR_PAYMENT_NOT_APPROVED = "error.paymentNotApproved";
  public static final String ERR_INTEGRATION_AIRPORT_ERROR = "error.integrationAirportError";
  private static final long serialVersionUID = 4435959686991135330L;

  private final String message;
  private final String description;

  private transient List<ErrorField> errorFields;

  public ErrorResponse(String message) {
    this(message, null);
  }

  public ErrorResponse(String message, String description) {
    this.message = message;
    this.description = description;
  }

  public ErrorResponse(String message, String description, List<ErrorField> errorFields) {
    this.message = message;
    this.description = description;
    this.errorFields = errorFields;
  }
}
