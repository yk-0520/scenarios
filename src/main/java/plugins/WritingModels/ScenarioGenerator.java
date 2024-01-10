package plugins.WritingModels;

import com.change_vision.jude.api.inf.exception.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import plugins.WritingModels.CreateScenarios;
import plugins.APIForGeneratorAI.APIForChatGPT;
import org.json.JSONObject;
import plugins.WritingModels.JsonExtractor;
import plugins.WritingModels.JsonToMap;

//シナリオ生成のためのクラス
public class ScenarioGenerator {
    static int requestNum=0;
    static Map<String,Map<String,String>> generatedClassMap = new HashMap<>();
    public static void ScenarioGenerate(Map<String,Map<String,String>> classmap,String system,String prompt ,String apikey,String modelVersion) throws IOException, ProjectNotFoundException, InvalidEditingException, InvalidUsingException, ClassNotFoundException {
        Gson gson = new Gson();
        String datamodel = gson.toJson(classmap);
        String scenario = APIForChatGPT.generateAnswer(datamodel, system, prompt ,apikey,modelVersion);
        JSONObject answer= JsonExtractor.extractAfterDatamodel(JsonExtractor.extractJsonObjects(scenario));
        System.out.println(answer.toString());

        //回答がリストの場合，再度生成
        if(JsonExtractor.isList(answer.toString())){
            for(requestNum=0;requestNum<3;requestNum++){
                System.out.println("不適切な回答：再度生成開始");
                scenario = APIForChatGPT.generateAnswer(datamodel, system, prompt,apikey,modelVersion);
                answer=JsonExtractor.extractAfterDatamodel(JsonExtractor.extractJsonObjects(scenario));
                System.out.println(answer.toString());
                if(!JsonExtractor.isList(answer.toString())){
                    break;
                }
            }
        }
        if(requestNum==3){
            System.out.println("回答生成失敗");
            return;
        }
        generatedClassMap = JsonToMap.jsonObjectToMap(answer);
        System.out.println("生成元のデータを表示\n");
        for (Map.Entry<String, Map<String, String>> entry : generatedClassMap.entrySet()) {
            System.out.println("クラス名: " + entry.getKey());
            Map<String, String> innerMap = entry.getValue();

            for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
                System.out.println("  属性名: " + innerEntry.getKey() + ", 値: " + innerEntry.getValue());
            }
        }
        CreateScenarios.createScenarioClass(generatedClassMap);
    }

//    public static void main(String[] args) throws ClassNotFoundException, IOException, ProjectNotFoundException, InvalidEditingException, InvalidUsingException {
//        String PROJECT_PATH = "./SampleModel.asta";
//        AstahAPI astahAPI = AstahAPI.getAstahAPI();
//        ProjectAccessor projectAccessor = astahAPI.getProjectAccessor();
//        projectAccessor.create(PROJECT_PATH);
//                Map<String, Map<String, String>> classmap = new LinkedHashMap<>();
//                Map<String, String> class1 = new LinkedHashMap<>();
//                class1.put("姓", "山田");
//                class1.put("名", "太郎");
//                class1.put("年齢", "20");
//                classmap.put("ユーザ情報", class1);
//                // Populate your classmap with data (replace this with your actual data)
//
//                // Replace "YourSystemName" and "YourPrompt" with actual system and prompt values
//                String system = "ECサイト";
//                String prompt = "商品を購入する";
//                // Call the ScenarioGenerate method from the ScenarioGenerator class
//                ScenarioGenerator.ScenarioGenerate(classmap, system, prompt);
//                // Close the project
//                projectAccessor.close();
//            }
}
