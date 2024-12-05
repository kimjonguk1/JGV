package dev.jwkim.jgv;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class JgvApplicationTests {

    @Test
    void contextLoads() throws IOException {
        Document CharacterDoc = Jsoup.connect("http://www.cgv.co.kr/movies/detail-view/cast.aspx?midx=88847#menu").get();
        Elements DirectorLinks = CharacterDoc.select("div.sect-staff-director > a");
        Elements CharacterLinks = CharacterDoc.select("div.sect-staff-actor > a");
        for (Element DirectorLink : DirectorLinks) {
            try {
                String DirectorHref = DirectorLink.attr("href");
                DirectorHref = "http://www.cgv.co.kr" + DirectorHref;
                Document DirectorDocs = Jsoup.connect(DirectorHref).get();
                String DirectorName = DirectorDocs.select("div.box-contents > div.title > strong").text();
                System.out.println(DirectorName);
            } catch (Exception e) {
                System.out.println("페이지가 존재하지 않습니다.");
            }
        }
    }

}
