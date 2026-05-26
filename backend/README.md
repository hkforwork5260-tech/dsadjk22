# 채용알리미 백엔드 (jobalert-backend)

Spring Boot 3.5 + Kotlin 2.0 + PostgreSQL 16 + Redis 7. Phase 1 mock 응답으로 모든 v0.1 엔드포인트 동작.

> 상세 컨벤션·의사결정은 루트 [`CLAUDE.md`](../CLAUDE.md), API 스펙은 [`API_CONTRACT.md`](../API_CONTRACT.md) 참고.

## 빠른 시작

```bash
# 1) 의존 인프라 띄우기 (postgres + redis)
docker compose up -d

# 2) 백엔드 실행 (mock 모드 기본 — 사람인 API 키 없이 동작)
./gradlew bootRun

# 3) 동작 확인
curl http://localhost:8080/api/v1/jobs/today | jq
curl http://localhost:8080/api/v1/onboarding/categories | jq
curl http://localhost:8080/actuator/health
```

## 사전 요구사항

- JDK 17+ (Java 21도 OK)
- Docker Desktop 또는 Docker Engine (Postgres·Redis 컨테이너용)
- Gradle 별도 설치 불필요 — wrapper 동봉 (`./gradlew`)

## 프로필

| 프로필 | 용도 | 사람인 모드 | 수집·푸시 cron |
|---|---|---|---|
| `local` (기본) | 로컬 개발 | mock | 끔 |
| `prod` | Railway/Fly.io | real (Phase 3+) | 켬 |

전환은 `SPRING_PROFILES_ACTIVE=prod`. 추가 환경변수는 `application-prod.yml` 참고.

## 엔드포인트 (v0.1)

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/v1/jobs/today` | 오늘의 공고 (NEW/UPDATE/CLOSING) |
| GET | `/api/v1/jobs/{id}` | 공고 상세 |
| GET | `/api/v1/jobs/{id}/similar` | 유사 공고 |
| GET | `/api/v1/jobs/search` | 키워드 검색 |
| GET | `/api/v1/jobs/upcoming` | 마감 임박 캘린더 |
| GET | `/api/v1/companies/{id}` | 회사 상세 |
| GET | `/api/v1/companies/{id}/jobs` | 회사 공고 목록 |
| GET | `/api/v1/onboarding/categories` | 21개 직군 |
| GET | `/api/v1/onboarding/popular-companies` | 온보딩용 인기 회사 |
| POST | `/api/v1/devices/register` | FCM 토큰 등록 |
| PATCH | `/api/v1/devices/{id}/preferences` | 푸시·관심직군 변경 |
| POST | `/api/v1/users/me/favorites/{companyId}` | 관심기업 추가 |
| DELETE | `/api/v1/users/me/favorites/{companyId}` | 관심기업 제거 |
| GET | `/api/v1/users/me/favorites` | 관심기업 목록 |
| GET | `/api/v1/notifications/history` | 알림 히스토리 |
| POST | `/api/v1/notifications/{id}/read` | 알림 읽음 처리 |

스펙은 [`../API_CONTRACT.md`](../API_CONTRACT.md) 참조.

## 패키지 구조

```
src/main/kotlin/com/jobalert/backend/
├── JobAlertApplication.kt          # 부트스트랩 (@EnableScheduling)
├── controller/                     # REST 컨트롤러 6개
├── service/                        # 비즈니스 로직 + MockDataProvider
├── repository/                     # Spring Data JPA 인터페이스
├── entity/                         # JPA Entity (8개 테이블)
├── dto/                            # 요청/응답 DTO
├── client/saramin/                 # 사람인 API client (mock/real 양방향)
├── scheduler/                      # @Scheduled cron
├── seed/                           # 회사 시드 로더
└── exception/                      # 글로벌 핸들러
src/main/resources/
├── application.yml                 # 공통
├── application-local.yml           # 로컬 DB 연결
├── application-prod.yml            # 운영 — env 변수 위주
├── db/migration/                   # Flyway 마이그레이션
└── seed/companies.csv              # 회사 시드 (Phase 1 ~57개 placeholder)
```

## 사람인 mock vs real 모드

- `jobalert.saramin.mode=mock` (기본) — `SaraminMockClient` 활성. 5건 하드코딩 응답.
- `jobalert.saramin.mode=real` — `SaraminRealClient` 활성. **Phase 3에서 실 API 호출 구현 예정** (현재는 stub).

전환은 `SARAMIN_MODE=real SARAMIN_API_KEY=xxx` 환경변수.

## 사람인 API 호출 한도 모니터링

- 약관: 1일 500 calls
- `api_call_log` 테이블에 모든 호출 기록 (source, endpoint, params, status, duration)
- `ApiCallLogger.countSaraminCallsLast24h()` 로 일일 사용량 조회
- v1.0에서 admin 대시보드 endpoint로 노출 예정

## DB 마이그레이션

Flyway 자동 실행. 새 마이그레이션은 `src/main/resources/db/migration/V{N+1}__{description}.sql`.

```bash
# 수동 검증
docker exec -it jobalert-postgres psql -U jobalert -d jobalert -c "\dt"
```

## 빌드·테스트

```bash
./gradlew compileKotlin          # 컴파일만
./gradlew test                   # 단위 테스트
./gradlew build                  # jar 빌드 → build/libs/backend-0.1.0-SNAPSHOT.jar
./gradlew bootRun                # 실행
```

## Phase 1 → Phase 3 TODO

- [ ] `SaraminRealClient.fetchJobs` 실제 RestClient 호출 구현
- [ ] `JobCollectorService.runDailyCollection` — dedup, normalize, upsert
- [ ] 1000개 회사 시드 (공정위 공시 데이터)
- [ ] FCM 통합 + 푸시 발송 cron
- [ ] Claude Haiku 통합 (공고 한줄 요약)
- [ ] Clearbit 로고 fetch + 캐시
- [ ] 회사 alias 자동 매핑 (회사명 정규화)
