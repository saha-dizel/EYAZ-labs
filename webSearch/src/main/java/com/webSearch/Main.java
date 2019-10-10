package com.webSearch;

import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.fetcher.*;
import edu.uci.ics.crawler4j.robotstxt.*;

public class Main {
    public static void main(String[] args) throws Exception {
        //TODO: choose folder to save temp files
        String crawlStorageFolder = "/data/crawl/root";
        int numberOfCrawlers = 10;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setPolitenessDelay(200);
        config.setMaxDepthOfCrawling(3);
        config.setMaxPagesToFetch(500);
        config.setIncludeBinaryContentInCrawling(false);
        config.setResumableCrawling(false);
        //in official example this is an option you can add (and I'd like to), but for some reason there is nothing like this anymore...
        //config.setHaltOnError(true);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        //TODO: add seeds to start from
        controller.addSeed("https://www.tut.by/");

        CrawlController.WebCrawlerFactory<Crawler> factory = () -> new Crawler();

        controller.start(factory, numberOfCrawlers);
    }
}