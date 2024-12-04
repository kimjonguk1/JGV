package dev.jwkim.jgv.services.Theater;

import dev.jwkim.jgv.entities.Theater.RegionEntity;
import dev.jwkim.jgv.entities.Theater.TheaterEntity;
import dev.jwkim.jgv.mappers.Theater.TheaterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TheaterService {
    private final TheaterMapper theaterMapper;

    public RegionEntity[] findRegionAll() {
        return this.theaterMapper.getRegionAll();
    }

    public TheaterEntity[] getTheatersByRegion(String region) {
        TheaterEntity[] theaters = this.theaterMapper.getTheatersByRegion(region);
        String[] addrs;
        for (TheaterEntity theater : theaters) {
            addrs = theater.getThAddr().split("\n");
            theater.setThAddr(Arrays.toString(addrs));
        }
        return theaters;
    }
}
