package com.jsebastian.eden.EdenSys.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

  public ResourceNotFoundException() {
      this("Resource not found");
  }
}
