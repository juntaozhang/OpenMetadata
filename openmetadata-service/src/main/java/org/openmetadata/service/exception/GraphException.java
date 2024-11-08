package org.openmetadata.service.exception;

public class GraphException extends RuntimeException {
  public GraphException(Exception e) {
    super(e);
  }
}
