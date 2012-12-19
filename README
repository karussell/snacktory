# Snacktory

This is a small helper utility for pepole don't want to write yet another java clone of Readability.

In most cases, this is applied to articles, although it should work for any website to find its major
area, extract its text, keywords, its main picture and more.

Have a look into http://jetsli.de where Snacktory is used. Jetslide is a new way to consume news, it does not only display the Websites' title but it displays a small preview of the site ('a snack') and the important image if available.

# License 

The software stands under Apache 2 License and comes with NO WARRANTY

# Features

Snacktory borrows some ideas and a lot of test cases from goose:
https://github.com/jiminoc/goose

The advantages over Goose are
    * similar article text detection although better detection for none-english sites (German, Japanese, ...)
    * snacktory does not depend on the word count in its text detection to support CJK languages
    * no external Services required to run the core tests => faster tests
    * better charset detection
    * with url resolving, but caching is still possible after resolving
    * skipping some known filetypes

The disadvantages to Goose are
    * only top image and top text supported at the moment
    * some articles which passed do not pass. 
      But added a bunch of other useful sites (stackoverflow, facebook, other languages ...)


# Usage

 Include the repo at see README: https://github.com/karussell/mvnrepo
 Then add the dependency
 <dependency>
    <groupId>de.jetwick</groupId>
    <artifactId>snacktory</artifactId>
    <version>1.1-SNAPSHOT</version>
 </dependency>
 
 Now you can use it as follows:
 
 HtmlFetcher fetcher = new HtmlFetcher();
 // set cache. e.g. take the map implementation from google collections:
 // fetcher.setCache(new MapMaker().concurrencyLevel(20).maximumSize(count).expireAfterWrite(minutes, TimeUnit.MINUTES).makeMap();

 JResult res = fetcher.fetchAndExtract(articleUrl, resolveTimeout, true);
 String text = res.getText(); 
 String title = res.getTitle(); 
 String imageUrl = res.getImageUrl();

# Build

via Maven. Maven will automatically resolve dependencies to jsoup, log4j and slf4j-api
