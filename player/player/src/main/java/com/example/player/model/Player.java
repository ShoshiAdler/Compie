package com.example.player.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class Player {
    @CsvBindByName
    private long id;
    @CsvBindByName
    private String nickName;
}
