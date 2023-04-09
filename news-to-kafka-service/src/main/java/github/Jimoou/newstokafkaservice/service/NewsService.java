package github.Jimoou.newstokafkaservice.service;

import github.Jimoou.newstokafkaservice.model.News;
import java.util.List;

public interface NewsService {
    List<News> getTopNews();
}
