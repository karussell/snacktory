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
    public void setup() throws Exception {
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

        res = extractor.extractContent(readFileAsString("test_data/3.html"));
//        System.out.println("3:" + res.getText());
        assertTrue(res.getText().startsWith("October 2010Silicon Valley proper is mostly suburban sprawl. At first glance it "));
        assertTrue(res.getText().endsWith(" and Jessica Livingston for reading drafts of this."));
    }

    @Test
    public void testData4() throws Exception {
        // http://blog.traindom.com/places-where-to-submit-your-startup-for-coverage/
        JResult res = extractor.extractContent(readFileAsString("test_data/4.html"));
        // TODO remove beginning        
        assertEquals("36 places where you can submit your startup for some coverage | Traindom Blog", res.getTitle());
        assertTrue("data4:" + res.getText(), res.getText().startsWith("Photo used under Creative Commons from theparadigmshifter So you have a new startup company and want some coverage"));
        assertTrue(res.getText().endsWith("Know of any other good ones? Please add in the comments."));
    }

    @Test
    public void testData5() throws Exception {
        JResult res = extractor.extractContent(readFileAsString("test_data/5.html"));
        assertTrue("data5:" + res.getText(), res.getText().startsWith("Hackers unite in Stanford"));
        assertTrue(res.getText().endsWith("have beats and bevvies a-plenty. RSVP here.    "));
    }

    @Test
    public void testCNN() throws Exception {
        // http://edition.cnn.com/2011/WORLD/africa/04/06/libya.war/index.html?on.cnn=1
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnn.html")));
        assertEquals("Gadhafi asks Obama to end NATO bombing - CNN.com", res.getTitle());
        assertEquals("/2011/WORLD/africa/04/06/libya.war/t1larg.libyarebel.gi.jpg", res.getImageUrl());
        assertTrue("cnn:" + res.getText(), res.getText().startsWith("Gadhafi's letter to Obama STORY HIGHLIGHTS British airstrike hits oil field, Libyan official says Clinton says Gadhafi must step down for bombing to stop Gadhafi asks President Obama to stop NATO's bombing Ex-U.S. Rep. Weldon to urge Gadhafi to step down, calls for cease-fire Tripoli, Libya (CNN) -"));
    }

    @Test
    public void testWordpress() throws Exception {
        // http://karussell.wordpress.com/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wordpress.html")));
//        System.out.println("wordpress:" + res.getText());
        assertEquals("Twitter API and Me « Find Time for the Karussell", res.getTitle());
        assertTrue("wordpress:" + res.getText(), res.getText().startsWith("I have a love hate relationship with Twitter. As a user I see "));
    }

    @Test
    public void testFirefox() throws Exception {
        // http://www.golem.de/1104/82797.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("golem.html")));
        System.out.println("firefox:" + res.getText());
        assertEquals("http://www.golem.de/1104/82797-9183-i.png", res.getImageUrl());
        assertEquals("Mozilla: Vorabversionen von Firefox 5 und 6 veröffentlicht - Golem.de", res.getTitle());
        assertTrue(res.getText().startsWith("Unter dem Namen \"Aurora\" hat Firefox einen"));
    }

    @Test
    public void testYomiuri() throws Exception {
        // http://www.yomiuri.co.jp/e-japan/gifu/news/20110410-OYT8T00124.htm
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("yomiuri.html")));
        assertEquals("色とりどりのチューリップ : 岐阜 : 地域 : YOMIURI ONLINE（読売新聞）", res.getTitle());
        assertTrue(res.getText().startsWith("色とりどりのチューリップ"));
    }

    @Test
    public void testFAZ() throws Exception {
        // http://www.faz.net/s/Rub469C43057F8C437CACC2DE9ED41B7950/Doc~EBA775DE7201E46E0B0C5AD9619BD56E9~ATpl~Ecommon~Scontent.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("faz.html")));
        assertEquals("", res.getImageUrl());
        assertTrue(res.getText().startsWith("Im Gespräch: Umweltaktivist Stewart Brand"));
    }

    @Test
    public void testRian() throws Exception {
        // http://en.rian.ru/world/20110410/163458489.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("rian.html")));
        assertEquals("Japanese rally against nuclear power industry | World | RIA Novosti", res.getTitle());
//        System.out.println("4:" + res.getText());
        assertTrue(res.getText().startsWith("About 15,000 people took to the streets in Tokyo on Sunday to protest against th"));
    }

    @Test
    public void testJetwickFallbackToDescription() throws Exception {
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("jetwick.html")));
        assertTrue(res.getText(), res.getText().startsWith("Search twitter without noise"));
        assertEquals("", res.getImageUrl());
    }

    @Test
    public void testVimeo() throws Exception {
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("vimeo.html")));
//        System.out.println("vimeo:" + res.getText());
        assertTrue(res.getText(), res.getText().startsWith("/ directed by Johannes Assig & finn. "));
        assertTrue(res.getTitle(), res.getTitle().startsWith("finn. & Dirk von Lowtzow \"CRYING IN THE RAIN\""));
//        assertEquals("http://b.vimeocdn.com/ts/134/104/134104048_200.jpg", res.getImageUrl());
        assertEquals("", res.getVideoUrl());
    }

    @Test
    public void testYoutube() throws Exception {
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("youtube.html")));
//        System.out.println("youtube:" + res.getText());
        assertTrue(res.getText(), res.getText().startsWith("Master of the Puppets by Metallica. Converted to 8 bit with GSXCC. Original verson can be found using limewire"));
        assertEquals("YouTube - Metallica - Master of the Puppets 8-bit", res.getTitle());
        assertEquals("http://i4.ytimg.com/vi/wlupmjrfaB4/default.jpg", res.getImageUrl());
        assertEquals("http://www.youtube.com/v/wlupmjrfaB4?version=3", res.getVideoUrl());
    }

    @Test
    public void testSpiegel() throws Exception {
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("spiegel.html")));
        // TODO remove beginning
        assertTrue(res.getText().startsWith("Drucken Senden Feedback 05.04.2011   Retro-PC Commodore reaktiviert"));
    }

    @Test
    public void testGithub() throws Exception {
        // jReadability / github
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("github.html")));
        System.out.println("github:" + res.getText());
//        assertTrue(res.getText().startsWith("= jReadability This is a small helper utility (only 130 lines of code) for pepole"));
//        assertTrue(res.getText().startsWith("ifesdjeen / jReadability Admin Watch Unwatch Fork Where do you want to for"));
    }

    @Test
    public void testITunes() throws Exception {
        // http://itunes.apple.com/us/album/songs-for-japan/id428401715
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("itunes.html")));
        assertTrue("itunes:" + res.getText(), res.getText().startsWith("Preview and download songs from Songs for Japan by Various Artists on iTunes."));
    }

    @Test
    public void testTwitpic() throws Exception {
        // http://twitpic.com/4k1ku3
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("twitpic.html")));
        assertEquals("It’s hard to be a dinosaur. on Twitpic", res.getTitle());
//        assertEquals("", res.getText());
        assertTrue(res.getText(), res.getText().isEmpty());
    }

    @Test
    public void testHeise() throws Exception {
        // http://www.heise.de/newsticker/meldung/Internet-Explorer-9-jetzt-mit-schnellster-JavaScript-Engine-1138062.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("heise.html")));
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
    public void testTwitterblog() throws Exception {
        // http://engineering.twitter.com/2011/04/twitter-search-is-now-3x-faster_1656.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("twitter.html")));
        assertEquals("Twitter Engineering: Twitter Search is Now 3x Faster", res.getTitle());
//        assertEquals("http://4.bp.blogspot.com/-CmXJmr9UAbA/TZy6AsT72fI/AAAAAAAAAAs/aaF5AEzC-e4/s72-c/Blender_Tsunami.jpg", res.getImageUrl());
        assertEquals("http://4.bp.blogspot.com/-CmXJmr9UAbA/TZy6AsT72fI/AAAAAAAAAAs/aaF5AEzC-e4/s400/Blender_Tsunami.jpg", res.getImageUrl());
        assertTrue("twitter:" + res.getText(), res.getText().startsWith("In the spring of 2010, the search team at Twitter started to rewrite our search engine in order to serve our ever-growin"));
    }

    @Test
    public void testTazBlog() throws Exception {
        // http://www.taz.de/1/politik/asien/artikel/1/anti-atomkraft-nein-danke/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("taz.html")));
        assertEquals("", res.getTitle());
        assertEquals("", res.getImageUrl());
        assertTrue("taz:" + res.getText(), res.getText().startsWith("In the spring of 2010, the search team at Twitter started to rewrite our search engine in order to serve our ever-growin"));
    }

    @Test
    public void testFaceBookFallbackToDescription() throws Exception {
        // uh, facebook has really ugly javascript stuff ... hard to parse!? but we have fallbacl to description
        // http://www.facebook.com/democracynow 
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("facebook.html")));
        assertEquals("http://profile.ak.fbcdn.net/hprofile-ak-snc4/50514_17414523278_4468493_n.jpg", res.getImageUrl());
        assertTrue(res.getText().startsWith("Welcome to the official Facebook Page about Democracy Now! Join Facebook to start connecting with Democracy Now!"));
    }

    @Test
    public void testNyt() throws Exception {
        // http://dealbook.nytimes.com/2011/04/11/for-defense-in-galleon-trial-no-time-to-rest/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("nyt.html")));
        assertEquals("http://graphics8.nytimes.com/images/2011/04/12/business/dbpix-raj-rajaratnam-1302571800091/dbpix-raj-rajaratnam-1302571800091-tmagSF.jpg",
                res.getImageUrl());
        assertTrue(res.getText().startsWith("Brendan Mcdermid/ReutersRaj Rajaratnam, right, and his lawyer, John Dowd, leaving court on Monday. I wouldn’t want to be Raj Rajaratnam’s lawyer right now."));
    }

    @Test
    public void testHuffingtonpost() throws Exception {
        // "http://www.huffingtonpost.com/2010/08/13/federal-reserve-pursuing_n_681540.html";        
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("huffingtonpost.html")));
        assertEquals("Federal Reserve's Low Rate Policy Is A 'Dangerous Gamble,' Says Top Central Bank Official", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("A top regional Federal Reserve official sharply"));
        assertEquals("", res.getImageUrl());
    }

    @Test
    public void testTechcrunch2() throws Exception {
        //String url = "http://techcrunch.com/2010/08/13/gantto-takes-on-microsoft-project-with-web-based-project-management-application/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("techcrunch2.html")));
        assertEquals("Gantto Takes On Microsoft Project With Web-Based Project Management Application", article.getTitle());
        assertTrue(article.getText(), article.getText().startsWith("Y Combinator-backed Gantto is launching"));
        assertEquals(article.getImageUrl(), "http://tctechcrunch.files.wordpress.com/2010/08/tour.jpg");

    }

    @Test
    public void testCnn2() throws Exception {
        //String url = "http://www.cnn.com/2010/POLITICS/08/13/democrats.social.security/index.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnn2.html")));
        assertEquals("Democrats to use Social Security against GOP this fall", article.getTitle());
        assertTrue(article.getText(), article.getText().startsWith("Washington (CNN) -- Democrats pledged "));
        assertEquals(article.getImageUrl(), "http://i.cdn.turner.com/cnn/2010/POLITICS/08/13/democrats.social.security/story.kaine.gi.jpg");
    }

    @Test
    public void testBusinessweek() throws Exception {
        // String url = "http://www.businessweek.com/magazine/content/10_34/b4192066630779.htm";

        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("businessweek.html")));
        assertEquals("Olivia Munn: Queen of the Uncool", article.getTitle());
        assertTrue(article.getText(), article.getText().startsWith("Six years ago, Olivia Munn arrived in Hollywood with fading ambitions of making it as a sports reporter and set about deploying"));
        assertEquals(article.getImageUrl(), "http://images.businessweek.com/mz/10/34/370/1034_mz_66popmunnessa.jpg");
    }

    @Test
    public void testBusinessweek2() throws Exception {
        //String url = "http://www.businessweek.com/magazine/content/10_34/b4192048613870.htm";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("businessweek2.html")));
        assertTrue(article.getText(), article.getText().startsWith("There's discord on Wall Street: Strategists at major American investment banks see a"));
        assertEquals(article.getImageUrl(), "http://images.businessweek.com/mz/covers/current_120x160.jpg");
    }

    @Test
    public void testFoxnews() throws Exception {
        //String url = "http://www.foxnews.com/politics/2010/08/14/russias-nuclear-help-iran-stirs-questions-improved-relations/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("foxnews.html")));
        assertTrue(article.getText(), article.getText().startsWith("Russia's announcement that it will help Iran get nuclear fuel is raising questions"));
        assertEquals(article.getImageUrl(), "http://a57.foxnews.com/static/managed/img/Politics/397/224/startsign.jpg");
    }

    @Test
    public void testAolnews() throws Exception {
        //String url = "http://www.aolnews.com/nation/article/the-few-the-proud-the-marines-getting-a-makeover/19592478";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("aolnews.html")));
        assertTrue(article.getText(), article.getText().startsWith("WASHINGTON (Aug. 13) -- Declaring &quot;the maritime soul of the Marine Corps"));
        assertEquals(article.getImageUrl(), "http://o.aolcdn.com/photo-hub/news_gallery/6/8/680919/1281734929876.JPEG");
    }

    @Test
    public void testWallstreetjournal() throws Exception {
        //String url = "http://online.wsj.com/article/SB10001424052748704532204575397061414483040.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wsj.html")));
        assertTrue(article.getText(), article.getText().startsWith("The Obama administration has paid out less than a third of the nearly $230 billion"));
        assertEquals(article.getImageUrl(), "http://si.wsj.net/public/resources/images/OB-JO747_stimul_G_20100814113803.jpg");
    }

    @Test
    public void testUsatoday() throws Exception {
        //String url = "http://content.usatoday.com/communities/thehuddle/post/2010/08/brett-favre-practices-set-to-speak-about-return-to-minnesota-vikings/1";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("usatoday.html")));
        assertTrue(article.getText(), article.getText().startsWith("Brett Favre couldn't get away from the"));
        assertEquals(article.getImageUrl(), "http://i.usatoday.net/communitymanager/_photos/the-huddle/2010/08/18/favrespeaksx-inset-community.jpg");
    }

    @Test
    public void testUsatoday2() throws Exception {
        //String url = "http://content.usatoday.com/communities/driveon/post/2010/08/gm-finally-files-for-ipo/1";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("usatoday2.html")));
        assertTrue(article.getText(), article.getText().startsWith("General Motors just filed with the Securities and Exchange "));
        assertEquals(article.getImageUrl(), "http://i.usatoday.net/communitymanager/_photos/drive-on/2010/08/18/cruzex-wide-community.jpg");
    }

    @Test
    public void testEspn() throws Exception {
        //String url = "http://sports.espn.go.com/espn/commentary/news/story?id=5461430";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("espn.html")));
        assertTrue(article.getText(), article.getText().startsWith("If you believe what college football coaches have said about sports"));
        assertEquals(article.getImageUrl(), "http://a.espncdn.com/photo/2010/0813/ncf_i_mpouncey1_300.jpg");
    }

    @Test
    public void testEspn2() throws Exception {
        //String url = "http://sports.espn.go.com/golf/pgachampionship10/news/story?id=5463456";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("espn2.html")));
        assertTrue(article.getText(), article.getText().startsWith("SHEBOYGAN, Wis. -- The only number that matters at the PGA Championship"));
        assertEquals(article.getImageUrl(), "http://a.espncdn.com/media/motion/2010/0813/dm_100814_pga_rinaldi.jpg");
    }

    @Test
    public void testWashingtonpost() throws Exception {
        //String url = "http://www.washingtonpost.com/wp-dyn/content/article/2010/12/08/AR2010120803185.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("washingtonpost.html")));
        assertTrue(article.getText(), article.getText().startsWith("The Supreme Court sounded "));
        assertEquals(article.getImageUrl(), "http://media3.washingtonpost.com/wp-dyn/content/photo/2010/10/09/PH2010100904575.jpg");
    }

    @Test
    public void testGizmodo() throws Exception {
        //String url = "http://gizmodo.com/#!5616256/xbox-kinect-gets-its-fight-club";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("gizmodo.html")));
        assertTrue(article.getText(), article.getText().startsWith("You love to punch your arms through the air"));
        assertEquals(article.getImageUrl(), "http://cache.gawkerassets.com/assets/images/9/2010/08/500x_fighters_uncaged__screenshot_3b__jawbreaker.jpg");
    }

    @Test
    public void testEngadget2() throws Exception {
        //String url = "http://www.engadget.com/2010/08/18/verizon-fios-set-top-boxes-getting-a-new-hd-guide-external-stor/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("engadget2.html")));
        assertTrue(article.getText(), article.getText().startsWith("Streaming and downloading TV content to mobiles is nice"));
        assertEquals(article.getImageUrl(), "http://www.blogcdn.com/www.engadget.com/media/2010/08/44ni600.jpg");
    }

    @Test
    public void testBoingboing() throws Exception {
        //String url = "http://www.boingboing.net/2010/08/18/dr-laura-criticism-o.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("boingboing.html")));
        assertTrue(article.getText(), article.getText().startsWith("Dr. Laura Schlessinger is leaving radio to regain"));
        assertEquals(article.getImageUrl(), "http://www.boingboing.net/images/drlaura.jpg");
    }

    @Test
    public void testWired() throws Exception {
        //String url = "http://www.wired.com/playbook/2010/08/stress-hormones-boxing/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wired.html")));
        assertTrue(article.getText(), article.getText().startsWith("On November 25, 1980, professional boxing"));
        assertEquals(article.getImageUrl(), "http://www.wired.com/playbook/wp-content/uploads/2010/08/fight_f-660x441.jpg");
        assertTrue(article.getTitle().equals("Stress Hormones Could Predict Boxing Dominance"));
    }

    @Test
    public void tetGigaohm() throws Exception {
        //String url = "http://gigaom.com/apple/apples-next-macbook-an-800-mac-for-the-masses/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("gigaom.html")));
        assertTrue(article.getText(), article.getText().startsWith("The MacBook Air is a bold move forward "));
        assertEquals(article.getImageUrl(), "http://gigapple.files.wordpress.com/2010/10/macbook-feature.png?w=300&h=200");
    }

    @Test
    public void testMashable() throws Exception {
        //String url = "http://mashable.com/2010/08/18/how-tonot-to-ask-someone-out-online/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("mashable.html")));
        assertTrue(article.getText(), article.getText().startsWith("Imagine, if you will, a crowded dance floor"));
        assertEquals(article.getImageUrl(), "http://9.mshcdn.com/wp-content/uploads/2010/07/love.jpg");
    }

    @Test
    public void testReadwriteWeb() throws Exception {
        //String url = "http://www.readwriteweb.com/start/2010/08/pagely-headline.php";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("readwriteweb.html")));
        assertTrue(article.getText(), article.getText().startsWith("In the heart of downtown Chandler, Arizona"));
        assertEquals(article.getImageUrl(), "http://rww.readwriteweb.netdna-cdn.com/start/images/pagelyscreen_aug10.jpg");
    }

    @Test
    public void testVenturebeat() throws Exception {
        //String url = "http://social.venturebeat.com/2010/08/18/facebook-reveals-the-details-behind-places/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("venturebeat.html")));
        assertTrue(article.getText(), article.getText().startsWith("Facebook just confirmed the rumors"));
        assertEquals(article.getImageUrl(), "http://cdn.venturebeat.com/wp-content/uploads/2010/08/mark-zuckerberg-facebook-places.jpg");
    }

    @Test
    public void testTimemagazine() throws Exception {
        //String url = "http://www.time.com/time/health/article/0,8599,2011497,00.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("time.html")));
        assertTrue(article.getText(), article.getText().startsWith("This month, the federal government released"));
        assertEquals(article.getImageUrl(), "http://img.timeinc.net/time/daily/2010/1008/bp_oil_spill_0817.jpg");
    }

    @Test
    public void testCnet() throws Exception {
        //String url = "http://news.cnet.com/8301-30686_3-20014053-266.html?tag=topStories1";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnet.html")));
        assertTrue(article.getText(), article.getText().startsWith("NEW YORK--Verizon Communications is prepping a new"));
        assertEquals(article.getImageUrl(), "http://i.i.com.com/cnwk.1d/i/tim//2010/08/18/Verizon_iPad_and_live_TV_610x458.JPG");
    }

    @Test
    public void testYahooNewsEvenThoughTheyFuckedUpDeliciousWeWillTestThemAnyway() throws Exception {
        //String url = "http://news.yahoo.com/s/ap/20110305/ap_on_re_af/af_libya";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("yahoo.html")));
        assertTrue(article.getText(), article.getText().startsWith("TRIPOLI, Libya &ndash; Moammar Gadhafi"));
        assertEquals(article.getImageUrl(), "http://d.yimg.com/a/p/ap/20110305/capt.20433579e6a949189ddb65a8c260183c-20433579e6a949189ddb65a8c260183c-0.jpg?x=213&y=142&xc=1&yc=1&wc=410&hc=273&q=85&sig=i4WKbNKMgqenVsxU3NCbOg--");
    }

    @Test
    public void testPolitico() throws Exception {
        //String url = "http://www.politico.com/news/stories/1010/43352.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("politico.html")));
        assertTrue(article.getText(), article.getText().startsWith("If the newest Census Bureau estimates stay close to form"));
        assertEquals(article.getImageUrl(), "http://images.politico.com/global/news/100927_obama22_ap_328.jpg");
    }

    @Test
    public void testNewsweek() throws Exception {
        //String url = "http://www.newsweek.com/2010/10/09/how-moscow-s-war-on-islamist-rebels-is-backfiring.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("newsweek.html")));
        assertTrue(article.getText(), article.getText().startsWith("At first glance, Kadyrov might seem"));
        assertEquals(article.getImageUrl(), "http://www.newsweek.com/content/newsweek/2010/10/09/how-moscow-s-war-on-islamist-rebels-is-backfiring/_jcr_content/body/mainimage.img.jpg/1286654107500.jpg");
    }

    @Test
    public void testLifehacker() throws Exception {
        //String url = "http://lifehacker.com/#!5659837/build-a-rocket-stove-to-heat-your-home-with-wood-scraps";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("lifehacker.html")));
        assertTrue(article.getText(), article.getText().startsWith("If you find yourself with lots of leftover wood"));
        assertEquals(article.getImageUrl(), "http://cache.gawker.com/assets/images/lifehacker/2010/10/rocket-stove-finished.jpeg");
    }

    @Test
    public void testNinjablog() throws Exception {
        //String url = "http://www.ninjatraderblog.com/im/2010/10/seo-marketing-facts-about-google-instant-and-ranking-your-website/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("ninjatraderblog.html")));
        assertTrue(article.getText(), article.getText().startsWith("Many users around the world Google their queries"));
    }

    @Test
    public void testNaturalhomemagazine() throws Exception {
        //String url = "http://www.naturalhomemagazine.com/diy-projects/try-this-papier-mache-ghostly-lanterns.aspx";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("naturalhomemagazine.html")));
        assertTrue(article.getText(), article.getText().startsWith("Guide trick or treaters and other friendly spirits to your front"));
        assertEquals(article.getImageUrl(), "http://www.naturalhomemagazine.com/uploadedImages/articles/issues/2010-09-01/NH-SO10-trythis-lantern-final2_resized400X266.jpg");
    }

    @Test
    public void testSfgate() throws Exception {
        //String url = "http://www.sfgate.com/cgi-bin/article.cgi?f=/c/a/2010/10/27/BUD61G2DBL.DTL";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("sfgate.html")));
        assertTrue(article.getText(), article.getText().startsWith("Fewer homes in California and"));
        assertEquals(article.getImageUrl(), "http://imgs.sfgate.com/c/pictures/2010/10/26/ba-foreclosures2_SFCG1288130091.jpg");
    }

    @Test
    public void testSportsillustrated() throws Exception {
        //String url = "http://sportsillustrated.cnn.com/2010/football/ncaa/10/15/ohio-state-holmes.ap/index.html?xid=si_ncaaf";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("sportsillustrated.html")));
        assertTrue(article.getText(), article.getText().startsWith("COLUMBUS, Ohio (AP) -- Ohio State has closed"));
        assertEquals(article.getImageUrl(), "http://i.cdn.turner.com/si/.e1d/img/4.0/global/logos/si_100x100.jpg");
    }

    // todo get this one working - I hate star magazine web designers, they put 2 html files into one
//  @Test public void testStarMagazine() throws Exception {
//
//    //String url = "http://www.starmagazine.com/news/17510?cid=RSS";
//    
//    JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("
//    assertTrue(article.getText(), article.getText().startsWith("The Real Reason Rihanna Skipped Katy's Wedding: No Cell Phone Reception!"));
//    assertEquals(article.getImageUrl(),"Rihanna has admitted the real reason she was a no show"));
//    assertTrue(article.getTitle().equals("http://www.starmagazine.com/media/originals/Rihanna_1010_230.jpg"));
//  }
    @Test public void testDailybeast() throws Exception {
        //String url = "http://www.thedailybeast.com/blogs-and-stories/2010-11-01/ted-sorensen-speechwriter-behind-jfks-best-jokes/?cid=topic:featured1";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("thedailybeast.html")));
        assertTrue(article.getText(), article.getText().startsWith("Legendary Kennedy speechwriter Ted Sorensen passed"));
        assertEquals(article.getImageUrl(), "http://www.tdbimg.com/files/2010/11/01/img-article---katz-ted-sorensen_163531624950.jpg");
    }

    @Test
    public void testBloomberg() throws Exception {
        //String url = "http://www.bloomberg.com/news/2010-11-01/china-becomes-boss-in-peru-on-50-billion-mountain-bought-for-810-million.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("bloomberg.html")));
        assertTrue(article.getText(), article.getText().startsWith("The Chinese entrepreneur and the Peruvian shopkeeper"));
        assertEquals(article.getImageUrl(), "http://www.bloomberg.com/apps/data?pid=avimage&iid=iimODmqjtcQU");
    }

    @Test
    public void testScientificdaily() throws Exception {
        //String url = "http://www.scientificamerican.com/article.cfm?id=bpa-semen-quality";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("scientificamerican.html")));
        assertTrue(article.getText(), article.getText().startsWith("The common industrial chemical bisphenol A (BPA) "));
        assertEquals(article.getImageUrl(), "http://www.scientificamerican.com/media/inline/bpa-semen-quality_1.jpg");
        assertTrue(article.getTitle().equals("Everyday BPA Exposure Decreases Human Semen Quality"));
    }

    @Test
    public void testScience() throws Exception {
        //String url = "http://news.sciencemag.org/sciencenow/2011/04/early-birds-smelled-good.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("sciencemag.html")));
        assertTrue(article.getText(), article.getText().startsWith("About 65 million years ago, most of the dinosaurs and many other animals and plants were wiped off Earth, probably due to an asteroid hitting our planet. Researchers have long debated how and why some "));
    }

    @Test
    public void testNature() throws Exception {
        //String url = "http://www.nature.com/news/2011/110411/full/472146a.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("nature.html")));
        assertTrue(article.getText(), article.getText().startsWith("As the immediate threat from Fukushima Daiichi's damaged nuclear reactors recedes, engineers and scientists are"));
    }

    @Test
    public void testSlamMagazine() throws Exception {
        //String url = "http://www.slamonline.com/online/nba/2010/10/nba-schoolyard-rankings/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("slamonline.html")));
        assertTrue(article.getText(), article.getText().startsWith("When in doubt, rank players and add your findings"));
        assertEquals(article.getImageUrl(), "http://www.slamonline.com/online/wp-content/uploads/2010/10/celtics.jpg");
        assertTrue(article.getTitle().equals("NBA Schoolyard Rankings"));
    }

    @Test
    public void testTheFrisky() throws Exception {
        //String url = "http://www.thefrisky.com/post/246-rachel-dratch-met-her-baby-daddy-in-a-bar/?eref=RSS";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("thefrisky.html")));
        assertTrue(article.getText(), article.getText().startsWith("Rachel Dratch had been keeping the identity of her baby daddy "));
        assertEquals(article.getImageUrl(), "http://cdn.thefrisky.com/images/uploads/rachel_dratch_102810_m.jpg");
        assertTrue(article.getTitle().equals("Rachel Dratch Met Her Baby Daddy At A Bar"));
    }

    @Test
    public void testUniverseToday() throws Exception {
        //String url = "http://www.universetoday.com/76881/podcast-more-from-tony-colaprete-on-lcross/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("universetoday.html")));
        assertTrue(article.getText(), article.getText().startsWith("I had the chance to interview LCROSS"));
        assertEquals(article.getImageUrl(), "http://www.universetoday.com/wp-content/uploads/2009/10/lcross-impact_01_01.jpg");
        assertTrue(article.getTitle().equals("More From Tony Colaprete on LCROSS"));
    }

    @Test
    public void testCNBC() throws Exception {
        //String url = "http://www.cnbc.com/id/40491584";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnbc.html")));
        assertTrue(article.getText(), article.getText().startsWith("A prominent expert on Chinese works "));
        assertEquals(article.getImageUrl(), "http://media.cnbc.com/i/CNBC/Sections/News_And_Analysis/__Story_Inserts/graphics/__ART/chinese_vase_150.jpg");
        assertTrue(article.getTitle().equals("Chinese Art Expert 'Skeptical' of Record-Setting Vase"));
    }

    @Test
    public void testEspn3WithFlashVideo() throws Exception {
        //String url = "http://sports.espn.go.com/nfl/news/story?id=5971053";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("espn3.html")));
        assertTrue(article.getText(), article.getText().startsWith("PHILADELPHIA -- Michael Vick missed practice Thursday"));
        assertEquals(article.getImageUrl(), "http://a.espncdn.com/i/espn/espn_logos/espn_red.png");
        assertEquals("Michael Vick of Philadelphia Eagles misses practice, unlikely to play vs. Dallas Cowboys", article.getTitle());
    }

    @Test
    public void testSportingNews() throws Exception {
        //String url = "http://www.sportingnews.com/nfl/feed/2011-01/nfl-coaches/story/raiders-cut-ties-with-cable";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("sportingnews.html")));
        assertTrue(article.getText(), article.getText().startsWith("ALAMEDA, Calif. &mdash; The Oakland Raiders informed coach Tom Cable"));
        assertEquals(article.getImageUrl(), "http://dy.snimg.com/story-image/0/69/174475/14072-650-366.jpg");
        assertTrue(article.getTitle().equals("Raiders cut ties with Cable"));
    }

    @Test
    public void testFoxSports() throws Exception {
        //String url = "http://msn.foxsports.com/nfl/story/Tom-Cable-fired-contract-option-Oakland-Raiders-coach-010411";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("foxsports.html")));
        assertTrue(article.getText(), article.getText().startsWith("The Oakland Raiders informed coach Tom Cable"));
        assertTrue(article.getTitle().equals("Oakland Raiders won't bring Tom Cable back as coach - NFL News"));
    }

    @Test
    public void testMsnbc() throws Exception {
        //String url = "http://www.msnbc.msn.com/id/41207891/ns/world_news-europe/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("msnbc.html")));
        assertTrue(article.getText(), article.getText().startsWith("Prime Minister Brian Cowen announced Saturday"));
        assertTrue(article.getTitle().equals("Irish premier resigns as party leader, stays as PM"));
        assertEquals(article.getImageUrl(), "http://msnbcmedia3.msn.com/j/ap/ireland government crisis--687575559_v2.grid-6x2.jpg");
    }

    @Test
    public void testEconomist() throws Exception {
        //String url = "http://www.economist.com/node/17956885";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("economist.html")));
        assertTrue(article.getText(), article.getText().startsWith("FOR beleaguered smokers, the world is an increasingly"));
        assertEquals(article.getImageUrl(), "http://media.economist.com/images/images-magazine/2011/01/22/st/20110122_stp004.jpg");
    }

    @Test
    public void testTheAtlantic() throws Exception {
        //String url = "http://www.theatlantic.com/culture/archive/2011/01/how-to-stop-james-bond-from-getting-old/69695/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("theatlantic.html")));
        assertTrue(article.getText(), article.getText().startsWith("If James Bond could age, he'd be well into his 90s right now"));
        assertEquals(article.getImageUrl(), "http://assets.theatlantic.com/static/mt/assets/culture_test/James%20Bond_post.jpg");
    }

    @Test
    public void testGawker() throws Exception {
        //String url = "http://gawker.com/#!5777023/charlie-sheen-is-going-to-haiti-with-sean-penn";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("gawker.html")));
        assertTrue(article.getText(), article.getText().startsWith("With a backlash brewing against the incessant media"));
        assertEquals(article.getImageUrl(), "http://cache.gawkerassets.com/assets/images/7/2011/03/medium_0304_pennsheen.jpg");
    }

    @Test
    public void testNyt2() throws Exception {
        //String url = "http://www.nytimes.com/2010/12/22/world/europe/22start.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("nyt2.html")));
        assertTrue(article.getText(), article.getText().startsWith("WASHINGTON &mdash; An arms control treaty paring back American"));
        assertEquals(article.getImageUrl(), "http://graphics8.nytimes.com/images/2010/12/22/world/22start-span/Start-articleInline.jpg");
    }

    @Test
    public void testTheVacationGals() throws Exception {
        //String url = "http://thevacationgals.com/vacation-rental-homes-are-a-family-reunion-necessity/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("thevacationgals.html")));
        assertTrue(article.getText(), article.getText().startsWith("Editors&rsquo; Note: We are huge proponents"));
        assertEquals(article.getImageUrl(), "http://thevacationgals.com/wp-content/uploads/2010/11/Gemmel-Family-Reunion-at-a-Vacation-Rental-Home1-300x225.jpg");
    }

    @Test
    public void testGettingVideosFromGraphVinyl() throws Exception {
        //String url = "http://grapevinyl.com/v/84/magnetic-morning/getting-nowhere";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("grapevinyl.html")));
        assertEquals(article.getVideoUrl(), "http://www.youtube.com/v/dsVWVtGWoa4&hl=en_US&fs=1&color1=d6d6d6&color2=ffffff&autoplay=1&iv_load_policy=3&rel=0&showinfo=0&hd=1");
    }

    @Test
    public void testShockYa() throws Exception {
        //String url = "http://www.shockya.com/news/2011/01/30/daily-shock-jonathan-knight-of-new-kids-on-the-block-publicly-reveals-hes-gay/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("shockya.html")));
        assertTrue(article.getText(), article.getText().startsWith("New Kids On The Block singer Jonathan Knight has publicly"));
        assertEquals(article.getImageUrl(), "http://www.shockya.com/news/wp-content/uploads/jonathan_knight_new_kids_gay.jpg");
    }

    @Test
    public void testLiveStrong() throws Exception {
        //String url = "http://www.livestrong.com/article/395538-how-to-decrease-the-rates-of-obesity-in-children/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("livestrong.html")));
        assertTrue(article.getText(), article.getText().startsWith("Childhood obesity increases a young person"));
        assertEquals(article.getImageUrl(), "http://photos.demandstudios.com/getty/article/184/46/87576279_XS.jpg");
    }

    @Test
    public void testLiveStrong2() throws Exception {
        //String url = "http://www.livestrong.com/article/396152-do-resistance-bands-work-for-strength-training/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("livestrong2.html")));
        assertTrue(article.getText(), article.getText().startsWith("Resistance bands or tubes are named because"));
        assertEquals(article.getImageUrl(), "http://photos.demandstudios.com/getty/article/142/66/86504893_XS.jpg");
    }

    @Test
    public void testCracked() throws Exception {
        //String url = "http://www.cracked.com/article_19029_6-things-social-networking-sites-need-to-stop-doing.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cracked.html")));
        assertTrue(article.getText(), article.getText().startsWith("Social networking is here to stay"));
        assertEquals(article.getImageUrl(), "http://i.crackedcdn.com/phpimages/article/2/1/5/45215.jpg?v=1");
    }

    @Test
    public void testTrailsCom() throws Exception {
        //String url = "http://www.trails.com/facts_41596_hot-spots-citrus-county-florida.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("trails.html")));
        assertTrue(article.getText(), article.getText().startsWith("Snorkel and view artificial reefs or chase"));
        assertEquals(article.getImageUrl(), "http://cdn-www.trails.com/imagecache/articles/295x195/hot-spots-citrus-county-florida-295x195.png");
    }

    @Test
    public void testTrailsCom2() throws Exception {
        //String url = "http://www.trails.com/facts_12408_history-alpine-skis.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("trails2.html")));
        assertTrue(article.getText(), article.getText().startsWith("Derived from the old Norse word"));
        assertEquals(article.getImageUrl(), "http://cdn-www.trails.com/imagecache/articles/295x195/history-alpine-skis-295x195.png");
    }

    @Test
    public void testEhow() throws Exception {
        //String url = "http://www.ehow.com/how_7734109_make-white-spaghetti.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("ehow.html")));
        assertTrue(article.getText(), article.getText().startsWith("Heat the oil in the"));
        assertTrue(article.getTitle().equals("How to Make White Spaghetti"));
    }

    @Test
    public void testGolfLink() throws Exception {
        //String url = "http://www.golflink.com/how_1496_eat-cheap-las-vegas.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("golflink.html")));
        assertTrue(article.getText(), article.getText().startsWith("Las Vegas, while noted for its glitz"));
        assertEquals(article.getImageUrl(), "http://cdn-www.golflink.com/Cms/images/GlobalPhoto/Articles/2011/2/17/1496/fotolia4152707XS-main_Full.jpg");
    }

    @Test
    public void testAnswerBag() throws Exception {
        //String url = "http://www.answerbag.com/q_view/2438372";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("answerbag.html")));
        assertTrue(article.getText(), article.getText().startsWith("You're reading True or false"));
    }

    @Test
    public void testAnswerBag2() throws Exception {
        // String url = "http://www.answerbag.com/q_view/2445112";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("answerbag2.html")));
        assertTrue(article.getText(), article.getText().startsWith("You're reading Can chamomille"));
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
