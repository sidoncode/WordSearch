package word.search.platform.dict;


import word.search.ui.dialogs.DictionaryDialog;

public interface WordMeaningRequest {
    void request(String word, DictionaryDialog.DictionaryCallback callback);
}
