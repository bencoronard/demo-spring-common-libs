package dev.hireben.demo.common_libs.http.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonPropertyOrder({ "field", "message" })
public final class HttpFieldValidationErrorMap {
  String field;
  String message;
}
