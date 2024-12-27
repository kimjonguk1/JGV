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
        this.requestPage = requestPage;

        this.totalCount = totalCount;
        this.movableMaxPage = totalCount / this.countPerPage + (totalCount % this.countPerPage == 0 ? 0 : 1);

        if (totalCount == 0) {
            this.displayMinPage = 1;
            this.displayMaxPage = 1;
        }
        else {
            this.displayMinPage = ((
                    requestPage - 1) / 10) * 10 + 1;
            this.displayMaxPage = Math.min(this.displayMinPage + 9, this.movableMaxPage);
        }
        this.offsetCount = (requestPage - 1) * this.countPerPage;

    }
}
