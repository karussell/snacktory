package com.jreadability.main;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.HashSet;

import java.util.regex.Pattern;
import java.util.*;
import java.io.*;
import java.util.HashMap;

public class ArticleTextExtractorTest {
  @Test
  public void testArticles () throws Exception {
    ArticleTextExtractor extractor = new ArticleTextExtractor();
    String articleText = extractor.getArticleText(readFileAsString("test_data/1.html"));
    
    Assert.assertTrue(articleText.startsWith("AFP/Getty Images"));
    Assert.assertTrue(articleText.endsWith("\"How Four Drinking Buddies Saved Brazil.\""));

    articleText = extractor.getArticleText(readFileAsString("test_data/2.html"));
    Assert.assertTrue(articleText.startsWith("This month is the 15th anniversary of my last CD."));
    Assert.assertTrue(articleText.endsWith("Take it as a compliment :)"));

    articleText = extractor.getArticleText(readFileAsString("test_data/3.html"));
    Assert.assertTrue(articleText.startsWith("October 2010"));
    Assert.assertTrue(articleText.endsWith(" and Jessica Livingston for reading drafts of this."));

    articleText = extractor.getArticleText(readFileAsString("test_data/4.html"));
    Assert.assertTrue(articleText.startsWith("So you have a new startup company and want some coverage"));
    Assert.assertTrue(articleText.endsWith("Know of any other good ones? Please add in the comments."));

    articleText = extractor.getArticleText(readFileAsString("test_data/5.html"));
    Assert.assertTrue(articleText.startsWith("Hackers unite in Stanford"));
    Assert.assertTrue(articleText.endsWith("have beats and bevvies a-plenty. RSVP here.    "));
  }

  /** @param filePath the name of the file to open. Not sure if it can accept URLs or just filenames. Path handling could 
   *   be better, and buffer sizes are hardcoded
  */ 
  public static String readFileAsString(String filePath)
  throws java.io.IOException{
    StringBuffer fileData = new StringBuffer(1000);
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    char[] buf = new char[1024];
    int numRead=0;
    while((numRead=reader.read(buf)) != -1){
      String readData = String.valueOf(buf, 0, numRead);
      fileData.append(readData);
      buf = new char[1024];
    }
    reader.close();
    return fileData.toString();
  }

}
