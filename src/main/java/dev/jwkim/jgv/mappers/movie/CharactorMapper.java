package dev.jwkim.jgv.mappers.movie;

import dev.jwkim.jgv.entities.movie.CharactorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CharactorMapper {
    // 인물 이름으로 chname 조회
    Integer selectCharacterIdByUniqueFields(String CharacterName, LocalDate date, String job);
    // 인물 저장 후 chnum 한봔
    int insertCharacter(CharactorEntity charactor);
    // 인물 이미지 db 삽입
    int insertCharacterImg(@Param("ChNum") int ChNum, @Param("imgUrl") String imgUrl);
    // 인물 영화 중복 방지
    boolean isMovieCharacterMappingExists(@Param("monum") int monum, @Param("chnum") int chnum);
    // 인물 영화 다대다 매핑
    void insertMovieCharacter(@Param("monum") int monum, @Param("chnum") int chnum);
    // 배우 이름 가져오기
    List<String> selectActorNamesByMovieId(@Param("monum") int monum);
    // 배우 이미지 가져오기
    List<String> selectActorImagesByMovieId(@Param("monum") int monum);
    // 이미지 가져오기(중복 처리를 위해)
    Integer selectCharacterImgIdByCharIdAndUrl(@Param("ChNum") int ChNum, @Param("characterImage") String characterImage);
}
