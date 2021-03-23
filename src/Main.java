import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties pr = new ExtendedProperties();
		pr.setProperty(Profile.MAIN_HOST, "localhost");
		pr.setProperty(Profile.MAIN_PORT, "3260");
		pr.setProperty(Profile.GUI, "true");
		Profile p = new ProfileImpl(pr);
		Runtime r = Runtime.instance();
		AgentContainer container = r.createMainContainer(p);
		try {
			container.start();
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AgentController Translator;
		AgentController User1;
		try {
			Translator = container.createNewAgent("Samuel","TranslatorAgents",null);
			Translator.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			User1 = container.createNewAgent("Muva","UserAgent",null);
			User1.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	   
	}

}