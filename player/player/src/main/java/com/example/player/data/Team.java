package com.example.player.data;

import lombok.Data;

@Data
public class Team {
    private int id;
    private String abbreviation;
    private String city;
    private String conference;
    private String division;
    private String full_name;
    private String name;
}
