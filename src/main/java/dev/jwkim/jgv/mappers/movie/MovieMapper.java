package dev.jwkim.jgv.mappers.movie;

import dev.jwkim.jgv.DTO.CharacterDTO;
import dev.jwkim.jgv.DTO.Movie_ImageDTO;
import dev.jwkim.jgv.DTO.Movie_InfoDTO;
import dev.jwkim.jgv.entities.movie.MovieEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MovieMapper {
    // 영화 데이터 insert
    int insertMovie(@Param("movie") MovieEntity movie, @Param("raiting") int rating);

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

    //감독 이름으로 영화 찾기
    List<Map<String, String>> getRelatedMoviesByDirector(String director);

    //search 키워드로 영화, 인물 찾기
    List<Movie_ImageDTO> findMovieByKeyword(@Param("keyword") String keyword);

    List<CharacterDTO> findCharacterByKeyword(@Param("keyword") String keyword);

    List<Movie_ImageDTO> searchMoviesByPersonKeyword(@Param("keyword") String keyword);

    // 필모그래피를 위해
    List<Map<String, Object>> findMoviesByActorName(@Param("actorName")String actorName);
}
