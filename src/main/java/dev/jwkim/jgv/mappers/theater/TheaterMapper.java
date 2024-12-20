package dev.jwkim.jgv.mappers.theater;

import dev.jwkim.jgv.entities.theater.RegionEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import dev.jwkim.jgv.vos.theater.TheaterVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TheaterMapper {
    RegionEntity[] getRegionAll();

    TheaterEntity[] getTheatersByRegion(@Param(value = "region") String region);

    TheaterVo[] selectAllTheaters(@Param(value = "theater") String theater);
}
