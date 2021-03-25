import java.io.Serializable;


public class TranslationRequestInfo implements Serializable {
	private static final long serialVersionUID = 8246364560313449848L;
	public String language;
	public float price;
	public TranslationRequestInfo(String lan, float payment)
	{	
		super();
		this.language = lan;
		this.price = payment;
	}
	
	
}
