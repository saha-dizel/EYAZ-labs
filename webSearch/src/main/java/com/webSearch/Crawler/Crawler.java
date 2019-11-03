package com.webSearch.Crawler;

import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.parser.*;
import edu.uci.ics.crawler4j.url.*;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Set;
import java.util.regex.Pattern;

public class Crawler extends WebCrawler {
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz))$");
    private final static Pattern TUTMATCHER = Pattern.compile("^https?:/{2}.*\\.?tut\\.by.*$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        //System.out.println(href);
        //here need to check sites for correctness (if we need to check them or not)
        return !FILTERS.matcher(href).matches()
                && TUTMATCHER.matcher(href).matches()
                && !href.contains("/poll/")
                && !href.contains("css");
    }

    @Override
    public void visit(Page page) {
        try {
            URL url = new URL(page.getWebURL().getURL());
            System.out.println("URL: " + page.getWebURL().getURL());

            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                String text = htmlParseData.getText();
                String html = htmlParseData.getHtml();
                Set<WebURL> links = htmlParseData.getOutgoingUrls();

                System.out.println("Text length: " + text.length());
                System.out.println("Html length: " + html.length());
                System.out.println("Number of outgoing links: " + links.size());

                //TODO: rework this into elsaticsearch api (maybe delete HttpURLConnection after this)
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("PUT");

                con.setDoOutput(true);

                OutputStreamWriter out = new OutputStreamWriter(
                        con.getOutputStream()
                );

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
