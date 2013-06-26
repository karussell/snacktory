package de.jetwick.snacktory;

import java.io.BufferedReader;
import java.io.FileReader;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArticleTextExtractorTodoTester {

    ArticleTextExtractor extractor;
    Converter c;

    @Before
    public void setup() throws Exception {
        c = new Converter();
        extractor = new ArticleTextExtractor();
    }

    @Test
    public void testEspn2() throws Exception {
        //String url = "http://sports.espn.go.com/golf/pgachampionship10/news/story?id=5463456";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("espn2.html")));
        assertTrue(article.getText(), article.getText().startsWith("PHILADELPHIA -- Michael Vick missed practice Thursday because of a leg injury and is unlikely to play Sunday wh"));
        assertEquals("http://a.espncdn.com/media/motion/2010/0813/dm_100814_pga_rinaldi.jpg", article.getImageUrl());
    }

    @Test
    public void testWashingtonpost() throws Exception {
        //String url = "http://www.washingtonpost.com/wp-dyn/content/article/2010/12/08/AR2010120803185.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("washingtonpost.html")));
        assertTrue(article.getText(), article.getText().startsWith("The Supreme Court sounded "));
        assertEquals("http://media3.washingtonpost.com/wp-dyn/content/photo/2010/10/09/PH2010100904575.jpg", article.getImageUrl());
    }

    @Test
    public void testBoingboing() throws Exception {
        //String url = "http://www.boingboing.net/2010/08/18/dr-laura-criticism-o.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("boingboing.html")));
        assertTrue(article.getText(), article.getText().startsWith("Dr. Laura Schlessinger is leaving radio to regain"));
        assertEquals("http://www.boingboing.net/images/drlaura.jpg", article.getImageUrl());
    }

    @Test
    public void testReadwriteWeb() throws Exception {
        //String url = "http://www.readwriteweb.com/start/2010/08/pagely-headline.php";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("readwriteweb.html")));
        assertTrue(article.getText(), article.getText().startsWith("In the heart of downtown Chandler, Arizona"));
        assertEquals("http://rww.readwriteweb.netdna-cdn.com/start/images/logopagely_aug10.jpg", article.getImageUrl());
    }

    @Test
    public void testYahooNewsEvenThoughTheyFuckedUpDeliciousWeWillTestThemAnyway() throws Exception {
        //String url = "http://news.yahoo.com/s/ap/20110305/ap_on_re_af/af_libya";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("yahoo.html")));
        assertTrue(article.getText(), article.getText().startsWith("TRIPOLI, Libya – Government forces in tanks rolled into the opposition-held city closest "));
        assertEquals("http://d.yimg.com/a/p/ap/20110305/http://d.yimg.com/a/p/ap/20110305/thumb.23c7d780d8d84bc4a8c77af11ecba277-23c7d780d8d84bc4a8c77af11ecba277-0.jpg?x=130&y=90&xc=1&yc=1&wc=130&hc=90&q=85&sig=LbIZK0rnJlZAcrAWn.brLw--",
                article.getImageUrl());
    }

    @Test
    public void testLifehacker() throws Exception {
        //String url = "http://lifehacker.com/#!5659837/build-a-rocket-stove-to-heat-your-home-with-wood-scraps";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("lifehacker.html")));
        assertTrue(article.getText(), article.getText().startsWith("If you find yourself with lots of leftover wood"));
        assertEquals("http://cache.gawker.com/assets/images/lifehacker/2010/10/rocket-stove-finished.jpeg", article.getImageUrl());
    }

    @Test
    public void testNaturalhomemagazine() throws Exception {
        //String url = "http://www.naturalhomemagazine.com/diy-projects/try-this-papier-mache-ghostly-lanterns.aspx";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("naturalhomemagazine.html")));
        assertTrue(article.getText(), article.getText().startsWith("Guide trick or treaters and other friendly spirits to your front"));
        assertEquals("http://www.naturalhomemagazine.com/uploadedImages/articles/issues/2010-09-01/NH-SO10-trythis-lantern-final2_resized400X266.jpg",
                article.getImageUrl());
    }

    @Test
    public void testSfgate() throws Exception {
        //String url = "http://www.sfgate.com/cgi-bin/article.cgi?f=/c/a/2010/10/27/BUD61G2DBL.DTL";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("sfgate.html")));
        assertTrue(article.getText(), article.getText().startsWith("Fewer homes in California and"));
        assertEquals("http://imgs.sfgate.com/c/pictures/2010/10/26/ba-foreclosures2_SFCG1288130091.jpg",
                article.getImageUrl());
    }

    @Test
    public void testScientificdaily() throws Exception {
        //String url = "http://www.scientificamerican.com/article.cfm?id=bpa-semen-quality";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("scientificamerican.html")));
        assertTrue(article.getText(), article.getText().startsWith("The common industrial chemical bisphenol A (BPA) "));
        assertEquals("http://www.scientificamerican.com/media/inline/bpa-semen-quality_1.jpg", article.getImageUrl());
        assertEquals("Everyday BPA Exposure Decreases Human Semen Quality", article.getTitle());
    }

    @Test
    public void testUniverseToday() throws Exception {
        //String url = "http://www.universetoday.com/76881/podcast-more-from-tony-colaprete-on-lcross/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("universetoday.html")));
        assertTrue(article.getText(), article.getText().startsWith("I had the chance to interview LCROSS"));
        assertEquals("http://www.universetoday.com/wp-content/uploads/2009/10/lcross-impact_01_01.jpg",
                article.getImageUrl());
        assertEquals("More From Tony Colaprete on LCROSS", article.getTitle());
    }

    @Test
    public void testCNBC() throws Exception {
        //String url = "http://www.cnbc.com/id/40491584";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnbc.html")));
        assertTrue(article.getText(), article.getText().startsWith("A prominent expert on Chinese works "));
        assertEquals("http://media.cnbc.com/i/CNBC/Sections/News_And_Analysis/__Story_Inserts/graphics/__ART/chinese_vase_150.jpg",
                article.getImageUrl());
        assertTrue(article.getTitle().equals("Chinese Art Expert 'Skeptical' of Record-Setting Vase"));
    }

    @Test
    public void testMsnbc() throws Exception {
        //String url = "http://www.msnbc.msn.com/id/41207891/ns/world_news-europe/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("msnbc.html")));
        assertTrue(article.getText(), article.getText().startsWith("DUBLIM -- Prime Minister Brian Cowen announced Saturday"));
        assertEquals("Irish premier resigns as party leader, stays as PM", article.getTitle());
        assertEquals("http://msnbcmedia3.msn.com/j/ap/ireland government crisis--687575559_v2.grid-6x2.jpg",
                article.getImageUrl());
    }

    @Test
    public void testTheAtlantic() throws Exception {
        //String url = "http://www.theatlantic.com/culture/archive/2011/01/how-to-stop-james-bond-from-getting-old/69695/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("theatlantic.html")));
        assertTrue(article.getText(), article.getText().startsWith("If James Bond could age, he'd be well into his 90s right now"));
        assertEquals("http://assets.theatlantic.com/static/mt/assets/culture_test/James%20Bond_post.jpg",
                article.getImageUrl());
    }

    @Test
    public void testGawker() throws Exception {
        //String url = "http://gawker.com/#!5777023/charlie-sheen-is-going-to-haiti-with-sean-penn";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("gawker.html")));
        assertTrue(article.getText(), article.getText().startsWith("With a backlash brewing against the incessant media"));
        assertEquals("http://cache.gawkerassets.com/assets/images/7/2011/03/medium_0304_pennsheen.jpg",
                article.getImageUrl());
    }

    @Test
    public void testNyt2() throws Exception {
        //String url = "http://www.nytimes.com/2010/12/22/world/europe/22start.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("nyt2.html")));
        assertTrue(article.getText(), article.getText().startsWith("WASHINGTON &mdash; An arms control treaty paring back American"));
        assertEquals("http://graphics8.nytimes.com/images/2010/12/22/world/22start-span/Start-articleInline.jpg",
                article.getImageUrl());
    }

    @Test
    public void testGettingVideosFromGraphVinyl() throws Exception {
        //String url = "http://grapevinyl.com/v/84/magnetic-morning/getting-nowhere";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("grapevinyl.html")));
        assertEquals("http://www.youtube.com/v/dsVWVtGWoa4&hl=en_US&fs=1&color1=d6d6d6&color2=ffffff&autoplay=1&iv_load_policy=3&rel=0&showinfo=0&hd=1",
                article.getVideoUrl());
    }

    @Test
    public void testLiveStrong() throws Exception {
        //String url = "http://www.livestrong.com/article/395538-how-to-decrease-the-rates-of-obesity-in-children/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("livestrong.html")));
        assertTrue(article.getText(), article.getText().startsWith("Childhood obesity increases a young person"));
        assertEquals("http://photos.demandstudios.com/getty/article/184/46/87576279_XS.jpg",
                article.getImageUrl());
    }

    @Test
    public void testLiveStrong2() throws Exception {
        //String url = "http://www.livestrong.com/article/396152-do-resistance-bands-work-for-strength-training/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("livestrong2.html")));
        assertTrue(article.getText(), article.getText().startsWith("Resistance bands or tubes are named because"));
        assertEquals("http://photos.demandstudios.com/getty/article/142/66/86504893_XS.jpg", article.getImageUrl());
    }

    @Test
    public void testCracked() throws Exception {
        //String url = "http://www.cracked.com/article_19029_6-things-social-networking-sites-need-to-stop-doing.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cracked.html")));
        assertTrue(article.getText(), article.getText().startsWith("Social networking is here to stay"));
        assertEquals("http://i-beta.crackedcdn.com/phpimages/article/2/1/6/45216.jpg?v=1", article.getImageUrl());
    }

    @Test
    public void testMidgetmanofsteel() throws Exception {
        //String url = "http://www.cracked.com/article_19029_6-things-social-networking-sites-need-to-stop-doing.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("midgetmanofsteel.html")));
        assertTrue(article.getText(), article.getText().startsWith("I've decided to turn my Facebook assholishnessicicity"));
        assertEquals("http://4.bp.blogspot.com/_F74vJj-Clzk/TPkzP-Y93jI/AAAAAAAALKM/D3w1sfJqE5U/s200/funny-dog-pictures-will-work-for-hot-dogs.jpg", article.getImageUrl());
    }

    @Test
    public void testTrailsCom() throws Exception {
        //String url = "http://www.trails.com/facts_41596_hot-spots-citrus-county-florida.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("trails.html")));
        assertTrue(article.getText(), article.getText().startsWith("Snorkel and view artificial reefs or chase"));
        assertEquals("http://cdn-www.trails.com/imagecache/articles/295x195/hot-spots-citrus-county-florida-295x195.png", article.getImageUrl());
    }

    @Test
    public void testTrailsCom2() throws Exception {
        //String url = "http://www.trails.com/facts_12408_history-alpine-skis.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("trails2.html")));
        assertTrue(article.getText(), article.getText().startsWith("Derived from the old Norse word"));
        assertEquals("http://cdn-www.trails.com/imagecache/articles/295x195/history-alpine-skis-295x195.png", article.getImageUrl());
    }

    @Test
    public void testEhow() throws Exception {
        //String url = "http://www.ehow.com/how_7734109_make-white-spaghetti.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("ehow.html")));
        assertTrue(article.getText(), article.getText().startsWith("Heat the oil in the"));
        assertEquals("How to Make White Spaghetti", article.getTitle());
    }

    @Test
    public void testGolfLink() throws Exception {
        //String url = "http://www.golflink.com/how_1496_eat-cheap-las-vegas.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("golflink.html")));
        assertTrue(article.getText(), article.getText().startsWith("Las Vegas, while noted for its glitz"));
        assertEquals("http://cdn-www.golflink.com/Cms/images/GlobalPhoto/Articles/2011/2/17/1496/fotolia4152707XS-main_Full.jpg",
                article.getImageUrl());
    }

    @Test
    public void testNewsweek() throws Exception {
        //String url = "http://www.newsweek.com/2010/10/09/how-moscow-s-war-on-islamist-rebels-is-backfiring.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("newsweek.html")));
        assertTrue(article.getText(), article.getText().startsWith("At first glance, Kadyrov might seem"));
//        assertEquals("http://www.newsweek.com/content/newsweek/2010/10/09/how-moscow-s-war-on-islamist-rebels-is-backfiring.scaled.small.1309768214891.jpg",
//                article.getImageUrl());
        assertEquals("http://www.newsweek.com/content/newsweek/2010/10/09/how-moscow-s-war-on-islamist-rebels-is-backfiring.scaled.small.1302869450444.jpg",
                article.getImageUrl());
    }

    @Test
    public void testBusinessweek() throws Exception {
        // String url = "http://www.businessweek.com/magazine/content/10_34/b4192066630779.htm";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("businessweek.html")));
        assertEquals("Olivia Munn: Queen of the Uncool - BusinessWeek", article.getTitle());
        assertTrue(article.getText(), article.getText().startsWith("Six years ago, Olivia Munn arrived in Hollywood with fading ambitions of making it "));
        assertEquals("http://images.businessweek.com/mz/10/34/370/1034_mz_66popmunnessa.jpg", article.getImageUrl());
    }

    @Test
    public void testNature() throws Exception {
        //String url = "http://www.nature.com/news/2011/110411/full/472146a.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("nature.html")));
        assertTrue(article.getText(), article.getText().startsWith("As the immediate threat from Fukushima "
                + "Daiichi's damaged nuclear reactors recedes, engineers and scientists are"));
    }

    /**
     * @param filePath the name of the file to open. Not sure if it can accept
     * URLs or just filenames. Path handling could be better, and buffer sizes
     * are hardcoded
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
