package com.webSearch.View;

import javax.swing.*;
import java.awt.*;

public class View {
    private JFrame frame;
    private JPanel panel;
    private JButton button;
    private JTextField textField;
    private JLabel label;

    public View() {
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

        frame.pack();
        frame.setVisible(true);
    }
}
