# JavaからGoogle Sheets APIを呼び出すサンプル

JavaからGoogle APIを呼び出すサンプルを示す。  
手順としては次の通り。
1.  Google側の設定
2.  Java実装

## 1.Google設定

1.1  プロジェクトを作成  
1.2  必要なAPIを有効にする  
1.3  スコープを追加する  
1.4  認証情報を作成する  
1.5  認証情報JSONダウンロード  


### 1.1 プロジェクトを作成
1. [Google Developer Console](https://console.developers.google.com)にアクセスして、プロジェクトを作成する。  
既存のものがあればそれでもOK。

### 1.2 必要なAPIを有効にする
1. 作成したプロジェクトで使用するAPIを有効にする。  
 *   Google Sheets API
 *   Google Drive API

### 1.3 スコープを追加する
1. APIとサービス>認証情報>OAuth同意画面>Google APIのスコープへ、前項で追加したAPIを追加する。

### 1.4 認証情報作成
1. APIとサービス>認証情報>認証情報にて、「認証情報を作成」をクリック。
2. OAuthクライアントIDを選択する。
3. アプリケーションの種類は「その他」を選択し、任意の名前を付け、作成をクリック。

### 1.5 認証情報JSONダウンロード
1. APIとサービス>認証情報にて、作成したクライアントIDのJSONをダウンロードする。


## 2.Javaの実装
2.1 クローン & 取り込み
2.2 認証情報JSON配置
2.3 ビルド & 実行

### 2.1 クローン & 取り込み
1. コマンド`git clone`にてクローン、もしくはファイルとしてダウンロードする。
2. EclipseにてMavenプロジェクトとして取り込む。

### 2.2 認証情報JSON配置
1. 1.6にてダウンロードしたJSONファイルを指定の場所へ格納する。  


```
　ディレクトリ:src/main/resources/settings  
　ファイル名:client_secret.json
```

### 2.3 ビルド & 実行
スプレッドシートIDに任意の値を指定する。  
プロジェクトのディレクトリへ移動し、`mvn package`を実行してビルドする。  
配下の`targetディレクトリ`にjarファイルが生成されていることを確認する。  
コマンド`java -jar java2gas-sample-0.0.1-SNAPSHOT-jar-with-dependencies.jar`にて実行。  
更新時刻が変更されていることを確認する。  
また、指定したスプレッドシートの更新時刻が更新されていることを確認する。
