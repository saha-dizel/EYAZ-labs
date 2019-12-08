package com.webSearch.View;

import com.webSearch.Crawler.Crawler;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Map;

public class View {
    private JFrame frame;
    private JPanel panel;
    private JButton button;
    private JTextField textField;
    private JLabel label;
    private RestHighLevelClient client;
    private JFrame results;
    private JTable table;
    private JScrollPane scrollPane;

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
                HighlightBuilder highlight = new HighlightBuilder();
                HighlightBuilder.Field highlightField = new HighlightBuilder.Field("content");
                highlight.field(highlightField);
                sourceBuilder.highlighter(highlight);
                sourceBuilder.query(matchQueryBuilder);
                searchRequest.source(sourceBuilder);

                SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

                results = new JFrame("Results");
                String[] columnNames = {"URL", "Highlight", "Score"};
                LinkedList<Object[]> data = new LinkedList<>();

                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    String url = (String) hit.getSourceAsMap().get("URL");

                    //highlight get
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    HighlightField highlightMatch = highlightFields.get("content");
                    Text[] fragments = highlightMatch.fragments();
                    String fragmentString = fragments[fragments.length - 1].string();

                    data.add(new Object[]{url, fragmentString, hit.getScore()});
                }

                scrollPane = new JScrollPane();
                table = new JTable(data.toArray(new Object[data.size()][3]), columnNames);
                scrollPane = new JScrollPane(table);
                table.setFillsViewportHeight(true);

                results.add(scrollPane);
                results.pack();
                results.setVisible(true);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}
