package com.example.player.model;

import lombok.Data;

@Data
public class Meta {
    private int total_pages;
    private int current_page;
    private int next_page;
    private int per_page;
    private int total_count;
}
