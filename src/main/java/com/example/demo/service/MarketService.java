package com.example.demo.service;

import com.example.demo.domain.Market;
import com.example.demo.domain.Times;
import com.example.demo.mapper.MarketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class MarketService {

    private MarketMapper mapper;

    @Autowired
    public MarketService(MarketMapper mapper) {
        this.mapper = mapper;
    }

    public int marketRegister(Market market) {

        List<Times> times = market.getBusinessTimes();
        Times canNotRegisterTime = times.stream().
                filter(time -> time.getOpen().equals(time.getClose()))
                .findAny()
                .orElse(null);

        if(canNotRegisterTime == null) {
            int i = mapper.insertMarket(market);
            if (i > 0) {
                for(Times t : times) {
                    t.setMarketId(market.getId());
                }
                int j = mapper.insertMarketTime(times);
            }
            return i;
        } else {
            return 0;
        }
    }

    public int marketDelete(int id) {
        Map<String,Integer> paraMap = new HashMap<>();
        paraMap.put("id", id);
        mapper.deleteTime(paraMap);
        return mapper.deleteMarket(paraMap);
    }

    public int marketInsertHoliday(Market market) {

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("id", market.getId());

        String temp = "";
        List<String> holidays = market.getHolidays();
        for(int i = 0; i < market.getHolidays().size(); i++) {

            temp += i == 0 ? market.getHolidays().get(i) : ", " + market.getHolidays().get(i);
        }
        paraMap.put("holidays", temp);

        return mapper.insertHoliday(paraMap);
    }

    public List<Market> selectAllMarket() {
        // 전체 매장 가져오기

        Map<String, Object> paraMap = new HashMap<>();

        paraMap.put("check", "");

        List<Market> marketList = mapper.selectAllMarket(paraMap);


        // 현재시간 가져오기

        SimpleDateFormat dateFormat = new SimpleDateFormat ( "yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat ( "HH:mm");

        Date now = new Date();

        String dateToday = dateFormat.format(now);
        String timeToday = timeFormat.format(now);


        // 현재시간과 업무시간이 맞는지 아닌지 확인
        for(Market market : marketList) {

            paraMap.put("id", market.getId());

            List<Times> timeList = mapper.selectAllTimes(paraMap);

            market.setBusinessTimes(timeList);

            for(Times time : timeList) {
                // 휴일인지 아닌지 확인

                String[] weekDay = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
                Calendar cal = Calendar.getInstance();
                int num = cal.get(Calendar.DAY_OF_WEEK)-1;
                String today = weekDay[num];

                if(today.equalsIgnoreCase(time.getDay())) {
                    try {
                        Date opentime = timeFormat.parse(time.getOpen());
                        Date closetime = timeFormat.parse(time.getClose());
                        Date present =  timeFormat.parse(timeToday);

                        if (present.compareTo(opentime) > 0 && present.compareTo(closetime) < 0) {
                            market.setBusinessStatus("OPEN");
                        } else {
                            market.setBusinessStatus("CLOSE");
                        }
                    } catch(ParseException e) {

                    }
                } else {
                    market.setBusinessStatus("CLOSE");
                }
            }
            if (market.getSelectHolidays() != null) {
                // 휴일인지 확인하시구요~
                String[] holidays = market.getSelectHolidays().split(", ");

                for(String holiday : holidays) {

                    if (dateToday.equals(holiday)) {
                        market.setBusinessStatus("HOLIDAY");
                    }
                }
            }
        }

        // 리턴하기
        return marketList;

    }

    public Market selectOneMarket(int id) {

        Map<String, Object> paraMap = new HashMap<>();

        paraMap.put("check", "check");
        paraMap.put("id", id);

        List<Market> marketList = mapper.selectAllMarket(paraMap);

        Market market = null;

        if (marketList.size() == 1) {
            market = marketList.get(0);
        } else {
            return market;
        }

        List<Times> timeList = mapper.selectAllTimes(paraMap);


        // 현재시간 가져오기

        SimpleDateFormat dateFormat = new SimpleDateFormat ( "yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat ( "HH:mm");

        Date now = new Date();

        String dateToday = dateFormat.format(now);
        String timeToday = timeFormat.format(now);


        // 오늘 요일 구하기
        String[] weekDay = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        Calendar cal = Calendar.getInstance();
        int num = cal.get(Calendar.DAY_OF_WEEK)-1;
        String today = weekDay[num];
        int temp = num;

        int[] check = new int[weekDay.length];

        if (timeList != null) {
            for(Times time : timeList) {
                for (int i = 0; i < weekDay.length; i++) {
                    if (weekDay[i].equals(time.getDay())) {
                        check[i] = i+1;
                    }
                }
            }

            String nextDay = "";
            String nexnextDay = "";

            int count = 0;

            for (int i = 1; i < check.length; i++) {
                temp = temp + i > 6 ? 0 : temp + 1;
                count += 1;
                if (check[temp] != 0) {
                    today = weekDay[temp];
                    break;
                } else continue;
            }

            cal.add(Calendar.DATE, count);
            dateToday = dateFormat.format(cal.getTime());

            count = 0;

            for (int i = 1; i < check.length; i++) {
                temp = temp + i > 6 ? 0 : temp + 1;
                count += 1;
                if (check[temp] != 0) {
                    nextDay = weekDay[temp];
                    break;
                } else continue;
            }

            cal.add(Calendar.DATE, count);
            String dateNx = dateFormat.format(cal.getTime());

            count = 0;
            for (int i = 1; i < check.length; i++) {
                temp = temp + i > 6 ? 0 : temp + 1;
                count += 1;
                if (check[temp] != 0) {
                    nexnextDay = weekDay[temp];
                    break;
                } else continue;
            }
            cal.add(Calendar.DATE, count);
            String dateNxNx = dateFormat.format(cal.getTime());


            Map<String, String> dateMap = new HashMap<>();

            dateMap.put(dateToday, "1"+today);
            dateMap.put(dateNx, "2"+nextDay);
            dateMap.put(dateNxNx, "3"+nexnextDay);


            Times[] timeThree = new Times[3];


            for (String a : dateMap.keySet()) {
                Times listUpTime = new Times();

                for(Times time : timeList) {


                    if(dateMap.get(a).substring(1).equalsIgnoreCase(time.getDay())) {
                        try {
                            Date opentime = timeFormat.parse(time.getOpen());
                            Date closetime = timeFormat.parse(time.getClose());
                            Date present =  timeFormat.parse(timeToday);

                            if (present.compareTo(opentime) > 0 && present.compareTo(closetime) < 0) {
                                time.setStatus("OPEN");
                            } else {
                                time.setStatus("CLOSE");
                            }

                            listUpTime = time;
                        } catch(ParseException e) {

                        }
                    }

                    if (market.getSelectHolidays() != null) {
                        // 휴일인지 확인하시구요~
                        String[] holidays = market.getSelectHolidays().split(", ");

                        for (String holiday : holidays) {
                            if (a.equals(holiday)) {
                                listUpTime.setStatus("HOLIDAY");
                            }
                        }
                    }
                }

                if ("1".equals(dateMap.get(a).substring(0,1))) {
                    timeThree[0] = listUpTime;
                }else if ("2".equals(dateMap.get(a).substring(0,1))) {
                    timeThree[1] = listUpTime;
                }else {
                    timeThree[2] = listUpTime;
                }
            }
            List<Times> timesList = new ArrayList<>();
            for (Times t : timeThree) {
                timesList.add(t);
            }

            market.setBusinessTimes(timesList);
        }
        return market;
    }
}
