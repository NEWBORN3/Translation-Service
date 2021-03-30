import java.awt.EventQueue;

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
		
		//Amharic Translator 1
		AgentController TranslatorAm;
		
		try {
			TranslatorAm = container.createNewAgent("AM-40 Amharic Translator","TranslatorAgents",
					new Object[] {TranslationService.Language.Amharic,40.00});
			TranslatorAm.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    AgentController TranslatorAmTwo;
		
		try {
			TranslatorAmTwo = container.createNewAgent("AM-30 Amharic Translator","TranslatorAgents",
					new Object[] {TranslationService.Language.Amharic,30.00});
			TranslatorAmTwo.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Oromifa Translator 13
		AgentController TranslatorOr;
		try {
			TranslatorOr = container.createNewAgent("OR-02 Oromo Translator","TranslatorAgents",
					new Object[] {TranslationService.Language.Oromifa,60.00});
			TranslatorOr.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AgentController TranslatorTr;
		try {
			TranslatorTr = container.createNewAgent("TR-09 Tigregena Translator","TranslatorAgents",
					new Object[] {TranslationService.Language.Tigrinya,50.00});
			TranslatorTr.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		
		AgentController User;
		try {
			User = container.createNewAgent("UserBot","UserAgent",null);
			User.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
