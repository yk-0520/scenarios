package plugins.System;/*
 * パッケージ名は、生成したプラグインのパッケージ名よりも
 * 下に移してください。
 * プラグインのパッケージ名=> com.example
 *   com.change_vision.astah.extension.plugin => X
 *   com.example                            　　　　　　　  => O
 *   com.example.internal                    　　　　　 => O
 *   learning                                　　　　　　　　 => X
 */


import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import plugins.ReadingModels.ReadingAllDiagrams;
import plugins.ReadingModels.ReadingDatamodel;
import plugins.ReadingModels.ReadingScenarios;
import plugins.WritingModels.ScenarioGenerator;

import javax.swing.JOptionPane;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Generator extends JPanel
        implements IPluginExtraTabView, ProjectEventListener, ActionListener {

    public static String APIKEY=null;
    public static String MODELVERSION="gpt-3.5-turbo-1106";

    private JPanel leftPanel;
    private JPanel rightPanel;

    private JPanel systemFieldPanel;

    private JPanel promptFieldPanel;

    private JPanel apikeyFieldPanel;
    private JPanel modelversionFieldPanel;

    private JPanel configButtonPanel;

    private JPanel buttonPanel;

    private JTextField systemTextField;

    private JTextField promptTextField;

    private JTextField apikeyTextField;
    private JTextField modelversionTextField;

    private JDialog configDialog;

    private JButton createButton;

    private JButton reloadButton;

    private JButton configButton;

    private JButton configSaveButton;
    private JButton configCancelButton;

    private DefaultListModel model= new DefaultListModel();

    private JList lists=null;

    private JTextArea textArea;


    public static AstahAPI api;
    public static ProjectAccessor prjAccessor;
    public static IModel project;

    protected String[] list= {"ファイルなし"};
    public Generator() {
        initComponents();
    }

    private void initComponents() {


        this.rightPanel = new JPanel();
        this.leftPanel = new JPanel();
        this.systemFieldPanel = new JPanel();
        this.promptFieldPanel = new JPanel();
        this.buttonPanel = new JPanel();
        this.configButtonPanel = new JPanel();
        this.configDialog= new JDialog();
        this.textArea= new JTextArea("生成元とするファイル，想定システム，シナリオを選択してください\n・ファイル一覧にファイルがない場合は再読み込みしてください");
        this.createButton = new JButton("生成");
        this.createButton.setActionCommand("create");
        this.createButton.addActionListener(this);
        this.reloadButton = new JButton("ファイル再読み込み");
        this.reloadButton.setActionCommand("reload");
        this.reloadButton.addActionListener(this);
        this.configButton = new JButton("設定");
        this.configButton.setActionCommand("config");
        this.configButton.addActionListener(this);
        this.configSaveButton = new JButton("保存");
        this.configSaveButton.setActionCommand("save");
        this.configSaveButton.addActionListener(this);
        this.configCancelButton = new JButton("キャンセル");
        this.configCancelButton.setActionCommand("cancel");
        this.configCancelButton.addActionListener(this);

        this.systemTextField = new JTextField(10);
        this.promptTextField = new JTextField(10);
        this.apikeyFieldPanel = new JPanel();
        this.modelversionFieldPanel = new JPanel();
        this.apikeyTextField = new JTextField(10);
        this.modelversionTextField = new JTextField(10);


        //レイアウト設定
        setLayout(new GridLayout(1,2));
        add(leftPanel);
        add(rightPanel);
        leftPanel.setLayout(new GridLayout(1,1));
        rightPanel.setLayout(new GridLayout(4,1));
        buttonPanel.setLayout(new GridLayout(1,3));
        configDialog.setLayout(new GridLayout(4,1));
        configButtonPanel.setLayout(new GridLayout(1,2));
        systemFieldPanel.setLayout(new BorderLayout());
        promptFieldPanel.setLayout(new BorderLayout());
        leftPanel.setBackground(Color.GRAY);
        rightPanel.setBackground(Color.WHITE);
        textArea.setBorder(new BevelBorder(1));
        textArea.setMargin(new Insets(3,3,3,3));
        textArea.setEditable(false);
        configDialog.setSize(400,400);
        configDialog.setLocationRelativeTo(rightPanel);
        configDialog.setMinimumSize(new Dimension(400,400));

        //configDialogの設定
        configDialog.add(new JTextArea("APIキーとモデルバージョンの設定\n最新の設定要件はOpenAIの公式ドキュメントを参照してください\nデフォルトのモデルバージョン:gpt-3.5-turbo-1106"));
        configDialog.add(apikeyFieldPanel);
        configDialog.add(modelversionFieldPanel);
        configDialog.add(configButtonPanel);
        configButtonPanel.add(configSaveButton);
        configButtonPanel.add(configCancelButton);
        apikeyFieldPanel.setLayout(new BorderLayout());
        modelversionFieldPanel.setLayout(new BorderLayout());
        apikeyFieldPanel.add(new JLabel("APIキー"),BorderLayout.NORTH);
        apikeyFieldPanel.add(apikeyTextField,BorderLayout.CENTER);
        modelversionFieldPanel.add(new JLabel("モデルバージョン"),BorderLayout.NORTH);
        modelversionFieldPanel.add(modelversionTextField,BorderLayout.CENTER);
        configDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        configDialog.setVisible(false);

        //ファイル名一覧の表示
        model.addElement("ファイル名一覧");
        lists= new JList(model);
        JScrollPane sp =new JScrollPane();
        sp.getViewport().setView(lists);
        leftPanel.add(sp);

        rightPanel.add(textArea);
        rightPanel.add(systemFieldPanel);
        rightPanel.add(promptFieldPanel);
        rightPanel.add(buttonPanel);

        systemFieldPanel.add(new JLabel("想定システム：（例：ECサイト）"),BorderLayout.NORTH);
        systemFieldPanel.add(systemTextField,BorderLayout.CENTER);

        promptFieldPanel.add(new JLabel("使用例：（例：一人のお届け先に商品を購入する）"),BorderLayout.NORTH);
        promptFieldPanel.add(promptTextField,BorderLayout.CENTER);

        buttonPanel.add(createButton);
        buttonPanel.add(reloadButton);
        buttonPanel.add(configButton);
        readingAPIKeyForPropaties();
        if(readingAPIKeyForPropaties()){
            JOptionPane.showMessageDialog(this,"propatiesからapikeyを読み込みました","通知",JOptionPane.WARNING_MESSAGE);
        }

        addProjectEventListener();
    }
    //リスナの初期設定
    private void addProjectEventListener() {
        try {
            api=AstahAPI.getAstahAPI();
            prjAccessor = api.getProjectAccessor();
            project = prjAccessor.getProject();
            System.out.println("API構築完了");
            prjAccessor.addProjectEventListener(this);

        } catch (ClassNotFoundException e) {
            e.getMessage();
        } catch (ProjectNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Container createLabelPane() {
        JLabel label = new JLabel("hello world");
        JScrollPane pane = new JScrollPane(label);
        return pane;
    }

    @Override
    public void projectChanged(ProjectEvent e) {
        ArrayList<IDiagram> diagrams;
        ArrayList<String> diagramNames = new ArrayList<>();
        try {
            diagrams = ReadingAllDiagrams.ListReadingAllDiagrams(project);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (ProjectNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        for (IDiagram diagramName : diagrams){
            diagramNames.add(diagramName.getName());
        }
        list=diagramNames.toArray(new String[diagramNames.size()]);
        model.removeAllElements();
        for(String names:list){
            model.addElement(names);
        }
    }

    @Override
    public void projectClosed(ProjectEvent e) {
    }
    @Override
    public void projectOpened(ProjectEvent e) {
        ArrayList<IDiagram> diagrams;
        ArrayList<String> diagramNames = new ArrayList<>();
        System.out.println("プラグイン初期化...");
        try {
            diagrams = ReadingAllDiagrams.ListReadingAllDiagrams(project);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (ProjectNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        for (IDiagram diagramName : diagrams){
            diagramNames.add(diagramName.getName());
        }
        list=diagramNames.toArray(new String[diagramNames.size()]);
        model.removeAllElements();
        for(String names:list){
            model.addElement(names);
        }
    }

    @Override
    public void addSelectionListener(ISelectionListener listener) {
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getDescription() {
        return "Show Hello World here";
    }

    @Override
    public String getTitle() {
        return "Generator";
    }

    public void activated() {
    }

    public void deactivated() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Map<String, Map<String,String>> classMap=new LinkedHashMap<>();
        //ボタンごとに処理分け
        if (e.getActionCommand().equals("create")) {
            //生成ボタンが押されたときの処理
            //シナリオ生成処理
            //ここから

            String selectedDiagram= (String) lists.getSelectedValue();
            String system=systemTextField.getText();
            String prompt=promptTextField.getText();
            if(selectedDiagram==null){
                System.out.println("生成元のファイルが選択されていません");
                JOptionPane.showMessageDialog(this,"生成元のファイルが選択されていません","警告",JOptionPane.WARNING_MESSAGE);
                return;
            } else if (APIKEY==null) {
                System.out.println("APIキーが設定されていません");
                JOptionPane.showMessageDialog(this,"APIキーが設定されていません","警告",JOptionPane.WARNING_MESSAGE);
                return;
            }else if(system.equals("")) {
                System.out.println("想定システムが入力されていません");
                JOptionPane.showMessageDialog(this, "想定システムが入力されていません", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            }else if(prompt.equals("")) {
                System.out.println("使用例が入力されていません");
                JOptionPane.showMessageDialog(this, "シナリオが入力されていません", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            } else{
                System.out.println("選択したデータモデル（シナリオ）："+selectedDiagram);
                System.out.println("想定システム："+system);
                System.out.println("シナリオ："+prompt);
                System.out.println("生成中...");

                if(selectedDiagram.equals("datamodel")){
                    System.out.println("datamodelを読み込みます");
                    try {
                        classMap= ReadingDatamodel.ReadingDatamodels(project);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (ProjectNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }else{
                    System.out.println("scenarioを読み込み");
                    try {
                        classMap= ReadingScenarios.ReadingScenario(project,selectedDiagram);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (ProjectNotFoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (InvalidUsingException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                //生成AIへの入力処理,シナリオファイルの生成
                try {
                    ScenarioGenerator.ScenarioGenerate(classMap,system,prompt,APIKEY,MODELVERSION);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ProjectNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (InvalidEditingException ex) {
                    throw new RuntimeException(ex);
                } catch (InvalidUsingException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //ここまで
        }
        if(e.getActionCommand().equals("reload")){
            //ファイル再読み込みボタンが押されたときの処理
            //ここから
            ArrayList<IDiagram> diagrams;
            ArrayList<String> diagramNames = new ArrayList<>();
            model.removeAllElements();
            try {
                diagrams = ReadingAllDiagrams.ListReadingAllDiagrams(project);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (ProjectNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            for (IDiagram diagramName : diagrams){
                diagramNames.add(diagramName.getName());
            }
            list=diagramNames.toArray(new String[diagramNames.size()]);
            //全listプリント
            for(String names:list){
                System.out.println(names);
            }
            for(String names:list){
                model.addElement(names);
            }
            //ここまで
        }
        //設定ボタン押下時の処理
        if(e.getActionCommand().equals("config")){
            configDialog.setVisible(true);
        }


        if(e.getActionCommand().equals("save")){
            if(!modelversionTextField.getText().equals("")){
                MODELVERSION=modelversionTextField.getText();
                System.out.println("モデルバージョン設定完了");
            }
            if(!apikeyTextField.getText().equals("")){
                APIKEY=apikeyTextField.getText();
                System.out.println("APIキー設定完了");
            }else{
                readingAPIKeyForPropaties();
                if(readingAPIKeyForPropaties()){
                    JOptionPane.showMessageDialog(configDialog,"propatiesのapikeyを読み込みました","通知",JOptionPane.WARNING_MESSAGE);
                }
            }
            if(modelversionTextField.getText().equals("")){
                //未入力ではデフォルトに設定
                MODELVERSION="gpt-3.5-turbo-1106";
            }
            configDialog.setVisible(false);
        }


        if(e.getActionCommand().equals("cancel")){
            configDialog.setVisible(false);
        }
    }
    private static boolean readingAPIKeyForPropaties(){
        String apikeys= ReadingAPIKey.ReadingAPIKeyForPropaties();
        if(apikeys!=null){
            Generator.APIKEY=apikeys;
            System.out.println("apikey.propertiesからAPIキーを読み込みました");
            return true;
        }else{
            return false;
        }
    }

}