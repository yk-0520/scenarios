package plugins.example;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
//jsonobjectをmap型に変換するクラス　（順序は保持されない）
public class JsonToMap {
    public static Map<String, Map<String, String>> jsonObjectToMap(JSONObject jsonObject) {
        Map<String, Map<String, String>> resultMap = new HashMap<>();

        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                // 再帰的に呼び出して内側のMapを処理
                resultMap.put(key, mapFromJSONObject((JSONObject) value));
            } else {
                // 内側のMapがない場合、単純な値を追加
                Map<String, String> innerMap = new HashMap<>();
                innerMap.put(key, value.toString());
                resultMap.put(key, innerMap);
            }
        }
        return resultMap;
    }

    private static Map<String, String> mapFromJSONObject(JSONObject jsonObject) {
        Map<String, String> innerMap = new HashMap<>();
        for (String key : jsonObject.keySet()) {
            Object value=jsonObject.get(key);
            innerMap.put(key, value.toString());
        }
        return innerMap;
    }


//テスト用
//    public static void main(String[] args) {
//        // サンプルのJSONデータを作成
//        String jsonString = "{\"key1\":{\"subkey1\":\"value1\",\"subkey2\":\"value2\"},\"key2\":\"value3\"}";
//
//        // JSON文字列をJSONObjectに変換
//        JSONObject jsonObject = new JSONObject(jsonString);
//
//        // JsonToMapクラスを使用して変換
//        Map<String, Map<String, String>> resultMap = JsonToMap.jsonObjectToMap(jsonObject);
//
//        // 結果の表示
//        for (Map.Entry<String, Map<String, String>> entry : resultMap.entrySet()) {
//            System.out.println("Key: " + entry.getKey());
//            Map<String, String> innerMap = entry.getValue();
//            for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
//                System.out.println("  SubKey: " + innerEntry.getKey() + ", Value: " + innerEntry.getValue());
//            }
//        }
//    }
}


