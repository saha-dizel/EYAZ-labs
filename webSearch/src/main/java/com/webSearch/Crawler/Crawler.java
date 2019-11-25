package com.webSearch.Crawler;

import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.parser.*;
import edu.uci.ics.crawler4j.url.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class Crawler extends WebCrawler {
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz))$");
    private final static Pattern TUTMATCHER = Pattern.compile("^https?:/{2}.*\\.?tut\\.by.*$");

    private RestHighLevelClient client;
    private final AtomicInteger numOfDocs;

    public Crawler(RestHighLevelClient client, AtomicInteger numOfDocs) {
        this.client = client;
        this.numOfDocs = numOfDocs;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        //here need to check sites for correctness (if we need to check them or not)
        return !FILTERS.matcher(href).matches()
                && TUTMATCHER.matcher(href).matches()
                && !href.contains("/poll/")
                && !href.contains("css");
    }

    @Override
    public void visit(Page page) {
        try {
            numOfDocs.incrementAndGet();
            URL url = new URL(page.getWebURL().getURL());
            System.out.println("URL: " + url);

            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                String text = htmlParseData.getText();
                String html = htmlParseData.getHtml();
                Set<WebURL> links = htmlParseData.getOutgoingUrls();

                System.out.println("Text length: " + text.length());
                System.out.println("Html length: " + html.length());
                System.out.println("Number of outgoing links: " + links.size());

                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("URL", url.toString());
                jsonMap.put("content", text);

                IndexRequest request = new IndexRequest("page");
                request.id(numOfDocs.toString());
                request.source(jsonMap, XContentType.JSON);


                IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
