package main;

/**
 * TODO: Add a checkbox for ignoring blacklist Add a checkbox for outputting to
 * .txt make an options jpanel output stuff to the ui add a silly easter egg
 * figure out why everything is errors Rename some stuff
 */
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Arrays;
import java.awt.datatransfer.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import javax.swing.JFileChooser;
import main.fbinterface.FBClient;
import main.humandataanalysisproject.*;

public class Main_UI extends JFrame {
    //UI vars
    // private JPanel mainPanel;
    private JPanel urlPanel;
    private JPanel optionPanel;
    private JPanel outputPanel;
    private JTextArea outputText;
    private JButton openButton;
    private JButton urlButton;
    private JButton pasteButton;
    private JButton analyzeButton;
    private JCheckBox childCommentBox;
    private JCheckBox blacklistIgnoreBox;
    private JCheckBox fileBox;
    private PrintWriter out;
    //restFB vars
    private FBClient FBClient;

    //HumanDataAnalysisProject
    private CommentListAnalyzer Analyzer;

    private JLabel urlLabel;
    private JTextField urlText;

    public Main_UI() throws IOException {
        //init
        ActionHandler ah = new ActionHandler();
        urlLabel = new JLabel("Url: ");
        urlText = new JTextField(20);
        pasteButton = new JButton("Paste");
        analyzeButton = new JButton("Analyze");
        openButton = new JButton("Open file...");
        urlButton = new JButton("Url");
        childCommentBox = new JCheckBox("Include child comments");
        blacklistIgnoreBox = new JCheckBox("Ignore blacklist");
        fileBox = new JCheckBox("Save to File");
        outputText = new JTextArea(10, 50);
        outputText.setLineWrap(true);
        outputText.setEditable(false);
        this.setLayout(new BorderLayout());

        FBClient = new FBClient();
        Analyzer = new CommentListAnalyzer();

        urlPanel = new JPanel();
        this.add(urlPanel, BorderLayout.CENTER);
        urlPanel.add(urlLabel);
        urlPanel.add(urlText);
        urlPanel.add(pasteButton);
        urlPanel.add(analyzeButton);

        optionPanel = new JPanel();
        this.add(optionPanel, BorderLayout.WEST);
        optionPanel.setLayout(new GridLayout(5, 1));
        optionPanel.add(childCommentBox);
        optionPanel.add(blacklistIgnoreBox);
        optionPanel.add(fileBox);

        outputPanel = new JPanel();
        this.add(outputPanel, BorderLayout.SOUTH);
        outputPanel.setVisible(false);
        outputPanel.add(new JScrollPane(outputText));

        openButton.addActionListener(ah);
        urlButton.addActionListener(ah);
        pasteButton.addActionListener(ah);
        analyzeButton.addActionListener(ah);

        this.setSize(500, 500);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Capstone");

    }

    private class ActionHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == pasteButton) {
                Clipboard clipboard = getToolkit().getSystemClipboard();

                Transferable clipData = clipboard.getContents(this);
                String s;
                try {
                    s = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
                } catch (Exception ex) {
                    s = ex.toString();
                }
                urlText.setText(s);
            } else if (e.getSource() == analyzeButton) {
                Analyzer.clearArray();
                FBClient.clearArray();
                String urlString = urlText.getText();

                if (urlString.equals(null) || urlString.equals("")) {
                    JOptionPane.showMessageDialog(null, "There's nothing to analyze.\nPlease paste a url.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    HashMap<String, String> stringMap = parseUrl(urlString);
                    try {
                        Boolean child = childCommentBox.isSelected();
                        Boolean blacklist = blacklistIgnoreBox.isSelected();
                        Boolean file = fileBox.isSelected();

                        if (file) {
                            JFileChooser chooser = new JFileChooser();
                            chooser.setCurrentDirectory(new File("."));
                            int result = chooser.showSaveDialog(Main_UI.this);
                            if (result == JFileChooser.APPROVE_OPTION) {
                                File f = chooser.getSelectedFile();
                                if (f.exists() == true) {
                                    int n = JOptionPane.showConfirmDialog(Main_UI.this,
                                            "This file already exists. Would you like to overwrite this file?",
                                            "Confirmation", JOptionPane.YES_NO_OPTION);
                                    if (n == JOptionPane.YES_OPTION) {
                                        try {
                                            out = new PrintWriter(new FileOutputStream(f, false));
                                            out.close();
                                        } catch (IOException ex) {
                                            JOptionPane.showMessageDialog(Main_UI.this, "File could not be opened.",
                                                    "Get a better file", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    }
                                }
                            }
                        }
                        if(stringMap.size() == 1){
                            FBClient.fetchRandomPagePost(stringMap.get("Page Name"), child);
                        }
                        else if(stringMap.size() == 3){
                            FBClient.fetchSpecificPagePost(stringMap.get("Page Name"), stringMap.get("Post Id"), child);
                        }

                        Analyzer.setComments(FBClient.getPostArray());
                        Analyzer.groupComments();
                        //outputPanel.setVisible(true);
                    } catch (NullPointerException npe) {
                        System.out.println(npe);
                    } catch (Exception ex) {
                        System.out.println(ex);
                        JOptionPane.showMessageDialog(null, "Something Broke", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(Main_UI.this, "Uh....",
                        "I don't know how you got here, but you need to leave", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public HashMap<String, String> parseUrl(String s) {
        if (!s.contains("facebook.com")) {
            JOptionPane.showMessageDialog(Main_UI.this, "Uh....",
                    "Url not recognized", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        int last = s.lastIndexOf("facebook.com/");
        int fbLength = "facebook.com/".length();

        String sub = s.substring(last + fbLength, s.length());
        String[] array = sub.split("/");
        HashMap<String, String> map = new HashMap<String, String>();
        if (array.length == 1) {
            map.put("Page Name", array[0]);
        } else if (array.length == 3) {
            map.put("Page Name", array[0]);
            map.put("Post Type", array[1]);
            map.put("Post Id", array[2]);
            if (array[2].equals("photos")) {
            JOptionPane.showMessageDialog(Main_UI.this, "Uh....",
                    "We cannot parse photo posts yet", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        } else {
            JOptionPane.showMessageDialog(Main_UI.this, "Uh....",
                    "Url not recognized", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        
        return map;
    }

    public static void main(String args[]) throws IOException {
        new Main_UI();
    }

}
