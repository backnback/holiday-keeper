package com.planit.holiday_keeper.domain.test.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class GoogleSheetsService {

  private static final String APPLICATION_NAME = "Holiday Keeper Sheets Reader";
  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  private static final String SHEETS_READONLY = "https://www.googleapis.com/auth/spreadsheets.readonly";

  /**
   * 서비스 계정 키 파일을 사용하여 Google 인증 정보를 생성합니다.
   * @return GoogleCredentials 객체
   * @throws IOException 키 파일을 읽는 중 오류 발생 시
   */
  private GoogleCredentials getCredentials() throws IOException {
    InputStream in = GoogleSheetsService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new IOException("Google 인증 정보 파일을 찾을 수 없습니다 : " + CREDENTIALS_FILE_PATH);
    }
    return GoogleCredentials.fromStream(in).createScoped(Collections.singleton(SHEETS_READONLY));
  }

  /**
   * Google Sheets API 서비스를 초기화하고 반환합니다.
   * @return Sheets 서비스 객체
   * @throws IOException
   * @throws GeneralSecurityException
   */
  private Sheets getSheetsService() throws IOException, GeneralSecurityException {
    GoogleCredentials credentials = getCredentials();
    HttpCredentialsAdapter credentialsAdapter = new HttpCredentialsAdapter(credentials);

    return new Sheets.Builder(
        new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        credentialsAdapter)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  /**
   * 지정된 스프레드시트 ID와 범위에서 데이터를 읽어옵니다.
   * @param spreadsheetId 구글 시트의 고유 ID
   * @param range 가져올 데이터의 범위 (예: "Sheet1!A1:E10")
   * @return 데이터가 포함된 2차원 리스트 (List of List of Object)
   */
  public List<List<Object>> getSheetData(String spreadsheetId, String range) {
    try {
      Sheets service = getSheetsService();
      ValueRange response = service.spreadsheets().values()
          .get(spreadsheetId, range)
          .execute();

      List<List<Object>> values = response.getValues();
      if (values == null || values.isEmpty()) {
        log.warn("spreadsheetId: {} and range: {}에 데이터가 없습니다", spreadsheetId, range);
        return Collections.emptyList();
      }
      log.info("Google 시트에서 {}개의 rows를 가져왔습니다..", values.size());
      return values;

    } catch (IOException | GeneralSecurityException e) {
      log.error("데이터 가져오기 실패 - Google 시트", e);
      throw new RuntimeException("데이터 가져오기 실패 - Google 시트", e);
    }
  }

  /**
   * 특정 시트의 모든 데이터를 가져옵니다.
   * @param spreadsheetId 구글 시트의 고유 ID
   * @param sheetName 시트 이름 (기본값: "Sheet1")
   * @return 데이터가 포함된 2차원 리스트
   */
  public List<List<Object>> getAllSheetData(String spreadsheetId, String sheetName) {
    String range = sheetName + "!A:Z";
    return getSheetData(spreadsheetId, range);
  }

  /**
   * 기본 시트(Sheet1)의 모든 데이터를 가져옵니다.
   * @param spreadsheetId 구글 시트의 고유 ID
   * @return 데이터가 포함된 2차원 리스트
   */
  public List<List<Object>> getAllSheetData(String spreadsheetId) {
    return getAllSheetData(spreadsheetId, "Sheet1");
  }
}
