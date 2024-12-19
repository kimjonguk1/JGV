package dev.jwkim.jgv.vos;

public class PageVo {
    public final int countPerPage = 6; // 한 페이지에 표시할 게시글의 개수
    public final int requestPage; // 클라이언트가 요청한 페이지 번호
    public final int totalCount; // 전체 게시글의 개수
    public final int displayMinPage; // 표시할 최소 페이지 번호
    public final int displayMaxPage; // 표시할 최대 페이지 번호
    public final int movableMinPage = 1; // 이동 가능한 최소 페이지 번호(항상 1)
    public final int movableMaxPage;// 이동 가능한 최대 페이지 번호
    public final int offsetCount; // 거르는 레코드 개수

    // 위 상수를 계산하는데 필요한 변수는 requestPage와 totalCount 두 개 임으로
    public PageVo(int requestPage, int totalCount) {
        this.requestPage = requestPage;
        this.totalCount = totalCount;
        this.movableMaxPage = totalCount / this.countPerPage + (totalCount % this.countPerPage == 0 ? 0 : 1);
        this.displayMinPage = ((requestPage - 1) / 10) * 10 + 1;
        this.displayMaxPage = Math.min(this.displayMinPage + 9, this.movableMaxPage);
        this.offsetCount = (requestPage - 1) * this.countPerPage;
    }
}
