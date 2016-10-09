package com.goodbarber.premierleaguene.rest;

import com.goodbarber.premierleaguene.domain.Notification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "notification")
public class NotificationResource {

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ResponseEntity<List<Notification>> list(@RequestParam(name = "start", defaultValue = "0") Integer start,
                                               @RequestParam(name = "limit", defaultValue = "20")Integer limit) {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = {RequestMethod.PUT})
    public ResponseEntity<Notification> schedule(@RequestBody Notification notification) {
        if (Objects.nonNull(notification.title) && Objects.nonNull(notification.body)){
            return new ResponseEntity<>(notification, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
