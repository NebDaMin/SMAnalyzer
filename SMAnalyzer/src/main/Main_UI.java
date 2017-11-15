package main;

/**
 * TODO: put giraffes in own class put the table in own class and turn them
 * destroy Matteo for consistently de-formatting my todo list make giraffes
 * modular aight imports don't have to be that big you guys jesus clean all the
 * things sweep everything under the rug put the rug in the closet burn the
 * closet SMANALYZE SMadd the SMeaster SMegg SMmore SMaction SMhandling
 * SMprovide SMadditional SMsupport to SMteamates SMstart the SMrobot
 * SMsingularity SMcomplete SManalysis
 */
import main.SMAnalyzer.CommentGroup;
import main.SMAnalyzer.CommentListAnalyzer;
import main.SMAnalyzer.CommentInstance;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import main.sminterfaces.FBClient;
import main.sminterfaces.YTClient;
import main.sminterfaces.TwitterClient;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class Main_UI extends JFrame {

    //UI vars
    private JPanel mainPanel;
    private JMenuBar menu;
    private JMenu options, file, graphs;
    private JCheckBoxMenuItem childCommentBox, blacklistIgnoreBox, saveFile;
    private JRadioButtonMenuItem /*barGraph,*/ pieGraph, threeDPieGraph;
    private ButtonGroup graphGroups;
    private JMenuItem loadFile, dictionaryAdd, exportToFile;
    JTable outputTable;
    private JButton openButton, urlButton, pasteButton, analyzeButton, clearButton;
    private PrintWriter out;
    private JScrollPane jsp;
    private JFileChooser jfc;
    private GridBagConstraints layoutConstraints;
    public final int BAR_CHART = 0;
    public final int PIE_CHART = 1;
    //restFB vars
    private FBClient FBClient;
    private YTClient YTClient;
    private TwitterClient TwitterClient;
    private String Site;

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
        //openButton = new JButton("Open file...");
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

        graphs = new JMenu("Graphs");
        graphs.setMnemonic('G');
        //barGraph = new JRadioButtonMenuItem("Bar Graph");
        pieGraph = new JRadioButtonMenuItem("Pie Graph");
        threeDPieGraph = new JRadioButtonMenuItem("3D Pie Graph");
        graphGroups = new ButtonGroup();
        //graphGroups.add(barGraph);
        graphGroups.add(pieGraph);
        graphGroups.add(threeDPieGraph);
        pieGraph.setSelected(true);
        //graphs.add(barGraph);
        graphs.add(pieGraph);
        graphs.add(threeDPieGraph);
        menu.add(graphs);

        this.setLayout(new GridBagLayout());
        layoutConstraints = new GridBagConstraints();
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.gridy = 0;
        this.add(menu, layoutConstraints);

        FBClient = new FBClient();
        YTClient = new YTClient();
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
                        for (CommentInstance c : comments) {
                            fw.write(c.getCommentRaw());
                            fw.write(System.lineSeparator());
                        }
                        fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else if (e.getSource() == loadFile) {
                ArrayList<String> comments = new ArrayList();
                String sCurrentLine;
                int returnVal = jfc.showOpenDialog(Main_UI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(jfc.getSelectedFile()));
                        while ((sCurrentLine = br.readLine()) != null) {
                            comments.add(sCurrentLine);
                        }
                        for (String s : comments) {
                            System.out.println(s);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else if (e.getSource() == analyzeButton) {
                Analyzer.clearArray();
                FBClient.clearArray();
                YTClient.clearArray();
                ClearUI();
                //TwitterClient.clearArray();
                String urlString = urlText.getText();
                Main_UI.this.mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                if (urlString.equals(null) || urlString.equals("")) {
                    JOptionPane.showMessageDialog(null, "There's nothing to analyze.\nPlease paste a url.", "Did you really hit analyze without puttng anything in?", JOptionPane.ERROR_MESSAGE);
                } else {
                    HashMap<String, String> stringMap = parseUrl(urlString);
                    Boolean child = childCommentBox.isSelected();
                    Boolean isBlacklistEnabled = !blacklistIgnoreBox.isSelected();
                    exportToFile.setEnabled(true);
                    loadFile.setEnabled(false);
                    Boolean file = saveFile.isSelected();

                    if (Site.equals("facebook")) {
                        if (stringMap.size() == 1) {
                            FBClient.fetchRandomPagePost(stringMap.get("Page Name"), child, file);
                        } else if (stringMap.size() == 3) {
                            FBClient.fetchSpecificPagePost(stringMap.get("Page Name"), stringMap.get("Post Id"), child, file);
                        }
                    } else if (Site.equals("youtube")) {
                        YTClient.fetchComments(stringMap.get("Page Type"), stringMap.get("Id"));
                    } else if (Site.equals("twitter")) {
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

    public HashMap<String, String> parseUrl(String s) {
        if (s.contains("facebook.com")) {
            setSite("facebook");
            return parseFacebookUrl(s);
        } else if (s.contains("youtube.com")) {
            setSite("youtube");
            return parseYoutubeUrl(s);
        } else if (s.contains("twitter.com")) {
            setSite("twitter");
            return parseTwitterUrl(s);
        } else if (s.contains("reddit.com") || s.contains("redd.it")) {
            setSite("reddit");
            return parseRedditUrl(s);
        } else {
            JOptionPane.showMessageDialog(Main_UI.this, "Url not recognized",
                    "We only do facebook, youtube, or twitter", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }

    HashMap<String, String> parseFacebookUrl(String s) {
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
        } else if (array.length == 5 && array[1].equals("photos")) {
            map.put("Page Name", array[0]);
            map.put("Post Type", array[1]);
            map.put("Post Id", array[3]);
        } else {
            JOptionPane.showMessageDialog(Main_UI.this, "Url not recognized",
                    "Uh...", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        return map;
    }

    HashMap<String, String> parseYoutubeUrl(String s) {
        int last = s.lastIndexOf("youtube.com/");
        int ytLength = "youtube.com/".length();

        String sub = s.substring(last + ytLength, s.length());
        String[] array = sub.split("/");
        HashMap<String, String> map = new HashMap<String, String>();
        if (array.length == 1) {
            map.put("Page Type", "video");
            array[0] = array[0].replace("watch?v=", "");
            map.put("Id", array[0]);
        } else if (array.length == 2) {
            map.put("Page Type", array[0]);
            map.put("Id", array[1]);
        } else if (array.length == 3) {
            map.put("Page Type", array[0]);
            map.put("Id", array[1]);
        } else {
            JOptionPane.showMessageDialog(Main_UI.this, "Url not recognized",
                    "Uh...", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return map;
    }

    HashMap<String, String> parseTwitterUrl(String s) {
        int last = s.lastIndexOf("twitter.com/");
        int twtLength = "twitter.com/".length();

        String sub = s.substring(last + twtLength, s.length());
        String[] array = sub.split("/");
        HashMap<String, String> map = new HashMap<String, String>();

        if (array.length == 3) {
            map.put("Username", array[0]);
            map.put("Status", array[1]);
            map.put("Post Id", array[2]);
        } else {
            JOptionPane.showMessageDialog(Main_UI.this, "Url not recognized",
                    "Uh...", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return map;
    }

    //This probably doesn't quite work yet but oh well
    HashMap<String, String> parseRedditUrl(String s) {
        HashMap<String, String> map = new HashMap<String, String>();

        if (s.contains("reddit.com")) {
            int last = s.lastIndexOf("reddit.com/");
            int redLength = "reddit.com/".length();

            String sub = s.substring(last + redLength, s.length());
            String[] array = sub.split("/");

            if (array.length == 3) {
                map.put("Subreddit", array[1]);
                map.put("Post Id", array[3]);
            } else {
                JOptionPane.showMessageDialog(Main_UI.this, "Url not recognized",
                        "Uh...", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
        } else if (s.contains("redd.it")) {
            int last = s.lastIndexOf("redd.it/");
            int redLength = "redd.it/".length();

            String sub = s.substring(last + redLength, s.length());
            String[] array = sub.split("/");

            map.put("Post Id", array[0]);
        }
        return map;
    }

    public void setSite(String site) {
        this.Site = site;
    }

    void analyzeAndDisplay(Boolean isBlacklistEnabled) {
        try {
            if (Site.equals("facebook")) {
                if (!FBClient.getPostArray().isEmpty()) {
                    Analyzer.setComments(FBClient.getPostArray(), isBlacklistEnabled);
                }
            } else if (Site.equals("youtube")) {
                if (!YTClient.getPostArray().isEmpty()) {
                    Analyzer.setComments(YTClient.getPostArray(), isBlacklistEnabled);
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
            layoutConstraints.gridy = 2;
            Main_UI.this.add(jsp, layoutConstraints);
            Main_UI.this.pack();

            Main_UI.this.mainPanel.setCursor(null);

            outputTable.setVisible(true);
            Main_UI.this.repaint();
            clearButton.setVisible(true);

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
        Main_UI.this.repaint();
        Main_UI.this.pack();
    }

    public static void main(String args[]) throws IOException {
        new Main_UI();
    }
}
