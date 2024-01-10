package plugins.ReadingModels;


import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;

import java.util.ArrayList;

//完成
//ルートから全てのダイアグラム（datamodelとscenario）を読み取り，返すクラス 　返り値はIDiagramのarraylist型
public class ReadingAllDiagrams {
    public static ArrayList<IDiagram> ListReadingAllDiagrams(IModel project) throws ClassNotFoundException, ProjectNotFoundException {
        //全ダイアグラムを取得するため再帰的に探索
        ArrayList<IDiagram> diagrams = new ArrayList<>();
        IDiagram[] inDiagram = project.getDiagrams();
        for(IDiagram diagram:inDiagram){
            diagrams.add(diagram);
        }
        INamedElement[] elements = project.getOwnedElements();
        for(int i=0;i<elements.length;i++){
            if (elements[i] instanceof IDiagram) {
                diagrams.add((IDiagram) elements[i]);
            }
            if(elements[i] instanceof IPackage){
                IDiagram[] diagram = elements[i].getDiagrams();
                for(int j=0;j<diagram.length;j++){
                    diagrams.add(diagram[j]);
                }
            }
        }
        return diagrams;
    }

}
