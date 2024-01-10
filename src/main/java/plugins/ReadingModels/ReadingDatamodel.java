package plugins.ReadingModels;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
//完成
//rootからastahファイル中の指定のクラス図情報（データモデル）を読み込むクラス　Map<String,Map<string,String>> (クラス名，属性名，型名)の形で返す
public class ReadingDatamodel {
    private static final String dataDiagramName="datamodel";
    private static IModel project;
    private static ArrayList<IClass> baseClasses;

    public static Map<String, Map<String,String>> ReadingDatamodels(IModel project) throws ClassNotFoundException, ProjectNotFoundException {
        Map<String, Map<String, String>> AllclassInfo = new LinkedHashMap<>();
        INamedElement[] elements = project.getOwnedElements();
        baseClasses = new ArrayList<>();

        for (int i = 0; i < elements.length; i++) {
            //package:datamodelがある場合，中のクラス情報を取得する
            if (elements[i] instanceof IPackage && elements[i].getName().equals(dataDiagramName)) {
                INamedElement[] classes = ((IPackage) elements[i]).getOwnedElements();
                for (int j = 0; j < classes.length; j++) {
                    if (classes[j] instanceof IClass) {
                        baseClasses.add((IClass) classes[j]);
                        IClass iclass = (IClass) classes[j];
                        AllclassInfo.put(iclass.getName(), getClassInfos(iclass));
                    }
            }
                System.out.println("datamodelを読み込みました");
                System.out.println("読み込んだdatamodelの情報を表示します");
                //データモデル一覧を表示
                for (Map.Entry<String, Map<String, String>> entry : AllclassInfo.entrySet()) {
                    System.out.println("クラス名: " + entry.getKey());
                    Map<String, String> innerMap = entry.getValue();

                    for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
                        System.out.println("  属性名: " + innerEntry.getKey() + ", 値: " + innerEntry.getValue());
                    }

                    System.out.println(); // Outer Keyごとに改行
                }
                System.out.println("ベースとなるクラス");
                for(int k=0;k<baseClasses.size();k++){
                    System.out.println(baseClasses.get(k).getName());
                }
                return AllclassInfo;
        }else{
                System.out.println("Package:datamodelが見つかりませんでした");
                return null;
            }
        }

        return AllclassInfo;
    }
    private static Map<String,String> getClassInfos(IClass iClass) {
        Map<String, String> attributeInfo = new LinkedHashMap<>();
        // 全属性を表示，取得
        IAttribute[] attributes = iClass.getAttributes();
        for (int i = 0; i < attributes.length; i++) {
            //全属性とその型名を取得
            attributeInfo.put(attributes[i].getName(),attributes[i].getTypeExpression());
        }
        return attributeInfo;
    }
    //読み込んだデータモデルのクラスを取得する
    public static ArrayList<IClass> getBaseClasses() {
        return baseClasses;
    }

}
