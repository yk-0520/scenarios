package plugins.example.APIForGeneratorAI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
//chatgptにプロンプトを投げ，結果を返すクラス
//インスタンスでapiキーを設定(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)
//引数はapiキー
public class ChatGPTAPI {
    private final String apiKey;
    private final String url="https://api.openai.com/v1/chat/completions";

    private final String model ="gpt-3.5-turbo";

    public ChatGPTAPI(String apiKey) {
        this.apiKey = apiKey;
    }
    //chatgptにプロンプトを投げ，結果を返すメソッド(文字列表現のデータモデル，想定システム，シナリオ)
    public String generateResponse(String datamodel,String system,String usecase) throws IOException {
        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer "+"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");


            // The request body
            //String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + createFirstPrompt(datamodel,system,usecase) + "\"}]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            //writer.write(body);
            writer.flush();
            writer.close();

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // calls the method to extract the message.
            return extractMessageFromJSONResponse(response.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content")+ 11;

        int end = response.indexOf("\"", start);

        return response.substring(start, end);

    }}




    //プロンプトを作成するメソッド
//    private String createFirstPrompt(String datamodel,String system,String usecase){
//
//        String templatePrompt="""
//                                   UMLのクラス図で以下のデータ群が定義されている。１はクラス名を表し、2以降は属性名とその型を表す。
//                                   """+datamodel+ """
//                                   今、"""+system+ """
//            を考えた際、ユースケース：「"""+ usecase +"""
//            」を達成した時の、上記で定義したデータの具体値の変化を、ユースケース開始前と終了時ひとつずつ表せ。ただし、以下の条件を守ること。
//・定義されたデータ構造以外のデータ構造は記述しない
//・ユースケース達成時に何も変化のないデータも存在する可能性がある
//
//    また、データ群の出力はjson形式で表す。以下に出力形式の一例を示す。
//    変更前：
//    {
//        "ユーザ情報": {
//        "ユーザID": "",
//                "ユーザ名": ""
//    }
//    }
//    変更後：
//    {
//        "ユーザ情報": {
//        "ユーザID": "123456",
//                "ユーザ名": "佐藤一郎"
//    }
//    }
//    回答をデータとしてそのまま利用するため、余計な文言は答えず、jsonデータのみを回答せよ;""";
//        return templatePrompt;
//    }
//
//
//
//
//    //テスト用実行メソッド(未実行)
//    static String text= """
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
//            """;
//
//    public static void main(String[] args) throws IOException {
//        ChatGPTAPI chat = new ChatGPTAPI("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");
//        System.out.println(chat.generateResponse(text,"通販サイト","商品を購入する"));
//
//    }
//}
//
//
///*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
//暫定プロンプト
//UMLのクラス図で以下のデータ群が定義されている。１はクラス名を表し、2以降は属性名とその型を表す。
//"""+datamodel+"""
//今、"""+system+"""を考えた際、ユースケース：「"""+ usecase +"""
//」を達成した時の、上記で定義したデータの具体値の変化を、ユースケース開始前と終了時ひとつずつ表せ。ただし、以下の条件を守ること。
//
//・具体値が必要な場合は、"""+datavalue.answer+"""にあるjsonのオブジェクトからランダムに使用すること。
//・"""+format+"""型を持つ属性の具体値のフォーマットは、(例："""+format_ex+""")のフォーマットに従う
//・定義されたデータ構造以外のデータ構造は記述しない
//・ユースケース達成時に何も変化のないデータも存在する可能性がある
//
//また、データ群の出力はjson形式で表す。以下に出力形式の一例を示す。
//変更前：
//{
//"ユーザ情報": {
//"ユーザID": "",
//"ユーザ名": ""
//}
//}
//
//変更後：
//{
//"ユーザ情報": {
//"ユーザID": "123456",
//"ユーザ名": "佐藤一郎"
//}
//}
//
//
//回答をデータとしてそのまま利用するため、余計な文言は答えず、jsonデータのみを回答せよ*/
//
