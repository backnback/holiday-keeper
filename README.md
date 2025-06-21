
```
holiday-keeper
├─ gradle
│  └─ wrapper
│     ├─ gradle-wrapper.jar
│     └─ gradle-wrapper.properties
├─ gradlew
├─ gradlew.bat
└─ src
   ├─ main
   │  ├─ generated
   │  ├─ java
   │  │  └─ com
   │  │     └─ planit
   │  │        └─ holiday_keeper
   │  │           ├─ api
   │  │           │  └─ holiday
   │  │           │     ├─ controller
   │  │           │     │  └─ ApiV1HolidayController.java
   │  │           │     └─ dto
   │  │           ├─ domain
   │  │           │  └─ holiday
   │  │           │     ├─ entity
   │  │           │     │  ├─ Country.java
   │  │           │     │  └─ Holiday.java
   │  │           │     ├─ repository
   │  │           │     │  └─ HolidayRepository.java
   │  │           │     ├─ scheduler
   │  │           │     │  └─ FetchHolidayScheduler.java
   │  │           │     └─ service
   │  │           │        └─ HolidayService.java
   │  │           ├─ global
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
   │     └─ application.yml
   └─ test
      └─ java
         └─ com
            └─ planit
               └─ holiday_keeper
                  ├─ domain
                  │  └─ holiday
                  │     └─ service
                  │        └─ HolidayServiceTest.java
                  └─ HolidayKeeperApplicationTests.java

```