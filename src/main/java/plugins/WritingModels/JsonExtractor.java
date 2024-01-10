package plugins.WritingModels;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


// 文字列からJSONオブジェクトを抽出するクラス
// GPTからの回答を整形するために使用

public class JsonExtractor {

    public static List<JSONObject> extractJsonObjects(String text) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        int openBrackets = 0;
        int startIndex = 0;
        // 文字列を1文字ずつ処理
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            //文章中に{があったら開始位置を記録
            if (character == '{') {
                if (openBrackets == 0) {
                    startIndex = i;
                }
                openBrackets++;
            } else if (character == '}') {
                openBrackets--;
                // { と } の数が一致したらJSONオブジェクトとして切り出す
                if (openBrackets == 0) {
                    try {
                        String jsonText = text.substring(startIndex, i + 1);
                        JSONObject jsonObject = new JSONObject(jsonText);
                        jsonObjects.add(jsonObject);
                    } catch (JSONException e) {
                        continue; // JSONデコードエラーは無視
                    }
                }
            }
        }
        // { と } の数が一致しなかった(回答が途中で途切れている)場合の処理
        if(openBrackets!= 0){
            System.out.println("JSONの形式が正しくありません");
            return null;
        }
        //jsonObjectsにはJSONオブジェクトが入っている
        return jsonObjects;
    }
    //jsonObjectsから，最後のデータモデルを抽出，返す
    public static JSONObject extractAfterDatamodel(List<JSONObject> jsonObjects){
        return jsonObjects.get(jsonObjects.size()-1);
    }

    public static List<JSONObject> JsonObjectParser(JSONObject jsonObject){
        List<JSONObject> resultList=new ArrayList<>();
        for (String key : jsonObject.keySet()) {
            JSONObject jsons=new JSONObject();
            System.out.println(key);
            jsons.put(key,jsonObject.get(key));
            resultList.add(jsons);
        }
        return resultList;
    }
    public static List<JSONObject> getClassJsonObjects(String answer){
        return JsonObjectParser(extractJsonObjects(answer).get(extractJsonObjects(answer).size()-1));
    }

    //文字列中にリスト表記[]が含まれているか判定
    public static boolean isList(String text){
        if((text.contains("]")) && (text.contains("["))){
            return true;
        }else{
            return false;
        }
    }


}




