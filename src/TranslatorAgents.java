import java.util.HashSet;
import java.util.Set;

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
	public void setup()
	{ 
		System.out.println("Translator---" + getAID().getName());
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Translator");
		sd.setName(getLocalName()+"AM");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		if(agent.getAID().getLocalName().contains("AM-"))
		{
			    serviceList.add( new TranslationProvideInfo(TranslationService.Language.Amharic,agent.getAID(),80));
			
		} 
		if(agent.getAID().getLocalName().contains("OR-"))
		{
				serviceList.add( new TranslationProvideInfo(TranslationService.Language.Oromifa,agent.getAID(),60));
		} 
		if(agent.getAID().getLocalName().contains("TR-")) 
		{
				serviceList.add( new TranslationProvideInfo(TranslationService.Language.Tigirigna,agent.getAID(),80));
				
		}
		for(var item :serviceList)
		{
			System.out.println("kio");
			System.out.println(item.getTranslatorAgent());
		}
		addBehaviour(new UserMessages());
	}

	public void takeDown()
	{
		System.out.println("Translator Closed" + getAID().getName());
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
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(myAgent + " Couldn't");
				}
			} else {
				System.out.println("This is No message");
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
        	System.out.println("search translator");
			HashSet<TranslationProvideInfo> translators = new HashSet<TranslationProvideInfo>();
			for(TranslationProvideInfo item : agent.serviceList)
			{
				System.out.println("sent---" + tri.language);
				System.out.println("db---" + item.getLanguage());
				if(!tri.language.equals(item.getLanguage())) 
				{
					continue;
				}
				translators.add(item.clone());
			}
			ACLMessage reply = msg.createReply();
			if(translators.size() == 0) {
		      System.out.println("Cults.");
			  reply.setPerformative(ACLMessage.REFUSE);
			} else {
				System.out.println("yap");
			  reply.setPerformative(ACLMessage.PROPOSE);
			}
			try {
			  System.out.println("well");
			  reply.setContentObject(translators);
			  this.myAgent.send(reply);
			} 
			catch (Exception e) {
			  System.out.println(agent +"Couldn't sent search results.");
			}
		} 
		
	}
}
