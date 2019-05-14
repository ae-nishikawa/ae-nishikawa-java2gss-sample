package com.sample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

public class SheetsQuickstart {

	// アプリケーション名
	private static final String APPLICATION_NAME = "SheetsQuickstart";

	// クライアントシークレット
	private static final String CREDENTIALS_FILE_PATH = "/settings/client_secret.json";

	// トークン保管場所
	private static final String TOKENS_DIRECTORY_PATH = System.getProperty("user.dir") + "/.google_credentials/"
			+ APPLICATION_NAME;

	// 権限
	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, DriveScopes.DRIVE);

	// 日時書式
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

	// JSON Factory
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	// リフレッシュトークン取得
	private static Credential getCredentialsRefresh(NetHttpTransport HTTP_TRANSPORT) throws Exception {

		// クライアントシークレットの読み込み
		InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		if (HTTP_TRANSPORT == null) {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		}

		// Google認証コードフロー生成
		Builder builder = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline");

		// AccessType「offline」でアクセストークンを取得
		// (AccessTokenのexpire前60秒以後のAPI呼出時に自動refreshが行われるようになる)
		builder.addRefreshListener(new CredentialRefreshListener() {
			@Override
			public void onTokenResponse(Credential credential, TokenResponse tokenResponse) throws IOException {
				System.out.println("AccessTokenのrefreshが成功しました。)");
			}

			@Override
			public void onTokenErrorResponse(Credential credential, TokenErrorResponse tokenErrorResponse)
					throws IOException {
				System.out.println("AccessTokenのrefreshが失敗しました。");
			}
		});

		GoogleAuthorizationCodeFlow flow = builder.build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	// sample
	public static void main(String... args) throws IOException, GeneralSecurityException {

		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

		// 対象スプレッドシートID
		final String spreadsheetId = "spreadsheetID";

		// 対象範囲
		final String range = "シート1!A1";

		try {

			Credential credential = getCredentialsRefresh(HTTP_TRANSPORT);

			System.out.println("有効期限:"+credential.getExpiresInSeconds()+"[s]");

			// Sheet API取得
			Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME)
					.build();

			// 更新前の値取得
			// 対象範囲のデータの取得
			ValueRange existValsRange = service.spreadsheets().values().get(spreadsheetId, range).execute();
			List<List<Object>> existVals = existValsRange.getValues();

			// とりあえず表示
			System.out.println("Before:" + existVals.get(0).get(0));

			// 更新処理開始
			System.out.println("Update start!");

			// 更新用データ準備
			List<List<Object>> newVals = new ArrayList<List<Object>>();

			List<Object> newRow = new ArrayList<Object>();

			String now = DATE_TIME_FORMATTER.format(LocalDateTime.now());

			newRow.add("更新時刻:" + now);

			newVals.add(newRow);

			ValueRange newValRange = new ValueRange().setValues(newVals);

			// 更新処理
			System.out.println("execute!!");
			service.spreadsheets().values().update(spreadsheetId, range, newValRange)
					.setValueInputOption("USER_ENTERED").execute();

			// 更新処理完了
			System.out.println("Update end!");

			// 更新後の値取得
			ValueRange updatedValsRange = service.spreadsheets().values().get(spreadsheetId, range).execute();
			List<List<Object>> updatedVals = updatedValsRange.getValues();

			System.out.println("After:" + updatedVals.get(0).get(0));

		} catch (Exception e) {
			// 例外発生
			e.printStackTrace();

			// トークン関連の例外の場合
			if (e instanceof TokenResponseException) {
				TokenResponseException tex = (TokenResponseException) e;
				System.out.println(tex.getHeaders());
				System.out.println(tex.getStatusCode());
			}
		}
	}
}