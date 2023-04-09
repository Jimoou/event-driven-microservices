package github.Jimoou.newstokafkaservice.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class AlphaVantageClient {
  private RestTemplate restTemplate;
  public String getNewsAndSentiments(String topic, int limit, String apiKey) {
    String url = String.format("https://www.alphavantage.co/query?function=NEWS_SENTIMENT&topics=%s&limit=%d&apikey=%s", topic, limit,apiKey);
    String json = restTemplate.getForObject(url, String.class);
    return json;
  }
}
