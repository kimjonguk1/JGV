package dev.jwkim.jgv.services.admin;

import dev.jwkim.jgv.DTO.AdminMovieDTO;
import dev.jwkim.jgv.DTO.AdminTheaterDTO;
import dev.jwkim.jgv.mappers.admin.AdminMapper;
import dev.jwkim.jgv.vos.PageVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

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


    public Pair<PageVo, AdminTheaterDTO[]> selectTheaterPage(int page) {
        page = Math.max(1, page);
        int totalCount = this.adminMapper.selectArticleCountByMovieName();
        PageVo pageVo = new PageVo(page, totalCount);

        AdminTheaterDTO[] adminTheaterDTO = this.adminMapper.selectAllDTOByTheater(
                pageVo.countPerPage,
                pageVo.offsetCount);
        return Pair.of(pageVo, adminTheaterDTO);
    }
}
