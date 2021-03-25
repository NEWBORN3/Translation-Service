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

public class UserAgent extends Agent{

	private UserAgent agent = this;
	public HashSet<DFAgentDescription> TranslationServiceList = new HashSet<DFAgentDescription>();
	public Set<String> elgibleTranslator = new HashSet<String>();
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
			       System.out.println(agent + "Creating Music Seeker UI...");
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

	
	public class TranslateWord extends SequentialBehaviour {
		    public TranslateWord() {
		      
		      super.addSubBehaviour(new LookForTranslator("Am",80));
//		      super.addSubBehaviour(new ResponseFromTranslators());
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
	
//	public class LookForTranslator extends OneShotBehaviour
//	{
//
//		private String theLanguage;
//		private float price;
//		public LookForTranslator(String theLanguage,float price)
//		{
//			this.theLanguage = theLanguage;
//			this.price = price;
//		}
//		@Override
//		public void action() {
//			// TODO Auto-generated method stub
//			 System.out.println(agent + " Sending language request to translaters ");
//		     ACLMessage msg = new ACLMessage(ACLMessage.CFP);
//		      for(DFAgentDescription df: TranslationServiceList) {
//		        msg.addReceiver(df.getName());
//		      }
//		      try {
//		        msg.setContentObject(new TranslationRequestInfo(theLanguage,price));
//		        agent.send(msg);
//		      } catch (Exception e) {
//		        System.out.println(e+"Couldn't send language request to Translator.");
//		      }
//		    }
//		}
	
	
	public class LookForTranslator extends CyclicBehaviour  
	{

		private String theLanguage;
		private float price;
		public LookForTranslator(String theLanguage,float price)
		{
			this.theLanguage = theLanguage;
			this.price = price;
		}
		@Override
		public void action() {
			// TODO Auto-generated method stub
			 System.out.println(agent + " Searching for translator ");
		     ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		      for(DFAgentDescription df: TranslationServiceList) {
		        msg.addReceiver(df.getName());
		      }
		      try {
		    	System.out.println("Once");
		        msg.setContentObject(new TranslationRequestInfo(theLanguage,price));
		        agent.send(msg);
		        addBehaviour(new ResponseFromTranslators());
		      } catch (Exception e) {
		        System.out.println(e+"Couldn't send language request to Translator.");
		      }
		    }
	 }
	
	public class ResponseFromTranslators extends SimpleBehaviour {
		
		 private int translatorCount = 0;
		 private long startTime;
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
			System.out.println(agent + "Receving language request.. " + translatorCount);
			ACLMessage msg = agent.receive();  // myagent and agent
			if (msg != null) 
			{
				try {
					HashSet<String> translatorReturned = (HashSet<String>) msg.getContentObject();
					elgibleTranslator.addAll(translatorReturned);
					
					System.out.println("name");
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(agent + " Couldn't collect the translators language results.");
				}
			} else {
				System.out.println("No messageL");
				block();
			}
		}
	

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
		    	
			return true;
		} 
		
	}
	
	 public class ShutdownAgent extends OneShotBehaviour {

		    @Override
		    public void action() {
		   agent.doDelete();
  }
}
}
	
    

