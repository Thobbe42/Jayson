package com.thobbe.jayson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Deque;

public class Jayson {

  public static Object parse(String json) {
    try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
      return parse(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Object parse(File file) {
    try (InputStream is = new FileInputStream(file)) {
      return parse(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Object parse(InputStream inputStream) throws IOException {
    Tokenizer tokenizer = new Tokenizer(inputStream);
    Deque<Token> tokens = tokenizer.tokenize();
    Parser parser = new Parser(tokens);
    return parser.parse();
  }
}
