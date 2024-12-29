package dev.jwkim.jgv.services.admin;

import dev.jwkim.jgv.DTO.AdminMovieDTO;
import dev.jwkim.jgv.DTO.AdminTheaterDTO;
import dev.jwkim.jgv.DTO.ScreenInfoDTO;
import dev.jwkim.jgv.mappers.admin.AdminMapper;
import dev.jwkim.jgv.vos.PageVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final AdminMapper adminMapper;

    public AdminService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }


    public Pair<PageVo, AdminMovieDTO[]> selectMoviePage(int page) {
        page = Math.max(1, page);
        int totalCount = this.adminMapper.selectArticleCountByMovieName();
        PageVo pageVo = new PageVo(page, totalCount);

        AdminMovieDTO[] adminMovieDTO = this.adminMapper.selectArticleByMovieName(
                pageVo.countPerPage,
                pageVo.offsetCount);
        return Pair.of(pageVo, adminMovieDTO);
    }


    public Pair<PageVo, AdminMovieDTO[]> searchMoviePage(int page, String filter, String keyword) {
        page = Math.max(1, page);
        int totalCount = this.adminMapper.searchArticleCountByMovieName(filter, keyword);
        PageVo pageVo = new PageVo(page, totalCount);

        AdminMovieDTO[] adminMovieDTO = this.adminMapper.searchArticleByMovieName(
                pageVo.countPerPage,
                pageVo.offsetCount, filter, keyword);
        return Pair.of(pageVo, adminMovieDTO);
    }


    public Pair<PageVo, Map<String, Map<String, List<ScreenInfoDTO>>>> getGroupedScreens(int requestPage) {
        // 전체 영화관 DTO 데이터 가져오기 (PageVo에서 limitCount와 offsetCount 계산)
        PageVo pageVo = new PageVo(requestPage, getTotalCount()); // 전체 데이터 수를 전달하는 메서드 필요

        // 영화 제목 + 영화관 -> 상영관 -> 상영 번호, 상영일, 상영 이미지
        Map<String, Map<String, List<ScreenInfoDTO>>> groupedData = new LinkedHashMap<>();

        // 매퍼에서 페이지네이션을 고려한 데이터 조회
        AdminTheaterDTO[] theaters = this.adminMapper.selectAllDTOByTheaters(
                pageVo.countPerPage,
                pageVo.offsetCount);

        // 영화관 정보 그룹화
        for (AdminTheaterDTO theater : theaters) {
            String key = theater.getMoTitle() + " - " + theater.getThName(); // 영화 제목 + 영화관
            String ciName = theater.getCiName(); // 상영관 이름

            ScreenInfoDTO screenInfoDTO = new ScreenInfoDTO();
            screenInfoDTO.setScNum(theater.getScNum());
            screenInfoDTO.setCiName(ciName);
            screenInfoDTO.setMImgUrl(theater.getMImgUrl());
            screenInfoDTO.setScStartDate(theater.getScStartDate());

            // 그룹화 처리
            Map<String, List<ScreenInfoDTO>> ciData = groupedData.computeIfAbsent(key, k -> new LinkedHashMap<>());
            ciData.computeIfAbsent(ciName, k -> new ArrayList<>()).add(screenInfoDTO);
        }

        // 페이지네이션 적용된 데이터를 반환
        return Pair.of(pageVo, groupedData);
    }

    private int getTotalCount() {
        return this.adminMapper.selectArticleCountByMovieName();
    }
}
