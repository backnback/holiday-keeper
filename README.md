# Holiday Keeper

Nager.Date 무인증 API를 활용한 전 세계 공휴일 데이터 관리 서비스

최근 5년(2020-2025)의 전 세계 공휴일 데이터를 저장·조회·관리하는 Mini Service

<br>

## ✔️ 목차



<br>
<br>

## ✔️ 프로젝트 개요


| **항목** | **내용** |
| --- | --- |
| **프로젝트명** | Holiday Keeper API |
| **설명** | 공휴일 관리 시스템 API |
| **API 버전** | v1.0.0 |
| **OpenAPI 버전** | 3.0.1 |
| **Base URL** | `https://api.backnback.com/api/v1` |
| **문서 버전** | 1.0.0 |
| **최종 수정일** | 2025-06-25 |

<br>

### 목표

* 외부 API 두 개만으로 최근 5년(2020 ~ 2025)의 전 세계 공휴일 데이터를 저장·조회·관리하는 Mini Service 구현

<br>

### 사용 외부 API

- **가능한 국가 목록** : `GET https://date.nager.at/api/v3/AvailableCountries`
- **공휴일 데이터** : `GET https://date.nager.at/api/v3/PublicHolidays/{year}/{countryCode}`

<br>
<br>

## ✔️ 주요 기술 스택


| **Backend** |  |
| --- | --- |
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 3.4.2 |
| 데이터베이스 | 인메모리 H2 |
| ORM | JPA(Hibernate) |
| 테스트 | JUnit 5 |
| 문서화 | Swagger UI |
| CI | Github Actions CI |


<br>
<br>

## ✔️ 빌드 & 실행 방법

### 실행 단계

1. Github 레포지토리 클론
    
    ```bash
    git clone https://github.com/backnback/holiday-keeper
    cd holiday-keeper
    ```
    

1. 애플리케이션 실행
    
    ```bash
    ./gradlew bootRun
    ```
    

- 테스트 실행
    
    ```bash
    ./gradlew clean test
    ```
    ![테스트 실행 성공 스크린샷](./docs/images/screenshot1.png)
    ![테스트 실행 성공 스크린샷](./docs/images/screenshot2.png)

<br>


### 접근 URL

- **애플리케이션(로컬)** : http://localhost:8090  ⇒  swagger-ui로 이동
- **Swagger UI** : http://localhost:8090/swagger-ui/index.html
- **H2 Console** : http://localhost:8090/h2-console
    - JDBC URL : `jdbc:h2:mem:hk_db`
    - Username : `sa`
    - Password : (공백)

<br>

### 최초 실행 시 데이터 적재 발생

애플리케이션 시작과 함께 최근 5년간의 전 세계 공휴일 데이터가 자동으로 적재됩니다. (약 5초 소요)

<br>
<br>

## ✔️ 공통 응답

### 성공 응답 예시

```json
{
  "resultCode": "200",
  "msg": "나라별 공휴일 개수 조회 성공",
  "data": {
	  ...
  }
}
```

<br>

### 에러 응답 예시

```json
{
  "timestamp": "2025-06-25T00:55:02.8142141",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "입력값이 올바르지 않습니다."
}
```

<br>

### 커스텀 페이지네이션 응답 예시

```json
{
  "page": 0,
  "size": 0,
  "totalElements": 0,
  "totalPages": 0,
  "empty": true,
  "numberOfElements": 0,
  "content": [
    {
      "id": 233,
      "date": "2025-10-13",
      "localName": "Columbus Day",
      "name": "Columbus Day",
      "countryCode": "US",
      "global": false,
      "counties": [
        "string"
      ],
      "launchYear": 0,
      "types": [
        "PUBLIC"
      ]
    }
  ],
  "first": true,
  "last": true,
  "offset": 0
}
```

<br>
<br>

## ✔️ API 명세

### 1. 공휴일 목록 조회 (페이징, 필터링, 정렬)

```
GET /api/v1/holidays
```

- **Query Parameters**
    
    
    | 파라미터 | 타입 | 필수 | 기본값 | 설명 |
    | --- | --- | --- | --- | --- |
    | page | int | N | 0 | 페이지 번호 (0부터 시작) |
    | size | int | N | 10 | 페이지 사이즈 |
    | year | Integer | N | - | 연도 필터 (2021-2025) |
    | countryCode  | String | N | - | 국가 코드 (예: KR, US) |
    | from | LocalDate | N | - | 시작 날짜 (YYYY-MM-DD) |
    | to | LocalDate | N | - | 종료 날짜 (YYYY-MM-DD) |
    | type | String | N | - | 공휴일 타입 (PUBLIC, BANK 등) |
    | name | String | N | - | 공휴일 이름 검색 |
    | sort | String | N | asc | 정렬 기준 (date) |
    

<br>

- **Response 200 OK**
    
    ```json
    {
      "resultCode": "200",
      "msg": "공휴일 목록 조회 성공",
      "data": {
        "page": 0,
        "size": 10,
        "totalElements": 15,
        "totalPages": 2,
        "empty": true,
        "numberOfElements": 0,
        "content": [
          {
            "id": 233,
            "date": "2025-10-13",
            "localName": "Columbus Day",
            "name": "Columbus Day",
            "countryCode": "US",
            "global": false,
            "counties": [
              "string"
            ],
            "launchYear": 0,
            "types": [
              "PUBLIC"
            ]
          }
        ],
        "first": true,
        "last": false,
        "offset": 0
      }
    }
    ```
    

<br>

<br>

### 2. 특정 연도 및 국가 데이터 동기화 (Refresh)

```
POST /api/v1/holidays/sync
```

- **Request Body**
    
    ```json
    {
      "year": 2025,
      "countryCode": "KR"
    }
    ```
    
    - **Validation Rules**
        - `year` : 필수
        - `countryCode` : 필수

<br>

- **Response 204 No Content**
    
    ```json
    {
      "resultCode": "204",
      "msg": "동기화 완료"
    }
    ```
    

<br>

<br>

### 3. 특정 연도 및 국각 데이터 전체 삭제

```
DELETE /api/v1/holidays/{countryCode}/{year}
```

- **Path Parameters**
    
    
    | 파라미터 | 타입 | 필수 | 기본값 | 설명 |
    | --- | --- | --- | --- | --- |
    | year | Integer | Y | - | 연도 필터 (2021-2025) |
    | countryCode  | String | Y | - | 국가 코드 (예: KR, US) |
    - path parameters null 불가
    

<br>

- **Response 204 No Content**
    
    ```json
    {
      "resultCode": "204",
      "msg": "15개 데이터 삭제 완료"
    }
    ```
    

<br>

<br>

### 4. 국가별 공휴일 개수 조회  (페이징)

```
GET /api/v1/holidays/countries
```

- **Query Parameters**
    
    
    | 파라미터 | 타입 | 필수 | 기본값 | 설명 |
    | --- | --- | --- | --- | --- |
    | page | int | N | 0 | 페이지 번호 (0부터 시작) |
    | size | int | N | 30 | 페이지 사이즈 |
    | year | Integer | N | 2025 | 연도 필터 (2021-2025) |
    | sort | String | N | desc | 정렬 기준 (holidayCount) |
    - `holidayCount` : 특정 연도, 특정 국가의 공휴일 수
    

<br>

- **Response 200 OK**
    
    ```json
    {
      "resultCode": "200",
      "msg": "나라별 공휴일 개수 조회 성공",
      "data": {
        "page": 0,
        "size": 30,
        "totalElements": 117,
        "totalPages": 4,
        "empty": false,
        "numberOfElements": 30,
        "content": [
          {
            "countryCode": "VE",
            "name": "Venezuela",
            "holidayCount": 34
          },
          {
            "countryCode": "ES",
            "name": "Spain",
            "holidayCount": 32
          },
    
    			...
    
          {
            "countryCode": "JP",
            "name": "Japan",
            "holidayCount": 16
          }
        ],
        "first": true,
        "last": false,
        "offset": 0
      }
    }
    ```
    

<br>

<br>

## ✔️ 데이터베이스 설계

### ERD

```
Countries (국가)
├── id (PK)
├── country_code (UK)
├── name
├── created_at
└── modified_at

Holidays (공휴일)
├── id (PK)
├── country_id (FK)
├── date
├── name
├── local_name
├── holiday_year
├── launch_year
├── global
├── counties (CSV)
├── types (CSV)
├── created_at
├── modified_at
└── UK(country_id, date, name)

```

<br>

### 인덱스 전략  (성능 최적화)

```sql
indexes = {
	@Index(name = "idx_year", columnList = "holidayYear"),
	@Index(name = "idx_country_year", columnList = "country_id, holidayYear")
}
```

- 자주 사용되는 쿼리

<br>
<br>

## ✔️ 주요 기능

### 1. 데이터 적재

- **최초 실행 시** : 최근 5년(2021-2025) × 전체 국가 데이터 일괄 적재
- **병렬 처리** : ForkJoinPool(30)을 활용한 API 호출
- **소요 시간** : 약 5초 내외  (117 국가 × 5년)

<br>

### 2. 데이터 재동기화 (Upsert 패턴)

```sql
INSERT INTO holidays (...) VALUES (...)
ON DUPLICATE KEY UPDATE
    local_name = VALUES(local_name),
    modified_at = :syncTime
```

- 신규 데이터는 INSERT, 기존 데이터는 UPDATE
    - `MySQL ON DUPLICATE KEY UPDATE` 활용
- 동기화 시점 기반으로 외부 API에서 누락된 데이터 DB에서 자동 삭제
- 데이터 일관성 보장

<br>

### 3. 다양한 검색 옵션

- **기본 필터** : 연도, 국가, 날짜 범위
- **고급 필터** : 공휴일 타입, 이름(영문명) 검색
- **정렬 및 페이징** : 날짜순 정렬

<br>


### 4. 자동 배치 스케줄링

- 매년 1월 2일 01:00 KST에 전년도·금년도 데이터 자동 동기화
    
    ```java
    @Scheduled(cron = "0 0 1 2 1 ?", zone = "Asia/Seoul")
    ```
    
- `application.yml`
    
    ```yaml
    schedule.useInit: true      # 초기 데이터 로딩 여부
    schedule.useSchedule: true  # 스케줄러 활성화 여부
    ```
    

<br>

### 5. 예외 처리 전략

- **전역 예외 핸들러** : `@RestControllerAdvice`
- **커스텀 예외** : `CustomException + ErrorCode`
    
    ```java
    @Getter
    @AllArgsConstructor
    public enum ErrorCode {
    
      // 공통 에러
      INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
      INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "타입이 올바르지 않습니다."),
      ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "엔티티를 찾을 수 없습니다."),
      INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    
      // Country 관련
      INVALID_COUNTRY_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 국가 코드입니다"),
      COUNTRY_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "국가 코드는 필수 입력값입니다."),
    
      // Holiday 관련
      INVALID_YEAR_RANGE(HttpStatus.BAD_REQUEST, "연도는 최근 5년 범위 내에서만 가능합니다."),
      YEAR_REQUIRED(HttpStatus.BAD_REQUEST, "연도는 필수 입력값입니다.");
      
    
      private final HttpStatus httpStatus;
      private final String message;
    }
    ```
    
- **일관된 응답** : `ErrorResponse` 래퍼 클래스


<br>

<br>

## ✔️ 테스트

### 테스트 실행

```bash
# 전체 테스트 실행
./gradlew clean test

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

<br>

### 테스트 범위

- **통합 테스트**
    - 전체 API 엔드포인트 시나리오 검증
    - Upsert 로직 및 데이터 일관성 검증
    - 예외 처리 테스트
- **스케줄러 테스트**
    - 자동 배치 동작 확인

<br>

### 주요 테스트 시나리오

1. **공휴일 목록 조회** - 기본 조회, 필터링, 페이징
2. **데이터 동기화** - Refresh API 및 Upsert 및 삭제 로직 검증
3. **데이터 삭제** - 특정 연도/국가 데이터 삭제
4. **통계 조회** - 국가별 공휴일 개수 조회
5. **예외 처리** - 잘못된 국가코드, 연도 범위 등
6. **스케줄러** - 매년 정기 배치 동작 확인

<br>

<br>


## ✔️ 성능 최적화

### 1. 데이터베이스 최적화

- **인덱스 전략**
    - 자주 사용되는 쿼리 패턴 최적화
- **N+1 문제 해결**
    - Batch Fetching 전략 사용 (`Hibernate batch_fetch_size = 100`)
- **쿼리 최적화  (동기화 로직)**
    - 선 조회 후 insert/update 쿼리 처리  ⇒   `upsert()` 로직 Native 쿼리로 구현

<br>

### 2. 네트워크 및 병렬 처리

- **병렬 구조로 외부 API 호출**
    - **커스텀 스레드풀**(`ForkJoinPool(30)`) 설정으로 동시 처리
    - Spring Bean 초기화 로직과 다른 스레드풀 사용
- **HTTP Connection Pool**
    - WebClient 최대 100개 **커넥션 재사용**
    - **응답 타임아웃 설정** (30초)

<br>

### 3. 성능 지표  (간단 `K6` 테스트)

```
데이터 적재: 200+ 국가 × 5년 = 약 5초 (3 ~ 5초)
단일 조회: 평균 10ms
```

- **인덱스 적용 전**
    ![테스트 실행 성공 스크린샷](./docs/images/no-index.png)
    
- **인덱스 적용 후**
    ![테스트 실행 성공 스크린샷](./docs/images/index.png)
    

<br>

<br>



## ✔️ 트러블슈팅

### DB 설계

1. Country 엔티티를 설정한 이유는 추후 확장성에서 **나라별 공휴일 수**나, UI에서 **지원하는 국가 목록**을 보여주고 선택할 수 있도록 하는 등의 용도로 사용될 수 있다고 판단
2. Country 엔티티의 `PK`는 `countryCode`가 UN에서 관리하는 공식 표준으로 겹치지 않다고 판단했지만, 코드 일관성을 위해서 `PK`는 기존의 `id`로 유지하고 countryCode는 UK로 설정

<br>

<br>

### 외부 API 호출

- **문제 상황**
    - 모든 국가에 대하여 5년간 데이터 적재 시 **순차 처리**를 하는 경우, 너무 오래 걸린다 판단하여 **병렬 설정**
    - `parallelStream()`을 설정했으나, **`ForkJoinPool.commonPool()`(JVM 전체가 공유하는 기본 공용 스레드 풀)**을 사용하여 Spring Bean 초기화 시 **데드락 발생**
        
        ```yaml
        2025-06-23T17:35:19.426+09:00  INFO 106096 --- [nPool-worker-18] o.s.b.f.s.DefaultListableBeanFactory     : Creating singleton bean 'org.springframework.data.jpa.repository.support.JpaEvaluationContextExtension' in thread "ForkJoinPool.commonPool-worker-18" while other thread holds singleton lock for other beans [org.springframework.data.jpa.repository.support.JpaEvaluationContextExtension, apiV1HolidayController, fetchHolidayScheduler]
        2025-06-23T17:35:19.425+09:00  INFO 106096 --- [nPool-worker-20] o.s.b.f.s.DefaultListableBeanFactory     : Creating singleton bean 'org.springframework.data.jpa.repository.support.JpaEvaluationContextExtension' in thread "ForkJoinPool.commonPool-worker-20" while other thread holds singleton lock for other beans [apiV1HolidayController, fetchHolidayScheduler]
        2025-06-23T17:35:19.426+09:00  INFO 106096 --- [onPool-worker-1] o.s.b.f.s.DefaultListableBeanFactory     : Creating singleton bean 'org.springframework.data.jpa.repository.support.JpaEvaluationContextExtension' in thread "ForkJoinPool.commonPool-worker-1" while other thread holds singleton lock for other beans [org.springframework.data.jpa.repository.support.JpaEvaluationContextExtension, apiV1HolidayController, fetchHolidayScheduler]
        2025-06-23T17:35:19.426+09:00  INFO 106096 --- [nPool-worker-17] o.s.b.f.s.DefaultListableBeanFactory     : Creating singleton bean 'org.springframework.data.jpa.repository.support.JpaEvaluationContextExtension' in thread "ForkJoinPool.commonPool-worker-17" while other thread holds singleton lock for other beans [org.springframework.data.jpa.repository.support.JpaEvaluationContextExtension, apiV1HolidayController, fetchHolidayScheduler]
        2025-06-23T17:35:19.426+09:00  INFO 106096 --- [nPool-worker-15] o.s.b.f.s.DefaultListableBeanFactory     : Creating singleton bean 'org.springframework.data.jpa.repository.support.JpaEvaluationContextExtension' in thread "ForkJoinPool.commonPool-worker-15" while other thread holds singleton lock for other beans [org.springframework.data.jpa.repository.support.JpaEvaluationContextExtension, apiV1HolidayController, fetchHolidayScheduler]
        
        ```
        

<br>

- **해결 시도 1  -  `@Async`**
    - @Async로 실행 타이밍을 늦춰도 내부에서 여전히 `parallelStream()`이 `commonPool()`을 사용하여 근본적 해결이 되지 않음

- **최종 해결  -  `Custom ForkJoinPool`**
    - `new ForkJoinPool(10)`으로 **독립적인 스레드 풀**을 생성하여 Spring 초기화 과정과 완전히 분리하여 충돌 방지

<br>

<br>

### upsert() 로직 Native 쿼리 구현

- **문제 상황**
    1. 외부 API (https://date.nager.at/api/v3/PublicHolidays/2022/AL 등)에서 **중복된 데이터**가 꽤 많이 존재하고 있음을 발견
        
        ```java
        [
          {
            "date": "2022-01-03",
            "localName": "Viti i Ri",
            "name": "New Year's Day",
            "countryCode": "AL",
            "fixed": false,
            "global": true,
            "counties": null,
            "launchYear": null,
            "types": [
              "Public"
            ]
          },
          {
            "date": "2022-01-03",
            "localName": "Viti i Ri",
            "name": "New Year's Day",
            "countryCode": "AL",
            "fixed": false,
            "global": true,
            "counties": null,
            "launchYear": null,
            "types": [
              "Public"
            ]
          },
          {  .....
        ```
        
        - 단순히 `saveAll()` 하는 경우 중복된 데이터가 저장된다.
    2. `upsert()` 로직을 MySQL의 `MySQL ON DUPLICATE KEY UPDATE` 활용하기 위해서 **Unique Key** 설정이 필요
        - 현재 데이터 상황에 따라 **복합 Unique Key**(`date`, `name`, `country_id`)를 설정했을 때, **데이터 중복**으로 인한 **에러 발생**

<br>

<br>

### Holiday 엔티티 리팩토링

- **문제 상황**
    - 처음 설계 시 외부 API 가져오는 데이터의 컬렉션 타입 필드(`counties`와 `types`)를  ElementCollection 기능으로 연관 테이블로 설계
        
        ```java
        @ElementCollection(targetClass = HolidayTypes.class, fetch = FetchType.LAZY)
        @Enumerated(EnumType.STRING)
        @CollectionTable(
            name = "holiday_types",
            joinColumns = @JoinColumn(name = "holiday_id")
        )
        @Column(name = "type")
        @Builder.Default
        private List<HolidayTypes> types = new ArrayList<>();
        ```
        
    - 요구 사항 구현에 중요하지 않는 필드이며, `upsert()` Native 쿼리 구현 시 코드 복잡도가 너무 증가했다.

<br>

- **해결**
    
    ```java
    @Column(length = 100)
    private String types;
    
    public List<HolidayTypes> getTypes() {
      if (types == null || types.trim().isEmpty()) {
        return List.of();
      }
      List<String> list = Arrays.asList(types.split(","));
      return HolidayTypes.fromStringList(list);
    }
    
    public void setTypes(List<String> list) {
      if (list == null || list.isEmpty()) {
        types = null;
      } else {
        types = String.join(",", HolidayTypes.validateList(list));
      }
    }
    ```
    
    - 연관 테이블이 아닌 DB에는 구분자(`,`)를 사용하여 CSV 형식으로 저장, 애플리케이션 레벨에서는 리스트로 사용
    - 헬퍼 메서드를 추가하여 보완

<br>

<br>

### 삭제 로직

- **문제 상황 1**
    - upsert 메서드 구현으로 중복 데이터 삽입 방지와 기존 데이터 업데이트를 모두 해결했지만, **만약 외부 API에서 삭제된 데이터를 현재 DB에서 가지고 있는 경우 삭제해야 하는 로직**이 필요하다고 생각
        - 이를 단순히 모두 지우고 새롭게 넣는 방식(**Replace 방식**)은 **비효율적**이라고 판단
    - **해결 1**
        - `upsert()` 시 해당하는 데이터의 **수정일시**가 바뀌게 되고 이 **수정일시**를 기준으로 판단해서 제거하는 로직을 생각
        - **기존의 생성일시과 수정일시**은 Native Query문의 `NOW()`로 설정
            - 이 경우 미세하게 수정일시가 달라질 수 있다   ⇒  **외부에서 공통된 수정일을 받아서 처리**

<br>

- **문제 상황 2 -** `fetchAllByYears(int years)`
    
    병렬 구조 내부의 메서드인 `fetchByYearAndCountry()`에서 `LocalDateTime syncTime = LocalDateTime.now();`로 날짜를 삽입했더니 병렬 실행마다 수정일이 달라져 서로 데이터를 지워버리는 상황 발생
    
    - **해결 2**
        
        ```java
        
              LocalDateTime syncTime = LocalDateTime.now();
              int currentYear = syncTime.getYear();
              try (ForkJoinPool customPool = new ForkJoinPool(10)) {
                for (int i = 0; i < years; i++) {
                  int year = currentYear - i;
                  customPool.submit(() ->
                      countries.parallelStream().forEach(country -> {
                        try {
                          fetchByYearAndCountry(country, year);
                        } catch (Exception e) {
                          log.error("국가 {} 데이터 동기화 실패", country.getCountryCode(), e);
                        }
                      })
                  ).get();
                }
              }
        ```
        
        - 병렬 구조 바깥에서 넣어주도록 설정  **(동기화라는 작업에 대한 생성(수정)일시 일괄 통일)**

<br>

- **문제 발생 3**
    - **Repository 파라미터**로 공통된 수정 일시를 넣어도 **H2 데이터베이스**의 **마이크로초 정밀도 손실이 발생할 수 있다.**
        - 동기화로 방금 저장한 것도 같지 않다고 판단하여 제거
        - **해결 3**
            - **1초의 여유**를 설정하여 안전하게 동기화

<br>

<br>

### 비동기 vs 동기

- 초기에 클라이언트 역할로 HttpURLConnection을 사용하여 외부 API 호출을 구현했다.
- 오래된 방식이고 속도 개선을 위해 `WebClient`를 우선 동기 방식으로 도입하고 추후 비동기 전환을 대비했다.
- 외부 API가 Rate Limiting이 없기 때문에 상당한 속도 향상을 기대
    - 자체 테스트 결과
        - 비동기 (flux 이용 배치 처리)  ⇒  8,138 ms
        - 기존 방식  ⇒   11,923 ms   (`ForkJoinPool(10)`일 때)
    - 데이터가 기하급수적으로 늘어나는 외부 API도 아니므로 **오버엔지니어링**으로 판단된다.
    - 오히려 코드가 복잡해져서 유지보수성만 나빠질 것 같으며, 현재 속도가 나쁘지 않기 때문에 동기를 유지
        
        ⇒   **커스텀 스레드풀을 늘리는 방향으로 전환  (속도 향상됨)**


<br>
<br>
<br>


## ✔️ 향후 개선 고려 사항

1. **Redis 캐싱** : 자주 조회되는 데이터 캐시 처리
2. **다국어 지원** : 공휴일 이름 다국어 처리
3. upsert() 로직 **Bulk** 처리


<br>
<br>
<br>

---

**개발자** : River Lee

**개발 기간** : 28 시간

**블로그** : www.blog.backnback.site

---