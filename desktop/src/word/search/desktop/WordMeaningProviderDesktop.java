package word.search.desktop;


import word.search.platform.dict.WordMeaningProvider;
import word.search.platform.dict.WordMeaningRequest;

public class WordMeaningProviderDesktop implements WordMeaningProvider {


    public WordMeaningRequest get(String langCode){
        if(langCode.equals("en"))
            return new WordMeaningRequest_en();

        return null;

    }

}
