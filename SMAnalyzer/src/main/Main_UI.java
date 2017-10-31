package main;

/**
 * TODO: have a secret coding party secretly implement youtube, twitter, and
 * reddit functionality laugh maniacally force a graph in a panel add pie
 * functionality add an option for different giraffes menus for days the menu is
 * MASSIVE wow fix that Save to file stuff Read from file stuff completely
 * rewrite the graph file to make it modular and not hard coded add more cheeky
 * message boxes maybe think about making the main ui file not so friggin big
 * add a silly easter egg
 *
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
import main.fbinterface.FBClient;
import static org.jfree.chart.ChartColor.DARK_GREEN;
import static org.jfree.chart.ChartColor.DARK_MAGENTA;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import org.json.JSONException;

public class Main_UI extends JFrame {

    //UI vars
    private JPanel mainPanel;
    private JMenuBar menu;
    private JMenu options, file;
    private JCheckBoxMenuItem childCommentBox, blacklistIgnoreBox, saveFile;
    private JMenuItem loadFile;
    private JTable outputTable;
    private JButton openButton, urlButton, pasteButton, analyzeButton, clearButton;
    private PrintWriter out;
    private JScrollPane jsp;
    private GridBagConstraints layoutConstraints;
    public final int BAR_CHART = 0;
    public final int PIE_CHART = 1;
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
        saveFile = new JCheckBoxMenuItem("Save to File");
        file.add(saveFile);
        menu.add(file);

        options = new JMenu("Options");
        options.setMnemonic('O');
        childCommentBox = new JCheckBoxMenuItem("Child Comments");
        blacklistIgnoreBox = new JCheckBoxMenuItem("Ignore BlackList");
        options.add(childCommentBox);
        options.add(blacklistIgnoreBox);
        menu.add(options);

        this.setLayout(new GridBagLayout());
        layoutConstraints = new GridBagConstraints();
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.gridy = 0;
        this.add(menu, layoutConstraints);

        FBClient = new FBClient();
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
                Main_UI.this.remove(jsp);
                Main_UI.this.repaint();
                //clearButton.setVisible(false);
                Main_UI.this.pack();
            } else if (e.getSource() == analyzeButton) {
                Analyzer.clearArray();
                FBClient.clearArray();
                String urlString = urlText.getText();
                Main_UI.this.mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                if (urlString.equals(null) || urlString.equals("")) {
                    JOptionPane.showMessageDialog(null, "There's nothing to analyze.\nPlease paste a url.", "Did you really hit analyze without puttng anything in?", JOptionPane.ERROR_MESSAGE);
                } else {
                    HashMap<String, String> stringMap = parseUrl(urlString);
                    Boolean child = childCommentBox.isSelected();
                    Boolean blacklist = blacklistIgnoreBox.isSelected();
                    Boolean file = saveFile.isSelected();
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
                    if (stringMap.size() == 1) {
                        FBClient.fetchRandomPagePost(stringMap.get("Page Name"), child);
                    } else if (stringMap.size() == 3) {
                        FBClient.fetchSpecificPagePost(stringMap.get("Page Name"), stringMap.get("Post Id"), child);
                    }
                    try {
                        Analyzer.setComments(FBClient.getPostArray(), blacklist);
                        //get group output data
                        ArrayList<CommentGroup> groups = Analyzer.groupComments();
                        //format into arrays for JTable constructor
                        Object[][] tableData = new Object[groups.size()][4];
                        Object[] columnNames = {"Group Keyword", "Number of Comments", ""," "};
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
                        outputTable.getColumn("").setCellRenderer(new ButtonRenderer());
                        outputTable.getColumn("").setCellEditor(
                                new ButtonEditor(new JCheckBox(), groups));
                        outputTable.getColumn(" ").setCellRenderer(new ButtonRenderer());
                        outputTable.getColumn(" ").setCellEditor(
                                new ButtonEditor(new JCheckBox(), groups));
                        jsp = new JScrollPane(outputTable);
                        Dimension d = outputTable.getPreferredSize();
                        jsp.setPreferredSize(
                                new Dimension(d.width, outputTable.getRowHeight() * row + 1));
                        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
                        layoutConstraints.gridy = 2;
                        Main_UI.this.add(jsp, layoutConstraints);
                        Main_UI.this.pack();
                        Main_UI.this.mainPanel.setCursor(null);

                        outputTable.setVisible(true);
                        clearButton.setVisible(true);
                    } catch (JSONException jse) {
                        JOptionPane.showMessageDialog(Main_UI.this, "Womp womp",
                                "That page doesn't even exist. Maybe try proofreading next time", JOptionPane.INFORMATION_MESSAGE);
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
            } else {
                JOptionPane.showMessageDialog(Main_UI.this, "Uh....",
                        "I don't know how you got here, but you need to leave", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public HashMap<String, String> parseUrl(String s) {

        if (s.contains("facebook.com")) {
            return parseFacebookUrl(s);
        } else if (s.contains("youtube.com")) {
            return parseYoutubeUrl(s);
        } else if (s.contains("twitter.com")) {
            return parseTwitterUrl(s);
        } else {
            JOptionPane.showMessageDialog(Main_UI.this, "Url not recognized",
                    "We only do facebook or youtube", JOptionPane.INFORMATION_MESSAGE);
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
            array[0].replace("watch?v=", "");
            map.put("Video Id", array[0]);
        } else if (array.length == 2) {
            map.put("Page Type", array[0]);
            map.put("Channel Id", array[1]);
        } else if (array.length == 3) {
            map.put("Page Type", array[0]);
            map.put("Username", array[1]);

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

        public ButtonEditor(JCheckBox checkBox, ArrayList<CommentGroup> groups) {
            super(checkBox);
            this.groups = groups;
            button = new JButton();
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
            if (isPushed) {
                // 
                // action handling for more info button
                JPanel dialogPanel = new JPanel();
                dialogPanel.setLayout(new GridLayout(2, 1));

                JLabel commentListLabel = new JLabel("Comment Text");

                JLabel rightPlaceHolder = new JLabel("Other output?");
                rightPlaceHolder.setHorizontalAlignment(SwingConstants.CENTER);
                rightPlaceHolder.setPreferredSize(new Dimension(300, 300));

                JFreeChart graph = Graph(1, "Groups and their percentages");
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
                //work in progress

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

    public JFreeChart Graph(int chartType, String chartTitle) {
        if (chartType == BAR_CHART) {
            //title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls 
            JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Word", "Percentage", createBarDataset(BAR_CHART), PlotOrientation.VERTICAL, true, true, false);
            final CategoryPlot plot = barChart.getCategoryPlot();
            final BarRenderer renderer = (BarRenderer) plot.getRenderer();

            renderer.setSeriesPaint(0, Color.black);
            renderer.setSeriesPaint(1, Color.magenta);
            renderer.setSeriesPaint(2, Color.cyan);
            return barChart;
        } else if (chartType == PIE_CHART) {
            PieDataset dataset = createPieDataset();

            JFreeChart pieChart = createPieChart(dataset, chartTitle);
            ChartPanel chartPanel = new ChartPanel(pieChart);
            chartPanel.setPreferredSize(new Dimension(500, 300));
            setContentPane(chartPanel);

            return pieChart;
        } else {
            return null;
        }
    }

    //values for bar chart
    private CategoryDataset createBarDataset(int chartType) {

        final String wordUno = "Uno";
        final String wordDos = "Dos";
        final String wordTres = "Tres";

        final String pos = "Positive";
        final String neu = "Neutral";
        final String neg = "Negative";

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(15, neg, wordUno);
        dataset.addValue(22, neu, wordUno);
        dataset.addValue(66, pos, wordUno);

        dataset.addValue(75, neg, wordDos);
        dataset.addValue(11, neu, wordDos);
        dataset.addValue(20, pos, wordDos);

        dataset.addValue(5, neg, wordTres);
        dataset.addValue(15, neu, wordTres);
        dataset.addValue(88, pos, wordTres);

        return dataset;
    }

    //values for pie graph
    public PieDataset createPieDataset() {
        DefaultPieDataset result = new DefaultPieDataset();

        result.setValue("Positive", 33);
        result.setValue("Neutral", 22);
        result.setValue("Negative", 45);

        return result;
    }

    //uncomment 3D to get the 3D version
    public JFreeChart createPieChart(PieDataset dataset, String title) {
        JFreeChart chart = ChartFactory.createPieChart/*3D*/(title, dataset, true, true, false);

        PiePlot/*3D*/ plot = (PiePlot/*3D*/) chart.getPlot();

        //You can put different colors or comment these out to have the original colors
        plot.setSectionPaint("Positive", DARK_GREEN);
        plot.setSectionPaint("Neutral", DARK_MAGENTA);
        plot.setSectionPaint("Negative", Color.black);

        plot.setStartAngle(0);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f); //sets the transparency of the graph -> 0 to 1  

        return chart;
    }

    public static void main(String args[]) throws IOException {
        new Main_UI();
    }

}
