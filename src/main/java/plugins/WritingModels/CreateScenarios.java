package plugins.WritingModels;
import com.change_vision.jude.api.inf.AstahAPI;

import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import plugins.ReadingModels.ReadingDatamodel;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;

import plugins.System.Generator;


public class CreateScenarios{
    //installer
    private static int scenarioNum=0;
    private static final String scenarioPackageName="scenario";
    private static final String scenarioDiagramName="scenario";

    public CreateScenarios(){
        super();
    }
    public static void createScenarioClass(Map<String, Map<String, String>> classMap) throws InvalidEditingException, ClassNotFoundException, InvalidUsingException, ProjectNotFoundException {
        Point2D location = new Point2D.Double(0, 0);
        int count = 0;
        double delta = 200.0d;
        IPackage scenarioPackage = null;
        ProjectAccessor prjAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
        IModel project = prjAccessor.getProject();
        INamedElement[] elements = Generator.project.getOwnedElements();
        System.out.println("シナリオ生成開始");
        //トランザクションの開始
        ITransactionManager transactionManager = prjAccessor.getTransactionManager();
        transactionManager.beginTransaction();

        // -----エディタ類-----
        BasicModelEditor basicModelEditor = ModelEditorFactory.getBasicModelEditor();
        IDiagramEditorFactory diagramEditorFactory = prjAccessor.getDiagramEditorFactory();
        ClassDiagramEditor classDiagramEditor = diagramEditorFactory.getClassDiagramEditor();
        scenarioPackage = null;
        for (int i = 0; i < elements.length; i++) {
            //package:scenarioがあるか判定
            if (elements[i] instanceof IPackage && elements[i].getName().equals(scenarioPackageName)) {
                System.out.println("package:" + elements[i].getName() + "が見つかりました");
                scenarioPackage = (IPackage) elements[i];
            }
        }
        if (scenarioPackage == null) {
            System.out.println("package:scenarioが見つかりませんでした:　作成中...");
            scenarioPackage = basicModelEditor.createPackage(Generator.project, scenarioPackageName);
        }

        try {
            classDiagramEditor.createClassDiagram(scenarioPackage, scenarioDiagramName + scenarioNum);
        } catch (InvalidEditingException e) {
            System.out.println("同じ名前のクラスが既に存在する可能性があります　名称を変更して作成");
            scenarioNum++;
            classDiagramEditor.createClassDiagram(scenarioPackage, scenarioDiagramName + scenarioNum);
        }
        //ベースクラスの取得
        ArrayList<IClass> baseClass = ReadingDatamodel.getBaseClasses();

        //各インスタンス仕様の生成 classmapのkeysetでfor文を回す
        for (String key : classMap.keySet()) {
            //クラス名が一致するベースクラスを取得
            IClass matchBaseClass = null;
            for (IClass iclass : baseClass) {
                if (key.contains(iclass.getName())) {
                    matchBaseClass = iclass;
                }
            }
            //ベースクラスが見つからない場合
            if (matchBaseClass == null) {
                System.out.println("クラス名:" + key + "のベースクラスが見つかりませんでした");
            }

            //インスタンス仕様の作成
            //ベースクラスの割り当て
            if (matchBaseClass != null) {
                IInstanceSpecification instance = null;
                IElement preinstance = classDiagramEditor.createInstanceSpecification(key, location).getModel();
                try {
                    instance = (IInstanceSpecification) preinstance;
                } catch (ClassCastException e) {
                    System.out.println("インスタンス仕様の作成に失敗しました");
                }
                if (instance != null) {
                    instance.setClassifier(matchBaseClass);
                    //インスタンス仕様の属性の取得
                    IAttribute[] attributes = matchBaseClass.getAttributes();
                    //インスタンス仕様の属性の変更
                    for (IAttribute attribute : attributes) {
                        //属性名が一致するか判定
                        if (classMap.get(key).containsKey(attribute.getName())) {
                            //属性名が一致した場合，属性の具体値を設定
                            instance.getSlot(attribute.getName()).setValue(classMap.get(key).get(attribute.getName()));
                        }
                    }
                }
            }
            //インスタンス仕様の位置を変更
            if (location.getX() < 1500) {
                location.setLocation(location.getX() + delta, location.getY());
            } else {
                location.setLocation(0, location.getY() + delta);
            }
        }
            scenarioNum++;
            // トランザクション終了
            TransactionManager.endTransaction();
            // プロジェクト保存，終了
            System.out.println("生成終了");
            //例外処理
    }
    //シナリオカウントを返す
    public static int getSenarioNum(){
        return scenarioNum;
    }
}

