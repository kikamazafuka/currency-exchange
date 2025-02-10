package com.godeltech.currencyexchange.integration.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.godeltech.currencyexchange.JsonFormatter;
import com.godeltech.currencyexchange.service.ExternalApiService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest
public class ExternalApiServiceIntegrationTest {

  @Autowired private ExternalApiService externalApiService;

  @Autowired private Map<String, Map<String, Double>> exchangeRatesBean;

  @LocalServerPort private Integer port;

  private static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16-alpine");

  @Value("${api.key.fixer}")
  private String apiFixerKey;

  @Value("${api.key.exchangerates}")
  private String apiExchangeratesKey;

  @RegisterExtension
  static WireMockExtension wireMockExtensionFixer =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  @RegisterExtension
  static WireMockExtension wireMockExtensionExchangerates =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  @DynamicPropertySource
  public static void setUpWireMockBaseUrl(DynamicPropertyRegistry registry) {
    registry.add("api.url.fixer", wireMockExtensionFixer::baseUrl);
    registry.add("api.url.exchangerates", wireMockExtensionExchangerates::baseUrl);
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @BeforeEach
  public void clearExchangeRatesBean() {
    exchangeRatesBean.clear();
  }

  @Test
  void updateExchangeRates_success() {

    final var mockedFixerProviderBody =
        JsonFormatter.transformJsonFormat("src/test/resources/expected_response.json");

    final var mockedExchangeratesProviderBody =
        JsonFormatter.transformJsonFormat("src/test/resources/response_exchangerate_prov.json");

    wireMockExtensionFixer.stubFor(
        get(urlPathEqualTo("/api/latest"))
            .withQueryParam("access_key", equalTo(apiFixerKey))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mockedFixerProviderBody)));

    wireMockExtensionExchangerates.stubFor(
        get(urlPathEqualTo("/v1/latest"))
            .withQueryParam("access_key", equalTo(apiExchangeratesKey))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mockedExchangeratesProviderBody)));

    externalApiService.updateExchangeRates();

    final var usdRates = exchangeRatesBean.get("EUR");
    assertEquals(5.593228, usdRates.get("USD"));
    assertEquals(416.202988, usdRates.get("GBP"));

    wireMockExtensionFixer.verify(getRequestedFor(urlPathEqualTo("/api/latest")));
    wireMockExtensionExchangerates.verify(getRequestedFor(urlPathEqualTo("/v1/latest")));
  }

  @Test
  void updateExchangeRates_providerFailure() {

    final var mockedExchangeratesProviderBody =
        JsonFormatter.transformJsonFormat("src/test/resources/response_exchangerate_prov.json");

    final var expectedEurRate =
        new BigDecimal(String.valueOf((1 / 5.593228)))
            .setScale(6, RoundingMode.HALF_UP)
            .doubleValue();
    final var expectedGbpRate =
        new BigDecimal(String.valueOf((416.202988 / 5.593228)))
            .setScale(6, RoundingMode.HALF_UP)
            .doubleValue();

    wireMockExtensionFixer.stubFor(
        get(urlPathEqualTo("/api/latest"))
            .withQueryParam("access_key", equalTo(apiFixerKey))
            .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

    wireMockExtensionExchangerates.stubFor(
        get(urlPathEqualTo("/v1/latest"))
            .withQueryParam("access_key", equalTo(apiExchangeratesKey))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mockedExchangeratesProviderBody)));

    externalApiService.updateExchangeRates();

    final var usdRates = exchangeRatesBean.get("USD");

    assertEquals(expectedEurRate, usdRates.get("EUR"));
    assertEquals(expectedGbpRate, usdRates.get("GBP"));

    wireMockExtensionFixer.verify(getRequestedFor(urlPathEqualTo("/api/latest")));
    wireMockExtensionExchangerates.verify(getRequestedFor(urlPathEqualTo("/v1/latest")));
  }
}
