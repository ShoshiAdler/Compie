package com.example.player.data;

import lombok.Data;

@Data
public class NBAPlayer {
    private int id;
    private String first_name;
    private String last_name;
    private String position;
    private int height_feet;
    private int height_inches;
    private int weight_pounds;
    private Team team;
}
