import jade.core.AID;
import jade.util.leap.Serializable;

public class TranslationService implements Cloneable, Serializable  {
	public Language lang;
	private AID translator;
	private double price;
	private String word;
	public TranslationService(String word,Language lang,AID translator, double price) {
	    super();
	    this.word = word;
	    this.lang = lang;
	    this.price = price;
	    this.translator = translator;
	  }
	public TranslationService(Language lang, float price) {
	    super();
	    this.word = null;
	    this.lang = lang;
	    this.price = price;
	  }
	public AID getTranslator() {
		return this.translator;
	}
	public Language getLanguage() {
	    return this.lang;
	}
	public double getPrice() {
		// TODO Auto-generated method stub
		return this.price;
	}
	public String getWord() {
		// TODO Auto-generated method stub
		return this.word;
	}
	public TranslationService clone() {
	    return new TranslationService(this.getWord(), this.getLanguage(), this.translator,this.getPrice());
	}
	public static enum Language { Amharic, Oromifa, Tigrinya }
	
}
