package com.example.demo.controller;

import com.example.demo.domain.Market;
import com.example.demo.domain.Times;
import com.example.demo.service.MarketService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MarketController {


    private MarketService service;

    @Autowired
    public MarketController(MarketService service) {
        this.service = service;
    }

    @PostMapping("/markets")
    public int marketRegister(@RequestBody Market market) {
        return service.marketRegister(market);
    }

    @DeleteMapping("/markets/{id}")
    public int marketDelete(@PathVariable int id) {
        return service.marketDelete(id);
    }

    @PostMapping("/holidays")
    public int marketInsertHoliday(@RequestBody Market market) {
        return service.marketInsertHoliday(market);
    }

    @GetMapping("/markets")
    public List< Map<String, Object>> findAll() {
        List<Market> markets = service.selectAllMarket();
        List<Map<String, Object>> marketList = null;

        if (markets != null) {
            marketList= new ArrayList<>();

            for (Market market : markets) {
                Map<String, Object> marketInfo = new HashMap<>();
                marketInfo.put("name",market.getName());
                marketInfo.put("description",market.getDescription());
                marketInfo.put("level",market.getLevel());
                marketInfo.put("businessStatus",market.getBusinessStatus());

                marketList.add(marketInfo);
            }
        } else {
            marketList.clear();
        }

        return marketList;
    }

    @GetMapping("/markets/{id}")
    public String findOne(@PathVariable int id) {

        Market market = service.selectOneMarket(id);

        Gson gson = new Gson();
        if (market != null) {

            JsonArray jsonArr = new JsonArray();

            for (Times time : market.getBusinessTimes()) {

                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("day", time.getDay());
                jsonObj.addProperty("open", time.getOpen());
                jsonObj.addProperty("close", time.getClose());
                jsonObj.addProperty("status", time.getStatus());

                jsonArr.add(jsonObj);
            }

            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("id", market.getId());
            jsonObj.addProperty("name", market.getName());
            jsonObj.addProperty("description", market.getDescription());
            jsonObj.addProperty("level", market.getLevel());
            jsonObj.addProperty("address", market.getAddress());
            jsonObj.addProperty("phone", market.getPhone());
            jsonObj.add("businessDays", jsonArr);

            return gson.toJson(jsonObj);

        }

        return null;
    }

}
