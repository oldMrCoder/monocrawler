/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.lanzisun.monocrawler.examples.localdata;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * This class is a demonstration of how crawler4j can be used to download a
 * single page and extract its title and text.
 */
public class Downloader {

    private static final Logger logger = LoggerFactory.getLogger(Downloader.class);

    private final Parser parser;
    private final PageFetcher pageFetcher;

    public Downloader() {

        String rootFolder = "/data/crawl/root";
        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(rootFolder);
        parser = new Parser(config);
        pageFetcher = new PageFetcher(config);
    }

    public static void main(String[] args) {
        Downloader downloader = new Downloader();
//    downloader.processUrl("http://en.wikipedia.org/wiki/Main_Page/");
//    downloader.processUrl("http://www.yahoo.com/");

//        downloader.processUrl("http://intranet.strcn.com/npr/loginPage.xhtml");
        downloader.processUrl("http://intranet.strcn.com/npr/PRListPage.xhtml");
    }

    public void processUrl(String url) {
        logger.info("Processing: {}", url);
        Page page = download(url);
        if (page != null) {
            ParseData parseData = page.getParseData();
            if (parseData != null) {
                if (parseData instanceof HtmlParseData) {
                    HtmlParseData htmlParseData = (HtmlParseData) parseData;
                    logger.info("Title: {}", htmlParseData.getTitle());
                    logger.info("Text length: {}", htmlParseData.getText().length());
                    logger.info("Html length: {}", htmlParseData.getHtml().length());
                    logger.info("Text : {}", htmlParseData.getText());
//                    logger.info("Html : {}", htmlParseData.getHtml());
                }
            } else {
                logger.warn("Couldn't parse the content of the page.");
            }
        } else {
            logger.warn("Couldn't fetch the content of the page.");
        }
        logger.debug("==============");
    }

    private Page download(String url) {
        WebURL curURL = new WebURL();
        curURL.setURL(url);
        PageFetchResult fetchResult = null;
        try {
            fetchResult = pageFetcher.fetchPage(curURL);
            if (fetchResult.getStatusCode() == HttpStatus.SC_OK) {
                Page page = new Page(curURL);
                fetchResult.fetchContent(page);
                parser.parse(page, curURL.getURL());
                return page;
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching url: " + curURL.getURL(), e);
        } finally {
            if (fetchResult != null) {
                fetchResult.discardContentIfNotConsumed();
            }
        }
        return null;
    }
}
