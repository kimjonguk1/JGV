package dev.jwkim.jgv.mappers.theater;

import dev.jwkim.jgv.entities.theater.RegionEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TheaterMapper {
    RegionEntity[] getRegionAll();

    TheaterEntity[] getTheatersByRegion(@Param(value = "region") String region);

    int getCinemaCountByTheater(@Param(value = "theater") String theater);

    int getSeatCountByCinema(@Param(value = "theater") String theater);
}
