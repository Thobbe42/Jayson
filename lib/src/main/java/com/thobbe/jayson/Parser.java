package com.thobbe.jayson;

import java.util.Deque;
import java.util.List;
import java.util.Map;

public class Parser {

  private final Deque<Token> tokens;
  private Token currentToken;

  public Parser(Deque<Token> tokens) {
    this.tokens = tokens;
    currentToken = tokens.poll();
  }

  private void advance() {}

  private void expect(TokenType expected) {}

  private boolean match(TokenType type) {
    return false;
  }

  public Object parse() {
    return null;
  }

  private Object parseValue() {
    return null;
  }

  private Map<String, Object> parseObject() {
    return null;
  }

  private List<Object> parseArray() {
    return null;
  }

  private String parseString() {
    return null;
  }

  private Number parseNumber() {
    return null;
  }

  private Boolean parseBoolean() {
    return null;
  }

  private Object parseNull() {
    return null;
  }
}
