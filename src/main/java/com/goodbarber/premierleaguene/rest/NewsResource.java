package com.goodbarber.premierleaguene.rest;

import com.goodbarber.premierleaguene.domain.News;
import com.goodbarber.premierleaguene.repository.NewsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "news")
public class NewsResource {

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ResponseEntity<List<News>> list(@RequestParam(name = "start", defaultValue = "0") Integer start,
                                           @RequestParam(name = "limit", defaultValue = "20")Integer limit,
                                           @RequestParam(name = "withContent", required = false) boolean withContent) {

        List<News> list = NewsRepository.list(start, limit, withContent);
        if (list.isEmpty()){
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}/get", method = {RequestMethod.GET})
    public ResponseEntity<News> get(@PathVariable("id") String id) {
        News news = NewsRepository.get(id);
        if (news == null){
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(news, HttpStatus.OK);
    }

}
