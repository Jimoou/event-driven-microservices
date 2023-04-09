package github.Jimoou.newstokafkaservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class News {
    private String title;
    private String url;
    private String summary;
    private String publishedAt;
}
