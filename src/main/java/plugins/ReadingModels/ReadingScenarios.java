package plugins.ReadingModels;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import plugins.ReadingModels.ReadingAllDiagrams;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
//完成
//Astah GUI上で選択されたシナリオ（オブジェクト図）を読み込み，Map<String,Map<String,String>>型で返すクラス
public class ReadingScenarios {
    private static final String scenarioPackageName="scenario";
    private static IModel project;

    public static Map<String,Map<String,String>> ReadingScenario(IModel project , String scenarioDiagramName) throws ClassNotFoundException, ProjectNotFoundException, InvalidUsingException {
        Map<String,Map<String,String>> AllclassInfo = new LinkedHashMap<>();
        ArrayList<IDiagram> diagrams= ReadingAllDiagrams.ListReadingAllDiagrams(project);

        for(IDiagram diagram:diagrams){
            if(diagram.getName().equals(scenarioDiagramName) && !diagram.getName().equals("datamodel")){
                AllclassInfo=getinformations(diagram);
                System.out.println("Scenario:"+scenarioDiagramName+"を読み込みました");
                System.out.println("読み込んだscenarioの情報を表示します");
                //データモデル一覧を表示:確認用
                for (Map.Entry<String, Map<String, String>> entry : AllclassInfo.entrySet()) {
                    System.out.println("クラス名: " + entry.getKey());
                    Map<String, String> innerMap = entry.getValue();

                    for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
                        System.out.println("  属性名: " + innerEntry.getKey() + ", 値: " + innerEntry.getValue());
                    }
                    System.out.println(); // Outer Keyごとに改行
                }
                return AllclassInfo;
            }
        }

        System.out.println("Package:scenarioまたは指定のシナリオが見つかりませんでした");
        return AllclassInfo;

    }
    //astah class diagramのinstanceSpecificationの名前，属性名，属性に対応する具体値を取得する
    private static Map<String,Map<String,String>> getinformations(IDiagram iDiagram) throws InvalidUsingException {
        Map<String,Map<String,String>> classInfo = new LinkedHashMap<>();
        Map<String,String> attributeInfo = new LinkedHashMap<>();
        IPresentation[] presentations = iDiagram.getPresentations();
        String className = null;
        IAttribute[] attributes=null;
        ISlot attributeValue=null;
        int protectCounter=0;
        System.out.println(presentations.length);
        for(int i=0;i<presentations.length;i++){
            System.out.println(presentations[i].getType());
        }
        for (int i = 0; i < presentations.length; i++) {
            if (presentations[i].getType().equals("InstanceSpecification")) {
                attributeInfo = new LinkedHashMap<>();
                IInstanceSpecification namedElement= (IInstanceSpecification) presentations[i].getModel();
                IClass parentClass=namedElement.getClassifier();
                className =namedElement.getName();
                for(String name:classInfo.keySet()){
                    if(name.equals(className)){
                        protectCounter++;
                        className=className+protectCounter;
                    }
                }
                for(int j=0;j<parentClass.getAttributes().length; j++) {
                    attributeValue = null;
                    attributes = parentClass.getAttributes();
                    try {
                        attributeValue = namedElement.getSlot(attributes[j].getName());
                        attributeInfo.put(attributes[j].getName(), attributeValue.getValue());
                    } catch (NullPointerException e) {
                        System.out.println("属性名:" + attributes[j].getName() + "の具体値が見つかりませんでした");
                        continue;
                    }
                }
                classInfo.put(className,attributeInfo);
            }
        }

        return classInfo;

    }
    //API確認用
    private static ArrayList<String> searchInstanceSpesification(IDiagram iDiagram) throws InvalidUsingException {
        IPresentation[] presentations = iDiagram.getPresentations();
        ArrayList<String> instanceNames = new ArrayList<>();
        System.out.println(presentations.length);
        for (int i = 0; i < presentations.length; i++) {
            if (presentations[i].getType().equals("InstanceSpecification")) {
                System.out.println(presentations[i].getType());
                System.out.println(presentations[i].getModel());
                IInstanceSpecification namedElement= (IInstanceSpecification) presentations[i].getModel();
                for(int j=0;j<namedElement.getAllSlots().length;j++){
                    System.out.println(namedElement.getAllSlots()[j].getValue());
                    for(IAttribute name :namedElement.getClassifier().getAttributes()){
                        System.out.println(name.getName());
                    }
                    instanceNames.add(namedElement.getAllSlots()[j].getValue());
                }
            }
        }
        return instanceNames;
    }

}
