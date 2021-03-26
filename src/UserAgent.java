import java.awt.EventQueue;
import java.util.HashSet;

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

import agent.MusicSeeker.FindAndPurchaseMusics.SelectMusic;

public class UserAgent extends Agent{

	private UserAgent agent = this;
	public final HashSet<DFAgentDescription> TranslationServiceList = new HashSet<DFAgentDescription>();
	
	private TranslationView ui;
	
	public void setup()
	{
//		System.out.println("User---" + getAID().getName());
//		DFAgentDescription dfd = new DFAgentDescription();
//		dfd.setName(getAID());
//		ServiceDescription sd = new ServiceDescription();
//		sd.setType("User");
//		sd.setName("TheUser");
//		dfd.addServices(sd);
//		try {
//			DFService.register(this, dfd);
//		}
//		catch (FIPAException fe) {
//			fe.printStackTrace();
//		}
		EventQueue.invokeLater(new Runnable() {
			@Override
			     public void run() {
			       System.out.println(agent + "Creating UI");
			       try {
			         ui = new TranslationView(agent);
			         ui.setVisible(true);
			       } catch (Exception e) {
			    	   System.out.println(e+ "Couldn't create UI!");
			       }
			     }
			   });
		addBehaviour(new TranslatorList());					
	}

		    public void takeDown()
			{
				System.out.println("Translator Closed" + getAID().getName());
			}

	
	public final class TranslateWord extends SequentialBehaviour {
		private Set<DFAgentDescription> knownAgentsAtTimeStarted;
		private Set<TranslationProvideInfo> elgibleTranslator = new HashSet<TranslationProvideInfo>();
		    public TranslateWord(TranslationService.Language lan,String word) {
		    	knownAgentsAtTimeStarted = (HashSet<DFAgentDescription>) agent.TranslationServiceList.clone();
		    ui.addMessageToConsole("this");
		      super.addSubBehaviour(new LookForTranslator(lan,word));
		      super.addSubBehaviour(new ResponseFromTranslators());
		      super.addSubBehaviour(new SelectCheapestTranslator(elgibleTranslator, word));
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
					 System.out.println(agent + " Sending language request to translaters ");
					 ui.addMessageToConsole(agent.getLocalName() +  "  Sending language request to translaters");
				     ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				      for(DFAgentDescription df: knownAgentsAtTimeStarted) {
				        msg.addReceiver(df.getName());
				      }
				      try {
				        msg.setContentObject(new TranslationRequestInfo(theLanguage,theWord));
				        agent.send(msg);
				      } catch (Exception e) {
				        System.out.println(e+"Couldn't send language request to Translator.");
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
					
					ui.addMessageToConsole(agent + "Receving translation request " + answerCount);
					ACLMessage msg = agent.receive();  // myagent and agent
					if (msg != null) 
					{
						try {
							answerCount++;
							 if(msg.getPerformative() == ACLMessage.REFUSE) {
								 ui.addMessageToConsole(agent+ "%s refuesd the message!"+ msg.getSender().getName());
						            return;
						     }
							HashSet<TranslationProvideInfo> translatorReturned = (HashSet<TranslationProvideInfo>) msg.getContentObject();
							if(translatorReturned == null || translatorReturned.size() == 0) { 
								ui.addMessageToConsole(agent + "No translator by the Language!" + msg.getSender().getName());
					            return;
					          }
							elgibleTranslator.addAll(translatorReturned);
							
							//toRemove
							for(var item : elgibleTranslator)
							{
							 ui.addMessageToConsole(item.getTranslatorAgent().getLocalName());
							}
							
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println(agent + " Couldn't collect the translators language results.");
						}
					} else {
						ui.addMessageToConsole("No messageL");
						System.out.println("No messageL");
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
				public SelectCheapestTranslator(Set<TranslationProvideInfo> tOffers,String tWord)
				{
					this.tOffers = tOffers;
					this.theWord = theWord;
				}
				@Override
				public void action() {
					// TODO Auto-generated method stub
					
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
					System.out.println("wer"+item.getName().getLocalName());
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
	

	
    

