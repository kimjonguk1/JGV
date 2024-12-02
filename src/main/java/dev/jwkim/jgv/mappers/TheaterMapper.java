package dev.jwkim.jgv.mappers;

import dev.jwkim.jgv.entities.RegionEntity;
import dev.jwkim.jgv.entities.TheaterEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TheaterMapper {
    RegionEntity[] getRegionAll();

    TheaterEntity[] getTheaterAll();

    TheaterEntity[] getTheatersAll();

    TheaterEntity[] getTheatersByRegion(@Param(value = "region") String region);
}
