package com.jreadability.main;

import java.io.BufferedReader;
import java.io.FileReader;
import junit.framework.Assert;
import org.junit.Test;

public class ArticleTextExtractorTest {

    ArticleTextExtractor extractor = new ArticleTextExtractor();
    Converter c = new Converter();

    @Test
    public void testArticles() throws Exception {
        String text = extractor.getArticleText(readFileAsString("test_data/1.html"));
        Assert.assertEquals("How Fake Money Saved Brazil : Planet Money : NPR", extractor.getTitle());
        Assert.assertTrue(text.startsWith("AFP/Getty Images"));
        Assert.assertTrue(text.endsWith("\"How Four Drinking Buddies Saved Brazil.\""));

        text = extractor.getArticleText(readFileAsString("test_data/2.html"));
        Assert.assertEquals("BenjaminSte.in - Hey guys, whatcha doing?", extractor.getTitle());
        Assert.assertTrue(text.startsWith("This month is the 15th anniversary of my last CD."));
        Assert.assertTrue(text.endsWith("Take it as a compliment :)"));

        text = extractor.getArticleText(readFileAsString("test_data/3.html"));
        Assert.assertTrue(text.startsWith("October 2010"));
        Assert.assertTrue(text.endsWith(" and Jessica Livingston for reading drafts of this."));

        text = extractor.getArticleText(readFileAsString("test_data/5.html"));
        Assert.assertTrue(text.startsWith("Hackers unite in Stanford"));
        Assert.assertTrue(text.endsWith("have beats and bevvies a-plenty. RSVP here.    "));
    }

    @Test
    public void testMySelected() throws Exception {
        // http://www.yomiuri.co.jp/e-japan/gifu/news/20110410-OYT8T00124.htm
        String text = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_ch.html")));
        Assert.assertEquals("色とりどりのチューリップ : 岐阜 : 地域 : YOMIURI ONLINE（読売新聞）", extractor.getTitle());
        Assert.assertTrue(text.startsWith("色とりどりのチューリップ"));

        text = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test4.html")));
        Assert.assertTrue(text.startsWith("About 15,000 people took to the streets in Tokyo on Sunday to protest"));

        text = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test.html")));
        Assert.assertTrue(text.startsWith("Im Gespräch: Umweltaktivist Stewart Brand"));
        
        // http://en.rian.ru/world/20110410/163458489.html
        text = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_rian.html")));
        Assert.assertEquals("Japanese rally against nuclear power industry | World | RIA Novosti", extractor.getTitle());        
        Assert.assertTrue(text.startsWith("About 15,000 people took to the streets in Tokyo on Sunday to protest against th"));        
    }

    @Test
    public void testArticlesWithWrangledBeginning() throws Exception {
        String text = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_spiegel.html")));
        // TODO remove beginning
        Assert.assertTrue(text.startsWith("Drucken Senden Feedback 05.04.2011   Retro-PC Commodore reaktiviert"));

        // jReadability / github
        text = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_github.html")));
        Assert.assertTrue(text.startsWith("ifesdjeen / jReadability Admin Watch Unwatch Fork Where do you want to for"));

        text = extractor.getArticleText(readFileAsString("test_data/4.html"));
        // TODO remove beginning
        Assert.assertTrue(text.startsWith("Photo used under Creative Commons from theparadigmshifter So you have a new startup company and want some coverage"));
        Assert.assertTrue(text.endsWith("Know of any other good ones? Please add in the comments."));

        // uh, facebook has really ugly javascript stuff ... hard to parse!?
        // http://www.facebook.com/democracynow 
        text = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_facebook.html")));
        Assert.assertEquals("http://profile.ak.fbcdn.net/hprofile-ak-snc4/50514_17414523278_4468493_n.jpg", extractor.getImageSource());
        Assert.assertTrue(text.startsWith("Welcome to the official Facebook Page about Democracy Now! Join Facebook to start connecting with Democracy Now!"));

        // http://itunes.apple.com/us/album/songs-for-japan/id428401715
        text = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_itunes.html")));
//        Assert.assertTrue(text.startsWith("Songs for Japan"));

        // http://twitpic.com/4k1ku3
        text = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_twitpic.html")));
        // TODO remove beginning
        Assert.assertTrue(text.startsWith("Rotate photo    View full size name twitter username @reply to tagged user Hide map and return to photo It’s hard to be a dinosaur."));

        // http://engineering.twitter.com/2011/04/twitter-search-is-now-3x-faster_1656.html
        text = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_twitter.html")));
        Assert.assertEquals("Twitter Engineering: Twitter Search is Now 3x Faster", extractor.getTitle());
        // TODO find <div class='post-body entry-content'>
        System.out.println("TEXT5:" + text);        
        Assert.assertEquals("http://4.bp.blogspot.com/-CmXJmr9UAbA/TZy6AsT72fI/AAAAAAAAAAs/aaF5AEzC-e4/s72-c/Blender_Tsunami.jpg", extractor.getImageSource());
//        Assert.assertTrue(text.startsWith("Twitter Search is Now 3x Faster In the spring of 2010, the search team at Twitter started to "));               
    }

    @Test
    public void testContentWithImage() throws Exception {
        // http://dealbook.nytimes.com/2011/04/11/for-defense-in-galleon-trial-no-time-to-rest/
        String text = extractor.getArticleText(c.streamToString(getClass().getResourceAsStream("test_nyt.html")));
        Assert.assertEquals("http://graphics8.nytimes.com/images/2011/04/12/business/dbpix-raj-rajaratnam-1302571800091/dbpix-raj-rajaratnam-1302571800091-tmagSF.jpg",
                extractor.getImageSource());
        Assert.assertTrue(text.startsWith("Brendan Mcdermid/ReutersRaj Rajaratnam, right, and his lawyer, John Dowd, leaving court on Monday. I wouldn’t want to be Raj Rajaratnam’s lawyer right now."));
    }

    /** @param filePath the name of the file to open. Not sure if it can accept URLs or just filenames. Path handling could 
     *   be better, and buffer sizes are hardcoded
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
