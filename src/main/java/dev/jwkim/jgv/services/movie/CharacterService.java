package dev.jwkim.jgv.services.movie;

import dev.jwkim.jgv.mappers.movie.CharactorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CharacterService {
    public final CharactorMapper charactorMapper;

    public CharacterService(CharactorMapper charactorMapper) {
        this.charactorMapper = charactorMapper;
    }

    @Transactional
    public void insertCharacterImageIfNotExists(int charId, String characterImage) {
        final String DEFAULT_IMAGE_PATH = "http://img.cgv.co.kr/R2014/images/common/default_230_260.gif";

        if (characterImage.equals(DEFAULT_IMAGE_PATH)) {
            charactorMapper.insertCharacterImg(charId, characterImage);
            return;
        }

        Integer CharImgId = charactorMapper.selectCharacterImgIdByCharIdAndUrl(charId, characterImage);
        if (CharImgId == null && !characterImage.isEmpty()) {
            charactorMapper.insertCharacterImg(charId, characterImage);
        }
    }
}
