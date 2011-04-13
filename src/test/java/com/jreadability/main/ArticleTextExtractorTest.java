package com.jreadability.main;

import java.io.BufferedReader;
import java.io.FileReader;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArticleTextExtractorTest {

    ArticleTextExtractor extractor;
    Converter c;

    @Before
    public void setup() {
        c = new Converter();
        extractor = new ArticleTextExtractor();
    }

    @Test
    public void testArticles() throws Exception {
        JResult res = extractor.getArticleText(readFileAsString("test_data/1.html"));
        assertEquals("How Fake Money Saved Brazil : Planet Money : NPR", res.getTitle());
        assertTrue(res.getText().startsWith("AFP/Getty Images"));
        assertTrue(res.getText().endsWith("\"How Four Drinking Buddies Saved Brazil.\""));

        res = extractor.getArticleText(readFileAsString("test_data/2.html"));
        assertEquals("BenjaminSte.in - Hey guys, whatcha doing?", res.getTitle());
        assertTrue(res.getText().startsWith("This month is the 15th anniversary of my last CD."));
        assertTrue(res.getText().endsWith("Take it as a compliment :)"));

        res = extractor.getArticleText(readFileAsString("test_data/3.html"));
        assertTrue(res.getText().startsWith("October 2010"));
        assertTrue(res.getText().endsWith(" and Jessica Livingston for reading drafts of this."));

        res = extractor.getArticleText(readFileAsString("test_data/5.html"));
        assertTrue(res.getText().startsWith("Hackers unite in Stanford"));
        assertTrue(res.getText().endsWith("have beats and bevvies a-plenty. RSVP here.    "));
    }

    @Test
    public void testMySelected() throws Exception {
        // http://www.yomiuri.co.jp/e-japan/gifu/news/20110410-OYT8T00124.htm
        JResult res = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_ch.html")));
        assertEquals("色とりどりのチューリップ : 岐阜 : 地域 : YOMIURI ONLINE（読売新聞）", res.getTitle());
        assertTrue(res.getText().startsWith("色とりどりのチューリップ"));

        res = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test4.html")));
        assertTrue(res.getText().startsWith("About 15,000 people took to the streets in Tokyo on Sunday to protest"));

        res = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test.html")));
        assertTrue(res.getText().startsWith("Im Gespräch: Umweltaktivist Stewart Brand"));

        // http://en.rian.ru/world/20110410/163458489.html
        res = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_rian.html")));
        assertEquals("Japanese rally against nuclear power industry | World | RIA Novosti", res.getTitle());
        assertTrue(res.getText().startsWith("About 15,000 people took to the streets in Tokyo on Sunday to protest against th"));
    }

    @Test
    public void testArticlesWithWrangledBeginning() throws Exception {
        JResult res = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_spiegel.html")));
        // TODO remove beginning
        assertTrue(res.getText().startsWith("Drucken Senden Feedback 05.04.2011   Retro-PC Commodore reaktiviert"));

        // jReadability / github
        res = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_github.html")));
        assertTrue(res.getText().startsWith("ifesdjeen / jReadability Admin Watch Unwatch Fork Where do you want to for"));

        res = extractor.getArticleText(readFileAsString("test_data/4.html"));
        // TODO remove beginning
        assertTrue(res.getText().startsWith("Photo used under Creative Commons from theparadigmshifter So you have a new startup company and want some coverage"));
        assertTrue(res.getText().endsWith("Know of any other good ones? Please add in the comments."));

        // http://itunes.apple.com/us/album/songs-for-japan/id428401715
        res = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_itunes.html")));
//        assertTrue(text.startsWith("Songs for Japan"));

        // http://twitpic.com/4k1ku3
        res = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_twitpic.html")));
        // TODO remove beginning
        assertTrue(res.getText().startsWith("Rotate photo    View full size name twitter username @reply to tagged user Hide map and return to photo It’s hard to be a dinosaur."));

        // http://engineering.twitter.com/2011/04/twitter-search-is-now-3x-faster_1656.html
        res = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_twitter.html")));
        assertEquals("Twitter Engineering: Twitter Search is Now 3x Faster", res.getTitle());
        assertEquals("http://4.bp.blogspot.com/-CmXJmr9UAbA/TZy6AsT72fI/AAAAAAAAAAs/aaF5AEzC-e4/s72-c/Blender_Tsunami.jpg", res.getImageUrl());

        // TODO find <div class='post-body entry-content'>
        System.out.println("TEXT5:" + res.getText());
//        assertTrue(text.startsWith("Twitter Search is Now 3x Faster In the spring of 2010, the search team at Twitter started to "));               
    }

    @Test
    public void testFallbackToDescription() throws Exception {
        // uh, facebook has really ugly javascript stuff ... hard to parse!? but we have fallbacl to description
        // http://www.facebook.com/democracynow 
        JResult res = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_facebook.html")));
        assertEquals("http://profile.ak.fbcdn.net/hprofile-ak-snc4/50514_17414523278_4468493_n.jpg", res.getImageUrl());
        assertTrue(res.getText().startsWith("Welcome to the official Facebook Page about Democracy Now! Join Facebook to start connecting with Democracy Now!"));
    }

    @Test
    public void testContentWithImage() throws Exception {
        // http://dealbook.nytimes.com/2011/04/11/for-defense-in-galleon-trial-no-time-to-rest/
        JResult res = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_nyt.html")));
        assertEquals("http://graphics8.nytimes.com/images/2011/04/12/business/dbpix-raj-rajaratnam-1302571800091/dbpix-raj-rajaratnam-1302571800091-tmagSF.jpg",
                res.getImageUrl());
//        System.out.println("TEXT6:" + res.getText());
        assertTrue(res.getText().startsWith("Brendan Mcdermid/ReutersRaj Rajaratnam, right, and his lawyer, John Dowd, leaving court on Monday. I wouldn’t want to be Raj Rajaratnam’s lawyer right now."));
    }

    /**
     * @param filePath the name of the file to open. Not sure if it can accept URLs 
     * or just filenames. Path handling could be better, and buffer sizes are hardcoded
     */
    public static String readFileAsString(String filePath)
            throws java.io.IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
}
