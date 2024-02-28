package plugins.APIForGeneratorAI;

import java.util.ArrayList;

//過去の履歴を保存
public class SavePost {
    private static ArrayList<String> postPrompt=new ArrayList<>();
    private static ArrayList<String> postAnswer=new ArrayList<>();

    public static void addPostPrompt(String prompt){
        postPrompt.add(prompt);
    }
    public static void addPostAnswer(String answer){
        postAnswer.add(answer);
    }
    public static void clearPostPrompt(){
        postPrompt.clear();
    }
    public static void clearPostAnswer(){
        postAnswer.clear();
    }
    public static ArrayList<String> getPostPrompt(){
        return postPrompt;
    }
    public static ArrayList<String> getPostAnswer(){
        return postAnswer;
    }
}
