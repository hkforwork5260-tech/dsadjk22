# PHASE PLAN — 채용알리미 v0.1

> v0.1 MVP를 6~8 세션 / clock time 3~5일에 완성하는 계획. 각 Phase가 하나의 세션 단위에 대응.

## 병렬 실행 전략

- **Window A (백엔드)**: `cd backend && claude` — Spring Boot Kotlin
- **Window B (안드로이드)**: `cd android-app && claude` — Compose
- 두 창은 `API_CONTRACT.md`라는 공통 계약 위에서 독립적으로 진행
- 매 세션 끝에 commit + push, 다음 세션은 git log + 이 파일 + CLAUDE.md 기준으로 재개

## v0.1 스코프 (확정)

**포함:**
- 안드로이드 앱: 핵심 18개 화면 (스플래시, 온보딩 ①②③④, 메인 피드 + 빈 상태, 공고 상세, 검색, 검색결과, 필터, 회사 상세, 관심기업, 마이페이지, 알림 히스토리, 캘린더)
- 백엔드: 사람인 수집 cron + REST API 7개 + FCM 등록 + 매일 9·21시 푸시 큐
- AI 한줄 요약 (Claude Haiku)
- Play Store 내부테스트 빌드 업로드

**제외 (v0.2로):**
- 홈 위젯 (Glance API) — 2일 추가 작업
- 잠금화면 풀스크린 인텐트 푸시 — 일반 푸시로 대체
- 찾아보기 Reels (VerticalPager) — 8개 안 만들고 placeholder
- 공유 시트, 마감 캘린더 고급 인터랙션, 마이페이지 서브 5개

## Phase 목록

### Phase 0 — 기획 문서 + GitHub 셋업 (현재 세션)
**Window**: 메인 (이 세션)
**Output**:
- `CLAUDE.md`, `PHASE_PLAN.md`, `API_CONTRACT.md` 작성 ✅
- 사용자가 GitHub repo 생성 → remote 연결 → push
- 1000개 회사 시드 CSV 만들기 (`backend/seed/companies.csv` placeholder, 실제 데이터는 Phase 1에서)

**Done when**: 사용자 PC에서 `git clone` 가능. 사용자 결정 사항: 모든 의사결정 record (CLAUDE.md).

---

### Phase 1 — 백엔드 코어 [Window A] ✅
**Subagent**: Backend Claude
**예상 시간**: 1 세션 (4~6시간 clock time) — **완료 2026-05-26**

**Tasks**:
1. Spring Boot Kotlin 프로젝트 스캐폴드 (`backend/`)
   - Spring Boot 3.x, Kotlin 2.0, Java 17
   - 의존성: web, jpa, postgresql, redis, validation, actuator
2. DB 스키마 (Flyway 마이그레이션)
   - `companies`, `company_aliases`, `jobs`, `job_categories`, `users`, `devices`, `user_favorites`, `notification_history`, `api_call_log`
3. JPA Entity + Repository 인터페이스
4. 1000개 회사 시드 (공정위 공시 데이터 → CSV → DB seed 스크립트)
5. 사람인 API client + **mock 모드** (키 없이 동작, 샘플 JSON 응답)
6. 매일 수집 cron 골격 (Spring `@Scheduled`)
7. REST API 엔드포인트 (mock 데이터로 응답)
   - `GET /api/v1/jobs/today`
   - `GET /api/v1/jobs/{id}`
   - `GET /api/v1/jobs/search`
   - `GET /api/v1/companies/{id}`
8. `application.yml` 프로필 분리 (`local`, `prod`)
9. Docker Compose (postgres + redis local)
10. 첫 실행 검증: `./gradlew bootRun` → curl로 `/api/v1/jobs/today` 200 응답

**Done when**: 로컬에서 `docker-compose up && ./gradlew bootRun` → mock 응답으로 모든 v0.1 엔드포인트 동작.

---

### Phase 2 — 안드로이드 남은 화면 [Window B] (Phase 1과 병렬) ✅
**Subagent**: Android Claude
**예상 시간**: 2 세션 (8~12시간 clock time)

**Tasks**:
1. **Session 2.A** ✅ — 온보딩 + 메인 보강
   - 온보딩 ②③④ 화면 (기업 규모/산업, 회사 스와이프, 위젯 권한)
   - 메인 빈 상태
   - 필터 풀스크린
   - 검색 + 검색 결과
2. **Session 2.B** ✅ — 상세·관심·마이
   - 회사 상세 (공고 있음/없음 2종)
   - 관심기업 그리드
   - 마이페이지 (서브는 placeholder 유지)
   - 알림 히스토리
   - 마감 캘린더 (기본형)
3. mock JSON으로 동작 (API_CONTRACT.md에 정의된 응답 그대로 시뮬레이션)
4. 각 화면은 기존 `HiFi*` 컴포넌트 + 디자인 시스템 재활용

**Done when**: 안드로이드 에뮬레이터에서 18개 화면 다 진입 가능, mock 데이터로 화면 보임. 빌드 성공 (`./gradlew :app:assembleDebug`).
**상태**: 13개 화면 + NavGraph 12개 라우트 + MockApi 8개 응답 완료. `./gradlew assembleDebug` 검증은 사용자 PC에서 필요.

---

### Phase 3 — 백엔드 실 연동 [Window A]
**Subagent**: Backend Claude
**예상 시간**: 1 세션 (4~6시간)

**Tasks**:
1. 사람인 API 키 발급 (사용자가 dev.saramin.co.kr 가입 후 키 제공)
2. mock 모드 → 실 API 호출 전환 (환경변수 토글)
3. 매일 18시 cron 실 호출 검증 (단발 수동 실행)
4. dedup·정규화 로직 (회사명 normalize + alias 테이블)
5. Clearbit 로고 fetch (회사 도메인 추정 → API 호출 → 캐시 DB 저장)
6. Claude Haiku 통합 (공고 본문 → 한줄 요약 + 태그 추출)
7. FCM 서버 키 설정 + 디바이스 토큰 등록 엔드포인트
8. 매일 9·21시 푸시 발송 cron (관심기업 매칭 사용자에게)
9. 호출량 모니터링 (api_call_log 테이블 + 일일 사용량 대시보드 endpoint)

**Done when**: 실제 사람인 공고 데이터가 DB에 들어옴, 푸시 발송 1회 성공, `/api/v1/jobs/today` 실 데이터 응답.

---

### Phase 4 — 통합 [Window A + B 합류]
**Subagent**: 단일 Claude (통합 책임)
**예상 시간**: 1 세션 (3~5시간)

**Tasks**:
1. 안드로이드 Repository를 mock → 실 API로 전환 (`Retrofit` baseUrl만 변경)
2. FCM 통합 — 안드로이드에서 토큰 받아 backend에 등록
3. 푸시 수신 시 안드로이드 처리 (notification + intent → 공고 상세 진입)
4. 엔드포인트별 실제 응답 vs 안드로이드 기대값 mismatch 디버그
5. 에러 처리·재시도·오프라인 fallback

**Done when**: 안드로이드 앱 실행 → 실 사람인 데이터 노출 → 푸시 받기 → 공고 상세 진입까지 끝까지 동작.

---

### Phase 5 — 출시 준비 [선택]
**예상 시간**: 1 세션 (3~5시간)

**Tasks**:
1. ProGuard / R8 minify 설정
2. 앱 아이콘 · 스플래시 이미지 (정식 일러스트 또는 placeholder)
3. Play Store 메타데이터 (앱 설명, 스크린샷, 카테고리)
4. Release 키스토어 생성 + 서명
5. AAB 빌드 + Play Console 내부테스트 트랙 업로드
6. 정책 검토 (개인정보처리방침 URL, 데이터 안전성 폼)
7. 백엔드 호스팅 (Railway/Fly.io 배포 + 환경변수 + Cron 설정)

**Done when**: Play Store 내부테스트 링크로 사용자 본인 안드로이드 폰에 설치 가능.

---

## 세션 종료 체크리스트

매 세션 끝에:
1. [ ] 작업 끝까지 commit (`git add` + `git commit`)
2. [ ] CLAUDE.md "현재 상태" 섹션 업데이트 (필요 시)
3. [ ] PHASE_PLAN.md 해당 Phase에 `✅` 표시
4. [ ] git push
5. [ ] 다음 Phase 시작 메모를 마지막 commit 메시지에 (예: "Phase 2.A 완료. 다음: Phase 2.B")

## 비상 시 — Plan B 옵션

상황별 fallback:

| 상황 | Plan B |
|---|---|
| 사람인 API 키 발급 지연 | mock 모드로 v0.1 전체 출시 후 키 받으면 전환 |
| Claude Haiku 비용·지연 이슈 | 요약 기능 v0.2로 미루고 본문 그대로 노출 |
| FCM 푸시 트러블 | v0.1는 푸시 없이 출시 + in-app 알림 배지만 |
| 사람인 데이터 부족 | 워크넷 API 보조 (대기업 약하지만 빈자리 채움) |
| Play Store 정책 거절 | 내부테스트 트랙 유지하며 정책 보강 |

## KPI (v0.1 출시 후)

- 30일 내: 첫 100명 가입
- 90일 내: DAU 200명 + 푸시 클릭률 15%+
- 180일 내: 사용자 1000명 → Phase 2 (사람인 정식 파트너십 협상 또는 자체 데이터 확장 시작)
