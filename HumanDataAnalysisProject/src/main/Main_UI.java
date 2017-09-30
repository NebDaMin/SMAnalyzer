package main;
/**
 * TODO: 
 * Play ARK
 * Get base mercilessly destroyed and razed to the ground
 * Rage quit
 * Make a method that parses things correctly
 * Add more ways for the parse method to do the thing
 * Make the JCheckBox do things
 * Fix that weird error message
 * Rename some stuff
 */
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Arrays;
import java.awt.datatransfer.*;
import java.io.*;
import java.net.*;
import javax.swing.JFileChooser;
import main.fbinterface.FBClient;
import main.humandataanalysisproject.*;

public class Main_UI extends JFrame 
{
    //UI vars
   // private JPanel mainPanel;
    private JPanel urlPanel;
    private JButton openButton;
    private JButton urlButton;
    private JButton pasteButton;
    private JButton analyzeButton;
    private JCheckBox childCommentBox;
    //restFB vars
    private FBClient FBClient;
    
    //HumanDataAnalysisProject
    private CommentListAnalyzer Analyzer;
 
    private JLabel urlLabel;
    private JTextField urlText;    
    
     public Main_UI() throws IOException 
    {
        //init
        ActionHandler ah = new ActionHandler();
        urlLabel = new JLabel("Url: ");
        urlText = new JTextField(20);
        pasteButton = new JButton("Paste");
        analyzeButton = new JButton("Analyze");
        openButton = new JButton("Open file...");
        urlButton = new JButton("Url");
        childCommentBox = new JCheckBox("Include child comments");
        this.setLayout(new BorderLayout());
        
        FBClient = new FBClient();
        Analyzer = new CommentListAnalyzer();
        
        //main panel only has two buttons
        //TODO: add a fancy splash screen
       // mainPanel = new JPanel();
     //    this.add(mainPanel, BorderLayout.CENTER);
       //  mainPanel.add(urlButton);
       // mainPanel.add(openButton);
       
        //url panel is the urls stuff
        //not sure if I like how it's formatted so I might remove the smoke and mirrors and fiddle around with layouts
        urlPanel = new JPanel();
        this.add(urlPanel, BorderLayout.CENTER);
       // urlPanel.setVisible(false);
        urlPanel.add(urlLabel);
        urlPanel.add(urlText);
        urlPanel.add(childCommentBox);
        urlPanel.add(pasteButton);
        urlPanel.add(analyzeButton);
        
        openButton.addActionListener(ah);
        urlButton.addActionListener(ah);
        pasteButton.addActionListener(ah);
        analyzeButton.addActionListener(ah);
        
        this.setSize(500,500);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Capstone");
   
    }
   private class ActionHandler implements ActionListener
   {
       public void actionPerformed(ActionEvent e)
       {
           if(e.getSource()==openButton)
           {
            /*FileReader fr;    
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            int result = chooser.showSaveDialog(Main_UI.this);
            if (result == JFileChooser.APPROVE_OPTION) 
            {
                File f = chooser.getSelectedFile();
                if (f.exists()==true)
                {
                    try
                    {
                        
                        BufferedReader reader = new BufferedReader(new FileReader(f));
                        String         line = null;
                        StringBuilder  stringBuilder = new StringBuilder();
                        while((line = reader.readLine()) != null) 
                        {
                            stringBuilder.append(line);
                            stringBuilder.append("/n");
                        }

                         String s=stringBuilder.toString();
                          Main_UI.this.setVisible(false);
                          //OutputFrame outputFrame = new OutputFrame(s);
                          //outputFrame.setVisible(true);    
                    }
                    catch(IOException ex)
                    {
                        JOptionPane.showMessageDialog(Main_UI.this, "File could not be opened.",
                             "Get a file that works.", JOptionPane.INFORMATION_MESSAGE);;
                    }
                }
                
            }
            else;*/
           }
           else if(e.getSource()==urlButton)
           {
            //urlPanel.setVisible(true);
           }
           else if(e.getSource()==pasteButton)
           {
               Clipboard clipboard = getToolkit().getSystemClipboard();
            
                 Transferable clipData = clipboard.getContents(this);
                 String s;
                try 
                {
                    s = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
                } 
                catch (Exception ex) 
                {
                    s = ex.toString();
                }
                urlText.setText(s);
           }
           else if(e.getSource()==analyzeButton)
           {
              String urlString = urlText.getText();
        
                if (urlString.equals(null) || urlString.equals(""))
                {
                    JOptionPane.showMessageDialog(null, "There's nothing to analyze.\nPlease paste a url.", "Error", JOptionPane.ERROR_MESSAGE);
                }        
                else
                {
                    try 
                    {
                        String parsedString = parseUrl(urlString);
                        if(childCommentBox.isSelected())
                        {
                            FBClient.fetchPagePost(parsedString, true); 
                        }
                        //Desktop.getDesktop().browse(new URL(urlString).toURI());
                        else FBClient.fetchPagePost(parsedString, false);
                        Analyzer.setComments(FBClient.getPostArray());
                    }                       
                    catch (Exception ex) 
                    {
                        System.out.println(ex);
                        JOptionPane.showMessageDialog(null, "Something Broke", "Error", JOptionPane.ERROR_MESSAGE);
                    }   
                }  
           }
           else
               JOptionPane.showMessageDialog(Main_UI.this, "Uh....",
            "I don't know how you got here, but you need to leave", JOptionPane.INFORMATION_MESSAGE);
       }
   }
   public String parseUrl(String s)
   {
       return s.replace("https://www.facebook.com/", "");
   }
    public static void main(String args[]) throws IOException 
    {
        new Main_UI();
    }   

    
} 

