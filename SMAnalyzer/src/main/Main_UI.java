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
import javax.swing.JFileChooser;
import main.sminterfaces.FBClient;
import main.sminterfaces.YTClient;
import main.sminterfaces.NormalizedComment;
import main.sminterfaces.RedditClient;
import main.sminterfaces.TwitterClient;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class Main_UI extends JFrame {

    //UI vars
    private JPanel mainPanel;
    private JPanel chartPanel;
    private JPanel postPanel;
    private ChartPanel mainChart;
    private JMenuBar menu;
    private JMenu options, file;
    private JCheckBoxMenuItem childCommentBox, blacklistIgnoreBox, saveFile;
    private JMenuItem loadFile, dictionaryAdd, exportToFile;
    JTable outputTable;
    private JButton urlButton, pasteButton, analyzeButton, clearButton;
    private JScrollPane jsp;
    private JFileChooser jfc;
    private GridBagConstraints layoutConstraints;
    public final int BAR_CHART = 0;
    public final int PIE_CHART = 1;
    //restFB vars
    private FBClient FBClient;
    private YTClient YTClient;
    private RedditClient RedditClient;
    private TwitterClient TwitterClient;
    private Parse parse;

    //HumanDataAnalysisProject
    private CommentListAnalyzer Analyzer;

    private JLabel urlLabel;
    private JTextField urlText;

    public Main_UI() throws IOException {
        //init
        ActionHandler ah = new ActionHandler();
        jfc = new JFileChooser("savedfiles");
        urlLabel = new JLabel("Url: ");
        urlText = new JTextField(20);
        pasteButton = new JButton("Paste");
        analyzeButton = new JButton("Analyze");
        clearButton = new JButton("Clear");
        urlButton = new JButton("Url");
        outputTable = new JTable();
        jsp = new JScrollPane(outputTable);
        mainPanel = new JPanel();
        

        //menu init
        menu = new JMenuBar();

        file = new JMenu("File");
        file.setMnemonic('F');
        loadFile = new JMenuItem("Load File...");
        loadFile.setMnemonic('L');
        file.add(loadFile);
        exportToFile = new JMenuItem("Export to file");
        exportToFile.setEnabled(false);
        file.add(exportToFile);
        saveFile = new JCheckBoxMenuItem("Save to File");
        file.add(saveFile);
        menu.add(file);

        options = new JMenu("Options");
        options.setMnemonic('O');
        childCommentBox = new JCheckBoxMenuItem("Child Comments");
        blacklistIgnoreBox = new JCheckBoxMenuItem("Ignore BlackList");
        dictionaryAdd = new JMenuItem("Add to dictionary");
        options.add(childCommentBox);
        options.add(blacklistIgnoreBox);
        options.add(dictionaryAdd);
        menu.add(options);

        this.setLayout(new GridBagLayout());
        layoutConstraints = new GridBagConstraints();
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.gridy = 0;
        this.add(menu, layoutConstraints);

        FBClient = new FBClient();
        YTClient = new YTClient();
        RedditClient = new RedditClient();
        //TwitterClient = new TwitterClient();
        
        Analyzer = new CommentListAnalyzer();

        outputTable.setVisible(false);
        clearButton.setVisible(true);
        outputTable.setPreferredScrollableViewportSize(new Dimension(400, 150));

        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(urlLabel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(urlText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(pasteButton)
                        .addComponent(analyzeButton)
                        .addComponent(clearButton))
        );
        layout.linkSize(SwingConstants.HORIZONTAL, pasteButton, analyzeButton, clearButton);

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(urlLabel)
                        .addComponent(urlText)
                        .addComponent(pasteButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(analyzeButton))
                .addComponent(clearButton)
        );
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.gridy = 1;
        this.add(mainPanel, layoutConstraints);
        
        chartPanel = new JPanel();
        postPanel = new JPanel();

        urlButton.addActionListener(ah);
        pasteButton.addActionListener(ah);
        analyzeButton.addActionListener(ah);
        clearButton.addActionListener(ah);
        loadFile.addActionListener(ah);
        dictionaryAdd.addActionListener(ah);
        exportToFile.addActionListener(ah);

        this.pack();
        this.setLocation(700, 300);
        this.setResizable(true);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("SMAnalyzer");
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
            } else if (e.getSource() == clearButton) {
                ClearUI();
                urlText.setText("");
            } else if (e.getSource() == dictionaryAdd) {
                JFrame inputFrame = new JFrame("Add to dictionary");
                String wordToAdd = JOptionPane.showInputDialog(inputFrame, "Please type the word to be added below");
                if (wordToAdd != null) {
                    Analyzer.addToDictionary(wordToAdd);
                }
            } else if (e.getSource() == exportToFile) {
                ArrayList<CommentInstance> comments = Analyzer.getComments();
                int returnVal = jfc.showSaveDialog(Main_UI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        FileWriter fw = new FileWriter(jfc.getSelectedFile() + ".txt");
                        fw.write(comments.get(0).getMedia() + "`");
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
            } else if (e.getSource() == loadFile) {
                ArrayList<NormalizedComment> comments = new ArrayList();
                ArrayList<String> idList = new ArrayList();
                ArrayList<String> textList = new ArrayList();
                ArrayList<String> timeList = new ArrayList();
                ArrayList<String> shareList = new ArrayList();
                int returnVal = jfc.showOpenDialog(Main_UI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        String contents = new String(Files.readAllBytes(Paths.get(jfc.getSelectedFile().getPath())));
                        char currentChar;
                        String media = "";
                        String currentId = "";
                        String currentText = "";
                        String currentTime = "";
                        String currentShares = "";
                        int currentState = 0;
                        for (int k = 0; k < contents.length(); k++) {
                            currentChar = contents.charAt(k);
                            if (currentChar == '`') {
                                if (currentState == 1) {
                                    idList.add(currentId);
                                    currentId = "";
                                } else if (currentState == 2) {
                                    textList.add(currentText);
                                    currentText = "";
                                } else if (currentState == 3) {
                                    timeList.add(currentTime);
                                    currentTime = "";
                                }
                                currentState++;
                            } else if (currentChar == '|') {
                                shareList.add(currentShares);
                                currentShares = "";
                                currentState = 1;
                            } else {
                                if (currentState == 0) {
                                    media += currentChar;
                                } else if (currentState == 1) {
                                    currentId += currentChar;
                                } else if (currentState == 2) {
                                    currentText += currentChar;
                                } else if (currentState == 3) {
                                    currentTime += currentChar;
                                } else if (currentState == 4) {
                                    currentShares += currentChar;
                                }
                            }
                        }
                        for (int j = 0; j < idList.size(); j++) {
                            //make JSON objects
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else if (e.getSource() == analyzeButton) {
                Analyzer.clearArray();
                FBClient.clearArray();
                YTClient.clearArray();
                RedditClient.clearArray();
                //TwitterClient.clearArray();
                ClearUI();
                
                String urlString = urlText.getText();
                Main_UI.this.mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                if (urlString.equals(null) || urlString.equals("")) {
                    JOptionPane.showMessageDialog(null, "There's nothing to analyze.\nPlease paste a url.", "Did you really hit analyze without puttng anything in?", JOptionPane.ERROR_MESSAGE);
                } else {
                    parse = new Parse();
                    HashMap<String, String> stringMap = parse.parseUrl(urlString);
                    Boolean child = childCommentBox.isSelected();
                    Boolean isBlacklistEnabled = !blacklistIgnoreBox.isSelected();
                    exportToFile.setEnabled(true);
                    loadFile.setEnabled(false);
                    Boolean file = saveFile.isSelected();

                    if (parse.getSite().equals("facebook")) {
                        if (stringMap.size() == 1) {
                            FBClient.fetchRandomPagePost(stringMap.get("Page Name"), child, file);
                        } else if (stringMap.size() == 3) {
                            FBClient.fetchSpecificPagePost(stringMap.get("Page Name"), stringMap.get("Post Id"), child, file);
                        }
                    } else if (parse.getSite().equals("youtube")) {
                        YTClient.fetchComments(stringMap.get("Page Type"), stringMap.get("Id"));
                    } else if (parse.getSite().equals("reddit")) {
                        RedditClient.fetchComments(stringMap.get("Post Id"));
                    }else if (parse.getSite().equals("twitter")) {
                        //TwitterClient.fetchComments(stringMap.get("Post Id"));
                    }
                    
                    //action handling moved down to method for reuse
                    analyzeAndDisplay(isBlacklistEnabled);
                }
            } else {
                JOptionPane.showMessageDialog(Main_UI.this, "Uh....",
                        "I don't know how you got here, but you need to leave", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    void analyzeAndDisplay(Boolean isBlacklistEnabled) {
        try {
            if (parse.getSite().equals("facebook")) {
                if (!FBClient.getPostArray().isEmpty()) {
                    Analyzer.setComments(FBClient.getPostArray(), isBlacklistEnabled);
                }
            } else if (parse.getSite().equals("youtube")) {
                if (!YTClient.getPostArray().isEmpty()) {
                    Analyzer.setComments(YTClient.getPostArray(), isBlacklistEnabled);
                }
            } else if (parse.getSite().equals("reddit")) {
                if (!RedditClient.getPostArray().isEmpty()) {
                    Analyzer.setComments(RedditClient.getPostArray(), isBlacklistEnabled);
                }
            } else {
                JOptionPane.showMessageDialog(Main_UI.this, "Uh....",
                        "I don't know how you got here, but you need to leave", JOptionPane.INFORMATION_MESSAGE);
            }
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
            // add a graph
            JFreeChart graph;
            GraphInstance g = new GraphInstance();
            int[] alignment = Analyzer.totalAlignment();
            graph = g.Graph("Total Level Of Positivity", false, alignment[1],alignment[0], alignment[2]);
            mainChart = new ChartPanel(graph);
            
            postPanel = new JPanel();
            JLabel postText = new JLabel();
            String text = "No title";
            if (parse.getSite().equals("facebook")) {
                text = FBClient.getPostArray().get(0).getMessage();
            } else if (parse.getSite().equals("youtube")) {
                text = YTClient.getPostArray().get(0).getMessage();
            } else if (parse.getSite().equals("reddit")) {
                text = RedditClient.getPostArray().get(0).getMessage();
            }
            postText.setText(text);
            postPanel.add(postText);
            layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            layoutConstraints.gridx = 1;
            layoutConstraints.gridy = 0;
            this.add(postPanel, layoutConstraints);
            
            layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            layoutConstraints.gridx = 1;
            layoutConstraints.gridy = 1;
            layoutConstraints.gridheight = 2;
            chartPanel.add(mainChart);
            this.add(chartPanel, layoutConstraints);
            
            //create and populate table
            outputTable = new JTable(tableData, columnNames);
            JButton infoButton = new JButton("More Info");
            JButton blacklistButton = new JButton("Add to Blacklist");
            outputTable.getColumn("").setCellRenderer(new ButtonRenderer());
            outputTable.getColumn("").setCellEditor(
                    new ButtonEditor(new JCheckBox(), groups, infoButton, this, Analyzer, jsp));
            outputTable.getColumn(" ").setCellRenderer(new ButtonRenderer());
            outputTable.getColumn(" ").setCellEditor(
                    new ButtonEditor(new JCheckBox(), groups, blacklistButton, this, Analyzer, jsp));
            jsp = new JScrollPane(outputTable);
            Dimension d = outputTable.getPreferredSize();
            jsp.setPreferredSize(new Dimension(500, 200));
            layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            layoutConstraints.gridx = 0;
            layoutConstraints.gridy = 2;
            layoutConstraints.gridheight = 1;
            Main_UI.this.add(jsp, layoutConstraints);
            Main_UI.this.pack();
            Main_UI.this.mainPanel.setCursor(null);
            outputTable.setVisible(true);
            Main_UI.this.repaint();
            clearButton.setVisible(true);
            Main_UI.this.setLocationRelativeTo(null);
            
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            JOptionPane.showMessageDialog(Main_UI.this, "Your array can't count that high",
                    "You pushed it too hard", JOptionPane.INFORMATION_MESSAGE);
            System.out.println(aioobe);
        } catch (IndexOutOfBoundsException ioobe) {
            //this one I've seen but shouldn't ever happen if the code is working
            JOptionPane.showMessageDialog(Main_UI.this, "Somewhere in the universe, an index is out of bounds",
                    "Wow you broke it great job", JOptionPane.INFORMATION_MESSAGE);
            System.out.println(ioobe);
        } catch (IOException ioe) {
            //this thing has to be thrown for the analyzer code
            JOptionPane.showMessageDialog(Main_UI.this, "You tried to access a file and it didn't work",
                    "Your files suck", JOptionPane.INFORMATION_MESSAGE);
            System.out.println(ioe);
        } catch (NullPointerException npe) {
            //the facebook stuff usually gives this error if bad happens
            System.out.println(npe);
        } catch (Exception ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, "Something Broke", "You broke it so bad that I don't even know what broke", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void ClearUI() {
        outputTable.setVisible(false);
        exportToFile.setEnabled(false);
        loadFile.setEnabled(true);
        Main_UI.this.remove(jsp);
        Main_UI.this.remove(postPanel);
        chartPanel.removeAll();
        Main_UI.this.remove(chartPanel);   
        Main_UI.this.repaint();
        Main_UI.this.pack();
    }

    public static void main(String args[]) throws IOException {
        new Main_UI();
    }
}
