package plugins.WritingModels;

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
}


