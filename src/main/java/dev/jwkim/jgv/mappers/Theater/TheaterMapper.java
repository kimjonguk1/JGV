package dev.jwkim.jgv.mappers.Theater;

import dev.jwkim.jgv.entities.Theater.RegionEntity;
import dev.jwkim.jgv.entities.Theater.TheaterEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TheaterMapper {
    RegionEntity[] getRegionAll();

    TheaterEntity[] getTheatersByRegion(@Param(value = "region") String region);
}
