package dev.jwkim.jgv.mappers.theater;

import dev.jwkim.jgv.entities.theater.RegionEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TheaterMapper {
    RegionEntity[] getRegionAll();

    TheaterEntity[] getTheaterAll();

    TheaterEntity[] getTheatersAll();

    TheaterEntity[] getTheatersByRegion(@Param(value = "region") String region);
}
