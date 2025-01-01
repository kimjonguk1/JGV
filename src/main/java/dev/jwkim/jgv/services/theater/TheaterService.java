package dev.jwkim.jgv.services.theater;

import dev.jwkim.jgv.entities.theater.RegionEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import dev.jwkim.jgv.mappers.theater.TheaterMapper;
import dev.jwkim.jgv.vos.theater.ScreenDataVo;
import dev.jwkim.jgv.vos.theater.ScreenVo;
import dev.jwkim.jgv.vos.theater.TheaterVo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TheaterService {
    private final TheaterMapper theaterMapper;

    public Map<Set<String>, Map<Set<String>, Set<String>>> selectAllScreensByCinemaType(String date, String region, String movie, String cinema) {
        if (date == null || date.isEmpty() ||
                region == null || region.isEmpty() ||
                movie == null || movie.isEmpty() ||
                cinema == null || cinema.isEmpty()) {
            return null;
        }
        ScreenVo[] screenVos = this.theaterMapper.selectAllScreensByCinemaType(date, region, movie, cinema);
        Map<Set<String>, Map<Set<String>, Set<String>>> map = new LinkedHashMap<>();
        for (ScreenVo screen : screenVos) {
            Set<String> keys = new LinkedHashSet<>();
            Set<String> values = new LinkedHashSet<>();
            Set<String> genreList = new TreeSet<>();
            Set<String> timeList = new LinkedHashSet<>();
            genreList.add(screen.getGeName());
            for (ScreenVo nextScreen : screenVos) {
                if (!nextScreen.getGeName().equals(screen.getGeName()) && nextScreen.getMoNum() == screen.getMoNum()) {
                    genreList.add(nextScreen.getGeName());  // 다른 영화 장르를 추가
                }
            }
            switch (screen.getRaGrade()) {
                case "청소년관람불가" -> screen.setRaGrade("nineteen");
                case "15세이상관람가" -> screen.setRaGrade("fifteen");
                case "12세이상관람가" -> screen.setRaGrade("twelve");
                case "전체관람가" -> screen.setRaGrade("all");
                case "미정" -> screen.setRaGrade("none");
            }
            keys.add(screen.getRaGrade());
            keys.add(screen.getMoTitle());
            keys.add(String.valueOf(screen.getMoTime()));
            keys.add(screen.getMoDate());
            keys.add(String.valueOf(screen.getSeatCount()));
            keys.add(String.valueOf(screen.getMoNum()));
            keys.add(genreList.toString());
            keys.add(screen.getThName());
            if (screen.getCitName().equals("4DX")) {
                screen.setCitName("DX");
            }
            if (screen.getCitNum() != 1 && screen.getCitNum() != 5) {
                screen.setCiName(screen.getCitName() + "&&");
            }
            values.add(screen.getCiName());
            values.add(screen.getCitName());
            timeList.add(String.valueOf(screen.getScStartDate()).split("T")[1]);
            ScreenDataVo screenDataVo = new ScreenDataVo(screen.getSeatCount(), screen.getUsedSeatCount());
            timeList.add(String.valueOf(screenDataVo.emptySeatCount));
            map.computeIfAbsent(keys, k -> new LinkedHashMap<>());
            map.get(keys).computeIfAbsent(values, k -> new LinkedHashSet<>()).add(timeList.toString());
        }
        return map;
    }

    public Map<Set<String>, Map<Set<String>, Set<String>>> selectAllScreens(String date, String theater) {
        if (date == null || date.isEmpty() ||
                theater == null || theater.isEmpty()) {
            return null;
        }
        ScreenVo[] screenVos = this.theaterMapper.selectAllScreens(date, theater);
        Map<Set<String>, Map<Set<String>, Set<String>>> map = new LinkedHashMap<>();
        for (ScreenVo screen : screenVos) {
            Set<String> keys = new LinkedHashSet<>();
            Set<String> values = new LinkedHashSet<>();
            Set<String> genreList = new TreeSet<>();
            Set<String> timeList = new LinkedHashSet<>();
            genreList.add(screen.getGeName());
            for (ScreenVo nextScreen : screenVos) {
                if (!nextScreen.getGeName().equals(screen.getGeName()) && nextScreen.getMoNum() == screen.getMoNum()) {
                    genreList.add(nextScreen.getGeName());  // 다른 영화 장르를 추가
                }
            }
            switch (screen.getRaGrade()) {
                case "청소년관람불가" -> screen.setRaGrade("nineteen");
                case "15세이상관람가" -> screen.setRaGrade("fifteen");
                case "12세이상관람가" -> screen.setRaGrade("twelve");
                case "전체관람가" -> screen.setRaGrade("all");
                case "미정" -> screen.setRaGrade("none");
            }
            keys.add(screen.getRaGrade());
            keys.add(screen.getMoTitle());
            keys.add(String.valueOf(screen.getMoTime()));
            keys.add(screen.getMoDate());
            keys.add(String.valueOf(screen.getSeatCount()));
            keys.add(String.valueOf(screen.getMoNum()));
            keys.add(genreList.toString());
            keys.add(String.valueOf(Period.between(LocalDate.parse(screen.getMoDate(), DateTimeFormatter.ofPattern("yyyy.MM.dd")), LocalDate.now())));
            if (screen.getCitName().equals("4DX")) {
                screen.setCitName("DX");
            }
            if (screen.getCitNum() != 1 && screen.getCitNum() != 5) {
                screen.setCiName(screen.getCitName() + "&&");
            }
            values.add(screen.getCiName());
            values.add(screen.getCitName());
            timeList.add(String.valueOf(screen.getScStartDate()).split("T")[1]);
            ScreenDataVo screenDataVo = new ScreenDataVo(screen.getSeatCount(), screen.getUsedSeatCount());
            timeList.add(String.valueOf(screenDataVo.emptySeatCount));
            map.computeIfAbsent(keys, k -> new LinkedHashMap<>());
            map.get(keys).computeIfAbsent(values, k -> new LinkedHashSet<>()).add(timeList.toString());
        }
        return map;
    }

    public Map<Set<String>, Map<Set<String>, Set<String>>> selectAllScreensByRegion(String date, String region, String movie) {
        if (date == null || date.isEmpty() ||
                region == null || region.isEmpty() ||
                movie == null || movie.isEmpty()) {
            return null;
        }
        ScreenVo[] screenVos = this.theaterMapper.selectAllScreensByRegion(date, region, movie);
        Map<Set<String>, Map<Set<String>, Set<String>>> map = new LinkedHashMap<>();
        for (ScreenVo screen : screenVos) {
            Set<String> keys = new LinkedHashSet<>();
            Set<String> values = new LinkedHashSet<>();
            Set<String> genreList = new TreeSet<>();
            Set<String> timeList = new LinkedHashSet<>();
            genreList.add(screen.getGeName());
            for (ScreenVo nextScreen : screenVos) {
                if (!nextScreen.getGeName().equals(screen.getGeName()) && nextScreen.getMoNum() == screen.getMoNum()) {
                    genreList.add(nextScreen.getGeName());  // 다른 영화 장르를 추가
                }
            }
            switch (screen.getRaGrade()) {
                case "청소년관람불가" -> screen.setRaGrade("nineteen");
                case "15세이상관람가" -> screen.setRaGrade("fifteen");
                case "12세이상관람가" -> screen.setRaGrade("twelve");
                case "전체관람가" -> screen.setRaGrade("all");
                case "미정" -> screen.setRaGrade("none");
            }
            keys.add(screen.getRaGrade());
            keys.add(screen.getMoTitle());
            keys.add(String.valueOf(screen.getMoTime()));
            keys.add(screen.getMoDate());
            keys.add(String.valueOf(screen.getSeatCount()));
            keys.add(String.valueOf(screen.getMoNum()));
            keys.add(genreList.toString());
            keys.add(screen.getThName());
            if (screen.getCitName().equals("4DX")) {
                screen.setCitName("DX");
            }
            if (screen.getCitNum() != 1 && screen.getCitNum() != 5) {
                screen.setCiName(screen.getCitName() + "&&");
            }
            values.add(screen.getCiName());
            values.add(screen.getCitName());
            timeList.add(String.valueOf(screen.getScStartDate()).split("T")[1]);
            ScreenDataVo screenDataVo = new ScreenDataVo(screen.getSeatCount(), screen.getUsedSeatCount());
            timeList.add(String.valueOf(screenDataVo.emptySeatCount));
            map.computeIfAbsent(keys, k -> new LinkedHashMap<>());
            map.get(keys).computeIfAbsent(values, k -> new LinkedHashSet<>()).add(timeList.toString());
        }
        return map;
    }

    public TheaterVo[] selectAllTheaters(String theater) {
        return this.theaterMapper.selectAllTheaters(theater);
    }

    public TheaterVo[] selectAllTheatersByRegion(String region, String movie) {
        return this.theaterMapper.selectAllTheatersByRegion(region, movie);
    }

    public RegionEntity[] findRegionAll() {
        return this.theaterMapper.getRegionAll();
    }

    public TheaterEntity[] getTheatersByRegion(String region) {
        if (region == null) {
            return null;
        }
        return this.theaterMapper.getTheatersByRegion(region);
    }

    public Map<String, String> getWeekdays(String theater) {
        // 화면의 시작 날짜들을 가져옴
        TheaterVo[] screens = this.theaterMapper.selectAllTheaters(theater);

        // 고유 날짜를 저장할 Set
        SortedSet<String> sortedSet = new TreeSet<>();

        // 날짜 리스트를 돌면서 고유 날짜만 저장
        for (TheaterVo screen : screens) {
            sortedSet.add(screen.getScStartDate().toString().split("T")[0]);
        }
        // 결과
        // [2024-12-11, 2024-12-12, 2024-12-13, 2024-12-14, 2024-12-15, 2024-12-16, 2024-12-17, 2024-12-18, 2024-12-19, 2024-12-20, 2024-12-21, 2024-12-22, 2024-12-23, 2024-12-24, 2024-12-25, 2024-12-26]

        SortedSet<String> sortSet = new TreeSet<>();
        for (String sort : sortedSet) {
            sortSet.add(sort.substring(0, 7));
        }
        // 결과
        // [2024-12]

        Map<String, String> map = new TreeMap<>();
        for (String title : sortSet) {
            List<String> list = new ArrayList<>();
            for (String day : sortedSet) {
                if (day.contains(title)) {
                    LocalDate localDate = LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    list.add(day.split("-")[2] + "-" + localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN).split("요일")[0]);
                }
            }
            title = title.substring(0, 7);
            map.put(title, list.toString().replace('[', ' ').replace(']', ' '));
        }
        // 결과
        // 2024-12 [11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27]

        // 결과 반환
        return map;
    }

    public Map<String, String> getWeekdaysByRegion(String region, String movie) {
        // 화면의 시작 날짜들을 가져옴
        TheaterVo[] screens = this.theaterMapper.selectAllTheatersByRegion(region, movie);

        // 고유 날짜를 저장할 Set
        SortedSet<String> sortedSet = new TreeSet<>();

        // 날짜 리스트를 돌면서 고유 날짜만 저장
        for (TheaterVo screen : screens) {
            sortedSet.add(screen.getScStartDate().toString().split("T")[0]);
        }
        // 결과
        // [2024-12-11, 2024-12-12, 2024-12-13, 2024-12-14, 2024-12-15, 2024-12-16, 2024-12-17, 2024-12-18, 2024-12-19, 2024-12-20, 2024-12-21, 2024-12-22, 2024-12-23, 2024-12-24, 2024-12-25, 2024-12-26]

        SortedSet<String> sortSet = new TreeSet<>();
        for (String sort : sortedSet) {
            sortSet.add(sort.substring(0, 7));
        }
        // 결과
        // [2024-12]

        Map<String, String> map = new TreeMap<>();
        for (String title : sortSet) {
            List<String> list = new ArrayList<>();
            for (String day : sortedSet) {
                if (day.contains(title)) {
                    LocalDate localDate = LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    list.add(day.split("-")[2] + "-" + localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN).split("요일")[0]);
                }
            }
            title = title.substring(0, 7);
            map.put(title, list.toString().replace('[', ' ').replace(']', ' '));
        }
        // 결과
        // 2024-12 [11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27]

        // 결과 반환
        return map;
    }
}
