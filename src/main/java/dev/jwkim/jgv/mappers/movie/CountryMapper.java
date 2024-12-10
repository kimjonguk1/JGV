package dev.jwkim.jgv.mappers.movie;

import dev.jwkim.jgv.entities.movie.CountryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CountryMapper {
    //국가 이름으로 conum 조회
    Integer selectCountryIdByName(String countryName);
    //국가 저장 후 conum 반환
    int insertMovieCountry(CountryEntity countryEntity);
    //다대다 매핑 테이블에 db 삽입
    void insertMovieCountryMapping(@Param("monum") int monum, @Param("conum") int conum);
    //국가 테이블 삭제
    int deleteAllCountry();
    //영화 국가 매핑 테이블 삭제
    int deleteAllCountryMapping();
}
