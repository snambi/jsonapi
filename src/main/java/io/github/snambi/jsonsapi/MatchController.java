package io.github.snambi.jsonsapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchController {

    @Autowired
    MatchService matchService;

    @GetMapping("/api/v1/matches")
    public ResponseEntity<String> getMatches(@RequestParam String team, @RequestParam int year){

        int matches = matchService.getMatches(team, year);

        return ResponseEntity.ok().build();
    }
}
