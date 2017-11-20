
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import main.SMAnalyzer.CommentGroup;
import main.SMAnalyzer.CommentInstance;
import main.SMAnalyzer.CommentListAnalyzer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author Amber
 */
public class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setOpaque(true);

    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {

    protected JButton button;
    private String label;
    private boolean isPushed;
    private ArrayList<CommentGroup> groups;
    public Main_UI mainUI;
    public CommentListAnalyzer Analyzer;
    public JScrollPane jsp;

    public ButtonEditor(JCheckBox checkBox, ArrayList<CommentGroup> groups, JButton aButton,
            Main_UI mainUI, CommentListAnalyzer Analyzer, JScrollPane jsp) {
        super(checkBox);
        this.mainUI = mainUI;
        this.Analyzer = Analyzer;
        this.jsp = jsp;
        this.groups = groups;
        button = aButton;
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed && label.equals("More Info")) {

            // action handling for more info button
            JPanel dialogPanel = new JPanel();
            dialogPanel.setLayout(new GridLayout(2, 1));

            JLabel commentListLabel = new JLabel("Comment Text");

            JPanel summaryPanel = new JPanel();
            summaryPanel.setPreferredSize(new Dimension(300, 300));

            ArrayList<CommentInstance> comments = new ArrayList();
            CommentGroup selectedGroup = groups.get(this.mainUI.outputTable.getSelectedRow());
            comments = selectedGroup.getComments();

            JTextPane commentList = new JTextPane();
            commentList.setText("");
            commentList.setEditable(false);
            StyledDocument doc = commentList.getStyledDocument();

            SimpleAttributeSet sasGreen = new SimpleAttributeSet();
            sasGreen.addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(0, 128, 0));

            SimpleAttributeSet sasRed = new SimpleAttributeSet();
            sasRed.addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(178, 34, 34));

            SimpleAttributeSet sasGray = new SimpleAttributeSet();
            sasGray.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.DARK_GRAY);

            String addString = "";
            int neg = 0;
            int pos = 0;
            int net = 0;
            try {
                for (int k = 0; k < selectedGroup.getComments().size(); k++) {
                    addString = comments.get(k).getCommentRaw();
                    addString += "\nWritten at: " + comments.get(k).getCommentTime() + "\n";
                    if (comments.get(k).getPositivityLevel() < 0) {
                        doc.insertString(doc.getLength(), addString, sasRed);
                        doc.insertString(doc.getLength(), "This comment is flagged as negative.\n\n", sasRed);
                        neg++;
                    } else if (comments.get(k).getPositivityLevel() > 0) {
                        doc.insertString(doc.getLength(), addString, sasGreen);
                        doc.insertString(doc.getLength(), "This comment is flagged as positive.\n\n", sasGreen);
                        pos++;
                    } else {
                        doc.insertString(doc.getLength(), addString, sasGray);
                        doc.insertString(doc.getLength(), "This comment is flagged as neutral.\n\n", sasGray);
                        net++;
                    }
                }
                //Code to display instances of the keyword in bold
                SimpleAttributeSet sasBold = new SimpleAttributeSet();
                StyleConstants.setBold(sasBold, true);

                Pattern word = Pattern.compile(selectedGroup.getKeyword());
                Matcher match = word.matcher(doc.getText(0, doc.getLength()).toLowerCase());

                while (match.find()) {
                    System.out.println(match.group() + ", " + match.start() + ", " + match.end());
                    commentList.getStyledDocument().setCharacterAttributes(match.start(), match.end() - match.start(), sasBold, false);
                }
            } catch (BadLocationException ble) {
                System.out.println(ble);
            }
            JFreeChart graph;
            GraphInstance g = new GraphInstance();
            graph = g.Graph("Level Of Positivity", false, pos, neg, net);
            ChartPanel chart = new ChartPanel(graph);
            // graph.setHorizontalAlignment(SwingConstants.CENTER);
            chart.setPreferredSize(new Dimension(600, 300));

            JScrollPane scrollPane = new JScrollPane(commentList);
            scrollPane.setPreferredSize(new Dimension(300, 300));
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.add(commentListLabel, BorderLayout.NORTH);
            textPanel.add(scrollPane, BorderLayout.CENTER);

            String summaryString = "";
            summaryString += "Number of comments: " + comments.size();
            summaryString += "\nTotal number of words ";

            double negCount = 0;
            double neutralCount = 0;
            double posCount = 0;
            double commentTotal = comments.size();
            int posScore = 0;
            int wordTotal = 0;
            ArrayList<CommentInstance> instances = selectedGroup.getComments();
            for (CommentInstance instance : instances) {
                wordTotal += instance.getUniqueWordList().size();
                if (instance.getPositivityLevel() > 0) {
                    posCount++;
                } else if (instance.getPositivityLevel() < 0) {
                    negCount++;
                } else {
                    neutralCount++;
                }
                posScore += instance.getPositivityLevel();
            }
            summaryString += wordTotal;
            double negPercentage = round((negCount / commentTotal * 100), 2);
            double posPercentage = round((posCount / commentTotal * 100), 2);
            double neutralPercentage = round(neutralCount / commentTotal * 100, 2);
            summaryString += "\n\nNegative Percentage: " + negPercentage;
            summaryString += "\nPositive Percentage: " + posPercentage;
            summaryString += "\nNeutral Percentage: " + neutralPercentage;
            summaryString += "\n\nPosScore: " + posScore;

            JTextPane summary = new JTextPane();
            summary.setPreferredSize(new Dimension(300, 300));
            summary.setText(summaryString);
            summaryPanel.add(summary);

            JPanel topPanel = new JPanel();
            topPanel.add(textPanel);
            topPanel.add(summaryPanel);

            JPanel bottomPanel = new JPanel();
            bottomPanel.add(chart);

            dialogPanel.add(topPanel);
            dialogPanel.add(bottomPanel);

            JDialog jd = new JDialog(mainUI, "Group Details", true);
            jd.add(dialogPanel);
            jd.setLocation(650, 200);
            jd.pack();
            jd.show();
        } else if (isPushed && label.equals("Add to blacklist")) {
            try {
                Analyzer.addToBlacklist(groups.get(mainUI.outputTable.getSelectedRow()).getKeyword());
            } catch (IOException ex) {
                Logger.getLogger(ButtonEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
            Analyzer.clearArray();
            mainUI.remove(jsp);
            mainUI.analyzeAndDisplay(true);
        }
        isPushed = false;
        return new String(label);
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
