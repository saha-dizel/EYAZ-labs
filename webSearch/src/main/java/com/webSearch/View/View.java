package com.webSearch.View;

import com.webSearch.Crawler.Crawler;
import edu.uci.ics.crawler4j.crawler.CrawlController;

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

    public View(CrawlController controller,
                CrawlController.WebCrawlerFactory<Crawler> factory,
                int numberOfCrawlers) {
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
            String query = textField.getText();
            System.out.println(query);
            //TODO process the query and then use it to start the engine
            Thread crawlerInitThread = new Thread(() -> controller.start(factory, numberOfCrawlers));
            crawlerInitThread.start();
        });

        frame.pack();
        frame.setVisible(true);
    }
}
