package word.search.platform.dict;

public interface WordMeaningProvider{
    WordMeaningRequest get(String langCode);
}
