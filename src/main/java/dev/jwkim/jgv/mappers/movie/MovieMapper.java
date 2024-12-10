package dev.jwkim.jgv.mappers.movie;

import dev.jwkim.jgv.DTO.Movie_ImageDTO;
import dev.jwkim.jgv.DTO.Movie_InfoDTO;
import dev.jwkim.jgv.entities.movie.MovieEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieMapper {
    // 영화 데이터 insert
    int insertMovie(@Param("movie") MovieEntity movie,@Param("raiting") int rating);
    //db에 영화 데이터가 존재하는지 유무만 확인
    int selectMovie();
    // 영화 리스트 모두 표시하기
    List<Movie_ImageDTO> selectAllMovies();
    //영화 중복 데이터 삭제
    boolean deleteDupleMovies();

    // 영화 제목으로 id를 들고오기 (업데이트를 위해)
    Integer selectMovieIdByTitle(@Param("movieTitle") String movieTitle);
    // 영화 데이터 업데이트
    void updateMovie(MovieEntity movie);
    //영화 기본정보와 상세정보 조회
    Movie_InfoDTO getMovieInfoById(@Param("id") Integer id);
}
