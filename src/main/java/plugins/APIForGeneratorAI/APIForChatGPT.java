package plugins.APIForGeneratorAI;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import plugins.ReadingModels.ReadingDatamodel;
import plugins.System.Generator;
import plugins.APIForGeneratorAI.SavePost;
public class APIForChatGPT {
    //以下の初期値apikeyは，開発時以外は消去する
    private static String DEVAPIKEY=null;

    public static ArrayList<String> postDatamodel=new ArrayList<>();
//    public static ArrayList<String> postPrompt=new ArrayList<>();
//
//    public static ArrayList<String> postAnswer=new ArrayList<>();
    private static String DEVMODELVERSION="gpt-3.5-turbo-1106";

    public static String generateAnswer(String datamodel,String system,String usecase ,String apikey,String modelversion,boolean datamodelflag) throws IOException, ProjectNotFoundException, ClassNotFoundException {
        System.out.println("chatGPTによる回答生成");
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).writeTimeout(300,TimeUnit.SECONDS).readTimeout(300,TimeUnit.SECONDS).build();
        System.out.println("OpenAI APIに接続");
        //開発時は以下をコメントアウトし，apikeyとmodelversionを使用する
        DEVAPIKEY=apikey.trim();
        DEVMODELVERSION=modelversion.trim();
        JsonObject json = new JsonObject();
        json.addProperty("model", DEVMODELVERSION);
        json.add("messages", new JsonArray());

        String prompt;
        if(datamodelflag){
            prompt = createFirstPrompt(datamodel,system,usecase);
        }
        else{
            prompt = createSecondPrompt(datamodel,system,usecase);
        }

        postDatamodel.add(datamodel);
        SavePost.addPostPrompt(prompt);
//json modeで応答速度が遅くなる可能性　要検証
//        json.add("response_format", new JsonObject());
//        json.get("response_format").getAsJsonObject().addProperty("type","json_object");

        json.get("messages").getAsJsonArray().add(buildMessage("system", "あなたはシステム開発者です"));

        //過去の回答がある場合，最大3回の過去の回答を付加 要検証
        if(postDatamodel.size()>1 && SavePost.getPostAnswer().size()>0){
            int maxflag=0;
            for (int i=0;i<=SavePost.getPostAnswer().size()-1;i++) {
                if(maxflag==2){
                    break;
                }
                json.get("messages").getAsJsonArray().add(buildMessage("user", SavePost.getPostPrompt().get(SavePost.getPostPrompt().size()-1-maxflag)));
                json.get("messages").getAsJsonArray().add(buildMessage("assistant",SavePost.getPostAnswer().get(SavePost.getPostAnswer().size()-1-maxflag)));
                maxflag++;
            }
        }
        json.get("messages").getAsJsonArray().add(buildMessage("user", prompt));

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + DEVAPIKEY)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            System.out.println("リクエスト失敗");
            throw new RuntimeException(e);
        }
        String answer = extractAnswer(response);
        System.out.println("chatGPTの回答原文");
        System.out.println(answer);
        SavePost.addPostAnswer(answer);
        return answer;
    }

    private static JsonObject buildMessage(String role, String content) {
        JsonObject message = new JsonObject();
        message.addProperty("role", role);
        message.addProperty("content", content);
        return message;
    }

    private static String extractAnswer(Response response) {
        JSONObject json= null;
        String answer = null;
        try {
            json = new JSONObject(response.body().string());
            System.out.println(json.toString());
            answer = json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } catch (IOException e) {
            System.out.println("回答取得失敗");
            throw new RuntimeException(e);
        }
        return answer;
    }
    //chatGPTのapiキーを設定する
    public static void setApiKey(String key){
        DEVAPIKEY=key;
    }

    private static String createFirstPrompt(String datamodel,String system,String usecase) throws IOException {
        ReadingTempleteText readingTempleteText=new ReadingTempleteText();
        String templete =readingTempleteText.readingTempleteTextForString("/second_templete.txt");
        String templatePrompt=templete.replace("ScreenInstanceModel","").replace("このデータ構造に対して，以下のデータ具体値が定義されている．","").replace("ScreenTransitionModel",datamodel).replace("System",system).replace("GenerateIntention",usecase);
        //String templatePrompt="json記法を用いて以下のデータ群とその型が定義されている。\r\n "+datamodel+"\n"+"今，"+system+"というシステムを考えた際，ある事例「"+usecase+"」を達成した時の，上記で定義したデータの具体値の変化を表せ．ただし，以下の条件群を守ること．\r\n ・具体値は日本で一般的に入力される値とし、セキュリティ上問題ない値とする\r\n ・定義されたデータ構造以外のデータ構造は記述しない \r\n・同様のデータ構造が複数ある場合は，リスト表記は絶対に使用せず，名前の後ろに番号を付加する \r\nまた、データ群の出力はjson形式で表す。以下に出力形式の一例を示す。\r\n 変更前： \r\n{ \"ユーザ情報\": { \"ID\": \"int\", \"ユーザ名\": \"string\" }, \"住所情報\":{ \"都道府県\":\"string\", \"市区町村\":\"string\" }， \"お支払方法\":{ \"簡単お届けリストを使う\": \"String\", \"お支払方法\": \"String\" } } \r\n変更後：\r\n\"ユーザ情報\": { \"ID\": \"taro1234\", \"ユーザ名\": \"佐藤一郎\" }, \"住所情報\":{ \"都道府県\":\"東京\", \"市区町村\":\"世田谷区\" }, \"お支払方法\":{ \"簡単お届けリストを使う\": \"True\", \"お支払方法\": \"クレジットカード\" } }\r\n 回答をデータとしてそのまま利用するため、余計な文言は答えず、jsonデータのみを回答せよ";
        System.out.println("入力するプロンプト");
        System.out.println(templatePrompt);
        return templatePrompt;
    }
    private static String createSecondPrompt(String datamodel ,String system,String usecase) throws IOException, ProjectNotFoundException, ClassNotFoundException {
        ReadingTempleteText readingTempleteText=new ReadingTempleteText();
        Map<String, Map<String,String>> classmap=ReadingDatamodel.ReadingDatamodels(Generator.project);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String firstdatamodel = gson.toJson(classmap);
        String templete=readingTempleteText.readingTempleteTextForString("/second_templete.txt");
        String templetePrompt=templete.replace("ScreenInstanceModel",datamodel).replace("System",system).replace("GenerateIntention",usecase).replace("ScreenTransitionModel",firstdatamodel);
        System.out.println("入力するプロンプト");
        System.out.println(templetePrompt);
        return templetePrompt;
    }
    //テンプレートが記載されたファイルを読み込む


    //readingtempletefileのテスト用
//    public static void main(String[] args) throws IOException {
//        System.out.println(createFirstPrompt("データモデル","システム","ユースケース"));
//    }
    //apiテスト用
//    public static void main(String[] args) throws UnsupportedEncodingException {
//        String datamodel = "{\"個人情報\": {\"名前\":\"ヤマダ\"}}";
//        String system = "システム";
//        String usecase = "ユースケース";
//        String apikey="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";
//        String modelversion="gpt-3.5-turbo-1106";
//        String scenario = APIForChatGPT.generateAnswer(datamodel, system, usecase,apikey,modelversion);
//        System.out.println(scenario);
//    }
}

