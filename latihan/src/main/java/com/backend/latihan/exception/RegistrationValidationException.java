package com.backend.latihan.exception;

import java.util.Map;

public class RegistrationValidationException extends RuntimeException {

  private final Map<String, String> errors;

      public RegistrationValidationException(Map<String,String>  errors){
          super("Registration validation error");
          this.errors = errors;
      }

      public Map<String, String> getErrors(){
          return errors;
      }

}
