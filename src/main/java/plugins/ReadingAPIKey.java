package plugins;

import java.util.ResourceBundle;

public class ReadingAPIKey {
    public static String ReadingAPIKeyForPropaties(){
        String key ="apikey";
        try{
            ResourceBundle bundle = ResourceBundle.getBundle("apikey");
            return bundle.getString(key);
        }catch (Exception e){
            System.out.println("apikey.propertiesが見つかりませんでした");
            return null;
        }
    }
}
