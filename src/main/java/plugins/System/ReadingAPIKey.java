package plugins.System;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ReadingAPIKey {
    public static String ReadingAPIKeyForPropaties(){
        String key ="apikey";
        try{
            //外部propertiesファイルを読み込むためのパス設定
            String jarPath2= FileSystems.getDefault().getPath("plugins","settings","apikey.properties").toAbsolutePath().toString();
            try(InputStream fis=new FileInputStream(jarPath2)){
                ResourceBundle bundle=new PropertyResourceBundle(fis);
                return bundle.getString(key);
            }catch (Exception e){
                System.out.println("apikey.propertiesが見つかりませんでした");
                return null;
            }
        }catch (Exception e){
            System.out.println("apikey.propertiesが見つかりませんでした");
            return null;
        }
    }
}
