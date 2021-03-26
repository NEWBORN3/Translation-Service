import java.io.Serializable;


public class TranslationRequestInfo implements Serializable {
	private static final long serialVersionUID = 8246364560313449848L;
	public TranslationService.Language language;
	public String word;
	public TranslationRequestInfo(TranslationService.Language language, String word)
	{	
		super();
		this.language = language;
		this.word = word;
	}
	
	
}
