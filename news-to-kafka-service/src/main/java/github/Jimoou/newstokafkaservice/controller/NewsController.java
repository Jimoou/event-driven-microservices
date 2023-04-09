package github.Jimoou.newstokafkaservice.controller;

import github.Jimoou.newstokafkaservice.model.News;
import github.Jimoou.newstokafkaservice.service.NewsService;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class NewsController {
    private NewsService newsService;

    @GetMapping("/news")
    public ResponseEntity<List<News>> getNews() {
        List<News> newsList = newsService.getTopNews();
        return new ResponseEntity<>(newsList, HttpStatus.OK);
    }
}
