package dev.jwkim.jgv.results.movie;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchResult<T> {
    private SearchStatus status;
    private T data;

    public SearchResult(SearchStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    // 성공 응답 생성 메서드
    public static <T> SearchResult<T> success(T data) {
        return new SearchResult<>(SearchStatus.SUCCESS, data);
    }

    // 실패 응답 생성 메서드
    public static <T> SearchResult<T> failure(T data) {
        return new SearchResult<>(SearchStatus.FAILURE, data);
    }
}
