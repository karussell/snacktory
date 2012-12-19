# Snacktory

This is a small helper utility for pepole who don't want to write yet another java clone of Readability.
In most cases, this is applied to articles, although it should work for any website to find its major
area, extract its text, keywords, its main picture and more.

The resulting quality is high, even [paper.li uses](https://twitter.com/timetabling/status/274193754615853056) the core of snacktory.
Also have a look into [this article](http://karussell.wordpress.com/2011/07/12/introducing-jetslide-news-reader/), 
it describes a news aggregator service which uses snacktory. But jetslide is no longer online.

# License 

The software stands under Apache 2 License and comes with NO WARRANTY

# Features

Snacktory borrows some ideas and a lot of test cases from goose:
https://github.com/jiminoc/goose

The advantages over Goose are

 * similar article text detection although better detection for none-english sites (German, Japanese, ...)
 * snacktory does not depend on the word count in its text detection to support CJK languages
 * no http GET required to run the core tests => faster tests
 * better charset detection
 * possible to do URL resolving, but caching is still possible after resolving
 * skipping some known filetypes

The disadvantages to Goose are

 * only top image and top text supported at the moment. see issues #16 for more infos
 * some articles which passed do not pass. But added a bunch of other useful sites (stackoverflow, facebook, other languages ...)


# Usage

 Include the repo at: https://github.com/karussell/mvnrepo

 Then add the dependency
 
 ```xml
 <dependency>
    <groupId>de.jetwick</groupId>
    <artifactId>snacktory</artifactId>
    <version>1.1</version>
    <!-- or if you prefer the latest build <version>1.2-SNAPSHOT</version> -->
 </dependency>
 ```
 
 Now you can use it as follows:
 
 ```java
 HtmlFetcher fetcher = new HtmlFetcher();
 // set cache. e.g. take the map implementation from google collections:
 // fetcher.setCache(new MapMaker().concurrencyLevel(20).maximumSize(count).
 //    expireAfterWrite(minutes, TimeUnit.MINUTES).makeMap();

 JResult res = fetcher.fetchAndExtract(articleUrl, resolveTimeout, true);
 String text = res.getText(); 
 String title = res.getTitle(); 
 String imageUrl = res.getImageUrl();
```

# Build

via Maven. Maven will automatically resolve dependencies to jsoup, log4j and slf4j-api
