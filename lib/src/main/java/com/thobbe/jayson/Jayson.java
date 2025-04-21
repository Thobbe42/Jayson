package com.thobbe.jayson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Deque;

public class Jayson {

  /**
   * Parses JSON data into a Java object tree.
   *
   * @param json A string containing the JSON data
   * @return An Object containing the data specified by te input in a structured object tree
   */
  public static Object parse(String json) {
    try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
      return parse(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Parses JSON data into a Java object tree.
   *
   * @param file A File containing the JSON data
   * @return An Object containing the data specified by te input in a structured object tree
   */
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
