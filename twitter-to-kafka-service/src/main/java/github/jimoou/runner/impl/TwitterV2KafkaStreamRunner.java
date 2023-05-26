package github.jimoou.runner.impl;

import github.jimoou.config.TwitterToKafkaServiceConfigData;
import github.jimoou.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnExpression("${twitter-to-kafka-service.enable-v2-tweets} && not ${twitter-to-kafka-service.enable-mock-tweets}")
public class TwitterV2KafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterV2KafkaStreamRunner.class);

    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

    private final TwitterV2StreamHelper twitterV2StreamHelper;

    public TwitterV2KafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData,
                                      TwitterV2StreamHelper twitterV2StreamHelper) {
        this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
        this.twitterV2StreamHelper = twitterV2StreamHelper;
    }

    @Override
    public void start() {
        String bearerToken = twitterToKafkaServiceConfigData.getTwitterV2BearerToken();
        if (null != bearerToken) {
            try {
                twitterV2StreamHelper.setupRules(bearerToken, getRules());
                twitterV2StreamHelper.connectStream(bearerToken);
            } catch (IOException | URISyntaxException e) {
                LOG.error("트윗 스트리밍 오류!", e);
                throw new RuntimeException("트윗 스트리밍 오류!", e);
            }
        } else {
            LOG.error("베어러 토큰을 가져오는 동안 문제가 발생했습니다. " +
                    "TWITTER_BEARER_TOKEN 환경 변수를 설정해야 합니다");
            throw new RuntimeException("베어러 토큰을 가져오는 동안 문제가 발생했습니다. +" +
                    "TWITTER_BEARER_TOKEN 환경 변수를 설정해야 합니다");
        }

    }

    private Map<String, String> getRules() {
        List<String> keywords = twitterToKafkaServiceConfigData.getTwitterKeywords();
        Map<String, String> rules = new HashMap<>();
        for (String keyword: keywords) {
            rules.put(keyword, "Keyword: " + keyword);
        }
        LOG.info("키워드에 대한 트위터 스트림 필터가 생성: {}", keywords);
        return rules;
    }
}
