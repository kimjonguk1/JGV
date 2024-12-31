package dev.jwkim.jgv.vos;

public class PageVo
{
    public final int countPerPage = 6;
    public final int requestPage;
    public final int totalCount;
    public final int displayMinPage;
    public final int displayMaxPage;
    public final int movableMinPage = 1;
    public final int movableMaxPage;
    public final int offsetCount;

    public PageVo(int requestPage, int totalCount) {
        this.totalCount = totalCount;

        // 최대 페이지 계산
        this.movableMaxPage = totalCount / this.countPerPage + (totalCount % this.countPerPage == 0 ? 0 : 1);

        // 유효하지 않은 요청 페이지 조정
        if (requestPage < movableMinPage) {
            requestPage = movableMinPage;
        } else if (requestPage > movableMaxPage) {
            requestPage = movableMaxPage;
        }
        this.requestPage = requestPage;

        // 화면에 표시될 페이지 범위 계산
        if (totalCount == 0) {
            this.displayMinPage = 1;
            this.displayMaxPage = 1;
            this.offsetCount = 0;
        } else {
            this.displayMinPage = ((this.requestPage - 1) / 10) * 10 + 1;
            this.displayMaxPage = Math.min(this.displayMinPage + 9, this.movableMaxPage);
            this.offsetCount = (this.requestPage - 1) * this.countPerPage;
        }

    }

}
