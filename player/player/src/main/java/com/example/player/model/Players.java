package com.example.player.model;

import com.example.player.data.NBAPlayer;
import lombok.Data;

import java.util.List;

@Data
public class Players {
    private List<NBAPlayer> data;
    private Meta meta;
}
