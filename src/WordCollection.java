import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class WordCollection {
	public static String FindWord(String tWord, String language)
	{
		String newWord = tWord.toLowerCase();
		newWord = newWord.substring(0, 1).toUpperCase() + newWord.substring(1);
		Object obj = null;
		try {
			obj = new JSONParser().parse(new FileReader("src/Dictionary.json"));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // typecasting obj to JSONArray
		JSONArray ja = (JSONArray) obj;
		Iterator itr2 = ja.iterator();      
        while (itr2.hasNext()) 
        {
        	Iterator<Map.Entry> itr1 = ((Map) itr2.next()).entrySet().iterator();
            while (itr1.hasNext()) {
                Map.Entry pair = itr1.next();       
                if(pair.getKey().equals(newWord))
                {
                	JSONObject jo = (JSONObject) pair.getValue();
                	return (String) jo.get(language);
                }
                
            }
        }
        return "Visit translate.google";
	}
	
}
