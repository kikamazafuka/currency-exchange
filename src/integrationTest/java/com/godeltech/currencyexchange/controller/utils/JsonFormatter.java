package com.godeltech.currencyexchange.controller.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonFormatter {

  private static final String WHITESPACE_REGEX = "\\s+";
  private static final String OPEN_BRACKET_REGEX = "\\[\\s+";
  private static final String OPEN_CURLY_BRACE_REGEX = "\\s+\\{";
  private static final String CLOSE_CURLY_BRACE_REGEX = "}\\s+";

  public static String transformJsonFormat(String jsonFilePath) {
    return cleanJson(readFile(jsonFilePath));
  }

  private static String cleanJson(String json) {
    return json.replaceAll(WHITESPACE_REGEX, "")
        .replaceAll(OPEN_BRACKET_REGEX, "[")
        .replaceAll(OPEN_CURLY_BRACE_REGEX, "{")
        .replaceAll(CLOSE_CURLY_BRACE_REGEX, "}");
  }

  private static String readFile(String path) {
    try {
      return new String(Files.readAllBytes(Paths.get(path)));
    } catch (IOException e) {
      throw new RuntimeException("Failed to read JSON file: " + path, e);
    }
  }
}
