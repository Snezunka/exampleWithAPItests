package com.home.enums;

import lombok.Getter;

@Getter
public enum StatusCode {

  OK(200),
  CREATED(201),
  FORBIDDEN(403),
  NOT_FOUND(404);

  private int value;

  private StatusCode(int value) {
    this.value = value;
  }

}
