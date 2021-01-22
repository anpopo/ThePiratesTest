package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Market {

    private Integer id;
    private String name;
    private String owner;
    private String description;
    private Integer level;
    private String address;
    private String phone;
    private String businessStatus;
    private List<String> holidays;

    private List<Times> businessTimes;
    private String selectHolidays;


}
