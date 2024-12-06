package dev.jwkim.jgv.mappers.movie;

import dev.jwkim.jgv.entities.movie.CharactorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CharactorMapper {
    // 인물 이름으로 chname 조회
    Integer selectCharacterIdByName(String CharacterName);
    // 인물 저장 후 chnum 한봔
    int insertCharacter(CharactorEntity charactor);
    // 인물 이미지 db 삽입
    int insertCharacterImg(@Param("ChNum") int ChNum, @Param("imgUrl") String imgUrl);
    // 인물 영화 다대다 매핑
    void insertMovieCharacter(@Param("monum") int monum, @Param("chnum") int chnum);
    // 인물 데이터 삭제
    int deleteAllCharacter();
    // 인물 이미지 테이블 삭제
    int deleteAllCharacterImg();
    // 인물 영화 다대다 매핑 테이블 삭제
    int deleteAllMovieCharacterMapping();
}
