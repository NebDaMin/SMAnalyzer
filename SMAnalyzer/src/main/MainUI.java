package main;

import main.SMAnalyzer.CommentGroup;
import main.SMAnalyzer.CommentListAnalyzer;
import main.SMAnalyzer.CommentInstance;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import main.sminterfaces.FBClient;
import main.sminterfaces.YTClient;
import main.sminterfaces.NormalizedComment;
import main.sminterfaces.RedditClient;
import main.sminterfaces.TwitterClient;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class MainUI extends JFrame {

    //UI vars
    private JPanel MainPanel;
    private JPanel ChartPanel;
    private JTextArea PostText;
    private ChartPanel MainChart;
    private JMenuBar Menu;
    private JMenu Options, File;
    private JCheckBoxMenuItem ChildCommentBox, BlacklistIgnoreBox, SaveFile;
    private JMenuItem LoadFile, DictionaryAdd, ExportToFile;
    public JTable OutputTable;
    private JButton UrlButton, PasteButton, AnalyzeButton, ClearButton;
    private JScrollPane ScrollPane;
    private JScrollPane Scroll;
    private JFileChooser FileChooser;
    private GridBagConstraints LayoutConstraints;
    public final int BAR_CHART = 0;
    public final int PIE_CHART = 1;
    //restFB vars
    private FBClient FBClient;
    private YTClient YTClient;
    private RedditClient RedditClient;
    private TwitterClient TwitterClient;
    private Parse Parse;

    //HumanDataAnalysisProject
    private CommentListAnalyzer Analyzer;

    private JLabel UrlLabel;
    private JTextField UrlText;

    public MainUI() throws IOException {
        //init
        ActionHandler ah = new ActionHandler();
        FileChooser = new JFileChooser("savedfiles");
        UrlLabel = new JLabel("Url: ");
        UrlText = new JTextField(20);
        PasteButton = new JButton("Paste");
        AnalyzeButton = new JButton("Analyze");
        ClearButton = new JButton("Clear");
        UrlButton = new JButton("Url");
        OutputTable = new JTable();
        ScrollPane = new JScrollPane(OutputTable);
        MainPanel = new JPanel();

        //menu init
        Menu = new JMenuBar();

        File = new JMenu("File");
        File.setMnemonic('F');
        LoadFile = new JMenuItem("Load File...");
        LoadFile.setMnemonic('L');
        File.add(LoadFile);
        ExportToFile = new JMenuItem("Export to file");
        ExportToFile.setEnabled(false);
        File.add(ExportToFile);
        SaveFile = new JCheckBoxMenuItem("Save to File");
        File.add(SaveFile);
        Menu.add(File);

        Options = new JMenu("Options");
        Options.setMnemonic('O');
        ChildCommentBox = new JCheckBoxMenuItem("Child Comments");
        BlacklistIgnoreBox = new JCheckBoxMenuItem("Ignore BlackList");
        DictionaryAdd = new JMenuItem("Add to dictionary");
        Options.add(ChildCommentBox);
        Options.add(BlacklistIgnoreBox);
        Options.add(DictionaryAdd);
        Menu.add(Options);

        this.setLayout(new GridBagLayout());
        LayoutConstraints = new GridBagConstraints();
        LayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        LayoutConstraints.gridy = 0;
        this.add(Menu, LayoutConstraints);

        FBClient = new FBClient();
        YTClient = new YTClient();
        RedditClient = new RedditClient();
//        TwitterClient = new TwitterClient();
        
        Analyzer = new CommentListAnalyzer();

        OutputTable.setVisible(false);
        ClearButton.setVisible(true);
        OutputTable.setPreferredScrollableViewportSize(new Dimension(400, 150));

        GroupLayout layout = new GroupLayout(MainPanel);
        MainPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(UrlLabel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(UrlText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(PasteButton)
                        .addComponent(AnalyzeButton)
                        .addComponent(ClearButton))
        );
        layout.linkSize(SwingConstants.HORIZONTAL, PasteButton, AnalyzeButton, ClearButton);

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(UrlLabel)
                        .addComponent(UrlText)
                        .addComponent(PasteButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(AnalyzeButton))
                .addComponent(ClearButton)
        );
        LayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        LayoutConstraints.gridy = 1;
        this.add(MainPanel, LayoutConstraints);
        
        ChartPanel = new JPanel();
        PostText = new JTextArea();
        Scroll = new JScrollPane(PostText);

        UrlButton.addActionListener(ah);
        PasteButton.addActionListener(ah);
        AnalyzeButton.addActionListener(ah);
        ClearButton.addActionListener(ah);
        LoadFile.addActionListener(ah);
        DictionaryAdd.addActionListener(ah);
        ExportToFile.addActionListener(ah);

        this.pack();
        this.setLocation(700, 300);
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("SMAnalyzer");
    }

    private class ActionHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == PasteButton) {
                Clipboard clipboard = getToolkit().getSystemClipboard();

                Transferable clipData = clipboard.getContents(this);
                String s;
                try {
                    s = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
                } catch (Exception ex) {
                    s = ex.toString();
                }
                UrlText.setText(s);
            } else if (e.getSource() == ClearButton) {
                clearUI();
                UrlText.setText("");
            } else if (e.getSource() == DictionaryAdd) {
                JFrame inputFrame = new JFrame("Add to dictionary");
                String wordToAdd = JOptionPane.showInputDialog(inputFrame, "Please type the word to be added below");
                if (wordToAdd != null) {
                    Analyzer.addToDictionary(wordToAdd);
                }
            } else if (e.getSource() == ExportToFile) {
                ArrayList<CommentInstance> comments = Analyzer.getComments();
                int returnVal = FileChooser.showSaveDialog(MainUI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        FileWriter fw = new FileWriter(FileChooser.getSelectedFile() + ".txt");
                        fw.write(comments.get(0).getMedia() + "`");
						fw.write(Analyzer.getOriginalPost() + "`");
                        for (CommentInstance c : comments) {
                            fw.write(c.getID() + "`");
                            fw.write(c.getCommentRaw() + "`");
                            fw.write(c.getCommentTime() + "`");
                            fw.write(c.getShares() + "|");
                        }
                        fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else if (e.getSource() == LoadFile) {
                ArrayList<NormalizedComment> comments = parseFile();
                Boolean isBlacklistEnabled = !BlacklistIgnoreBox.isSelected();
                try {
                    Analyzer.setComments(comments);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                Analyzer.analyze(isBlacklistEnabled);
                displayData();
            } else if (e.getSource() == AnalyzeButton) {
                Analyzer.clearArray();
                FBClient.clearArray();
                YTClient.clearArray();
                RedditClient.clearArray();
  //              TwitterClient.clearArray();
                clearUI();
                
                String urlString = UrlText.getText();
                MainUI.this.MainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                if (urlString.equals(null) || urlString.equals("")) {
                    JOptionPane.showMessageDialog(null, "There's nothing to analyze.\nPlease paste a url.", "Did you really hit analyze without puttng anything in?", JOptionPane.ERROR_MESSAGE);
                } else {
                    Parse = new Parse();
                    HashMap<String, String> stringMap = Parse.parseUrl(urlString);
                    Boolean child = ChildCommentBox.isSelected();
                    Boolean isBlacklistEnabled = !BlacklistIgnoreBox.isSelected();
                    ExportToFile.setEnabled(true);
                    LoadFile.setEnabled(false);
                    Boolean file = SaveFile.isSelected();

                    if (Parse.getSite().equals("facebook")) {
                        if (stringMap.size() == 1) {
                            FBClient.fetchRandomPagePost(stringMap.get("Page Name"), child);
                        } else if (stringMap.size() == 3) {
                            FBClient.fetchSpecificPagePost(stringMap.get("Page Name"), stringMap.get("Post Id"), child);
                        }
                    } else if (Parse.getSite().equals("youtube")) {
                        YTClient.fetchComments(stringMap.get("Page Type"), stringMap.get("Id"), child);
                    } else if (Parse.getSite().equals("reddit")) {
                        RedditClient.fetchComments(stringMap.get("Post Id"));
                    }else if (Parse.getSite().equals("twitter")) {
//                        TwitterClient.fetchComments(stringMap.get("Post Id"));
                    }
                    try {
                        if (Parse.getSite().equals("facebook")) {
                            if (!FBClient.getPostArray().isEmpty()) {
                                Analyzer.setComments(FBClient.getPostArray());
                                Analyzer.setOriginalPost(FBClient.getPostArray().get(0).getMessage());
                            }
                        } else if (Parse.getSite().equals("youtube")) {
                            if (!YTClient.getPostArray().isEmpty()) {
                                Analyzer.setComments(YTClient.getPostArray());
                                Analyzer.setOriginalPost(YTClient.getPostArray().get(0).getMessage());
                            }
                        } else if (Parse.getSite().equals("reddit")) {
                            if (!RedditClient.getPostArray().isEmpty()) {
                                Analyzer.setComments(RedditClient.getPostArray());
                                Analyzer.setOriginalPost(RedditClient.getPostArray().get(0).getMessage());
                            }
                        } else {
                            JOptionPane.showMessageDialog(MainUI.this, "Uh....",
                                    "I don't know how you got here, but you need to leave", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        System.out.println(ex);
                        JOptionPane.showMessageDialog(null, ex, "Something Broke", JOptionPane.ERROR_MESSAGE);
                    }
                    Analyzer.analyze(isBlacklistEnabled);
                    displayData();
                }
            } else {
                JOptionPane.showMessageDialog(MainUI.this, "Uh....",
                        "I don't know how you got here, but you need to leave", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    void displayData() {
        try {
            //get group output data
            ArrayList<CommentGroup> groups = Analyzer.groupComments();
            //format into arrays for JTable constructor
            Object[][] tableData = new Object[groups.size()][4];
            Object[] columnNames = {"Group Keyword", "Number of Comments", "", " "};
            int row = 0;
            for (CommentGroup g : groups) {
                tableData[row][0] = g.getKeyword();
                tableData[row][1] = g.getComments().size();
                tableData[row][2] = "More Info";
                tableData[row][3] = "Add to blacklist";
                row++;
            }
            // add a chart
            JFreeChart chart;
            ChartInstance chartInstance = new ChartInstance();
            int[] alignment = Analyzer.totalAlignment();
            chart = chartInstance.Chart("Total Level Of Positivity", alignment[1],alignment[0], alignment[2]);
            MainChart = new ChartPanel(chart);
            PostText.setText(Analyzer.getOriginalPost());

            //postPanel.add(new JScrollPane(postText));
            PostText.setLineWrap(true);
            PostText.setWrapStyleWord(true);
            PostText.setEditable(false);
            Scroll.setPreferredSize(new Dimension(500, 100));
            LayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            LayoutConstraints.gridx = 1;
            LayoutConstraints.gridy = 0;
            LayoutConstraints.gridheight = 4;
            this.add(ChartPanel, LayoutConstraints);
            
            LayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            LayoutConstraints.gridx = 0;
            LayoutConstraints.gridy = 2;
            LayoutConstraints.gridheight = 1;
            ChartPanel.add(MainChart);
            this.add(Scroll, LayoutConstraints);
            
            //create and populate table
            OutputTable = new JTable(tableData, columnNames);
            JButton infoButton = new JButton("More Info");
            JButton blacklistButton = new JButton("Add to Blacklist");
            OutputTable.getColumn("").setCellRenderer(new ButtonRenderer());
            OutputTable.getColumn("").setCellEditor(
                    new ButtonEditor(new JCheckBox(), groups, infoButton, this, Analyzer));
            OutputTable.getColumn(" ").setCellRenderer(new ButtonRenderer());
            OutputTable.getColumn(" ").setCellEditor(
                    new ButtonEditor(new JCheckBox(), groups, blacklistButton, this, Analyzer));
            ScrollPane = new JScrollPane(OutputTable);
	    ScrollPane.setPreferredSize(new Dimension(500, 200));
            LayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            LayoutConstraints.gridx = 0;
            LayoutConstraints.gridy = 3;
            LayoutConstraints.gridheight = 1;
            MainUI.this.add(ScrollPane, LayoutConstraints);
            MainUI.this.pack();
            MainUI.this.MainPanel.setCursor(null);
            OutputTable.setVisible(true);
            MainUI.this.repaint();
            MainUI.this.setLocationRelativeTo(null);
            
        }catch (Exception ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, ex, "Something Broke", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addToBlacklist(String wordToAdd) {
        try {
            Analyzer.addToBlacklist(wordToAdd);

        } catch (IOException ex) {
            Logger.getLogger(ButtonEditor.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        Analyzer.analyze(true);
        clearUI();
        displayData();
    }

    public void clearUI() {
        OutputTable.setVisible(false);
        ExportToFile.setEnabled(false);
        LoadFile.setEnabled(true);
        MainUI.this.remove(ScrollPane);
        MainUI.this.remove(Scroll);
        PostText.setText("");
        ChartPanel.removeAll();
        MainUI.this.remove(ChartPanel);   
        MainUI.this.repaint();
        MainUI.this.pack();
    }

    private ArrayList<NormalizedComment> parseFile() {
        ArrayList<NormalizedComment> comments = new ArrayList();
        ArrayList<String> idList = new ArrayList();
        ArrayList<String> textList = new ArrayList();
        ArrayList<String> timeList = new ArrayList();
        ArrayList<String> shareList = new ArrayList();
        int returnVal = FileChooser.showOpenDialog(MainUI.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String contents = new String(Files.readAllBytes(Paths.get(FileChooser.getSelectedFile().getPath())));
                char currentChar;
                String media = "";
                String originalPost = "";
                String currentId = "";
                String currentText = "";
                String currentTime = "";
                String currentShares = "";
                int currentState = 0;
                for (int k = 0; k < contents.length(); k++) {
                    currentChar = contents.charAt(k);
                    if (currentChar == '`') {
                        if (currentState == 2) {
                            idList.add(currentId);
                            currentId = "";
                        } else if (currentState == 3) {
                            textList.add(currentText);
                            currentText = "";
                        } else if (currentState == 4) {
                            timeList.add(currentTime);
                            currentTime = "";
                        }
                        currentState++;
                    } else if (currentChar == '|') {
                        shareList.add(currentShares);
                        currentShares = "";
                        currentState = 2;
                    } else {
                        if (currentState == 0) {
                            media += currentChar;
                        } else if (currentState == 1) {
                            originalPost += currentChar;
                        } else if (currentState == 2) {
                            currentId += currentChar;
                        } else if (currentState == 3) {
                            currentText += currentChar;
                        } else if (currentState == 4) {
                            currentTime += currentChar;
                        } else if (currentState == 5) {
                            currentShares += currentChar;
                        }
                    }
                }

                for (int k = 0; k < idList.size(); k++) {
                    NormalizedComment normCom = new NormalizedComment();
                    normCom.setMedia(media);
                    normCom.setId(idList.get(k));
                    normCom.setMessage(textList.get(k));
                    normCom.setTime(timeList.get(k));
                    normCom.setShares(shareList.get(k));
                    comments.add(normCom);
                }
                Analyzer.setOriginalPost(originalPost);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return comments;
    }
	
    public static void main(String args[]) throws IOException {
       
        try {
           UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
	} catch(Exception e) { }
         new MainUI();
    }
}