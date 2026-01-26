package bank.web.app.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Getter
public class ExchangeRateService {

    private final RestTemplate restTemplate;

    /**
     * A map to store the exchange rates for each currency.
     */
    private Map<String, Double> rates = new HashMap<>();

    /**
     * A set of currencies for which exchange rates are fetched.
     */
    private final Set<String> CURRENCIES = Set.of(
            "USD",
            "EUR",
            "GBP",
            "JPY",
            "NGN",
            "INR"
    );

    @Value("${currencyApiKey}")
    private String apiKey;

    public void getExchangeRates() {
        String CURRENCY_API = "https://api.currencyapi.com/v3/latest?apikey=";
        var response = restTemplate.getForEntity(CURRENCY_API + apiKey, JsonNode.class);
        var data = Objects.requireNonNull(response.getBody()).get("data");
        for (var currency : CURRENCIES) {
            rates.put(currency, data.get(currency).get("value").doubleValue());
        }
        System.out.println(rates);
    }
}
