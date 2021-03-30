import java.awt.Component;
import java.awt.Font;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.Charset;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class TranslationView extends JFrame {
	  JTextField englishWord;
	  JComboBox languagesList;
	  JPanel contentPane;
	  List console;
	  JButton search;
	  JLabel TheWord;

	  public void addMessageToConsole(String message) {
	        console.add(message);
	      }
	
	  public TranslationView(final UserAgent agent) {
		    setResizable(false);
		    setTitle("User Agent: " + agent.getLocalName());
		    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    setBounds(100, 100, 640, 440);
		    contentPane = new JPanel();
		    contentPane.setBorder(new EmptyBorder(3, 3, 3, 3));
		    setContentPane(contentPane);
		    contentPane.setLayout(null);
		    
		    TheWord = new JLabel();
		    TheWord.setHorizontalAlignment(SwingConstants.CENTER);
		    TheWord.setBounds(180, 160, 200, 16);
		    TheWord.setFont(new Font("Nyala",1,14));
		    contentPane.add(TheWord);
		    
		    JLabel lblInfo = new JLabel("Translation Service Using Agents");
		    lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
		    lblInfo.setVerticalAlignment(SwingConstants.TOP);
		    lblInfo.setBounds(6, 6, 628, 21);
		    contentPane.add(lblInfo);
		    
		    JLabel lblMinRating = new JLabel("Translated:");
		    lblMinRating.setHorizontalAlignment(SwingConstants.RIGHT);
		    lblMinRating.setBounds(16, 160, 170, 16);
		    contentPane.add(lblMinRating);
		    
		    JLabel lblGenre = new JLabel("English Word");
		    lblGenre.setHorizontalAlignment(SwingConstants.RIGHT);
		    lblGenre.setBounds(16, 56, 170, 16);
		    contentPane.add(lblGenre);
		    
		    
		    JLabel lblPricePerMusic = new JLabel("Translate to Languages: ");
		    lblPricePerMusic.setHorizontalAlignment(SwingConstants.RIGHT);
		    lblPricePerMusic.setBounds(16, 111, 170, 16);
		    contentPane.add(lblPricePerMusic);
		    
		    search = new JButton("Translate!");
		    search.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent e) {
		    	  search.setEnabled(false);
		 
		    	  TranslationService.Language lan = (TranslationService.Language)languagesList.getSelectedItem();
		    	  String word;
	
		    	  try {
		    		  
		    		  word = englishWord.getText();
		    		  if(word.isEmpty())
		    		  {
		    			  console.removeAll(); 
		    			  console.add(agent + " : Please provide a valid word");
		    			  enableUI();
		    			  TheWord.setText("");	
		    			  return;
		    		  }
		    	  } catch (Exception ex) {
		    		  console.removeAll();
		    		  console.add(agent + " --  It's not a word, Please provide a valid word");
		    		  enableUI();
		    		  return;
		    	  }
	       
	       console.removeAll();
	       agent.addBehaviour(agent.new TranslateWord(lan, word));
	      }
	    });
		    
		    search.setBounds(450, 80, 170, 26);
//		    16, 81, 170, 16
		    contentPane.add(search);
		    
		    
		    englishWord = new JTextField();
		    englishWord.setBounds(198, 52, 221, 27);
		    contentPane.add(englishWord);
		    englishWord.setColumns(10);
		    
		    languagesList = new JComboBox();
		    languagesList.setBounds(198, 100, 221, 40);
		    contentPane.add(languagesList);
		    
		    console = new List();
		    console.setBounds(16, 195, 594, 189);
		    contentPane.add(console);
		    
		    for(TranslationService.Language l: TranslationService.Language.values()) {
		    	languagesList.addItem(l);
		    }
		    
	  }
	  public void enableUI() {
		    search.setEnabled(true);
		  }
}