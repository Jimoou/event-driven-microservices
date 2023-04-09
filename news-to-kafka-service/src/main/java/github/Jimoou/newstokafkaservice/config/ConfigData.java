package github.Jimoou.newstokafkaservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "news-to-kafka-service")
public class ConfigData {
    private List<String> topics;
    private String key;
}
