package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Times {

    private Integer id;
    private String day;
    private String open;
    private String close;
    private Integer marketId;


    private String status;
}
