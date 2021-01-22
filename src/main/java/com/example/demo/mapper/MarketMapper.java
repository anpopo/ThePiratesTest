package com.example.demo.mapper;

import com.example.demo.domain.Market;
import com.example.demo.domain.Times;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MarketMapper {
    int insertMarket(Market market);


    int insertMarketTime(List<Times> times);

    void deleteTime(Map<String, Integer> paraMap);

    int deleteMarket(Map<String, Integer> paraMap);

    int insertHoliday(Map<String, Object> paraMap);

    List<Market> selectAllMarket(Map<String, Object> paraMap);

    List<Times> selectAllTimes(Map<String, Object> paraMap);
}
