package plugins.APIForGeneratorAI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadingTempleteText {
    ReadingTempleteText(){
    }
    public String readingTempleteTextForString(String path){
        String finalText="";
        try(InputStream inputStream=getClass().getResourceAsStream(path);
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"))){
            String text = "";
            while((text=bufferedReader.readLine())!=null){
                finalText+=text+"\n";
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return finalText;
    }
}
