import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.introspection.ACLMessage;

public class UserAgent extends Agent{

	public void setup()
	{
		System.out.println("User" + getAID().getName());
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("User");
		sd.setName("TheUser");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new TranslatorSearch());
		
	}
	public void takeDown()
	{
		System.out.println("User Closed" + getAID().getName());
	}
	public class TranslatorSearch extends OneShotBehaviour 
	{
		
		ACLMessage msg;
		TranslationRequestInfo tri;
//		public TranslatorSearch(TranslationRequestInfo tri, ACLMessage msg) {
//			this.tri = tri;
//			this.msg = msg;
//		    }
		@Override
		public void action() {
			System.out.println("hello");
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("Translator");
			template.addServices(sd);
			try 
			{
				DFAgentDescription[] result = DFService.search(myAgent, template);
				for( int i=0; i < result.length; i++ )
				{
					System.out.println("I found " + result[i].getName().getLocalName());
				}
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}
		
	}
	}
}
