package com.webSearch;

import com.webSearch.Crawler.Crawler;
import com.webSearch.View.View;
import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.fetcher.*;
import edu.uci.ics.crawler4j.robotstxt.*;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws Exception {
        //TODO: choose folder to save temp files
        String crawlStorageFolder = "/data/crawl/root";
        int numberOfCrawlers = 10;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setPolitenessDelay(200);
        config.setMaxDepthOfCrawling(2);
        config.setMaxPagesToFetch(100);
        config.setIncludeBinaryContentInCrawling(false);
        config.setResumableCrawling(false);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        AtomicInteger numOfDocs = new AtomicInteger(0);

        //TODO: add seeds to start from
        controller.addSeed("https://news.tut.by/");

        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ));

        CreateIndexRequest indexRequest = new CreateIndexRequest("page");
        indexRequest.settings(Settings.builder()
            .put("index.number_of_shards", 1)
            .put("index.number_of_replicas", 2)
        );

        Map<String, Object> message = new HashMap<>();
        message.put("type", "text");

        Map<String, Object> properties = new HashMap<>();
        properties.put("URL", message);
        properties.put("content", message);

        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);

        indexRequest.mapping(mapping);

        CreateIndexResponse indexResponse = client.indices().create(indexRequest, RequestOptions.DEFAULT);
        System.out.println("IndexResponse id: " + indexResponse.index());

        CrawlController.WebCrawlerFactory<Crawler> factory = () -> new Crawler(client, numOfDocs);

        View view = new View(controller, factory, numberOfCrawlers, client);
    }
}
