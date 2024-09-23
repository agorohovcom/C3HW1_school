package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

@RestController
public class InfoController {
    Logger log = LoggerFactory.getLogger(InfoController.class);

    @Value("${server.port}")
    private String port;

    @GetMapping("get_port")
    public ResponseEntity<String> getPort() {
        return ResponseEntity.ok(port);
    }

    @GetMapping("get_some_value")
    public ResponseEntity<Integer> getSomeValue() {
        long start = System.currentTimeMillis();

        Integer result = IntStream.rangeClosed(1, Integer.MAX_VALUE)
                .parallel()
                .reduce(0, Integer::sum);

        long total = System.currentTimeMillis() - start;

        log.info("Method getSomeValue completed from {} milliseconds", total);
        return ResponseEntity.ok(result);
    }
}
