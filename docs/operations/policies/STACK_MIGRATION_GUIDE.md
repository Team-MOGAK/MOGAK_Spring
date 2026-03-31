# Stack Migration Guide

이 문서는 `MOGAK_Spring`의 공개 스택 마이그레이션 기준 문서다.

## Scope
- 현재 기준: Java 17, Spring Boot 3.5.5, Gradle wrapper 8.10.2
- 최종 목표: Java 25, Spring Boot 4.x
- 기본 전략: 현재 `3.5.x` 브리지 기준선에서 `4.x` 최종 단계로 올라간다.

## Why Staged Migration
- Spring Boot 2.7.x는 Java 21까지의 호환성을 기준으로 운영되므로 Java 25의 최종 안착 지점이 될 수 없다.
- Spring Boot 3.5.x는 Java 25와의 호환 범위를 제공하므로 Java 25 도입의 브리지 릴리스로 사용한다.
- Spring Boot 4.x는 Spring Framework 7, Spring Security 7, Hibernate 7.1, Jackson 3.0, Jakarta Servlet 6.1 등을 동반하므로 3.5.x를 거치지 않으면 변경 폭이 너무 커진다.
- Gradle은 Java 25를 안정적으로 사용하려면 9.1+를 기준으로 잡는다.

## Final Target
- Java 25
- Spring Boot 4.x
- Gradle 9.1+
- PostgreSQL
- Flyway
- Jakarta namespace only

## Repo-Specific Hotspots
- JWT:
  - `build.gradle`의 `io.jsonwebtoken:jjwt:0.9.1`
  - `src/main/java/com/mogak/spring/jwt/*`
  - `src/main/java/com/mogak/spring/login/JwtTokenHandler.java`
  - `src/main/java/com/mogak/spring/auth/*`
- OpenAPI / Swagger:
  - `build.gradle`의 `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8`
  - `src/main/java/com/mogak/spring/config/SwaggerConfig.java`
  - `src/main/java/com/mogak/spring/config/WebConfig.java`
  - `src/main/java/com/mogak/spring/jwt/JwtTokenFilter.java`
- Spring Cloud / Feign:
  - `build.gradle`의 `org.springframework.cloud:spring-cloud-dependencies:2025.0.0`
  - `src/main/java/com/mogak/spring/auth/AppleClient.java`
  - `src/main/java/com/mogak/spring/config/FeignClientConfig.java`
- AWS:
  - `build.gradle`의 `io.awspring.cloud:spring-cloud-aws-starter`
  - `build.gradle`의 `io.awspring.cloud:spring-cloud-aws-starter-s3`
  - `src/main/java/com/mogak/spring/service/AwsS3Service.java`
  - `src/main/java/com/mogak/spring/config/WebConfig.java`
  - `src/main/resources/application-dev.yml`
- Legacy/nullability:
  - `build.gradle`의 `javax.xml.bind:jaxb-api:2.3.1`
- Build/JDK pins:
  - `build.gradle`의 Java toolchain 17
  - `gradle/wrapper/gradle-wrapper.properties`의 Gradle 8.10.2

## Mandatory Migration Path
### Stage 0. Baseline Lock
- `2.7.17` 기준선을 확보했다.
- 현재 테스트를 통과하는 기준 커밋을 확보한다.
- deprecated API와 경고를 수집한다.

### Stage 1. Boot 3.5 Bridge
- Spring Boot `3.5.5`와 Java toolchain 17 기준선을 확보했다.
- `javax.*` import는 `jakarta.*` 기준으로 전환했다.
- `springdoc-openapi`는 v2 starter로 교체했다.
- Spring Cloud는 Boot 3.5 호환 release train으로 올렸다.
- AWS는 AWSpring 3.x와 S3Client 기준으로 재정렬했다.
- 남은 작업은 JWT 교체 검토와 deprecated 경고 정리다.

### Stage 2. Java 25 Enablement
- Gradle wrapper를 9.1+로 올린다.
- Lombok을 JDK 25 지원 버전으로 올린다.
- Java toolchain과 release target을 25로 올린다.
- JDK 25에서 애플리케이션 기동과 테스트를 확인한다.
- `jdeps`로 JDK 내부 API 의존 여부를 확인한다.

### Stage 3. Boot 4 Finalization
- Spring Boot를 4.0.x로 올린다.
- Spring Framework 7, Spring Security 7, Hibernate 7.1, Jackson 3.0 기준으로 코드를 재검증한다.
- Servlet 6.1, Validation 3.1, Persistence 3.2 기준의 API 변화와 동작 차이를 점검한다.
- Boot 4에서 지원되지 않는 선택지는 제거한다.

## Dependency Direction
### Build Tooling
- Gradle wrapper: `8.10.2 -> 9.1+`
- Lombok: `1.18.26 -> JDK 25 지원 버전`

### Framework
- Spring Boot: `3.5.5 -> 4.0.x`
- Spring Cloud BOM: `2025.0.0 -> Boot 4.x 호환 release train`

### API Docs
- `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8`
  - Boot 4.x 전환 시 호환 라인을 다시 맞춘다.

### JWT
- `io.jsonwebtoken:jjwt:0.9.1`
  - 최신 API, impl, jackson 분리 구조로 이동을 우선 검토한다.

### AWS
- `io.awspring.cloud:spring-cloud-aws-starter:3.4.0`
- `io.awspring.cloud:spring-cloud-aws-starter-s3:3.4.0`
  - Boot 4.x 전환 시 호환 라인과 설정 키를 다시 확인한다.

### JAXB / Legacy Java EE
- `javax.xml.bind:jaxb-api:2.3.1`
  - Jakarta 전환 또는 의존 제거 여부를 먼저 판단한다.

## Runtime Concerns on Java 25
- 기본 charset은 UTF-8 기준으로 본다.
- locale data 차이로 날짜/통화 포맷 결과가 달라질 수 있다.
- JDK 내부 API에 대한 reflective access는 허용되지 않는 방향을 기본으로 본다.
- 오래된 라이브러리에서 `InaccessibleObjectException` 또는 경고가 나오는지 확인한다.

## Execution Order
1. 문서와 현재 기준선을 고정한다.
2. Gradle과 Lombok을 정리한 뒤 Java 25를 활성화한다.
3. JWT와 남은 deprecated 경고를 정리한다.
4. 마지막으로 Boot 4.x로 올리고 4.x 전용 회귀 검증을 수행한다.

## Verification Gates
- 각 단계마다 `sh gradlew test`를 기본 게이트로 사용한다.
- Stage 1 이후에는 인증, Swagger/OpenAPI, S3 업로드, Apple login 연동, Feign 호출 경로를 우선 검증한다.
- Stage 2 이후에는 JDK 25에서 테스트와 애플리케이션 기동을 모두 확인한다.
- Stage 3 이후에는 직렬화, JPA 매핑, 보안 필터 동작, 설정 바인딩 회귀를 확인한다.

## Non-Goals
- 이번 가이드는 실제 버전 변경을 자동 수행하지 않는다.
- 이번 가이드는 PostgreSQL/Flyway 실마이그레이션을 즉시 수행하지 않는다.
- 비공개 정책이 필요한 인증/보안 판단 기준은 이 문서에 적지 않는다.
