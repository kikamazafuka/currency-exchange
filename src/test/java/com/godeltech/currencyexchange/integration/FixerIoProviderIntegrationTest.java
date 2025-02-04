package com.godeltech.currencyexchange.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.godeltech.currencyexchange.JsonFormatter;
import com.godeltech.currencyexchange.provider.ExternalApiResponse;
import com.godeltech.currencyexchange.provider.FixerIoProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClientException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest
public class FixerIoProviderIntegrationTest {

  @Autowired private FixerIoProvider fixerIoProvider;

  @Value("${api.key.fixer}")
  private String apiKey;

  @RegisterExtension
  static WireMockExtension wireMockExtension =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  @DynamicPropertySource
  public static void setUpWireMockBaseUrl(DynamicPropertyRegistry registry) {
    registry.add("api.url.fixer", wireMockExtension::baseUrl);
  }

  @Test
  @SneakyThrows
  void getExchangeRates() {

    final var mockedBody =
        JsonFormatter.transformJsonFormat("src/test/resources/expected_response.json");

    wireMockExtension.stubFor(
        get(urlPathEqualTo("/api/latest"))
            .withQueryParam("access_key", equalTo(apiKey))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mockedBody)));

    final var objectMapper = new ObjectMapper();
    final var expectedResponse = objectMapper.readValue(mockedBody, ExternalApiResponse.class);
    final var responses = fixerIoProvider.getExchangeRates();

    assertEquals(expectedResponse, responses.getFirst());
  }

  @Test
  public void getExchangeRates_failure() {
    wireMockExtension.stubFor(
        get(urlPathEqualTo("/api/latest"))
            .withQueryParam("access_key", equalTo(apiKey))
            .willReturn(aResponse().withStatus(HttpStatus.BAD_REQUEST.value())));

    assertThrows(RestClientException.class, () -> fixerIoProvider.getExchangeRates());
  }
}
