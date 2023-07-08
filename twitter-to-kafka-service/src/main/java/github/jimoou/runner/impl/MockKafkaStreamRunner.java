package github.jimoou.runner.impl;

import github.jimoou.config.TwitterToKafkaServiceConfigData;
import github.jimoou.exception.TwitterToKafkaServiceException;
import github.jimoou.listener.TwitterKafkaStatusListener;
import github.jimoou.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ConditionalOnProperty(
 name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {
  // 각종 로깅을 위한 Logger 객체를 생성합니다.
  private static final Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);

  // Kafka로 스트림할 트위터 데이터에 관한 설정값을 관리하는 객체입니다.
  private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

  // 트위터 스트림의 각종 이벤트를 처리하는 리스너 객체입니다.
  private final TwitterKafkaStatusListener twitterKafkaStatusListener;

  private static final Random RANDOM = new Random();
  // 트윗 생성에 사용할 임의의 단어들을 담고 있는 배열입니다.
  private static final String[] WORDS =
      new String[] {
        "Java",
        "Kafka",
        "Zipkin",
        "Postgres",
        "Oracle",
        "AWS",
        "EDD",
        "Config",
        "CloudFront",
        "DynamoDB",
        "DynamoAccelerator",
        "S3Glacier",
        "S3Standard"
      };

  // 트윗을 JSON 형태로 표현하는 템플릿 문자열입니다.
  private static final String tweetAsRawJson =
      "{"
          + "\"created_at\":\"{0}\","
          + "\"id\":\"{1}\","
          + "\"text\":\"{2}\","
          + "\"user\":{\"id\":\"{3}\"}"
          + "}";

  // 트윗 생성 시각을 표현하기 위한 날짜/시각 포맷 문자열입니다.
  private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

  // 생성자입니다. 필요한 두 개의 객체를 주입받습니다.
  public MockKafkaStreamRunner(
      TwitterToKafkaServiceConfigData ConfigData, TwitterKafkaStatusListener statusListener) {
    this.twitterToKafkaServiceConfigData = ConfigData;
    this.twitterKafkaStatusListener = statusListener;
  }

  // 스트림 처리를 시작하는 메서드입니다.
  @Override
  public void start() throws TwitterException {
    String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
    int minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetLength();
    int maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetLength();
    long sleepTimeMs = twitterToKafkaServiceConfigData.getMockSleepMs();
    LOG.info("Starting mock filtering twitter streams for keywords {}", Arrays.toString(keywords));
    simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);
  }

  // 실제로 트윗을 생성하고 처리하는 메서드입니다.
  private void simulateTwitterStream(
      String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs) {
    Executors.newSingleThreadExecutor()
        .submit(
            () -> {
              try {
                while (true) {
                  String formattedTweetAsRawJson =
                      getFormattedTweet(keywords, minTweetLength, maxTweetLength);
                  Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                  twitterKafkaStatusListener.onStatus(status);
                  sleep(sleepTimeMs);
                }
              } catch (TwitterException e) {
                LOG.error("Error creating twitter status!", e);
              }
            });
  }

  // 트윗을 생성하는 데 필요한 일정 시간을 대기하는 메서드입니다.
  private void sleep(long sleepTimeMs) {
    try {
      Thread.sleep(sleepTimeMs);
    } catch (InterruptedException exception) {
      throw new TwitterToKafkaServiceException(
          "Error while sleeping for waiting new status to create!!");
    }
  }

  // 설정값과 템플릿을 기반으로 트윗을 생성하는 메서드입니다.
  private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
    String[] params =
        new String[] {
          ZonedDateTime.now()
              .format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
          String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
          getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
          String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };
    return formatTweetAsJsonWithParams(params);
  }

  // 트윗 템플릿에 파라미터를 적용하는 메서드입니다.
  private static String formatTweetAsJsonWithParams(String[] params) {
    String tweet = tweetAsRawJson;
    for (int i = 0; i < params.length; i++) {
      tweet = tweet.replace("{" + i + "}", params[i]);
    }
    return tweet;
  }

  // 임의의 트윗 내용을 생성하는 메서드입니다.
  private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
    StringBuilder tweet = new StringBuilder();
    int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
    return constructRandomTweet(keywords, tweet, tweetLength);
  }

  // 실제로 트윗 내용을 구성하는 메서드입니다.
  private static String constructRandomTweet(
      String[] keywords, StringBuilder tweet, int tweetLength) {
    for (int i = 0; i < tweetLength; i++) {
      tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
      if (i == tweetLength / 2) {
        tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
      }
    }
    return tweet.toString().trim();
  }
}
