import jade.util.leap.Serializable;

public class TranslationService implements Cloneable, Serializable  {
	public Language lang;
	private float price;
	private String word;
	public TranslationService(String word,Language lang, float price) {
	    super();
	    this.word = word;
	    this.lang = lang;
	    this.price = price;
	  }
	public TranslationService(Language lang, float price) {
	    super();
	    this.word = null;
	    this.lang = lang;
	    this.price = price;
	  }
	public Language getLanguage() {
	    return this.lang;
	}
	private float getPrice() {
		// TODO Auto-generated method stub
		return this.price;
	}
	private String getWord() {
		// TODO Auto-generated method stub
		return this.word;
	}
	public TranslationService clone() {
	    return new TranslationService(this.getWord(), this.getLanguage(), this.getPrice());
	}
	public static enum Language { Amharic, Oromifa, Tigirigna }
	
}
