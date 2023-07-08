package github.jimoou.runner.impl;

import github.jimoou.config.TwitterToKafkaServiceConfigData;
import github.jimoou.listener.TwitterKafkaStatusListener;
import github.jimoou.runner.StreamRunner;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.util.Arrays;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "false", matchIfMissing = true)
public class TwitterKafkaStreamRunner implements StreamRunner {

  private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamRunner.class);

  private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

  private final TwitterKafkaStatusListener twitterKafkaStatusListener;

  private TwitterStream twitterStream;

  public TwitterKafkaStreamRunner(
      TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData,
      TwitterKafkaStatusListener twitterKafkaStatusListener) {
    this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
    this.twitterKafkaStatusListener = twitterKafkaStatusListener;
  }

  @Override
  public void start() throws TwitterException {
    twitterStream = new TwitterStreamFactory().getInstance();
    twitterStream.addListener(twitterKafkaStatusListener);
    addFilter();
  }

  @PreDestroy
  public void shutdown() {
    if (twitterStream != null) {
      LOG.info("트위터 스트림 종료!");
      twitterStream.shutdown();
    }
  }

  private void addFilter() {
    String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
    FilterQuery filterQuery = new FilterQuery(keywords);
    twitterStream.filter(filterQuery);
    LOG.info("키워드에 대한 트위터 스트림 필터가 생성 {}", Arrays.toString(keywords));
  }
}
