package com.webSearch.View;

import com.webSearch.Crawler.Crawler;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View {
    private JFrame frame;
    private JPanel panel;
    private JButton button;
    private JTextField textField;
    private JLabel label;
    private RestHighLevelClient client;

    public View(CrawlController controller,
                CrawlController.WebCrawlerFactory<Crawler> factory,
                int numberOfCrawlers,
                RestHighLevelClient client) {
        this.client = client;

        frame = new JFrame();
        panel = new JPanel();

        frame.setLocationRelativeTo(null);

        frame.add(panel);

        button = new JButton("Search");
        textField = new JTextField();
        label = new JLabel("Enter text to search:");
        textField.setPreferredSize(new Dimension(300, 27));

        panel.add(label);
        panel.add(textField);
        panel.add(button);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        button.addActionListener(e -> {
            try {
                String query = textField.getText();
                System.out.println(query);
                //TODO process the query and then use it to start the engine
                Thread crawlerInitThread = new Thread(() -> controller.start(factory, numberOfCrawlers));
                crawlerInitThread.start();
                crawlerInitThread.join(0);

                System.out.println("Join ended");

                SearchRequest searchRequest = new SearchRequest("page");
                QueryBuilder matchQueryBuilder = QueryBuilders.queryStringQuery(textField.getText());
                SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
                sourceBuilder.query(matchQueryBuilder);
                searchRequest.source(sourceBuilder);

                SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    System.out.println(hit.getIndex());
                    System.out.println(hit.getScore());
                    String url = (String) hit.getSourceAsMap().get("URL");
                    System.out.println(url);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}
