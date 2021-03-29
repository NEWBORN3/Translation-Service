import java.io.Serializable;

import jade.core.AID;

public class TranslationProvideInfo implements Serializable, Cloneable {
	private static final long serialVersionUID = 8246364560313449848L;
	public TranslationService.Language language;
	private final AID translatorAgent;
	private double price;
	public TranslationProvideInfo(TranslationService.Language lan, AID theAgent, double payment)
	{	
		super();
		this.language = lan;
		this.price = payment;
		this.translatorAgent = theAgent;
	}
	 public AID getTranslatorAgent() {
		    return translatorAgent;
	}
	 public TranslationService.Language getLanguage() {
		    return language;
	}
	 public double getPrice()
	 {
		 return price;
	 }
	 @Override
	 public TranslationProvideInfo clone() {
		    return new TranslationProvideInfo(this.getLanguage(), this.getTranslatorAgent(), this.getPrice());
		  }
}
