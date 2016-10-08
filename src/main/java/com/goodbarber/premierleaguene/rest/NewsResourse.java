package com.goodbarber.premierleaguene.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Niko on 10/8/2016.
 */
@RestController
@RequestMapping(value = "/news")
public class NewsResourse {
    @RequestMapping(value = "/{key}", method = {RequestMethod.GET})
    public String getConfiguration(@PathVariable("key") String key) {

        return "Ika-> Nini, Niko-> Nani";

    }

    @RequestMapping(value = "/custom", method = RequestMethod.POST)
    public String custom() {
        return "custom";
    }

}
