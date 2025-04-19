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
    current = is.read();
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

  private void take() {
    spelling.append(current);
    skip();
  }

  private void skip() {
    try {
      int old = current;
      current = is.read();
      if (old == -1 && current == -1) {
        throw new RuntimeException("Reached EOF while scanning");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private TokenType scanString() {
    skip();
    while (current != '"') {
      if (current != '\\') {
        take();
        continue;
      }

      skip();
      switch (current) {
        case '"':
        case '\\':
          take();
          break;
        case 'n':
          spelling.append('\n');
          skip();
          break;
        case 'r':
          spelling.append('\r');
          skip();
          break;
        case 't':
          spelling.append('\t');
          skip();
          break;
        default:
          System.err.println("Invalid escape sequence: \\" + current);
          take();
          break;
      }
    }
    skip();
    return TokenType.STRING;
  }

  private TokenType scanNumber() {

    if (current == '-') {
      take();
    }

    if (current == 0) {
      take();
    } else if (isDigit(current)) {
      while (isDigit(current)) {
        take();
      }
    } else {
      return TokenType.ERROR;
    }

    if (current == '.') {
      take();
      if (!isDigit(current)) {
        return TokenType.ERROR;
      }
      while (isDigit(current)) {
        take();
      }
    }

    if (current == 'e' || current == 'E') {
      take();
      if (current == '+' || current == '-') {
        take();
      }
      if (!isDigit(current)) {
        return TokenType.ERROR;
      }
      while (isDigit(current)) {
        take();
      }
    }

    return TokenType.NUMBER;
  }

  private TokenType scanKeyword() {
    take();
    while (!isDelimiter(current)) {
      take();
    }

    String value = spelling.toString();
    if (value.equals("null")) {
      return TokenType.NULL;
    }
    if (value.equals("true") || value.equals("false")) {
      return TokenType.BOOLEAN;
    }
    return TokenType.ERROR;
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

    int cc = current;
    take();
    return switch (cc) {
      case '{' -> TokenType.L_BRACE;
      case '}' -> TokenType.R_BRACE;
      case '[' -> TokenType.L_BRACKET;
      case ']' -> TokenType.R_BRACKET;
      case ',' -> TokenType.COMMA;
      case ':' -> TokenType.COLON;
      default -> null;
    };
  }

  private boolean isLetter(int c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
  }

  private boolean isDigit(int c) {
    return (c >= '0' && c <= '9');
  }

  private boolean isDelimiter(int c) {
    return c == '{'
        || c == '}'
        || c == '['
        || c == ']'
        || c == ':'
        || c == ','
        || c == '"'
        || Character.isWhitespace(c);
  }
}
