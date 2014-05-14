# Snacktory

This is a small helper utility for people who don't want to write yet another java clone of Readability.
In most cases, this is applied to articles, although it should work for any website to find its major
area, extract its text, keywords, its main picture and more.

The resulting quality is high, even [paper.li uses](https://twitter.com/timetabling/status/274193754615853056) the core of snacktory.
Also have a look into [this article](http://karussell.wordpress.com/2011/07/12/introducing-jetslide-news-reader/), 
it describes a news aggregator service which uses snacktory. But jetslide is no longer online.

Snacktory borrows some ideas and a lot of test cases from [goose](https://github.com/GravityLabs/goose) 
and [jreadability](https://github.com/ifesdjeen/jReadability):

# License 

The software stands under Apache 2 License and comes with NO WARRANTY

# Features

 * article text detection 
 * get top image url(s)
 * get top video url
 * extraction of description, keywords, ...
 * good detection for none-english sites (German, Japanese, ...), snacktory does not depend on the word count in its text detection to support CJK languages 
 * good charset detection
 * possible to do URL resolving, but caching is still possible after resolving
 * skipping some known filetypes
 * no http GET required to run the core tests

TODOs

 * only top text supported at the moment


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

 If you need this for Android be sure you read [this issue](https://github.com/karussell/snacktory/issues/36).

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
