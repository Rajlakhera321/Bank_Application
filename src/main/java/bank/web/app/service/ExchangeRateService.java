package bank.web.app.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        try {
            String url = "https://api.currencyapi.com/v3/latest?apikey=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode data = root.get("data");

            for (String currency : CURRENCIES) {
                JsonNode node = data.get(currency);

                if (node == null) {
                    System.out.println("Missing currency: " + currency);
                    continue;
                }

                rates.put(currency, node.get("value").asDouble());
            }

            System.out.println("Rates: " + rates);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
