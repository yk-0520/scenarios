package plugins.example;

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


//    static String plaintext= """
//            ユースケース「2人のお届け先に商品を送る」を達成するためのデータの具体値の変化を以下に示します。
//
//            変更前：
//            {
//            "お支払方法": {
//            "簡単お届けリストを使う": "",
//            "お支払方法": ""
//            },
//            "依頼主情報": {
//            "お名前（姓）": "",
//            "お名前（名）": "",
//            "フリガナ（姓）": "",
//            "フリガナ（名）": "",
//            "役職名，肩書": "",
//            "連名": "",
//            "会社名": "",
//            "郵便番号": "",
//            "都道府県": "",
//            "市区": "",
//            "町村": "",
//            "番地": "",
//            "電話番号": ""
//            },
//            "商品情報": {
//            "数量": ""
//            },
//            "お届け先情報": {
//            "お名前（姓）": "",
//            "お名前（名）": "",
//            "フリガナ（姓）": "",
//            "フリガナ（名）": "",
//            "役職名，肩書": "",
//            "連名": "",
//            "会社名": "",
//            "郵便番号": "",
//            "都道府県": "",
//            "市区": "",
//            "町村": "",
//            "番地": "",
//            "電話番号": ""
//            }
//            }
//
//            変更後：
//            {
//            "お支払方法": {
//            "簡単お届けリストを使う": "",
//            "お支払方法": ""
//            },
//            "依頼主情報": {
//            "お名前（姓）": "山田",
//            "お名前（名）": "太郎",
//            "フリガナ（姓）": "ヤマダ",
//            "フリガナ（名）": "タロウ",
//            "役職名，肩書": "社長",
//            "連名": "山田太郎",
//            "会社名": "株式会社ABC",
//            "郵便番号": "123-4567",
//            "都道府県": "東京都",
//            "市区": "千代田区",
//            "町村": "大手町",
//            "番地": "1-2-3",
//            "電話番号": "090-1234-5678"
//            },
//            "商品情報": {
//            "数量": "2"
//            },
//            "お届け先情報": {
//            "お名前（姓）": "田中",
//            "お名前（名）": "花子",
//            "フリガナ（姓）": "タナカ",
//            "フリガナ（名）": "ハナコ",
//            "役職名，肩書": "一般社員",
//            "連名": "田中花子",
//            "会社名": "株式会社XYZ",
//            "郵便番号": "987-6543",
//            "都道府県": "大阪府",
//            "市区": "大阪市",
//            "町村": "中央区",
//            "番地": "4-5-6",
//            "電話番号": "080-9876-5432"
//            }
//            }
//            """;
//
//    テスト用
//    public static void main(String[] args) {
//        String text = "This is some text with {\"個人情報\": {\"名前\":\"ヤマダ\"}} JSON objects {\"住所\": {\"県\":\"東京\",\"市区\":\"新宿\"}} inside.";
//
//        List<JSONObject> jsonObjects = extractJsonObjects(text);
//
//        for (JSONObject jsonObject : jsonObjects) {
//            System.out.println(jsonObject.toString());
//            System.out.println(jsonObject.keySet());
//
//        }
//    }
}




