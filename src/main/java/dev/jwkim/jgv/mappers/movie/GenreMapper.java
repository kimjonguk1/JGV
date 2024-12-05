package dev.jwkim.jgv.mappers.movie;

import dev.jwkim.jgv.entities.Movie.GenereEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GenreMapper {
    //장르 저장 후 genum 반환
    int insertMovieGenre(GenereEntity genre);
    // 장르 이름으로 genum 조회
    Integer selectGenereIdByName(String geName);
    // 다대다 매핑 테이블에 data 삽입
    void insertMovieGenreMapping(@Param("monum") int monum, @Param("genum") int genum);
    // 모든 데이터 삭제
    int deleteAllMovieGenre();
    // 다대다 매핑 테이블 삭제
    int deleteMovieGenreMapping();
}
