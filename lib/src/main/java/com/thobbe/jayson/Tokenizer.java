package com.thobbe.jayson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

public class Tokenizer {

  private final InputStream is;
  private StringBuilder spelling;
  private int current;

  public Tokenizer(InputStream is) throws IOException {
    this.is = is;
  }

  public Deque<Token> tokenize() {
    Deque<Token> tokens = new ArrayDeque<>();
    while (current != -1) {

      spelling = new StringBuilder();
      TokenType type = scanToken();
      tokens.add(new Token(type, spelling.toString()));
    }

    tokens.add(new Token(TokenType.EOF, TokenType.EOF.pattern));
    return tokens;
  }

  private TokenType scanString() {
    return null;
  }

  private TokenType scanNumber() {
    return null;
  }

  private TokenType scanKeyword() {
    return null;
  }

  private TokenType scanToken() {

    if (isLetter(current)) {
      return scanKeyword();
    }
    if (current == '-' || isDigit(current)) {
      return scanNumber();
    }

    if (current == '\"') {
      return scanString();
    }

    return switch (current) {
      case '{' -> TokenType.L_BRACE;
      case '}' -> TokenType.R_BRACE;
      case '[' -> TokenType.L_BRACKET;
      case ']' -> TokenType.R_BRACKET;
      case ',' -> TokenType.COMMA;
      case ':' -> TokenType.COLON;
      default -> null;
    };
  }

  private void take() {}

  private void skip() {}

  private boolean isLetter(int c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
  }

  private boolean isDigit(int c) {
    return (c >= '0' && c <= '9');
  }
}
