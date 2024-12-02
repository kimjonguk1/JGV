package dev.jwkim.jgv.services;

import dev.jwkim.jgv.entities.RegionEntity;
import dev.jwkim.jgv.entities.TheaterEntity;
import dev.jwkim.jgv.mappers.TheaterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TheaterService {
    private final TheaterMapper theaterMapper;

    public RegionEntity[] findRegionAll() {
        return this.theaterMapper.getRegionAll();
    }

    public TheaterEntity[] findTheaterAll() {
        return this.theaterMapper.getTheaterAll();
    }

    public TheaterEntity[] findTheatersAll() {
        return this.theaterMapper.getTheatersAll();
    }

    public TheaterEntity[] getTheatersByRegion(String region) {
        return this.theaterMapper.getTheatersByRegion(region);
    }
}
