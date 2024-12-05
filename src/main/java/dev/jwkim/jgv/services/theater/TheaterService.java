package dev.jwkim.jgv.services.theater;

import dev.jwkim.jgv.entities.theater.RegionEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import dev.jwkim.jgv.mappers.theater.TheaterMapper;
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
