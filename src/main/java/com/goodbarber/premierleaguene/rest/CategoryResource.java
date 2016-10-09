package com.goodbarber.premierleaguene.rest;

import com.goodbarber.premierleaguene.domain.Category;
import com.goodbarber.premierleaguene.listeners.AppListener;
import com.goodbarber.premierleaguene.parser.NewsParser;
import com.goodbarber.premierleaguene.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@RestController
@RequestMapping(value = "category")
public class CategoryResource {

    Predicate<String> validFeed = a -> a.matches("^(http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ResponseEntity<List<Category>> list(@RequestParam(name = "start", defaultValue = "0") Integer start,
                                           @RequestParam(name = "limit", defaultValue = "20")Integer limit,
                                           @RequestParam(name = "withImage", required = false) boolean withImage) {

        List<Category> list = CategoryRepository.list(start, limit, withImage);
        if (list.isEmpty()){
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @RequestMapping(value = "{name}/get", method = {RequestMethod.GET})
    public ResponseEntity<Category> get(@PathVariable("name") String name) {
        Category category = CategoryRepository.get(name);
        if (category == null){
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(category, HttpStatus.OK);
    }

    @RequestMapping(method = {RequestMethod.PUT})
    public ResponseEntity<Category> add(@RequestBody Category category) {
        if (Objects.nonNull(category.name) && validFeed.test(category.rssFeed)){
            return CategoryRepository.add(category);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "{name}", method = {RequestMethod.POST})
    public ResponseEntity<Category> update(@PathVariable("name") String name, @RequestBody Category category) {
        if (validFeed.test(category.rssFeed)) {
            return CategoryRepository.update(name, category);
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "{name}", method = {RequestMethod.DELETE})
    public ResponseEntity<Category> delete(@PathVariable("name") String name) {
        return CategoryRepository.delete(name);
    }

    @RequestMapping(value = "{name}/updateFeed", method = {RequestMethod.POST})
    public ResponseEntity<Category> updateFeed(@PathVariable("name") String name) {

        AppListener.submit(new NewsParser(name));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
