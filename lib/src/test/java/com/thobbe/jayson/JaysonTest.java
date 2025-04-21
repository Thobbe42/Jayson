package com.thobbe.jayson;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JaysonTest {

  @Test
  void parseValid() {
    assertDoesNotThrow(() -> Jayson.parse("{\"name\": \"Alice\", \"age\": 30}"));
    assertDoesNotThrow(
        () -> Jayson.parse("{\"user\": {\"id\": 1, \"info\": {\"email\": \"test@example.com\"}}}"));
    assertDoesNotThrow(() -> Jayson.parse("[true, false, null, 123, 45.67, \"hello\"]"));
    assertDoesNotThrow(() -> Jayson.parse("[{\"id\": 1}, {\"id\": 2}, {\"id\": 3}]"));
    assertDoesNotThrow(() -> Jayson.parse("{\"emptyObj\": {}, \"emptyArray\": []}"));
    assertDoesNotThrow(
        () -> Jayson.parse("{\"message\": \"Line1\\nLine2\\tTabbed\\\"Quote\\\"\"}"));
    assertDoesNotThrow(
        () ->
            Jayson.parse(
                "{\"emoji\": \"\\uD83D\\uDE03\", \"symbols\": \"\\u20AC\\u00A9\\u2122\"}"));
    assertDoesNotThrow(() -> Jayson.parse("[1e3, -2.5E-2, 6.022e23]"));
    assertDoesNotThrow(() -> Jayson.parse("[[[[[[[42]]]]]]]"));
    assertDoesNotThrow(
        () ->
            Jayson.parse(
                "{\"a\": 1, \"b\": [true, null, {\"c\": \"text\"}], \"d\": {\"nested\": []}}"));
  }

  @Test
  void parseInvalid() {
    assertThrows(RuntimeException.class, () -> Jayson.parse("{\"name\": \"Bob\" \"age\": 25}"));
    assertThrows(RuntimeException.class, () -> Jayson.parse("[1, 2, 3,]"));
    assertThrows(RuntimeException.class, () -> Jayson.parse("{name: \"Charlie\"}"));
    assertThrows(RuntimeException.class, () -> Jayson.parse("{\"number\": 0123}"));
    assertThrows(RuntimeException.class, () -> Jayson.parse("{\"text\": \"hello}"));
  }
}
