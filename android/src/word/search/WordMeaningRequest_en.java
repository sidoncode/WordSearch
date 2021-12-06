package word.search;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import word.search.config.GameConfig;
import word.search.platform.dict.WordMeaningRequest;
import word.search.ui.dialogs.DictionaryDialog;


public class WordMeaningRequest_en implements WordMeaningRequest {

    private  DictionaryDialog.DictionaryCallback callback;
    private String word;

    @Override
    public void request(String word, DictionaryDialog.DictionaryCallback callback) {
        this.word = word;
        this.callback = callback;


        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl("https://api.wordnik.com/v4/word.json/" + word.toLowerCase(Locale.ENGLISH) +"/definitions?limit=" + GameConfig.ENGLISH_DICTIONARY_MAX_RESULT + "&includeRelated=false&useCanonical=false&includeTags=false&api_key=" + GameConfig.WORDNIC_API_KEY);

        request.setHeader("sec-fetch-dest", "document");
        request.setHeader("sec-fetch-mode", "navigate");
        request.setHeader("sec-fetch-site", "none");
        request.setHeader("sec-fetch-user", "?1");
        request.setHeader("upgrade-insecure-requests", "1");



        app.log(request.getUrl());


        RequestSender sender = new RequestSender();
        sender.request = request;
        sender.run();

    }


    class RequestSender implements Runnable{

        Net.HttpRequest request;
        String response;

        @Override
        public void run() {
            Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(final Net.HttpResponse httpResponse) {



                    try {
                        response = parseResponse(httpResponse.getResultAsString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        response = "Error in parsing dictionary response";
                    }


                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            WordMeaningRequest_en.this.callback.onMeaning(WordMeaningRequest_en.this.word, response);
                        }
                    });

                }

                @Override
                public void failed(Throwable t) {
                    final String text = t.getMessage();
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            WordMeaningRequest_en.this.callback.onMeaning(WordMeaningRequest_en.this.word, text);
                        }
                    });
                }

                @Override
                public void cancelled() {

                }
            });
        }




        private String parseResponse(String html) throws JSONException {
            app.log(html);

            if(html.trim().charAt(0) == '{') {
                JSONObject jsonObject = new JSONObject(html);
                if (jsonObject.has("statusCode")) {
                    if (jsonObject.has("message")) return jsonObject.getString("message");
                    else if (jsonObject.has("error")) return jsonObject.getString("error");
                    else return String.valueOf(jsonObject.getInt("statusCode"));
                }
            }

            JSONArray array = new JSONArray(html);
            StringBuilder sb = new StringBuilder();
            int index = 1;
            for(int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                if(object.has("text")) {
                    String text = object.getString("text");
                    text = text.replaceAll("<[^>]+>", "");
                    sb.append((index++) + ". " + text + "\n");
                }
            }

            return sb.toString();
        }
    }


}
