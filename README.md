# MOGAK
- 모각 Spring 서버

## Quick Links
- [Local Run](#local-run)
- [Verification](#verification)
- [REST API Document](#rest-api-document)
- [Operations Docs](#operations-docs)
- [Project Information](#project-information)

## Back-end Member
|![오지민](https://avatars.githubusercontent.com/u/27052233?v=4)| ![이현석](https://avatars.githubusercontent.com/u/110045522?v=4) |
|:----------------------------------------------------------:|:-------------------------------------------------------------:|
|              [오지민](https://github.com/Ojimin)              |              [이현석](https://github.com/Hyunstone)              |
|               Post API, Comment API, 인프라/DB                |                  모각 상태 관리, 인증/로그인, 스택 마이그레이션                  |

---

## Service Information
1. 모각
   - 카테고리와 기간, 반복주기를 설정하여 기간에 맞게 각 요일의 조각이 생성되고 자기계발을 진행할 수 있음
   - 조각 진행 후 레포트 생성
2. 네트워킹
   - 전체 네트워킹 : 원하는 지역, 카테고리별로 설정하여 다른 유저의 레포트를 볼 수 있음
   - 페이스메이커 네트워킹 : 내가 팔로우한 유저의 레포트를 볼 수 있음
+ 서비스 소개: https://ivy-soapwort-586.notion.site/MOGAK-658ae1222840406ea9fbca137c1bac5c


## Tech Stacks
- Java 25
- Spring Boot 4.0.2
- Spring Security
- Spring Data JPA
- PostgreSQL
- Springdoc OpenAPI
- Gradle 9.1.0

![image](https://github.com/Team-MOGAK/MOGAK_Spring/assets/27052233/d23cf2c1-5cbf-43da-93a2-f5a01728561e)


## Project Information
- JDK: 25
- Spring Boot: 4.0.2
- Gradle: 9.1.0
- Database: PostgreSQL
- Local database: PostgreSQL 17 via Docker Compose

## Local Run

```bash
sh gradlew run
```

`run` task starts the local PostgreSQL container through Docker Compose and runs the API with the `local` profile.

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Local DB: `localhost:5435/mogak_local`

Stop the local database:

```bash
sh gradlew localDbDown
```

## Verification

```bash
sh gradlew test
```

## REST API Document

- [Docs Index](docs/README.md)
- [Post List API](docs/api/post-list.md)
- [Social Login API](docs/api/social-login.md)

## Operations Docs

- [Project Conventions](docs/operations/policies/PROJECT_CONVENTIONS.md)
- [Stack Migration Guide](docs/operations/policies/STACK_MIGRATION_GUIDE.md)
- [Query Strategy and N+1 Guardrails](docs/operations/policies/QUERY_STRATEGY_AND_N_PLUS_ONE.md)
- [Domain Document Registry](docs/operations/policies/DOMAIN_DOCUMENT_REGISTRY.md)
