import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class UserAgent extends Agent{

	public void setup()
	{
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
		Object[] args = getArguments();
		if( args != null && args.length > 0)
		{
			System.out.println("Hello World" + args[1]);
		} else {
			System.out.println("noWord Specified");
			doDelete();
		}
	}
	public void takeDown()
	{
		System.out.println("Closed" + getAID().getName());
	}

}
