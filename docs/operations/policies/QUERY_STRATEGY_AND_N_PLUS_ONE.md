# Query Strategy and N+1 Guardrails

이 문서는 `MOGAK_Spring`의 공개 가능한 ORM-first 조회 전략과 N+1 검증 기준을 정리한다.

## Scope
- Spring Data JPA와 JPQL을 기본 조회 수단으로 본다.
- SQL 튜닝, 인덱스, 캐시, 비공개 서비스 정책 판단은 이 문서의 범위가 아니다.
- API별 응답 필드와 접근 권한처럼 공개 규칙만으로 판단할 수 없는 내용은 비공개 정책 consult 또는 사용자 확인이 필요하다.

## Default Strategy
- 새 조회 코드는 ORM-first로 설계한다.
- Repository는 조회 형태와 저장만 담당하고, 비즈니스 정책 판단을 넣지 않는다.
- 응답 조립에서 접근하는 연관관계는 쿼리 전략에 명시적으로 반영한다.
- 목록, 상세, 배치 조회를 구현할 때는 DTO 조립 과정의 연관 접근까지 포함해 N+1 위험을 점검한다.
- 최적화는 실제 응답에 필요한 데이터 기준으로 수행하고, 불필요한 범용 fetch 전략을 만들지 않는다.

## To-One Associations
- 목록 또는 상세 응답에서 필요한 `ManyToOne`, `OneToOne` 연관은 JPQL `join fetch`를 우선 검토한다.
- to-one fetch join은 row 증폭 위험이 낮으므로, 응답 조립에서 항상 필요한 소유자, 작성자, 부모 참조 조회에 적합하다.
- 조건부로만 필요한 to-one 데이터는 별도 projection 또는 전용 조회 메서드를 검토한다.
- EntityGraph는 기존 Repository 패턴과 더 잘 맞고 조회 의도가 분명할 때 사용한다.

## Collections and Paging
- 페이징 쿼리에서는 컬렉션 fetch join을 사용하지 않는다.
- 컬렉션 fetch join은 row 중복, 잘못된 page size, 메모리 페이징 위험을 만들 수 있다.
- 페이징된 루트 엔티티 목록을 먼저 조회한 뒤, 필요한 컬렉션 데이터는 루트 id 목록으로 배치 JPQL 조회한다.
- 배치 JPQL은 `where root.id in :ids` 형태로 필요한 컬렉션 또는 projection만 조회한다.
- 조회 결과는 애플리케이션 계층에서 루트 id 기준으로 그룹핑해 DTO에 조립한다.
- page size가 커지거나 여러 컬렉션이 동시에 필요하면 API 응답 형태, projection, 별도 summary 필드로 요구사항을 다시 분리한다.

## Recommended Patterns
- 상세 조회:
  - 필요한 to-one 연관은 JPQL fetch join 또는 EntityGraph로 함께 조회한다.
  - 필요한 컬렉션이 작고 페이징이 없을 때만 컬렉션 fetch join을 제한적으로 검토한다.
- 페이징 목록 조회:
  - 루트 엔티티 page 조회와 to-one fetch join을 결합한다.
  - 컬렉션 데이터는 page의 루트 id 목록으로 별도 배치 조회한다.
  - DTO 조립은 이미 조회한 데이터만 사용하고 lazy loading에 의존하지 않는다.
- 댓글, 이미지, 좋아요 수처럼 컬렉션 전체가 아니라 요약값만 필요한 경우:
  - collection entity 로딩보다 count, exists, projection 조회를 우선 검토한다.
- 배치/스케줄러 조회:
  - 처리 단위별 id를 먼저 제한하고, 필요한 연관을 명시적으로 조회한다.
  - 반복문 내부에서 lazy association을 열어 추가 쿼리가 누적되지 않게 한다.

## Query Count Validation
- 검증 목표는 사전에 고정한 절대 쿼리 수를 맞추는 것이 아니라 데이터 크기 증가에 따른 비선형 증가를 막는 것이다.
- 대표 page size와 더 큰 page size를 비교해 쿼리 수가 page item 수에 비례해 증가하지 않는지 확인한다.
- 같은 응답 형태에서 루트 row 수를 늘렸을 때 쿼리 수가 선형 또는 상수 범위로 유지되는지 본다.
- 반복문 내부 lazy loading으로 `1 + N`, `1 + 2N`, `1 + N * M` 형태가 나타나면 N+1 위험으로 본다.
- 테스트 또는 로깅 기반 검증은 API 특성, 사용 데이터, 현재 ORM 설정을 함께 기록한다.
- hard target이 필요한 성능 기준은 이 공개 문서가 아니라 별도 운영 기준에서 다룬다.

## Current Coverage
현재 공개 회귀 테스트는 `ListQueryCountIntegrationTest`에서 Hibernate statistics의 `prepareStatementCount`를 사용한다. 절대 쿼리 수를 고정하지 않고, 동일 응답 형태에서 1개 루트 row와 3개 루트 row를 조회했을 때 쿼리 수가 증가하지 않는지 확인한다.

| 목록 | 검증 대상 | 조회 전략 | 회귀 테스트 |
| --- | --- | --- | --- |
| 네트워크 게시글 목록 | 게시글, 작성자, 직무, 이미지 | 게시글 page 조회에 to-one fetch join, 이미지는 post id 배치 조회 | `networkPostsQueryCountDoesNotGrowWithPostCount` |
| 페이스메이커 게시글 목록 | 게시글, 작성자, 직무, 이미지, 댓글 작성자 | 게시글 page 조회에 to-one fetch join, 이미지/댓글은 post id 배치 조회 | `pacemakerPostsQueryCountDoesNotGrowWithPostCount` |
| 모각별 게시글 목록 | 게시글, daily jogak, jogak, mogak | 게시글 page 조회에 필요한 to-one fetch join | `mogakPostsQueryCountDoesNotGrowWithPostCount` |
| 모각 목록 | 모각, 대분류 | 모각 목록 조회에 대분류 fetch join | `mogakListQueryCountDoesNotGrowWithMogakCount` |
| 조각 목록 | 조각, 모각, 대분류, 요일 | 조각 목록 조회에 응답 변환용 fetch graph | `jogakListQueryCountDoesNotGrowWithJogakCount` |
| 댓글 목록 | 댓글, 게시글, 작성자 | 댓글 조회에 게시글/작성자 fetch join | `commentListQueryCountDoesNotGrowWithCommentCount` |

## Review Checklist
- 응답 DTO 조립 중 접근하는 모든 연관관계를 확인했는가?
- 페이징 쿼리에 컬렉션 fetch join이 들어가지 않았는가?
- page 루트 id 기준 배치 조회로 컬렉션 데이터를 가져오는가?
- to-one 연관은 JPQL fetch join 또는 EntityGraph로 명시했는가?
- query-count 검증이 고정 숫자 대신 데이터 증가에 따른 증가 패턴을 확인하는가?
- 공개 문서만으로 결정할 수 없는 정책 판단을 조회 계층에 숨기지 않았는가?
