# astahファイルの設定
- ファイル名：任意
- 画面遷移モデルのパッケージ名:ScreenTransitionModel
- 画面遷移モデルのクラス図名：ScreenTransitionModel
- 画面遷移モデルのクラスの:ScreenTransitionModelパッケージ内
- 生成されるオブジェクト図の配置:scenarioパッケージ内
- 生成されるオブジェクト図の名前:scenario0.1.2...

##### データモデルのクラスとクラス図はdatamodelパッケージ内に定義してください


### openAI APIkey の設定
設定方法は2通り
###### 1.propertiesファイルに設定
1. scenarios-1.0-SNAPSHOT.jarと同じ階層にsettingsというフォルダを作成
2. settingsフォルダの中にapikey.properties ファイルを作成
3. apikey.propertiesに以下を記述する
```
apikey=設定するAPIキー
```
###### 2. astahプラグインで設定
1. プラグインの設定から，apiキーを直接入力する
   **apiキーの情報は毎回入力する必要あり**
2. プラグインのjarファイル(snapshot-1.0.jar）api.propertiesファイルをsettingsフォルダの中に作成