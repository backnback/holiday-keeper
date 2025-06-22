
```
holiday-keeper
├─ gradle
│  └─ wrapper
│     ├─ gradle-wrapper.jar
│     └─ gradle-wrapper.properties
├─ gradlew
├─ gradlew.bat
├─ README.md
└─ src
   ├─ main
   │  ├─ generated
   │  ├─ java
   │  │  └─ com
   │  │     └─ planit
   │  │        └─ holiday_keeper
   │  │           ├─ domain
   │  │           │  └─ holiday
   │  │           │     ├─ controller
   │  │           │     │  └─ ApiV1HolidayController.java
   │  │           │     ├─ dto
   │  │           │     │  ├─ external
   │  │           │     │  │  ├─ AvailableCountriesApiResponse.java
   │  │           │     │  │  └─ PublicHolidaysApiResponse.java
   │  │           │     │  ├─ request
   │  │           │     │  └─ response
   │  │           │     │     └─ HolidayResponse.java
   │  │           │     ├─ entity
   │  │           │     │  ├─ Country.java
   │  │           │     │  └─ Holiday.java
   │  │           │     ├─ enums
   │  │           │     │  └─ HolidayTypes.java
   │  │           │     ├─ repository
   │  │           │     │  ├─ CountryRepository.java
   │  │           │     │  └─ HolidayRepository.java
   │  │           │     ├─ scheduler
   │  │           │     │  └─ FetchHolidayScheduler.java
   │  │           │     └─ service
   │  │           │        ├─ CountryService.java
   │  │           │        └─ HolidayService.java
   │  │           ├─ global
   │  │           │  ├─ config
   │  │           │  │  └─ WebClientConfig.java
   │  │           │  ├─ exceptions
   │  │           │  │  ├─ CustomException.java
   │  │           │  │  ├─ ErrorCode.java
   │  │           │  │  ├─ ErrorResponse.java
   │  │           │  │  └─ GlobalExceptionHandler.java
   │  │           │  ├─ jpa
   │  │           │  │  └─ BaseEntity.java
   │  │           │  ├─ rsData
   │  │           │  │  └─ RsData.java
   │  │           │  └─ util
   │  │           │     └─ Empty.java
   │  │           └─ HolidayKeeperApplication.java
   │  └─ resources
   │     ├─ application-test.yml
   │     └─ application.yml
   └─ test
      └─ java
         └─ com
            └─ planit
               └─ holiday_keeper
                  ├─ domain
                  │  └─ holiday
                  │     ├─ scheduler
                  │     │  └─ FetchHolidaySchedulerTest.java
                  │     └─ service
                  │        └─ HolidayServiceTest.java
                  └─ HolidayKeeperApplicationTests.java

```