package io.github.snambi.jsonsapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchController {

    private static final Logger logger = LoggerFactory.getLogger(MatchController.class);

    @Autowired
    MatchService matchService;

    @GetMapping("/api/v1/goals")
    public ResponseEntity<Goals> getMatches(@RequestParam String team, @RequestParam int year){

        Goals matches = matchService.getTotalGoals(team, year);

        return ResponseEntity.status(HttpStatus.OK)
                .body(matches);
    }
}
