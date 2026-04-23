# ERD Decisions and Lifecycle

이 문서는 `MOGAK_Spring`의 공개 ERD 판단 기준과 엔티티 삭제/보존 정책을 정리한다.

## Scope
- 이 문서는 공개 가능한 구조 결정만 담는다.
- 비공개 정책의 제목, 본문, 예시 판단 기준은 적지 않는다.
- `AgreeUser` 및 withdrawn user 처리처럼 정책 확정이 남은 항목은 pending으로 표기한다.

## ERD Decisions
- `AgreeUser`는 실제 terms consent 모델로 유지한다.
- `DailyJogak`는 `Jogak`에서 파생되는 날짜 단위 인스턴스로 본다.
- `DailyJogak.title`과 `DailyJogak.isRoutine`은 생성 시점 스냅샷으로 취급한다.
- 미래 일정은 `Jogak` + `JogakPeriod` 조합으로 계산하고, 미래 일정 표현용 `DailyJogak` row는 만들지 않는다.

## Deletion Lifecycle

| Entity | Lifecycle | Public note |
| --- | --- | --- |
| `User` | soft delete | withdrawn user의 email, nickname, profile anonymization 정책은 pending이다. |
| `Modarat` | soft delete | 관련 삭제는 소프트 삭제 기준을 따른다. |
| `Mogak` | soft delete | 관련 삭제는 소프트 삭제 기준을 따른다. |
| `Jogak` | soft delete | 관련 삭제는 소프트 삭제 기준을 따른다. |
| `DailyJogak` | soft delete | 날짜 인스턴스 이력을 보존한다. |
| `Post` | soft delete | 게시물 삭제는 소프트 삭제 기준을 따른다. |
| `PostComment` | soft delete | 댓글 삭제는 소프트 삭제 기준을 따른다. |
| `JogakPeriod` | hard delete | 주기 정보는 하드 삭제한다. |
| `PostLike` | hard delete | 좋아요 관계는 하드 삭제한다. |
| `PostImg` | hard delete | 이미지 관계는 하드 삭제한다. |
| `Follow` | hard delete | 팔로우 관계는 하드 삭제한다. |
| `BlockUser` | hard delete | 차단 관계는 하드 삭제한다. |
| `Report` | preserve | 신고 데이터는 보존한다. |
| `AgreeUser` | pending | 실제 terms consent 모델로 유지하며, retention/anonymization/deletion policy는 pending이다. |

## DailyJogak Creation Rules
- routine midnight batch는 오늘자 `DailyJogak` row를 생성한다.
- one-time schedule은 시작 시점에 `DailyJogak` row를 생성한다.
- future schedule은 `DailyJogak` row 없이 `Jogak` + `JogakPeriod`로 계산한다.
- 기존 `DailyJogak` row의 `title`과 `isRoutine`은 생성 시점 값을 유지한다.

## Pending Items
- `AgreeUser` retention policy
- `AgreeUser` anonymization policy
- `AgreeUser` deletion policy
- withdrawn user email anonymization policy
- withdrawn user nickname anonymization policy
- withdrawn user profile anonymization policy
