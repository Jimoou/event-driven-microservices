package github.jimoou.runner.impl;

import github.jimoou.config.TwitterToKafkaServiceConfigData;
import github.jimoou.listener.TwitterKafkaStatusListener;
import github.jimoou.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.TwitterException;

import java.util.Random;

@Component
public class MockKafkaStreamRunner implements StreamRunner {

  private static final Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);

  private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

  private final TwitterKafkaStatusListener twitterKafkaStatusListener;

  private static final Random RANDOM = new Random();
  

  public MockKafkaStreamRunner(
      TwitterToKafkaServiceConfigData ConfigData, TwitterKafkaStatusListener statusListener) {
    this.twitterToKafkaServiceConfigData = ConfigData;
    this.twitterKafkaStatusListener = statusListener;
  }

  @Override
  public void start() throws TwitterException {}
}
