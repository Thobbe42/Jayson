package com.thobbe.jayson;

import java.util.*;

public class Parser {

  private final Deque<Token> tokens;
  private Token currentToken;

  public Parser(Deque<Token> tokens) {
    this.tokens = tokens;
    currentToken = tokens.poll();
  }

  private void advance() {
    currentToken = tokens.poll();
    if (currentToken == null || currentToken.type().equals(TokenType.ERROR)) {
      throw new RuntimeException("Unexpected end of file or error");
    }
  }

  private void expect(TokenType expected) {
    if (!currentToken.type().equals(expected)) {
      throw new RuntimeException(
          "Unexpected token type: expected " + expected + "but got " + currentToken.type());
    }
    advance();
  }

  private boolean match(TokenType type) {
    if (currentToken.type().equals(type)) {
      advance();
      return true;
    }
    return false;
  }

  public Object parse() {
    Object value = parseValue();
    if (!currentToken.type().equals(TokenType.EOF)) {
      throw new RuntimeException("Unexpected additional data after JSON input");
    }
    return value;
  }

  private Object parseValue() {
    return switch (currentToken.type()) {
      case L_BRACE -> parseObject();
      case L_BRACKET -> parseArray();
      case STRING -> parseString();
      case NUMBER -> parseNumber();
      case BOOLEAN -> parseBoolean();
      case NULL -> parseNull();
      default -> throw new RuntimeException("Unexpected token type: " + currentToken.type());
    };
  }

  private Map<String, Object> parseObject() {
    expect(TokenType.L_BRACE);

    Map<String, Object> object = new HashMap<>();

    while (match(TokenType.STRING)) {
      String key = parseString();
      expect(TokenType.COLON);
      Object value = parseValue();

      object.put(key, value);
    }

    expect(TokenType.R_BRACE);
    return object;
  }

  private List<Object> parseArray() {
    expect(TokenType.L_BRACKET);
    List<Object> array = new ArrayList<>();

    if (!match(TokenType.R_BRACKET)) {
      do {
        array.add(parseValue());
      } while (match(TokenType.COMMA));
    }

    expect(TokenType.R_BRACKET);
    return array;
  }

  private String parseString() {
    String value = currentToken.value();
    advance();
    return value;
  }

  private Number parseNumber() {
    String value = currentToken.value();
    if (value.contains(".") || value.contains("e") || value.contains("E")) {
      return Double.valueOf(value);
    } else {
      return Long.valueOf(value);
    }
  }

  private Boolean parseBoolean() {
    if (currentToken.value().equals("true")) {
      advance();
      return Boolean.TRUE;
    } else if (currentToken.value().equals("false")) {
      advance();
      return Boolean.FALSE;
    } else {
      throw new RuntimeException(
          "Unexpected value: expected " + TokenType.BOOLEAN + "but got " + currentToken.value());
    }
  }

  private Object parseNull() {
    if (currentToken.value().equals("null")) {
      advance();
      return null;
    } else {
      throw new RuntimeException(
          "Unexpected value: expected " + TokenType.NULL + "but got " + currentToken.value());
    }
  }
}
