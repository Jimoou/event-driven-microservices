package github.Jimoou.newstokafkaservice.service.implement;

import github.Jimoou.newstokafkaservice.client.AlphaVantageClient;
import github.Jimoou.newstokafkaservice.config.ConfigData;
import github.Jimoou.newstokafkaservice.model.News;
import github.Jimoou.newstokafkaservice.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
  private final AlphaVantageClient alphaVantageClient;
  private final ConfigData configData;

  @Override
  public List<News> getTopNews() {
    List<String> topics = configData.getTopics();
    String API_KEY = configData.getKey();

    List<News> topNews = new ArrayList<>();

    for (String topic : topics) {
      String json = alphaVantageClient.getNewsAndSentiments(topic, 2, API_KEY);
      JSONObject jsonObject = new JSONObject(json);
      JSONArray feed = jsonObject.getJSONArray("feed");

      IntStream.range(0, feed.length()).mapToObj(feed::getJSONObject).forEach(newsObject -> {
        News news = new News();
        news.setTitle(newsObject.getString("title"));
        news.setUrl(newsObject.getString("url"));
        news.setSummary(newsObject.getString("summary"));
        news.setPublishedAt(newsObject.getString("time_published"));
        topNews.add(news);
      });
    }
    return topNews;
  }
}
