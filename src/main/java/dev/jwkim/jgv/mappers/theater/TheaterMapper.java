package dev.jwkim.jgv.mappers.theater;

import dev.jwkim.jgv.entities.theater.RegionEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import dev.jwkim.jgv.vos.theater.ScreenVo;
import dev.jwkim.jgv.vos.theater.TheaterVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TheaterMapper {
    RegionEntity[] getRegionAll();

    TheaterEntity[] getAllTheaters();

    TheaterEntity[] getTheatersByRegion(@Param(value = "region") String region);

    TheaterVo[] selectAllTheaters(@Param(value = "theater") String theater);

    TheaterVo[] selectAllTheatersByRegion(@Param(value = "region") String region,
                                          @Param(value = "movie") String movie);

    ScreenVo[] selectAllScreens(@Param(value = "date") String date,
                                @Param(value = "theater") String theater);

    ScreenVo[] selectAllScreensByRegion(@Param(value = "date") String date,
                                        @Param(value = "region") String region,
                                        @Param(value = "movie") String movie);

    ScreenVo[] selectAllScreensByCinemaType(@Param(value = "date") String date,
                                            @Param(value = "region") String region,
                                            @Param(value = "movie") String movie,
                                            @Param(value = "cinema") String cinema);
}
