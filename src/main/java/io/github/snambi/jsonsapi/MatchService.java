package io.github.snambi.jsonsapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static java.net.http.HttpClient.newHttpClient;

@Service
public class MatchService {

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    public Goals getTotalGoals(String team, int year) {

        Goals result = new Goals();

        if (team == null || team.isBlank() || year < 2000 || year > 2024) {
            throw new RuntimeException("Invalid inputs. ");
        }

        int t1 = getGoalsFor(year, team, true);
        int t2 = getGoalsFor(year, team, false);

        result.setGoals(t1+t2);
        result.setYear(year);
        result.setTeam(team);

        return result;
    }

    public int getGoalsFor(int year, String team, boolean team1) {

        int goals = 0;

        String url = buildUrl(year, team, team1, 1);
        logger.info("P1 URL: " + url);

        Page p1 = requestData(url);

        if( team1 ){
            goals += p1.countTeam1Goals();
        }else{
            goals += p1.countTeam2Goals();
        }

        logger.info( "Goals : "+ goals);

        // are there more pages??
        if( p1.getTotal_pages() > 1 ){
            for( int i=2 ; i <= p1.getTotal_pages() ; i++){
                String u = buildUrl(year, team, team1, i);
                logger.info("P" + i +" URL: " + u);
                Page p = requestData(u);

                if( team1 ){
                    goals += p.countTeam1Goals();
                }else{
                    goals += p.countTeam2Goals();
                }

                logger.info( "Goals : "+ goals);
            }
        }

        return goals;
    }

    String buildUrl(int year, String team, boolean team1, int page) {
        StringBuilder url = new StringBuilder();

        url.append("https://jsonmock.hackerrank.com/api/football_matches?year=");
        url.append(year);
        url.append("&");
        if (team1) {
            url.append("team1=");
        } else {
            url.append("team2=");
        }
        url.append(team);
        url.append("&page=");
        url.append(page);

        return url.toString();
    }

    Page requestData(String url){

        Page p = null;

            try {

                HttpClient httpClient = newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .GET()
                        .header(HttpHeaders.ACCEPT, "application/json")
                        .build();

                HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                String jsonStr = res.body();

                logger.debug("Response received: " + jsonStr);

                // parse the JSON
                ObjectMapper mapper = new ObjectMapper();
                p = mapper.readValue(jsonStr, Page.class);


            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        return p;
    }

    /**
     *
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FootballMatch {
        private String competition;
        private int year;
        private String round;
        private String team1;
        private String team2;
        private int team1goals;
        private int team2goals;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Page {
        int page;
        int per_page;
        int total;
        int total_pages;
        List<FootballMatch> data = new ArrayList<>();

        int countTeam1Goals(){
            int goals = 0;
            for( FootballMatch f : getData()){
                goals += f.getTeam1goals();
            }

            return goals;
        }

        int countTeam2Goals(){
            int goals = 0;
            for( FootballMatch f : getData()){
                goals += f.getTeam2goals();
            }

            return goals;
        }
    }
}
