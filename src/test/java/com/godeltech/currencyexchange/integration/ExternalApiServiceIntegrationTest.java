package com.godeltech.currencyexchange.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.godeltech.currencyexchange.JsonFormatter;
import com.godeltech.currencyexchange.service.ExternalApiService;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest
public class ExternalApiServiceIntegrationTest {

  @Autowired private ExternalApiService externalApiService;

  @Autowired private Map<String, Map<String, Double>> exchangeRatesBean;

  @Value("${api.key.fixer}")
  private String apiFixerKey;

  @Value("${api.key.local}")
  private String apiLocalKey;

  @RegisterExtension
  static WireMockExtension wireMockExtensionFixer =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  @RegisterExtension
  static WireMockExtension wireMockExtensionLocal =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  @DynamicPropertySource
  public static void setUpWireMockBaseUrl(DynamicPropertyRegistry registry) {
    registry.add("api.url.fixer", wireMockExtensionFixer::baseUrl);
    registry.add("api.url.local", wireMockExtensionLocal::baseUrl);
  }

  @BeforeEach
  public void clearExchangeRatesBean() {
    exchangeRatesBean.clear();
  }

  @Test
  void updateExchangeRates_success() {

    final var mockedFixerProviderBody =
        JsonFormatter.transformJsonFormat("src/test/resources/expected_response.json");

    final var mockedLocalProviderBody =
        JsonFormatter.transformJsonFormat("src/test/resources/response_arr.json");

    wireMockExtensionFixer.stubFor(
        get(urlPathEqualTo("/api/latest"))
            .withQueryParam("access_key", equalTo(apiFixerKey))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mockedFixerProviderBody)));

    wireMockExtensionLocal.stubFor(
        get(urlPathEqualTo("/api/v1/local-rates"))
            .withQueryParam("access_key", equalTo(apiLocalKey))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mockedLocalProviderBody)));

    externalApiService.updateExchangeRates();

    Map<String, Double> usdRates = exchangeRatesBean.get("EUR");
    assertEquals(5.593228, usdRates.get("USD"));
    assertEquals(416.202988, usdRates.get("GBP"));
  }

  @Test
  void updateExchangeRates_providerFailure() {

    final var mockedLocalProviderBody =
        JsonFormatter.transformJsonFormat("src/test/resources/response_arr.json");

    wireMockExtensionFixer.stubFor(
        get(urlPathEqualTo("/api/latest"))
            .withQueryParam("access_key", equalTo(apiFixerKey))
            .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

    wireMockExtensionLocal.stubFor(
        get(urlPathEqualTo("/api/v1/local-rates"))
            .withQueryParam("access_key", equalTo(apiLocalKey))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(mockedLocalProviderBody)));

    externalApiService.updateExchangeRates();

    Map<String, Double> usdRates = exchangeRatesBean.get("USD");
    assertEquals(0.840751, usdRates.get("EUR"));
    assertEquals(0.202988, usdRates.get("GBP"));
  }
}
