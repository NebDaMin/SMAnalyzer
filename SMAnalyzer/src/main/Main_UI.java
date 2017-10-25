package main;

/**
 * TODO: add a silly easter egg listen to Wintergatan and feel better
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

public class Main_UI extends JFrame {

    //UI vars
    // private JPanel mainPanel;
    //private JPanel urlPanel;
    //private JPanel optionPanel;
    private JPanel mainPanel;
    private JTable outputTable;
    private JButton openButton;
    private JButton urlButton;
    private JButton pasteButton;
    private JButton analyzeButton;
    private JButton clearButton;
    private JCheckBox childCommentBox;
    private JCheckBox blacklistIgnoreBox;
    private JCheckBox fileBox;
    private JCheckBox testBox;
    private PrintWriter out;
    private JScrollPane jsp;
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
        clearButton = new JButton("Clear");
        urlButton = new JButton("Url");
        childCommentBox = new JCheckBox("Include child comments");
        blacklistIgnoreBox = new JCheckBox("Ignore blacklist");
        fileBox = new JCheckBox("Save to File");
        testBox = new JCheckBox("placeholder");
        outputTable = new JTable();
        jsp = new JScrollPane(outputTable);
        mainPanel = new JPanel();

        this.setLayout(new GridLayout(2, 1));

        FBClient = new FBClient();
        Analyzer = new CommentListAnalyzer();

        outputTable.setVisible(false);
        clearButton.setVisible(true);
        outputTable.setPreferredScrollableViewportSize(new Dimension(400, 150));

        childCommentBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        blacklistIgnoreBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fileBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        testBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(urlLabel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(urlText)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(childCommentBox)
                                        .addComponent(blacklistIgnoreBox))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(fileBox)
                                        .addComponent(testBox))))
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
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(childCommentBox)
                                        .addComponent(fileBox))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(blacklistIgnoreBox)
                                        .addComponent(testBox)))
                        .addComponent(analyzeButton))
                .addComponent(clearButton)
        );
        this.add(mainPanel);

        openButton.addActionListener(ah);
        urlButton.addActionListener(ah);
        pasteButton.addActionListener(ah);
        analyzeButton.addActionListener(ah);
        clearButton.addActionListener(ah);

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
                        Object[][] tableData = new Object[groups.size()][3];
                        Object[] columnNames = {"Group Keyword", "Number of Comments", ""};
                        int row = 0;
                        for (CommentGroup g : groups) {
                            tableData[row][0] = g.getKeyword();
                            tableData[row][1] = g.getComments().size();
                            tableData[row][2] = "More Info";
                            row++;
                        }
                        //create and populate table
                        outputTable = new JTable(tableData, columnNames);
                        outputTable.getColumn("").setCellRenderer(new ButtonRenderer());
                        outputTable.getColumn("").setCellEditor(
                                new ButtonEditor(new JCheckBox(), groups));
                        jsp = new JScrollPane(outputTable);
                        Dimension d = outputTable.getPreferredSize();
                        jsp.setPreferredSize(
                                new Dimension(d.width, outputTable.getRowHeight() * row + 1));
                        Main_UI.this.add(jsp);
                        Main_UI.this.pack();
                        Main_UI.this.mainPanel.setCursor(null);

                        outputTable.setVisible(true);
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
            } else {
                JOptionPane.showMessageDialog(Main_UI.this, "Uh....",
                        "I don't know how you got here, but you need to leave", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public HashMap<String, String> parseUrl(String s) {
        if (!s.contains("facebook.com")) {
            JOptionPane.showMessageDialog(Main_UI.this, "Url not recognized",
                    "We only do facebook", JOptionPane.INFORMATION_MESSAGE);
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

                JLabel bottomPlaceHolder = new JLabel("Here there be graphs");
                bottomPlaceHolder.setHorizontalAlignment(SwingConstants.CENTER);
                bottomPlaceHolder.setPreferredSize(new Dimension(600, 300));

                ArrayList<CommentInstance> comments = new ArrayList();
                CommentGroup selectedGroup = groups.get(Main_UI.this.outputTable.getSelectedRow());
                comments = selectedGroup.getComments();
                String outputString = "";
                for (int k = 0; k < selectedGroup.getComments().size(); k++) {
                    outputString += comments.get(k).getCommentRaw();
                    outputString += "\nWritten at: "+comments.get(k).getCommentTime();
                    outputString += "\n\n";
                }

                JTextPane commentList = new JTextPane();
                commentList.setText(outputString);
                commentList.setEditable(false);
                /*Code to display instances of the keyword in bold
                  work in progress
                
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setBold(sas, true);

                Pattern word = Pattern.compile(selectedGroup.getKeyword());
                Matcher match = word.matcher(outputString);

                while (match.find()) {
                    commentList.getStyledDocument().setCharacterAttributes(match.start(), match.end(), sas, true);
                }
                 */

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
                bottomPanel.add(bottomPlaceHolder);

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

    public static void main(String args[]) throws IOException {
        new Main_UI();
    }

}
