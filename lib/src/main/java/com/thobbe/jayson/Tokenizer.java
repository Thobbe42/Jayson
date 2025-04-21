package com.thobbe.jayson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

public class Tokenizer {

  private final Reader reader;
  private StringBuilder spelling;
  private int current;

  /**
   * @param is An InputStream on the JSON data to tokenize
   * @throws IOException Exception is thrown if no InputStreamReader can be opened on the input
   */
  public Tokenizer(InputStream is) throws IOException {
    reader = new InputStreamReader(is, StandardCharsets.UTF_8);
    current = reader.read();
  }

  /**
   * Run this tokenizer to structure JSON data into single JSON spec compliant tokens
   *
   * @return A Deque of tokens containing all the tokens gathered from the InputStream
   */
  public Deque<Token> tokenize() {
    Deque<Token> tokens = new ArrayDeque<>();
    while (current != -1) {

      while (current == ' ' || current == '\n' || current == '\r' || current == '\t') skip();

      spelling = new StringBuilder();
      TokenType type = scanToken();
      tokens.add(new Token(type, spelling.toString()));
    }

    tokens.add(new Token(TokenType.EOF, TokenType.EOF.pattern));
    return tokens;
  }

  /** Consumes a single character and appends it to the current spelling */
  private void take() {
    spelling.append((char) current);
    skip();
  }

  /** Skips a character and additionally checks, if an EOF has been reached while scanning */
  private void skip() {
    try {
      int old = current;
      current = reader.read();
      if (old == -1 && current == -1) {
        throw new RuntimeException("Reached EOF while scanning");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Scan a String value. This includes escape sequences as well
   *
   * @return A STRING token if a spec conform String is scanned, otherwise an ERROR token
   */
  private TokenType scanString() {
    skip();
    while (current != '"' && current != -1) {
      if (current != '\\') {
        take();
        continue;
      }

      skip();
      switch (current) {
        case '"':
        case '\\':
        case '/':
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
        case 'b':
          spelling.append('\b');
          skip();
          break;
        case 'f':
          spelling.append('\f');
          skip();
          break;
        case 'u':
          skip();
          spelling.append(parseUnicode());
          break;
        default:
          System.err.println("Invalid escape sequence: \\" + current);
          take();
          break;
      }
    }

    if (current == -1) {
      return TokenType.ERROR;
    }

    skip();
    return TokenType.STRING;
  }

  /**
   * Scans a number. This includes negatives, fractals and scientific exponential notation
   *
   * @return A NUMBER token if a spec compliant number is scanned, an ERROR token otherwise
   */
  private TokenType scanNumber() {

    if (current == '-') {
      take();
    }

    if (current == '0') {
      take();
      if (isDigit(current)) {
        return TokenType.ERROR;
      }
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

  /**
   * Scans for keywords. Keywords here are values like true/false and null, that are not handled as
   * strings
   *
   * @return A BOOLEAN or NULL token depending on keyword, ERROR token otherwise
   */
  private TokenType scanKeyword() {
    do {
      take();
    } while (!isDelimiter(current));

    String value = spelling.toString();
    if (value.equals("null")) {
      return TokenType.NULL;
    }
    if (value.equals("true") || value.equals("false")) {
      return TokenType.BOOLEAN;
    }
    return TokenType.ERROR;
  }

  /**
   * Scans a single token. This includes string, numbers, keywords, but also syntactical JSON
   * characters
   *
   * @return The type of the scanned token
   */
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

  /**
   * Parses a Unicode escape sequence in the form of \\uXXXX. Also checks if it is part of a
   * surrogate pair, and parses the low surrogate as well if possible
   *
   * @return A String consisting of the parsed Unicode characters
   */
  private String parseUnicode() {
    StringBuilder hex = new StringBuilder();
    for (int i = 0; i < 4; i++) {
      if (notIsHexDigit(current)) {
        throw new RuntimeException("Invalid Unicode escape sequence");
      }
      hex.append((char) current);
      skip();
    }

    int codeUnit = Integer.parseInt(hex.toString(), 16);

    if (Character.isHighSurrogate((char) codeUnit)) {
      // Try to parse the low surrogate
      if (current == '\\') {
        skip();
        if (current == 'u') {
          skip();
          StringBuilder lowHex = new StringBuilder();
          for (int i = 0; i < 4; i++) {
            if (notIsHexDigit(current)) {
              throw new RuntimeException("Invalid low surrogate in Unicode escape");
            }
            lowHex.append((char) current);
            skip();
          }

          int lowCodeUnit = Integer.parseInt(lowHex.toString(), 16);
          if (Character.isLowSurrogate((char) lowCodeUnit)) {
            int codePoint = Character.toCodePoint((char) codeUnit, (char) lowCodeUnit);
            return new String(Character.toChars(codePoint));
          } else {
            throw new RuntimeException("Expected low surrogate after high surrogate");
          }
        } else {
          throw new RuntimeException("Expected \\u after high surrogate");
        }
      } else {
        throw new RuntimeException("Expected \\u after high surrogate");
      }
    }

    return new String(Character.toChars(codeUnit));
  }

  private boolean isLetter(int c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
  }

  private boolean isDigit(int c) {
    return (c >= '0' && c <= '9');
  }

  private boolean notIsHexDigit(int c) {
    return (c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F');
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
