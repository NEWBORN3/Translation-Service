import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.SwingUtilities;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class TranslatorAgents extends Agent {
	private TranslatorAgents agent = this;
	private Set<TranslationProvideInfo> serviceList = new HashSet<TranslationProvideInfo>();
	private TranslationView ui;
	public void setup()
	{ 
		System.out.println("Translator: " + getAID().getName());
		
		Object[] args = getArguments();
		if (args != null && args.length == 2)
		{
			
			serviceList.add( new TranslationProvideInfo((TranslationService.Language) args[0],agent.getAID(),(double)args[1]));
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType("Translator");
			sd.setName(getLocalName());
			dfd.addServices(sd);
			try {
				DFService.register(this, dfd);
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			} 
		} else {
				System.out.println("Invalid Translator Agent Input");
				doDelete();
		}
		addBehaviour(new UserMessages());
	}

	public void takeDown()
	{
		try {
			DFService.deregister(agent);
			System.out.println(getAID().getName() + ": Closed " );
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
	}
	public class UserMessages extends CyclicBehaviour 
	{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			System.out.println(agent + "Waiting To Translate");
			ACLMessage msg = this.myAgent.receive();
			if (msg != null) 
			{
				try 
				{
					Object content = msg.getContentObject();
					if(content.getClass().equals(TranslationRequestInfo.class)) {
					 agent.addBehaviour(agent.new TranslationSearch((TranslationRequestInfo)content, msg));
				          return;
				     }
					if(content.getClass().equals(TranslationService.class)) {
						 agent.addBehaviour(agent.new TranslateWord((TranslationService)content, msg));
					          return;
					     }
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(myAgent + ": Not a vaid translation request");
				}
			} else {
				System.out.println(agent.getLocalName() + ": No request message");
				block();
			}
			
		}
		
	}
	
	public class TranslationSearch extends OneShotBehaviour 
	{
		TranslationRequestInfo tri;
	    ACLMessage msg;
	    public TranslationSearch(TranslationRequestInfo tri, ACLMessage msg) {
	      this.tri = tri;
	      this.msg = msg;
	    }
		@Override
		public void action() {
			// TODO Auto-generated method stub
			HashSet<TranslationProvideInfo> translators = new HashSet<TranslationProvideInfo>();
			for(TranslationProvideInfo item : agent.serviceList)
			{
				if(!tri.language.equals(item.getLanguage())) 
				{
					continue;
				}
				translators.add(item.clone());
			}
			ACLMessage reply = msg.createReply();
			if(translators.size() == 0) {
			  reply.setPerformative(ACLMessage.REFUSE);
			} else {
			  reply.setPerformative(ACLMessage.PROPOSE);
			}
			try {
			  reply.setContentObject(translators);
			  this.myAgent.send(reply);
			} 
			catch (Exception e) {
			  System.out.println(agent +" Couldn't sent search results.");
			}
		} 
		
	}
	
	public class TranslateWord extends OneShotBehaviour {
		TranslationService tsi;
	    ACLMessage msg;
	    public TranslateWord(TranslationService tsi, ACLMessage msg) {
	      this.tsi = tsi;
	      this.msg = msg;
	    }
	    public void action() {
	        
	        ACLMessage reply = msg.createReply();
	        reply.setPerformative(ACLMessage.AGREE);
	        try {
	          String rWord = WordCollection.FindWord(tsi.getWord(),tsi.lang.toString());
//	          System.out.println("==== "+ rWord);
	          reply.setContentObject(rWord);
	          this.myAgent.send(reply);
	          
	          System.out.println(agent.getLocalName() + ": A word is translated to agent " + msg.getSender().getName());
	          
	        } catch (Exception e) {
	          System.out.println(agent.getLocalName() + ": Couldn't sent the word.");
	        }
	      }
		}
	public class ShutdownAgent extends OneShotBehaviour {

	    public void action() {
	    	agent.doDelete();
	    	return;
	   
	    }
 }	
}

	