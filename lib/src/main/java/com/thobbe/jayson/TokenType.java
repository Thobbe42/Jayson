package com.thobbe.jayson;

public enum TokenType {
  L_BRACE("{"),
  R_BRACE("}"),
  L_BRACKET("["),
  R_BRACKET("]"),
  COMMA(","),
  COLON(":"),
  STRING("\".*?\""),
  NUMBER("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?"),
  BOOLEAN("true|false"),
  NULL("null"),
  EOF("<eof>");

  final String pattern;

  TokenType(String pattern) {
    this.pattern = pattern;
  }
}
