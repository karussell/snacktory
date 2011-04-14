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
        JResult res = extractor.extractContent(readFileAsString("test_data/1.html"));
        assertEquals("How Fake Money Saved Brazil : Planet Money : NPR", res.getTitle());
        assertTrue(res.getText().startsWith("AFP/Getty Images"));
        assertTrue(res.getText().endsWith("\"How Four Drinking Buddies Saved Brazil.\""));

        res = extractor.extractContent(readFileAsString("test_data/2.html"));
        assertEquals("BenjaminSte.in - Hey guys, whatcha doing?", res.getTitle());
        assertTrue(res.getText().startsWith("This month is the 15th anniversary of my last CD."));
        assertTrue(res.getText().endsWith("Take it as a compliment :)"));

        // TODO remove start
        res = extractor.extractContent(readFileAsString("test_data/3.html"));
//        System.out.println("3:" + res.getText());
        assertTrue(res.getText().startsWith("October 2010"));
        assertTrue(res.getText().endsWith(" and Jessica Livingston for reading drafts of this."));
    }

    @Test
    public void testWordpress() throws Exception {
        // http://karussell.wordpress.com/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wordpress.html")));
//        System.out.println("wordpress:" + res.getText());
        assertEquals("Twitter API and Me « Find Time for the Karussell", res.getTitle());
        assertTrue(res.getText().startsWith("I have a love hate relationship with Twitter. As a user I see "));
    }

    @Test
    public void testFirefox() throws Exception {
        // http://www.golem.de/1104/82797.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("golem.html")));
        System.out.println("firefox:" + res.getText());
        assertEquals("http://www.golem.de/1104/82797-9183-i.png", res.getImageUrl());
        assertEquals("Mozilla: Vorabversionen von Firefox 5 und 6 veröffentlicht - Golem.de", res.getTitle());
        // TODO
//        assertTrue(res.getText().startsWith("Unter dem Namen \"Aurora\" hat Firefox einen"));
    }

    @Test
    public void testMySelected() throws Exception {
        // http://www.yomiuri.co.jp/e-japan/gifu/news/20110410-OYT8T00124.htm
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("yomiuri.html")));
        assertEquals("色とりどりのチューリップ : 岐阜 : 地域 : YOMIURI ONLINE（読売新聞）", res.getTitle());
        assertTrue(res.getText().startsWith("色とりどりのチューリップ"));

        // http://www.faz.net/s/Rub469C43057F8C437CACC2DE9ED41B7950/Doc~EBA775DE7201E46E0B0C5AD9619BD56E9~ATpl~Ecommon~Scontent.html
        res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test.html")));
        assertTrue(res.getText().startsWith("Im Gespräch: Umweltaktivist Stewart Brand"));

        // http://en.rian.ru/world/20110410/163458489.html
        res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_rian.html")));
        assertEquals("Japanese rally against nuclear power industry | World | RIA Novosti", res.getTitle());
//        System.out.println("4:" + res.getText());
        assertTrue(res.getText().startsWith("About 15,000 people took to the streets in Tokyo on Sunday to protest against th"));
    }

    @Test
    public void testJetwickFallbackToDescription() throws Exception {
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_jetwick.html")));
        assertEquals("", res.getImageUrl());
        assertTrue(res.getText().startsWith("Search twitter without noise - Jetwick!"));
    }

    @Test
    public void testVideo() throws Exception {
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_vimeo.html")));
        assertEquals("finn. & Dirk von Lowtzow \"CRYING IN THE RAIN\" on Vimeo", res.getTitle());
        assertEquals("http://b.vimeocdn.com/ts/134/104/134104048_200.jpg", res.getImageUrl());
//        assertEquals("http://www.youtube.com/v/wlupmjrfaB4?version=3", res.getVideoUrl());
        assertEquals("", res.getVideoUrl());

        res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_youtube.html")));
        assertEquals("YouTube - Metallica - Master of the Puppets 8-bit", res.getTitle());
//        assertEquals("http://b.vimeocdn.com/ts/134/104/134104048_200.jpg", res.getImageUrl());
        assertEquals("http://i4.ytimg.com/vi/wlupmjrfaB4/default.jpg", res.getImageUrl());
        assertEquals("http://www.youtube.com/v/wlupmjrfaB4?version=3", res.getVideoUrl());
    }

    @Test
    public void testArticlesWithWrangledBeginning() throws Exception {
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_spiegel.html")));
        // TODO remove beginning
        assertTrue(res.getText().startsWith("Drucken Senden Feedback 05.04.2011   Retro-PC Commodore reaktiviert"));

        // jReadability / github
        res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_github.html")));
//        System.out.println("github:" + res.getText());
        assertTrue(res.getText().startsWith("= jReadability This is a small helper utility (only 130 lines of code) for pepole"));
//        assertTrue(res.getText().startsWith("ifesdjeen / jReadability Admin Watch Unwatch Fork Where do you want to for"));

        res = extractor.extractContent(readFileAsString("test_data/4.html"));
        // TODO remove beginning
        assertTrue(res.getText().startsWith("Photo used under Creative Commons from theparadigmshifter So you have a new startup company and want some coverage"));
        assertTrue(res.getText().endsWith("Know of any other good ones? Please add in the comments."));

        // http://itunes.apple.com/us/album/songs-for-japan/id428401715
        res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_itunes.html")));
//        assertTrue(text.startsWith("Songs for Japan"));

        res = extractor.extractContent(readFileAsString("test_data/5.html"));
//        System.out.println("5:" + res.getText());
        assertTrue(res.getText().startsWith("Edit Delete Autopost   Airbnb & Y Combinator Present A Party of Epic Proportions! "
                + "Hackers unite in Stanford"));
        assertTrue(res.getText().endsWith("have beats and bevvies a-plenty. RSVP here.    "));
    }

    @Test
    public void testEmptyText() throws Exception {
        // http://twitpic.com/4k1ku3
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_twitpic.html")));
        assertEquals("It’s hard to be a dinosaur. on Twitpic", res.getTitle());
        assertEquals("", res.getText());
    }

    @Test
    public void testHeise() throws Exception {
        // http://www.heise.de/newsticker/meldung/Internet-Explorer-9-jetzt-mit-schnellster-JavaScript-Engine-1138062.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_heise.html")));
        assertEquals("", res.getImageUrl());
        assertEquals("heise online - Internet Explorer 9 jetzt mit schnellster JavaScript-Engine", res.getTitle());
        assertTrue(res.getText().startsWith("Microsoft hat heute eine siebte Platform Preview des Internet Explorer veröffentlicht. In den nur dr"));
    }

    @Test
    public void testTechcrunch() throws Exception {
        // http://techcrunch.com/2011/04/04/twitter-advanced-search/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("techcrunch.html")));
//        System.out.println("techcrunch:" + res.getTitle());        
        assertEquals("http://tctechcrunch.files.wordpress.com/2011/04/screen-shot-2011-04-04-at-12-11-36-pm.png?w=285&h=85", res.getImageUrl());
        assertEquals("Twitter Finally Brings Advanced Search Out Of Purgatory; Updates Discovery Algorithms", res.getTitle());
        assertTrue(res.getText().startsWith("A couple weeks ago, we wrote a post wishing Twitter a happy fifth birthday, but also noting "));
    }

    @Test
    public void testEngadget() throws Exception {
        // http://www.engadget.com/2011/04/09/editorial-androids-problem-isnt-fragmentation-its-contamina/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("engadget.html")));
//        System.out.println("engadget:" + res.getText());
//        System.out.println("engadget:" + res.getImageUrl());
        assertEquals("http://www.blogcdn.com/www.engadget.com/media/2011/04/11x0409mnbvhg.jpg", res.getImageUrl());
        assertEquals("Editorial: Android's problem isn't fragmentation, it's contamination -- Engadget", res.getTitle());
        assertTrue(res.getText().startsWith("This thought was first given voice by Myriam Joire on last night's Mobile Podcast, and the"));
    }

    @Test
    public void testTwitterBlog() throws Exception {
        // http://engineering.twitter.com/2011/04/twitter-search-is-now-3x-faster_1656.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_twitter.html")));
        assertEquals("Twitter Engineering: Twitter Search is Now 3x Faster", res.getTitle());
        assertEquals("http://4.bp.blogspot.com/-CmXJmr9UAbA/TZy6AsT72fI/AAAAAAAAAAs/aaF5AEzC-e4/s400/Blender_Tsunami.jpg", res.getImageUrl());
        assertTrue(res.getText().startsWith("In the spring of 2010, the search team at Twitter started to rewrite our search engine in order to serve our ever-growin"));
    }

    @Test
    public void testFallbackToDescription() throws Exception {
        // uh, facebook has really ugly javascript stuff ... hard to parse!? but we have fallbacl to description
        // http://www.facebook.com/democracynow 
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_facebook.html")));
        assertEquals("http://profile.ak.fbcdn.net/hprofile-ak-snc4/50514_17414523278_4468493_n.jpg", res.getImageUrl());
        assertTrue(res.getText().startsWith("Welcome to the official Facebook Page about Democracy Now! Join Facebook to start connecting with Democracy Now!"));
    }

    @Test
    public void testContentWithImage() throws Exception {
        // http://dealbook.nytimes.com/2011/04/11/for-defense-in-galleon-trial-no-time-to-rest/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("test_nyt.html")));
        assertEquals("http://graphics8.nytimes.com/images/2011/04/12/business/dbpix-raj-rajaratnam-1302571800091/dbpix-raj-rajaratnam-1302571800091-tmagSF.jpg",
                res.getImageUrl());
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
