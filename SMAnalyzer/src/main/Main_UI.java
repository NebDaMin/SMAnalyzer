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
import java.util.List;
import java.util.Arrays;
import java.awt.datatransfer.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import main.sminterfaces.FBClient;
import main.sminterfaces.YTClient;
import main.sminterfaces.TwitterClient;
import static org.jfree.chart.ChartColor.DARK_GREEN;
import static org.jfree.chart.ChartColor.DARK_MAGENTA;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

public class Main_UI extends JFrame {

    //UI vars
    private JPanel mainPanel;
    private JMenuBar menu;
    private JMenu options, file, graphs;
    private JCheckBoxMenuItem childCommentBox, blacklistIgnoreBox, saveFile;
    private JRadioButtonMenuItem barGraph, pieGraph, threeDPieGraph;
    private ButtonGroup graphGroups;
    private JMenuItem loadFile, dictionaryAdd, exportToFile;
    private JTable outputTable;
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
        barGraph = new JRadioButtonMenuItem("Bar Graph");
        pieGraph = new JRadioButtonMenuItem("Pie Graph");
        threeDPieGraph = new JRadioButtonMenuItem("3D Pie Graph");
        graphGroups = new ButtonGroup();
        graphGroups.add(barGraph);
        graphGroups.add(pieGraph);
        graphGroups.add(threeDPieGraph);
        barGraph.setSelected(true);
        graphs.add(barGraph);
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
                urlText.setText("");
                outputTable.setVisible(false);
                exportToFile.setEnabled(false);
                loadFile.setEnabled(true);
                Main_UI.this.remove(jsp);
                Main_UI.this.repaint();
                Main_UI.this.pack();
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
                        while((sCurrentLine = br.readLine()) != null) {
                            comments.add(sCurrentLine);
                        }
                        for(String s : comments) {
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
        int ytLength = "twitter.com/".length();

        String sub = s.substring(last + ytLength, s.length());
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

    public void setSite(String site) {
        this.Site = site;
    }

    void analyzeAndDisplay(Boolean isBlacklistEnabled) {
        try {
            Analyzer.setComments(FBClient.getPostArray(), isBlacklistEnabled);
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
                    new ButtonEditor(new JCheckBox(), groups, infoButton));
            outputTable.getColumn(" ").setCellRenderer(new ButtonRenderer());
            outputTable.getColumn(" ").setCellEditor(
                    new ButtonEditor(new JCheckBox(), groups, blacklistButton));
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

    class ButtonRenderer extends JButton implements TableCellRenderer {

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

        public ButtonEditor(JCheckBox checkBox, ArrayList<CommentGroup> groups, JButton aButton) {
            super(checkBox);
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

        public Object getCellEditorValue() {
            if (isPushed && label.equals("More Info")) {

                // action handling for more info button
                JPanel dialogPanel = new JPanel();
                dialogPanel.setLayout(new GridLayout(2, 1));

                JLabel commentListLabel = new JLabel("Comment Text");

                JLabel rightPlaceHolder = new JLabel("Other output?");
                rightPlaceHolder.setHorizontalAlignment(SwingConstants.CENTER);
                rightPlaceHolder.setPreferredSize(new Dimension(300, 300));
                JFreeChart graph;
                GraphInstance g = new GraphInstance();
                if (barGraph.isSelected()) {
                    graph = g.Graph(0, "Groups and their percentages", false);
                } else if (pieGraph.isSelected()) {
                    graph = g.Graph(1, "Groups and their percentages", false);
                } else {
                    graph = g.Graph(1, "Groups and their percentages", true);
                }

                ChartPanel chart = new ChartPanel(graph);
                // graph.setHorizontalAlignment(SwingConstants.CENTER);
                chart.setPreferredSize(new Dimension(600, 300));

                ArrayList<CommentInstance> comments = new ArrayList();
                CommentGroup selectedGroup = groups.get(Main_UI.this.outputTable.getSelectedRow());
                comments = selectedGroup.getComments();
                String outputString = "";
                for (int k = 0; k < selectedGroup.getComments().size(); k++) {
                    outputString += comments.get(k).getCommentRaw();
                    outputString += "\nWritten at: " + comments.get(k).getCommentTime();
                    if (comments.get(k).getPositivityLevel() < 0) {
                        outputString += "\nThis comment is flagged as negative.";
                    } else if (comments.get(k).getPositivityLevel() > 0) {
                        outputString += "\nThis comment is flagged as positive.";
                    } else {
                        outputString += "\nThis comment is flagged as neutral.";
                    }
                    outputString += "\n\n";
                }

                JTextPane commentList = new JTextPane();
                commentList.setText(outputString);
                commentList.setEditable(false);

                //Code to display instances of the keyword in bold
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setBold(sas, true);

                Pattern word = Pattern.compile(selectedGroup.getKeyword());
                Matcher match = word.matcher(outputString.toLowerCase());

                while (match.find()) {
                    System.out.println(match.group() + ", " + match.start() + ", " + match.end());
                    commentList.getStyledDocument().setCharacterAttributes(match.start(), match.end() - match.start(), sas, true);
                }

                JScrollPane scrollPane = new JScrollPane(commentList);
                scrollPane.setPreferredSize(new Dimension(300, 300));
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                JPanel textPanel = new JPanel(new BorderLayout());
                textPanel.add(commentListLabel, BorderLayout.NORTH);
                textPanel.add(scrollPane, BorderLayout.CENTER);

                JPanel topPanel = new JPanel();
                topPanel.add(textPanel);
                topPanel.add(rightPlaceHolder);

                JPanel bottomPanel = new JPanel();
                bottomPanel.add(chart);

                dialogPanel.add(topPanel);
                dialogPanel.add(bottomPanel);

                JDialog jd = new JDialog(Main_UI.this, "Group Details", true);
                jd.add(dialogPanel);
                jd.setLocation(650, 200);
                jd.pack();
                jd.show();
            } else if (isPushed && label.equals("Add to blacklist")) {
                try {
                    Analyzer.addToBlacklist(groups.get(Main_UI.this.outputTable.getSelectedRow()).getKeyword());
                    Analyzer.clearArray();
                    Main_UI.this.remove(jsp);
                    analyzeAndDisplay(true);
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(Main_UI.this, "File not found",
                            "You done messed up the temp blacklist path", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            isPushed = false;
            return new String(label);
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    public static void main(String args[]) throws IOException {
        new Main_UI();
    }
}
