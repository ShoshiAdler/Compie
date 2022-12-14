package com.example.player.service;

import com.example.player.data.NBAPlayer;
import com.example.player.model.Player;
import com.example.player.model.Players;
import com.google.gson.Gson;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class PlayerService implements PlayerInterface {
    RestTemplate restTemplate = new RestTemplate();
    private String baseUrl = "https://www.balldontlie.io/api/v1/players";
    Gson gson = new Gson();
    List<Player> playersLst;

    public static String[] CSV_HEADERS = {
            "Player ID", "First Name", "Last Name", "Position", "Height Feet", "Height Inches", "Weight Pounds",
            "Team ID", "Team Abbreviation", "Team City", "Team Conference", "Team Division", "Team Full Name", "Team Name"
    };

    public ResponseEntity<Resource> readCsv(MultipartFile file) {

        // parse CSV file to create a list of `Player` objects
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // create csv bean reader
            CsvToBean<Player> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(Player.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            // convert `CsvToBean` object to list of users
            playersLst = csvToBean.parse();

        } catch (Exception ex) {

        }

        List<List<String>> csvBody = new ArrayList<>();

        //call to Balldontlie’s API
        NBAPlayer nBAPlayer;
        for (Player p : playersLst) {
            nBAPlayer = getAdditionalPlayerInfo(p.getId());
            //nBAPlayerLst.add(nBAPlayer);
            csvBody.add(Arrays.asList(String.valueOf(nBAPlayer.getId()), nBAPlayer.getFirst_name(), nBAPlayer.getLast_name(), nBAPlayer.getPosition(), String.valueOf(nBAPlayer.getHeight_feet()), String.valueOf(nBAPlayer.getHeight_inches()), String.valueOf(nBAPlayer.getWeight_pounds()),
                    String.valueOf(nBAPlayer.getTeam().getId()), nBAPlayer.getTeam().getAbbreviation(), nBAPlayer.getTeam().getCity(), nBAPlayer.getTeam().getConference(), nBAPlayer.getTeam().getDivision(), nBAPlayer.getTeam().getFull_name(), nBAPlayer.getTeam().getName()));
        }

        ByteArrayInputStream byteArrayOutputStream;

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                // defining the CSV printer
                CSVPrinter csvPrinter = new CSVPrinter(
                        new PrintWriter(out),
                        // withHeader is optional
                        CSVFormat.DEFAULT.withHeader(CSV_HEADERS)
                );
        ) {
            // populating the CSV content
            for (List<String> record : csvBody)
                csvPrinter.printRecord(record);

            // writing the underlying stream
            csvPrinter.flush();

            byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        InputStreamResource fileInputStream = new InputStreamResource(byteArrayOutputStream);

        String csvFileName = "NBAPlayer.csv";

        // setting HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName);
        // defining the custom Content-Type
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(fileInputStream, headers, HttpStatus.OK);
    }

    @Cacheable
    private NBAPlayer getAdditionalPlayerInfo(long id) {
        System.out.println("inside getAdditionalPlayerInfo func ");
        String result = restTemplate.getForObject(baseUrl + "/{id}", String.class, String.valueOf(id));
        return gson.fromJson(result, NBAPlayer.class);
    }

    @Scheduled(fixedRate = 9000000) //900000
    public void schedule() {
        System.out.println("print func ");
        String result = restTemplate.getForObject(baseUrl, String.class);
        Players players =  gson.fromJson(result, Players.class);
        for(NBAPlayer nbPlayer : players.getData()) {
            getAdditionalPlayerInfo(nbPlayer.getId());
        }
    }
}
