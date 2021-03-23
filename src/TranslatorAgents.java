import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class TranslatorAgents extends Agent {
	public void setup()
	{
		System.out.println("Tranlator" + getAID().getName());
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Translator");
		sd.setName("TheFirst");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	public void takeDown()
	{
		System.out.println("Translator Closed" + getAID().getName());
	}
}
