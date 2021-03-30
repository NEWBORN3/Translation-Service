import java.awt.EventQueue;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


public class UserAgent extends Agent{

	private UserAgent agent = this;
	public final HashSet<DFAgentDescription> TranslationServiceList = new HashSet<DFAgentDescription>();
	
	private TranslationView ui;
	
	public void setup()
	{
		DFAgentDescription df = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Translator");
		df.addServices(sd);
		try 
		{
			DFAgentDescription[] result = DFService.search(agent, df);
			TranslationServiceList.clear();
			for(DFAgentDescription item : result )
			{
				TranslationServiceList.add(item);			
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			     public void run() {
			       System.out.println(agent + ": Creating UI");
			       try {
			         ui = new TranslationView(agent);
			         ui.setVisible(true);
			       } catch (Exception e) {
			    	   System.out.println(e+ ": Couldn't create UI!");
			       }
			     }
			   });
		addBehaviour(new TranslatorList());					
	}

		    public void takeDown()
			{
				System.out.println(getAID().getName() + ": Translator Service Closed. ");
			}

	
	public final class TranslateWord extends SequentialBehaviour {
		private Set<DFAgentDescription> knownAgentsAtTimeStarted;
		private Set<TranslationProvideInfo> elgibleTranslator = new HashSet<TranslationProvideInfo>();
		private TranslationService theService;
		public TranslateWord(TranslationService.Language lan,String word) {
		      knownAgentsAtTimeStarted = (HashSet<DFAgentDescription>) agent.TranslationServiceList.clone();
		      super.addSubBehaviour(new LookForTranslator(lan,word));
		      super.addSubBehaviour(new ResponseFromTranslators());
		      super.addSubBehaviour(new SelectCheapestTranslator(elgibleTranslator,word));
		      super.addSubBehaviour(new TranslateTheWord());
		      super.addSubBehaviour(new ListenAnswer());
		    }
		
			public class LookForTranslator extends OneShotBehaviour
			{
				private TranslationService.Language theLanguage;
				private String theWord;
				public LookForTranslator(TranslationService.Language theLanguage,String theWord)
				{
					this.theLanguage = theLanguage;
					this.theWord = theWord;
				}
				@Override
				public void action() {
					// TODO Auto-generated method stub
					 ui.addMessageToConsole(agent.getLocalName() +  ": Looking for an elgible translators");
				     ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				      for(DFAgentDescription df: knownAgentsAtTimeStarted) {
				        msg.addReceiver(df.getName());
				      }
				      try {
				        msg.setContentObject(new TranslationRequestInfo(theLanguage,theWord));
				        agent.send(msg);
				      } catch (Exception e) {
				        System.out.println(agent +": Couldn't send language request to Translator.");
				      }
				    }
				}
			
			public class ResponseFromTranslators extends SimpleBehaviour {
				 private long startTime;
				 private int answerCount = 0;
				 private long timeOut = 20000;
				 private long wait = 1000;
				 private final long TIMEOUT_MS = 15000;
			
				public ResponseFromTranslators() {
				        super();
				        startTime = System.currentTimeMillis();
				      }
				@Override
				public void action() {
					// TODO Auto-generated method stub
					ACLMessage msg = agent.receive();  
					if (msg != null) 
					{
						try {
							answerCount++;
							 if(msg.getPerformative() == ACLMessage.REFUSE) {
						            return;
						     }
							HashSet<TranslationProvideInfo> translatorReturned = (HashSet<TranslationProvideInfo>) msg.getContentObject();
							if(translatorReturned == null || translatorReturned.size() == 0) { 
								System.out.println(agent + "No translator" + msg.getSender().getName());
					            return;
					          }
							elgibleTranslator.addAll(translatorReturned);
							
							//toRemove
							
							
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println(agent + " Couldn't collect the translators language results.");
						}
					} else {
						block();
					}
				}
			

				@Override
				public boolean done() {
					// TODO Auto-generated method stub
				    if(System.currentTimeMillis() - startTime > TIMEOUT_MS) {
				    	ui.addMessageToConsole(agent + "Timeout occured while waiting for response.");
				          return true;
				        }
				        
				      
						if(answerCount >= knownAgentsAtTimeStarted.size()) 
				        {
				          return true;
				        }
						
				        return false;
				      }	
				} 
			public class SelectCheapestTranslator extends OneShotBehaviour
			{
				private Set<TranslationProvideInfo> tOffers;
				private String theWord;
				public SelectCheapestTranslator(Set<TranslationProvideInfo> tOffers,String theWord)
				{
					this.tOffers = tOffers;
					this.theWord = theWord;
				}
				@Override
				public void action() {
					ui.addMessageToConsole("-----Eligible Translators----");
					for(TranslationProvideInfo item : elgibleTranslator)
					{
					    ui.addMessageToConsole(item.getTranslatorAgent().getLocalName() + "- Price :" + item.getPrice());
					}
					ui.addMessageToConsole("------------------");
					// TODO Auto-generated method stub
					TranslationProvideInfo ths = Collections.min(tOffers, Comparator.comparing(s -> s.getPrice()));
					theService = new TranslationService(theWord,ths.getLanguage(),ths.getTranslatorAgent(),ths.getPrice());
					ui.addMessageToConsole(agent.getLocalName() + ": Selects Translator - " + theService.getTranslator().getLocalName());
					ui.addMessageToConsole("    ");
				}
			}
			
			public class TranslateTheWord extends OneShotBehaviour{
				@Override
				public void action() {
					// TODO Auto-generated method stub
					ACLMessage msg = new ACLMessage(ACLMessage.AGREE);
					  
					msg.addReceiver(theService.getTranslator());
			           try {  
			             msg.setContentObject(theService);
			             this.myAgent.send(msg);
			           } catch (Exception e) {
			             System.out.println(agent  + "Couldn't send the word");
			             ui.addMessageToConsole(agent + "Couldn't send the word");
			           }			           
				}
				
			}
			
		    private class ListenAnswer extends SimpleBehaviour {
		        private int songCount = 0;
		        private final long TIMEOUT_MS = 15000;
		        private final long WAIT_MS = 1000;
		        private long startTime;
		        private boolean isFirstRun = true;
		        private String finalWord;
		        public ListenAnswer() {
		          super();
		        }
		        
		        @Override
		        public void action() {
		          if(isFirstRun) { startTime = System.currentTimeMillis(); }        
		          ACLMessage msg = this.myAgent.receive();
		          if (msg == null) { block(WAIT_MS); return; }
		          
		          try {
		            if(msg.getPerformative() == ACLMessage.REFUSE) {
		              ui.addMessageToConsole(agent + " - " +  msg.getSender().getName() + " Refused To Translate!");
		              return;
		            }
		            
		            finalWord = (String)msg.getContentObject();
		            if(finalWord == null) { 
		            	ui.addMessageToConsole(agent + " - " + msg.getSender().getName() + " Sorry The word is not present in our archive ");
		              return;
		            }
		           
		            
		         
		            ui.addMessageToConsole(agent.getLocalName() +": Translation is Done succesuflly ");
		           
		            
		          } catch (Exception e) {
		        	  ui.addMessageToConsole(agent + " Some error happend");
		          }
		        }

		        @Override
		        public boolean done() {
		          Runnable enableUI = new Runnable() { 
		            @Override
		            public void run() {
		            	ui.TheWord.setText("");	
		            	ui.enableUI();
		            	ui.TheWord.setText(finalWord);
		            	System.out.println("Result: " + finalWord);
		            }
		          };
		          
		          if(System.currentTimeMillis() - startTime > TIMEOUT_MS) {
		            System.out.println(agent + " Timeout occured while waiting for response.");
		            SwingUtilities.invokeLater(enableUI);
		            return true;
		          }
		          
		          if(finalWord != null && !finalWord.isEmpty() ) {
		            SwingUtilities.invokeLater(enableUI);
		            return true;
		          }
		          
		          return false;
		        }
		      }
	}
	
	public class TranslatorList extends TickerBehaviour 
	{
		
		public TranslatorList() {
			super(agent, 10000);
			// TODO Auto-generated constructor stub
		}
		ACLMessage msg;
		@Override
		public void onTick() {
			DFAgentDescription df = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("Translator");
			df.addServices(sd);
			try 
			{
				DFAgentDescription[] result = DFService.search(myAgent, df);
				TranslationServiceList.clear();
				for(DFAgentDescription item : result )
				{
					TranslationServiceList.add(item);
				}
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}
	}
}

	public class ShutdownAgent extends OneShotBehaviour {
		    @Override
		    public void action() {
		        agent.doDelete();
		   
		    }
	}
}
	

	
    

