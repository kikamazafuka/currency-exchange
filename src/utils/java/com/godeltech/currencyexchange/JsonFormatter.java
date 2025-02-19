package com.godeltech.currencyexchange;

import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
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

  @SneakyThrows
  private static String readFile(String path) {
    return new String(Files.readAllBytes(Paths.get(path)));
  }
}
