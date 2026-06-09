# 채용알리미(JobAlert) — 제작 전 과정 기록 (상세 전사판)

> 임현경 / 코딩 초보 1인 개발 / Claude Code와 함께. 2026-05-26 ~ 2026-06-09.
> 이 문서는 세션별 기록(`session_end_*.md`)과 `CLAUDE.md`에 흩어져 있던 내용을 **빠짐없이 한 곳에 전사·통합**한 원자료다. claude.ai에 가져가 블로그·회고·포트폴리오로 가공하기 위한 것. 요약하지 않고 사실·커밋·함정·수치를 그대로 옮겼다.

---

## 0. 한 줄 요약

취준생을 위한 **매일 1분 채용 알리미 앱**. 듀오링고처럼 짧게 자주 들르고, 매일 새 공고를 푸시·위젯으로 알려준다. 안드로이드(Jetpack Compose) + Spring Boot 백엔드 + 여러 공공/공개 채용 API 하이브리드 수집. 마스코트 시바견 **'단이'**, 신뢰의 블루 컬러(#4F6EF0).

- 안드로이드 ~70개 .kt / 백엔드 ~82개 .kt / 커밋 ~140개.
- 라이브: `https://dsadjk22-production.up.railway.app` (Railway 무료 박스 + 관리형 Postgres). 활성 공고 ~1,350건, 회사 ~530곳.
- repo: `hkforwork5260-tech/dsadjk22` (jobalert 전체, backend는 `backend/` 서브폴더). Root Directory=`backend`, push 시 자동 재배포.

---

## 1. 비전 · 타겟 · 핵심가치 · 디자인

- **비전 한 줄**: 취준생을 위한 매일 1분 채용 정보 앱. 듀오링고처럼 짧게 자주 들르고, 매일 새 공고 푸시로 잠금화면에 노출.
- **타겟**: 고스펙 대기업·중견·빅테크·공기업 지망 취준생. 자기 직군의 새 공채/수시 공고를 놓치기 싫어한다. 한 번에 길게 보지 않고 짧게 자주 확인하는 패턴.
- **핵심가치 5**:
  1. 자동 수집·자동 분류 — 매일 18시 KST 수집, 어제 대비 diff로 NEW / UPDATE / CLOSING 자동 라벨링.
  2. 고정 시간 푸시 — 매일 9시·21시 자동 발송. 사용자가 시간 설정 안 함(귀찮음 제거).
  3. 잠금화면 풀스크린 푸시 — 알림에서 바로 공고 보고 지원 이탈.
  4. 홈 위젯 — 듀오링고 스트릭처럼 매일 활성.
  5. (폐기) AI 한줄요약 — Claude Haiku 요약. 비용 이유로 폐기.
- **디자인 톤**: 듀오링고 + 한국 모바일 UX. (초기) 코랄 브랜드(#FF6B35) + 3D 깊이감 버튼 + 마스코트 '꽁이'(고양이) → (리브랜딩) 블루(#4F6EF0) + 시바 '단이'. 폰트 Pretendard. Compose엔 box-shadow가 없어 3D 버튼은 그림자박스+본체 2겹 스택(`HiFiButton.kt`).

## 2. 기술 스택 (확정)

- **안드로이드**: Kotlin 2.0.21 + Jetpack Compose(BOM 2024.10) + Navigation Compose. Compose BOM 2024.10, AGP 8.6. Retrofit/OkHttp/kotlinx.serialization. 패키지 `com.jobalert.app`.
- **백엔드**: Kotlin + Spring Boot 3.5 + PostgreSQL + Flyway. Java 17. Layered(`controller`→`service`→`repository`→`client/source`). DTO↔Entity 분리(Entity는 JPA, DTO는 controller-service 경계). 모든 시간은 `Instant`/`OffsetDateTime`(UTC) DB 저장, 노출 시 KST 변환. 패키지 `com.jobalert.backend`. API 응답 envelope는 `API_CONTRACT.md` 따름.
- **AI 요약**: (폐기됨) Claude Haiku `claude-haiku-4-5-20251001`.
- **푸시**: Firebase Cloud Messaging(FCM). 프로젝트 `jobjob-533ca`(Spark 무료).
- **호스팅**: Railway(무료) + 관리형 Postgres. (초기엔 Railway/Fly.io 후보, 매출 발생 시 AWS/GCP 이전 검토.)
- **로고**: Clearbit Logo API(`logo.clearbit.com/{domain}`) + 한글 회사명 사전(30+) + fallback 첫 글자 텍스트.

### Git 컨벤션
- 한 세션 = 하나 이상의 의미 있는 commit. 메시지 한국어 OK(제목+본문=왜). main 직접 push(1인 프로젝트).

### Kotlin/Compose 컨벤션
- 컴포넌트 네이밍 `HiFi*` 접두사(디자인 시스템), 화면은 `*Screen`. 디자인 토큰은 `ui/theme/` 안에서만 정의. `MaterialTheme`은 일부 슬롯만 매핑, 컴포넌트는 거의 다 커스텀.

---

## 3. 세션별 상세 기록 (시간순 전사)

### 3-1. 2026-05-27 (안드로이드 Phase 2.B)
- Phase 2.B 5개 화면 + 실기기 검증 + UX fix 6라운드 완료.

### 3-2. 2026-05-27 v2 (안드로이드 Phase 2.C)
- Phase 2.C placeholder 7개 화면 채우기(찾아보기 Reels VerticalPager, 공유 시트 BottomSheet, 비슷한 공고, 마이페이지 서브 4개=알림설정/위젯설정/관심직군/피드백) + 사람인 키 신청 + docs 동기화(commit `307c4e1`). 다음은 사람인 키 승인 대기 → Phase 3.
- `data/api/MockApi.kt` — API_CONTRACT.md 형식 그대로 mock 응답(백엔드 붙기 전 임시). 디자인 시스템 컴포넌트 10개 + 라우트 16개.

### 3-3. 2026-05-27 (백엔드 Phase 1 코어)
- Spring Boot 3.5 + Kotlin 2.0 스캐폴드 / Flyway 스키마 10개 테이블 / JPA Entity·Repository / REST API 15개 엔드포인트 mock 응답 / 사람인 mock client / 수집 cron 골격 / 회사 시드 57개 placeholder / Docker Compose. `./gradlew compileKotlin` 통과. (62개 파일.)

### 3-4. 2026-05-27 v2 (백엔드 환경 셋업)
- 환경 셋업(JDK·Docker) + bootRun 실 검증: postgres+redis 도커 + Spring Boot 2.3초 부팅 + 주요 5개 엔드포인트 200. `NoResourceFoundException` 404 매핑 fix(`d1a0529`).

### 3-5. 2026-05-28 (백엔드 Phase 3 사전작업)
사람인 키 도착 전, 키 없이 가능한 작업 풀스택 사전 구현(단위 테스트 39개 PASS):
- **카테고리 코드 키 sync**(`74c4a85`) — 백엔드↔안드로이드 6개 코드 키 통일, API_CONTRACT에 정식 21개 직군 표 추가.
- **SaraminRealClient 본구현**(`c346dae`) — 공식 명세 정확 반영 / RestClient 3s·7s 타임아웃 / 4xx·5xx + 사람인 자체 에러코드(1/2/3/4/99) 매핑 / ApiCallLogger 연동 / JobCollectorService 페이지네이션 + 5xx 1회 재시도 + 일일 한도 도달 시 중단.
- **회사명 정규화 + dedup 매처**(`88304be`) — `(주)`·`㈜`·`주식회사`·`Corp` 등 마커 제거 + NFKC + 영문 lowercase, exact match → alias fallback.
- **Clearbit 로고 리졸버**(`4704a23`) — homepage URL > 한글 회사명 dictionary(30+) > 영문 slug 휴리스틱.
- 의존성: MockK 1.13.13 추가(testImplementation, Kotlin 친화 모킹).
- → 사람인 키 도착 시 환경변수 토글로 즉시 동작하도록 준비.

### 3-6. 2026-06-03
- 사람인 약관 재확인(앱 출시 OK) + 1주일 무응답에 직접 문의 메일 발송. 코드 변경 없음.

### 3-7. 2026-06-05 (백엔드 v4 — 데이터소스 대전환)
전날(06-04) 사람인 거절 메일로 시작해 06-05까지 이어진 세션. 코드 + 대규모 리서치 + 비전 조정.

**완료**:
- **사람인 거절 확인**: 담당자 최호성 "개인 프로젝트 승인 불가, 비영리 공공기관만". 재신청 무의미.
- **하이브리드 수집기 전환(commit `9097385`)** — 소스 어댑터 패턴:
  - `RawJobPosting`(소스무관 공통모델, 시간 epoch sec 통일) + `JobSource` 인터페이스
  - `GreenhouseSource`·`LeverSource`(공개 API, 한국 근무지 필터) + DTO
  - `SourceUtil`(한국필터·시간정규화) + `SourceUtilTest` 5개 PASS
  - `HybridCollectorService`(모든 소스 순회·집계·dedup), 스케줄러 배선
  - `SourceRegistry`(보드 토큰 목록)
  - compileKotlin/compileTestKotlin 통과
- **Greenhouse 실측**: 쿠팡 한국공고 261건(신입포함)·크래프톤 50건 → 한국 빅테크 실커버 확인. databricks·spotify도.
- **국내 대기업 robots 30곳 실측**: 카카오식 강차단 ~7%(카카오 401·롯데 WAF), 80%는 안 막음. 단 robots≠허락.
- **합법 무료 데이터소스 전수조사**: 스택 도출, CLAUDE.md 데이터소스 섹션 전면 정정.

**비전 조정(중요)**: "1000개 대기업 전체 자동수집"은 개인이 합법으로 어렵다고 결론. 삼성·LG·네이버·카카오 직접 공채 주는 합법 무료 API 없음. → "취준생 채용 알리미(대기업은 부분)"로 조정. 사용자 수용함.

**06-05 후반(포트폴리오 모드 결정 후)**: 사용자가 "포트폴리오용으로 만들자, 스택=Greenhouse(쿠팡·크래프톤+α)+기재부+가능하면 워크넷·원티드" 결정. "데이터 확인 후 빌드" 원칙 강조(프론트 먼저 만들다 사람인에 데인 교훈).
- **Greenhouse 한국 회사 확정**(`b78c93e`): SourceRegistry에 쿠팡·크래프톤·당근·몰로코·센드버드 등록. databricks/spotify 제거. Lever는 한국 회사 0 → 비움.
- **기재부 공공기관 채용 API 소스 구현**(`b78c93e`): `PublicInstitutionSource` + DTO + 실응답 파싱 테스트 + yyyyMMdd→KST epoch 파서. **키 활성화·실호출 검증**(총 110,696건). 키는 환경변수(JOBALERT_PUBINST_KEY)로만, 코드/커밋에 없음. application.yml에 sources 설정 추가.
- **라이브 통합 테스트 통과**(`f18b9e3`): `HybridCollectionLiveIT` — 전체 Spring 부팅 + 실 수집 **889건**(greenhouse 389 + public-institution 500) + api_call_log DB 적재 확인. 쿠팡·국립공원공단 실 공고 매핑 정상. @EnabledIfEnvironmentVariable로 게이트.
- 사용자 data.go.kr 키: `JOBALERT_PUBINST_KEY` 환경변수로 주입(2026-06-05 발급). 코드엔 없음.

**미완/다음 후보**: 워크넷 신청 문의(043-870-8556 / workmaster@keis.or.kr, 4유형=비영리OK지만 기업회원전용+영리법인불가+심사라 불확실) · 원티드 OpenAPI 약관 문의 · diff·DB적재·푸시(Phase 3 본작업) · 공공기관 max-pages=5(500건) → 날짜필터 개선 · Greenhouse 한국 토큰 추가 발굴.

**Parking**: 그리팅(두들린) 제휴 문의(고객사 키 필요, v0.5 제휴) · 사업자등록 후 사람인 재신청.

**핵심 학습(사용자에게 설명한 개념)**:
- robots.txt = 기술잠금 아닌 신사협정(팻말). 무시해도 대부분 응답. "되냐"와 "해도 되냐"는 별개.
- 사실(제목·마감일) = 저작권 X / 공고 본문 글 = 어문저작권 O. 사실+링크만 쓰면 회피.
- "회사에 이득" = 실질위험 낮추나 법적 방패는 아님.

### 3-8. 2026-06-05 v2 (엔드투엔드 첫 실연결)
이전 세션(v4)에서 388건 "긁어오는 것"까진 됐지만 DB에 안 쌓고 버리던 상태. 이번에 빠진 절반을 채우고 앱까지 연결.

**완료(커밋 3개, 단위 57개 + 라이브 검증 PASS)**:
1. **적재·diff 파이프라인**(`adfed35`) — `JobPersistenceService`: RawJobPosting → jobs upsert + 어제 대비 diff(INSERT=NEW / 제목·마감변경=UPDATE / 마감3일내=CLOSING우선 / 사라짐=만료). 회사 매칭 미스 시 **자동 생성(isApproved=false)**. 안전장치: 회사 중복생성 방지(정규화명 캐시+matchOrNull 재매칭), **0건 받은 소스는 만료 스윕 제외**(일시 장애가 전체 공고 닫는 사고 방지). `TimeConfig` Clock 빈. `HybridCollectorService`에 persist 배선.
2. **조회 API 실 DB화**(`a12ab17`) — `JobService`·`CompanyService` MockDataProvider→Repository. `JobMapper`(엔티티→DTO, 회사임베드 N+1회피, D-day KST 파생·마감없으면 "상시"). `AdminController` POST `/api/v1/admin/collect` 수동 트리거(v0.1 무인증).
3. **안드로이드 메인 백엔드 연결**(`99d4ac1`) — Retrofit+OkHttp+kotlinx.serialization. `ApiClient`(Json SnakeCase 네이밍전략으로 camelCase↔snake_case 자동, BASE_URL=10.0.2.2:8080), `ApiService`(jobs 엔드포인트만), `JobRepository`(DTO→도메인 Job), `MainViewModel`(Loading/Success/Error, **@JvmOverloads로 viewModel() 무인자생성자 호환**), `MainScreen` 3상태. ApiModels @Serializable + deadline/postingDate nullable.

**기재부 공공기관 소스 활성화(같은 세션 후반)**:
- `JOBALERT_PUBINST_KEY` + `PUBINST_ENABLED=true`로 bootRun → 검증 완료.
- 수집 결과: 공공기관 500건 추가 → **회사 5곳 → 188곳**(근로복지공단56·한국보훈복지의료공단32·한국폴리텍30·국립중앙의료원29·국립공원공단27·한전KPS26·대한적십자사·한국수자원공사 등 공기업 대거, isApproved=false 자동생성).
- 공공기관은 마감일이 있어 `/jobs/upcoming` 캘린더 실동작(14일내 369건, D-day 정확). Greenhouse(상시)는 마감일 없음.
- pubinst max-pages=5(500건 캡).
- **Greenhouse 한국 토큰 추측 발굴은 실패** — 60여 후보 전부 404, 검증된 5곳(coupang·krafton·daangn·moloco·sendbird)이 사실상 전부. 회사별 채용페이지 수동 확인 필요(저효율). ① 보류, ② pubinst가 회사 확장의 실질 지렛대.
- (디버깅 교훈: `/upcoming` 0건으로 보였던 건 확인 스크립트가 응답 키를 `byDate`로 본 탓 — 실제 키는 `by_date`(Jackson snake_case). 앱·백엔드는 정상. 헛다리.)

**라이브 검증(Greenhouse 메인 연결분)**:
- docker postgres + Greenhouse 실호출 → **388건 적재**(쿠팡268·크래프톤51·당근40·Moloco20·Sendbird9), 회사 3곳 자동생성(당근·Moloco·Sendbird), 쿠팡·크래프톤은 시드 매칭.
- 2회차 멱등성: inserted=0 unchanged=388.
- **에뮬레이터(Pixel7 API34) 메인 화면에 실 쿠팡 공고 388건 표시 성공.** BUILD SUCCESSFUL.

**빌드 중 잡은 것**: `deadline: String → String?` 변경이 CalendarScreen/SearchResultsScreen의 `displayDeadlineShort/displayDeadline(iso: String)`와 충돌 → 둘 다 `String?` 받고 null이면 "" 반환으로 수정.

**함정**:
- 안드로이드 빌드·실행 검증은 사용자 PC에서만(이 환경 Google Maven 차단). FE 코드는 짜되 컴파일/실행은 사용자가.
- 메인이 쿠팡만 보이는 건 버그 아님: 쿠팡 268/388(69%) + 첫 수집이라 전부 동일 시각 NEW → "최신 50"이 쿠팡으로. 소스 늘고 일일수집 돌면 해소.
- `com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0` 안 잡히면 대체: `com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0`.
- 실기기 테스트 시 `ApiClient.BASE_URL`을 PC LAN IP로.

**세션 후반 추가(검색·캘린더 연결 + 직군 분류)**:
- **검색·캘린더 화면 백엔드 연결**(`d05ed8a`): JobRepository.search/upcoming + SearchViewModel/CalendarViewModel. 캘린더는 디자인기 2026-05 하드코딩 → 현재 달(KST) 일반화.
- **검색 입력 실동작**(`e95d16e`): SearchScreen 검색박스가 탭→빈쿼리 이동하는 가짜였음 → BasicTextField로 교체. 인기/최근 검색어를 실데이터로. (에뮬은 맥 키보드로 한글 입력 불가=IME 한계, 정상.)
- **직군 자동 분류**(`cd17d1d`): `JobCategoryClassifier`(21직군 키워드 규칙, 제목+부서(NCS분류명)+키워드 매칭). JobPersistenceService newJob+applyDiff에서 태깅. JobService.today가 `?categories=`로 필터(넓게 fetch 후 교집합, 2000건 window). 재수집 후 21직군 전부 채워짐(medical196·IT개발165·회계재무149…, 미분류14%). 정확도는 NCS 묶음 탓에 다소 넓음 → v0.2 AI분류로 개선. FE 필터화면 연결은 미완.
- 보너스: 2회차+ 수집에서 **CLOSING 51건** 등장(마감 3일내). diff의 NEW→CLOSING 전이 실동작 확인.
- 전체 백엔드 테스트 63개 PASS.

**추가 화면 연결 + 푸시(세션 최후반, `9a7c8a4`~`a7299fa`)**:
- 회사상세(`/companies/{id}/page`), 직군필터(ActiveFilter 홀더+`?categories=`), **관심기업**(익명 기기ID UUID+X-Device-Id 인터셉터, user_favorites DB, 별표토글+목록+회사page isFavorited), 온보딩 관심직군(고른 직군→ActiveFilter 영속→메인 기본필터), 알림 히스토리(notification_history DB, generateDigest morning/evening, AdminController /digest), **FCM 푸시**(백엔드 firebase-admin FcmSender + DeviceService DB화 토큰저장 + 앱 google-services·firebase-messaging·FcmRegistrar·MessagingService·MainActivity 토큰등록/권한).
- Firebase: 프로젝트 `jobjob-533ca`(Spark 무료). google-services.json(com.jobalert.app.debug) 커밋됨. 서비스계정키 backend/secrets/(gitignore 미커밋).
- ✅✅ **푸시 end-to-end 라이브 검증 성공**: Pixel7 API34 에뮬레이터 알림에 "마감 임박 51건 🔥" 실제 표시. delivered=true, fcm_message_id=projects/jobjob-533ca/messages/…. 수집→DB→분류→API→앱→잠금화면 푸시 전 사슬 동작.
- 디버깅 교훈: FE(kotlinx)는 기본값 있는 필드를 JSON에서 생략(encodeDefaults=false) → DeviceRegisterRequest.platform 누락→백엔드 파싱 실패. 백엔드 platform에 기본값 부여로 해결. FCM notification 메시지는 앱 포그라운드 시 시스템 자동표시 안 함(백그라운드에서만), onMessageReceived는 no-op.

**매일 자동 푸시 스케줄 + 개인화 다이제스트(`47ec03b`, `85d4ddd`)**:
- **PushScheduler**: 09:00/21:00 KST cron → sendDailyDigestToAll(토큰 등록 전체 기기, pushMorning/Evening 존중). jobalert.push.enabled=true일 때 활성.
- **개인화 다이제스트**: DeviceCategory 엔티티(device_categories, job_categories FK) + DeviceService.register가 preferences.categories 저장. NotificationService.generateDigest가 기기 직군으로 공고 필터 → "내 직군 새 공고 N건". 직군 없으면 전역.
- ✅✅✅ **개인화 푸시 end-to-end 라이브 검증**: 온보딩서 5직군(회계·IT·디자인·의료·건설) 선택→백엔드 동기화→푸시 "내 직군 새 공고 471건☀️"(전체846중 471) 에뮬 잠금화면 표시. 수집→분류→개인화→정시 자동푸시 완전체.

**나머지 화면·기능 12개 마저 연결(2026-06-05~06, `c5fa89a`~`86028e8`)** — 사용자가 "미완 1~12 다 해":
- #1 공고상세(/jobs/{id}), #2 지원버튼(원본URL Intent), #3 찾아보기(todayFeed), #4 비슷한공고(/similar), #5 온보딩추천회사(공고많은순 실데이터), #6 마이페이지(알림설정 토글→기기설정 PATCH + 관심직군 카드 ActiveFilter 반영), #9 검색 회사명 확대, #11 알림읽음 백엔드 동기화, #12 회사상세 평균마감 계산 — 전부 ✅
- #10 필터: **직군+경력+규모** 작동. 경력은 ExperienceClassifier(공공기관 recrutSeNm 우선+제목 보충)로 신입404·경력160·인턴75 분류, 규모는 출처기준(공공기관→public, 132곳). /jobs/today에 experiences·sizes 파라미터. "신입만" 필터가 핵심가치. 지역·마감 facet은 데이터 형식편차로 미적용.
- #8 꽁이 한줄요약(Haiku): 사용자가 비용 이유로 보류 + 본문 미수집.
- #7 메인빈상태: 메인이 빈 상태 자체처리 → 중복이라 미연결.

**상세 개편 + 추가 요청(2026-06-06 새벽, `0f4e0a4` 등)**:
- 상세 화면: 탭 4개(요약/원문/회사/비슷한)→**2개(정보/비슷한)**. 정보탭=핵심정보(마감·경력·학력·근무지·태그)+원문링크+회사링크. 꽁이 한줄요약·가짜 우대사항 제거. 도메인 Job에 companyId 추가.
- 직군 분류 검증: 86% 분류, 키워드 오탐 일부("개발/데이터"가 물류·PM 잘못 잡음).

**다음 세션 요청(2026-06-06 우선순위)**: ①정보탭 빈약→원문 본문 가져오기(Greenhouse content=true·공공기관 상세) ②찾아보기 추천 알고리즘(관심직군+좋아요 회사 가중치) ③쿠팡 상시공고 도배(회사별 다양화 interleave) ④온보딩 규모·산업군 데이터 빈약 ⑤마이페이지 미구현·캘린더 월이동.

### 3-9. 2026-06-06 (UX 개선 6건 + 본문 수집 + 서울 소스)
사용자 실사용 피드백 반영(`0662e36`·`cda0ba9`·`c1a06c8`·`bec5186`·`54f0218`):
- **G1** 찾아보기 진행바 제거 + 검색 기업탭 제거(공고만) + 한줄요약 흔적 정리.
- **G3 쿠팡 편중 해소 + 개인화 피드** — `JobService.today()`에 회사 라운드로빈 interleave(한 회사 연속 노출 차단) + 관심기업(+2)·관심직군(+1) 가점. `/jobs/today`에 X-Device-Id 선택 헤더. 라이브: 30건 전부 다른 회사·연속중복 0, 관심기업 11→1위.
- **G4 관심기업 추가 버그** — 관심기업 화면 "기업 추가" 버튼이 빈 람다(NavGraph TODO)였음 → 검색 연결. setFavorite runCatching silent 실패 → 결과 콜백+롤백+토스트. 남은 트레이드오프: 검색 결과에서 회사 섹션을 빼서 "기업 추가"→검색 시 회사가 안 나옴(공고만).
- **G5 저장한 공고(북마크) 풀스택 신규** — `saved_jobs` 테이블(V2) + SavedJob 엔티티/repo/service/controller(`/users/me/saved`). JobDetailDto.isSaved. 안드: 상세 북마크 서버연동, `SavedJobsScreen` 신규, 마이페이지 "저장한 공고"→실화면.
- **G2 카드/상세 충실화** — JobDto.jobCategories + CompanyEmbedDto.size 추가. 찾아보기 카드·공고상세에 직군·회사규모 배지.
- **G6 공고 본문 수집 완료**(`8017227`·`c7bb3fb`) — 양 소스 모두 본문 채움(899/899):
  - **Greenhouse**: `content=true`로 본문+부서. `SourceUtil.htmlToText`(Jsoup) HTML→평문. 399/399, 쿠팡 2730자.
  - **공공기관**: 상세 API `recruitment/detail?sn=`에 응시자격(aplyQlfcCn)·전형방법(scrnprcdrMthdExpln)·우대·학력(acbgCondNmLst) → 공고당 상세 1회 호출로 description+education. 500/500. 공공누리라 본문 자유 활용. 상세호출=목록건수(개발계정 1000 한도 내).
  - 상세화면 "📄 상세 내용" 섹션(8줄 접기+더보기). `JobPersistenceService` reactivate(만료→재수집 시 복귀) 버그 수정.
  - **급여는 여전히 미수집**(Greenhouse pay_input_ranges=null, 공공기관 상세에도 급여 텍스트 없음).
  - 온보딩 관심회사 고르기(onb3) 제거(`9a5f205`). 약관 본문활용 허용으로 완화(`d5368c8`).

**서울시 일자리포털 소스 추가**(`7950821`·`7378fe8`) — `SeoulJobSource`:
- 총 23,145건(공공누리 1유형, 상업OK). **급여(HOPE_WAGE)를 주는 첫 소스** + 본문·학력·경력. 서울 API가 Content-Type을 xml로 잘못 보내 String 받아 직접 파싱.
- **서울시 데이터 실체**: 서울일자리센터 알선이라 요양보호사·경비·청소 등 노인·중장년 위주(취준생 타겟 불일치) → `isElderlyOrCareJob` 네거티브 필터(1000→441건). 남은 건 용접·영업·사무·생산관리·기사 등 나이무관.
- **전체 양**: 활성 1,340건(공공기관 500 + 서울 441 + greenhouse 399), 회사 530곳.
- 실행: `JOBALERT_PUBINST_KEY=<키> PUBINST_ENABLED=true JOBALERT_SEOUL_KEY=<키> SEOUL_ENABLED=true ./gradlew bootRun`.

**리서치 확정(중요, 재시도 금지)**: 토종 대기업(토스·배민·네이버·카카오·무신사·컬리·야놀자·직방·두나무 등)은 Greenhouse/Lever 공개 보드 미사용(35개 후보 전부 404). 자체 ATS/그리팅 사용. → GH/Lever로 한국 대기업 발굴 불가. 양 확보는 서울시 등 공공 소스가 답. 워크넷은 라이선스 상업금지라 보류.

**관심기업 UX**(`9e87735`) — 찾아보기 "좋아요"→"관심기업"(회사 기준 서버연동), 공고상세 앱바 관심기업 하트(is_favorited). 기업추가는 검색 경로 유지.
**공공기관 지원링크**(`d608998`) — 기관별 srcUrl 부정확 → JOB-ALIO 통합 URL(`job.alio.go.kr/recruitview.do?recrutPblntSn=`)로 통일.

### 3-10. 2026-06-07 (UX 대개편 + 데이터 확장)
긴 세션. 사용자가 빌드해서 실기기 피드백 → 즉시 반영 사이클 다수. 커밋 35개. 전부 커밋됨(미커밋 0). (커밋 `f80760a`~`6579307`)

- **검색**: 직군별 둘러보기 = 직군 코드 필터(`/jobs/search?categories=`), 검색어 = 공백 단어분해 제목·회사명 부분일치(OR). 오타교정 v0.2.
- **찾아보기**: 좋아요→관심기업(회사기준 서버연동), 저장 서버연동, 본 공고 후순위(`SeenJobs` 로컬), 카드 제목정제(`displayRole`)+급여칩+근무조건 태그+본문 미리보기.
- **"오늘 새 공고" NEW 누적 버그 수정**: `JobKind.ACTIVE` 도입. applyDiff UNCHANGED→ACTIVE(NEW는 INSERT만). 메인=오늘 변화(NEW/UPDATE/CLOSING), 찾아보기=전체 진행중(ACTIVE 포함). 메인/캘린더/MainEmpty when에 ACTIVE 분기.
- **알림**: 듀오링고풍 간단 문구 + 다양 템플릿(`NotificationService` 아침5·저녁4·빈날3), dayOfYear 순환. "꽁이가 새 공고 N개 찾았어요 🐱" 식.
- **회사상세**: description 없으면 산업·규모·근무지·공고수로 소개문 자동생성(`buildAbout`).
- **★ 홈 위젯**: 새 공고 수 + 꽁이(상황별 표정 — 0건 Sleep/3일+미방문 Sad/5개+ Wow/그외 Happy). `Mascot`을 `drawMascot`(DrawScope) 추출 → `MascotRenderer`로 Bitmap 렌더(RemoteViews, Glance 미사용). 크기 3레이아웃(tiny 1x1/wide 2x1·4x1/세로 2x2·4x2, getAppWidgetOptions 분기). 큰 위젯에 마감임박. `WidgetState`(SharedPreferences). 사용자 PC 빌드·실기기 검증 완료(꽁이 정상 렌더).
- 한줄요약(Haiku) 폐기(비용), 약관 본문활용 허용.
- **온보딩 회사규모 ↔ 데이터 정합화**(`c35b639`): 온보딩 규모 6개 중 5개가 데이터 0(중소=서울 size null, 중견·외국계·스타트업 없음)이라 3개(대기업·공기업·중소)로 정리. 백엔드 `JobPersistenceService.inferSize`(공공기관→public, 서울→small, greenhouse/lever→large_corp). `JobService.today` pool 1000→3000. 라이브: large_corp 399·public 500·small 441. 온보딩 선택→코드 변환 ActiveFilter 저장(기존엔 선택이 저장 안 됐음).

**현재 상태/운영 메모(당시)**: 백엔드는 로컬(개발자 맥)에서만 실행 중, 클라우드 미배포. 키는 환경변수(JOBALERT_PUBINST_KEY, JOBALERT_SEOUL_KEY). 실행 후 `POST /api/v1/admin/collect`(공공기관 상세호출로 ~2분).

### 3-11. 2026-06-07 (Railway 클라우드 첫 배포)
백엔드를 로컬→**Railway 클라우드로 첫 배포**. 24시간 켜진 서버 + 매일 18시 자동수집이 처음 실제 가동.

**배포 결과(live)**:
- 플랫폼 Railway, 프로젝트명 `enchanting-wisdom`(GitHub 계정 `hkforwork5260-tech`).
- 공개 주소 `https://dsadjk22-production.up.railway.app` — health UP, jobs/today 399건.
- GitHub repo `hkforwork5260-tech/dsadjk22`. Railway 서비스 **Root Directory=`backend`** 필수(Dockerfile 위치). Auto deploy on push to main.
- DB: Railway 관리형 Postgres(서비스명 `Postgres`, DB명 `railway`). Redis 제거.

**커밋(main)**:
- `ffd20e8` 배포 준비: Dockerfile(멀티스테이지 JDK17→JRE) + .dockerignore(secrets 제외) + Redis 제거 + prod 정비 + DEPLOY_RAILWAY.md
- `d8df879` 수집 수동 트리거 비동기화(@Async + 202) — 클라우드 프록시 502 방지
- `47b2484` **수집 멈춤 수정** — Greenhouse content=false 기본 + 힙 75% + 단계 로그

**함정·교훈(재방문 시 중요)**:
1. **Railway DATABASE_URL은 `postgresql://`(JDBC 아님)** → 그대로 쓰면 부팅 크래시. 환경변수에 `DATABASE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/railway` 직접 박음. username/password는 `${{Postgres.PGUSER}}`/`${{Postgres.PGPASSWORD}}`. DB명은 `railway` 글자로 고정이 안전.
2. **수집이 작은 클라우드 박스에서 무한정 멈춤** — `GreenhouseSource`의 `content=true`가 쿠팡 261건 본문(HTML)까지 받아 응답이 매우 커서 제한된 메모리에서 처리속도 바닥(GC 쥐어짜기). 로컬(큰 힙)에선 안 드러남. → `content=false` 기본(`jobalert.sources.greenhouse.content` 토글) + `-XX:MaxRAMPercentage=75.0`. **이 트레이드오프로 greenhouse 본문 현재 미수집**(지원 링크는 있음).
3. **무거운 동기 HTTP 엔드포인트는 클라우드 프록시 502** — `POST /admin/collect`를 @Async로 즉시 202 반환, 수집은 백그라운드. 일일 cron은 서버 내부 호출이라 동기 유지.
4. 배포 중 실수로 중복 프로젝트 `zonal-learning` 생성됨(빌드 실패). 삭제 권장.

**환경변수(설정됨)**: DATABASE_URL/USERNAME/PASSWORD, SPRING_PROFILES_ACTIVE=prod, COLLECTOR_ENABLED=true, PUSH_ENABLED=false. 아직 안 넣은 것: JOBALERT_PUBINST_KEY(+PUBINST_ENABLED), JOBALERT_SEOUL_KEY(+SEOUL_ENABLED). 자세한 절차는 `backend/DEPLOY_RAILWAY.md`.

### 3-12. 2026-06-07 (클라우드 데이터 확장 + 관리자 인증)
이전 배포의 "미설정 3개"를 전부 처리한 후속 세션. 커밋 `21f3ef4`(main, push 완료). 전부 라이브 검증.
- **데이터 399 → 1,343건** — Railway Variables에 공공기관·서울 키 추가: `PUBINST_ENABLED=true`+`JOBALERT_PUBINST_KEY`, `SEOUL_ENABLED=true`+`JOBALERT_SEOUL_KEY`(+`SEOUL_MAX_ROWS=1000`). 라이브 실측: **공공기관 500 + 서울 445 + greenhouse 398, 회사 534곳**. 매일 18시 cron도 이 키로 자동수집.
- **`/api/v1/admin/*` 토큰 인증** — `AdminAuthInterceptor` + `WebConfig`. `ADMIN_TOKEN`(=`jobalert.admin.token`) 설정 시 `X-Admin-Token` 헤더 일치 요구(불일치 401), 빈값이면 개방+경고로그(로컬용). spring-security 안 씀(전체 잠금 방지, 경로만 매핑). 로컬 4케이스 검증. Railway에 `ADMIN_TOKEN` 설정됨(라이브 401 확인). 수동수집: `curl -X POST -H "X-Admin-Token: <토큰>" .../api/v1/admin/collect`.
- **안드 `ApiClient.BASE_URL`** = `https://dsadjk22-production.up.railway.app/`(로컬은 주석 보존).
- 중복 프로젝트 `zonal-learning` 삭제(사용자가 대시보드에서).

**오늘 1시간 헤맨 함정**:
- **Railway는 변수 수정 후 `Deploy`(Apply changes) 버튼을 눌러야 반영.** 안 누르면 "N Changes" 대기 상태로 옛 값 유지 → 키가 안 먹은 것처럼 보임. 이게 진짜 원인. (소스는 `@ConditionalOnProperty(PUBINST_ENABLED=true)`라 enabled는 먹었는데 키 값이 옛것=무효라 0건 → 399 그대로)
- **적재는 모든 소스 fetch 후 맨 끝에 한 번에**(`HybridCollectorService.runDailyCollection` line 78). 공공기관 상세 500여 호출이 끝나야 399→1,343으로 한순간 점프. 중간엔 변화 없음.
- **측정 함정**: `jobs/search?limit=N`의 `total_estimate`는 총량이 아니라 반환 개수(limit에 묶임). 총량 보려면 limit 크게(예 5000). 소스별 = id prefix(`greenhouse-`/`pubinst-`/`seoul-`)로 셈.
- Railway CLI 미설치 → 변수/삭제는 사용자가 대시보드에서. 로그는 Deploy Logs 탭(`active sources`로 소스 등록 확인).

**안드로이드 빌드 — 완료·검증됨**: 사용자 PC Android Studio에서 BUILD SUCCESSFUL + Pixel 7 에뮬에서 클라우드 1,343건 실표시 확인. 로컬 서버 없이 동작. "클라우드 연결" 목표 달성.
**502 주의(작은 박스 한계)**: 수집이 도는 동안엔 메모리 압박으로 `jobs/today` 등이 일시 502 → 앱에 "공고를 불러오지 못했어요 HTTP 502". 수집 끝나고 `다시 시도`면 정상. 매일 18시 자동수집 순간 재현 가능. 근본 해결은 박스 업그레이드 or 수집 시간대 회피.

### 3-13. 2026-06-08 (안드 출시빌드 + UX 대개편 + 찾아보기 랭킹)
사용자가 폰에 직접 설치해 쓰면서 피드백→수정→재빌드 사이클을 길게 반복. 빌드·설치는 사용자 PC(Android Studio, `cd ~/jobalert/android-app && ./gradlew assembleRelease` → APK를 바탕화면 `채용알리미-v0.1.apk`로 → 카톡 '나에게'로 폰 설치).

**정식 release 빌드(출시 준비, Play Console만 남음)**:
- `keystore.properties`(gitignore) + `release.jks`(alias `jobalert-release`). **비번·jks 백업 필수**(분실 시 Play 업데이트 영영 불가). build.gradle: 키 있을 때만 서명.
- google-services.json = 프로젝트 `jobjob-533ca`에 release 패키지 `com.jobalert.app` 추가(처음 실수로 새 프로젝트 `-91b81` 만듦→안 씀). debug=`.debug` 패키지라 둘 다 포함돼야 함.
- lint `InvalidFragmentVersionForActivityResult` 오탐 → build.gradle `lint { disable += ... }`(안 하면 lintVitalRelease가 release 빌드 막음).
- **Play Console 미등록**(개발자 계정 $25). 지금은 서명 APK 사이드로드. 서명 APK `CN=JobAlert`.

**핵심 구조 변경(재방문 시 중요)**:
- **관심↔필터 2-tier**(`ActiveFilter`): `interestCategories`/`interestSizes`(영속, 온보딩·내정보·푸시) vs 세션 `categories`/`experiences`/`sizes`/`deadlineDays`(비영속, init 시 관심으로 복사, 피드가 사용). `setInterest`(온보딩·내정보) / `setFilter`(필터 다이얼로그·일회성). prefs 키 문자열은 기존 호환 위해 `filter_*` 유지. 필터 적용해도 관심 안 바뀜·앱 재시작 시 관심으로 리셋.
- **오늘 vs 찾아보기 분리**: 오늘=세션 필터(앱 켜진 동안 유지, 재시작 시 관심 리셋). 찾아보기=필터 없음, `/jobs/discover`.
- **온보딩 1회만**: `ActiveFilter.onboardingDone`(처음 설치 1회→이후 NavGraph start=Main). `goMain()`에서 markOnboardingDone. 직군·규모 미리선택 제거(빈→직접 고름).

**새 백엔드 엔드포인트/로직**:
- `GET /jobs/by-ids?ids=` — 여러 ID 입력순 조회(본 공고 목록용). `/{id}`보다 리터럴 우선 매칭.
- `GET /jobs/discover` — 찾아보기 인스타 랭킹: 점수(관심기업+5·관심직군+3·저장취향+2·최신+2/+1·마감임박+1) + **소스+회사 2단 라운드로빈**(공공·gh·서울 1/3씩 번갈아 검증됨) + 관심:발견 3:1(발견 25%) + 그룹 난수셔플. 본 공고 후순위는 클라 로컬 SeenJobs.
- today(): 카운트를 필터된 후보에서 계산(필터 시 숫자 반영), `deadlineDays` 마감일 필터 추가. today·검색 limit 사실상 전부.
- search(): `total_estimate`=자르기 전 실제 매칭 수(공고(50) 하드코딩 해소). FilterScreen 더미 기본값·"17건" 제거.
- **ALIO 원문 직링크**: `recrutPblntSn`→`idx`(WebSearch 확인 — recrutPblntSn은 무시돼 통합목록으로 빠졌음). 기존 행은 **Flyway `V3__fix_alio_url.sql`**로 일괄 UPDATE(재수집이 박스서 불안정해 마이그레이션이 결정적). `applyDiff`는 originalUrl 갱신함(line 190).
- **본 공고 기록함**: `GET /jobs/by-ids`(입력순) + `SeenJobsScreen`. 상세 진입 시 `SeenJobs.markSeen`(영속, LinkedHashSet). 내정보 "본 공고 N ›" 탭→목록.

**UX 세부**:
- 가짜 상태바(9:41)/하단 제스처막대 = `HiFiStatusBar`/`HiFiGestureNav` 본문 비워 23개 화면 일괄 제거(루트 `windowInsetsPadding(systemBars)`가 처리). 카드 우측 버튼 영역 72dp gutter+제목 3줄 제한. **카드 로고 자리=근무지역**(`Job.regionShort`). 내정보 "관심"(관심기업 제거·부제 제거·톱니 제거·규모 실제값). 위젯 pin(`requestPinAppWidget`, WidgetPinner)·2x1은 새 공고 수 숫자. 런처 아이콘=꽁이얼굴(`ic_launcher_foreground.xml`). 본 공고=상세진입 markSeen(영속). 마이페이지 카운트 하드코딩 87 제거.

**트라이얼 박스 불안정(출시 전 필수 해결)**:
- 무료 Railway 박스가 수집·대량응답(오늘 limit 1000)에 **OOM 크래시** 반복(앱 502/서버 000). 데이터는 Postgres라 안전, **push 재배포로 부활**. 대시보드 클릭 시 404 버그도. → **유료 플랜 or 페이지네이션** 필요. 재수집보다 Flyway가 안정적.
- ADMIN_TOKEN=`09c92bec8b56d0d7e1dc914212fd17e9`. 미푸시 커밋 `0337bfa`(프론트는 push해도 백엔드 무관하나 Railway가 재배포돼서 보류).

**찾아보기 인스타 탐색 랭킹**(`82eb8f0`·`f46855a`·`0337bfa`): `GET /jobs/discover`(오늘 필터와 완전 분리, 필터버튼 없음). 미구현=머문시간 학습(4번, 앱 계측 필요).

### 3-14. 2026-06-09 (리브랜딩 + UX 40여건 + 분류 사투 + 502 cold-start + keep-alive)
매우 긴 단일 세션(06-08 밤 ~ 06-09 새벽). 사용자 실사용 피드백을 빠르게 반복 반영. 빌드는 **이 세션부터 개발 머신에서 직접 `./gradlew assembleDebug`로 검증**(웹샌드박스 불가는 옛 정보). 매 변경 후 APK 제공(카톡 사이드로드). 커밋 `6f0a864`(리브랜딩)~`1ef48c3`(응답경량화), ~30+ 커밋.

**완료**:
- **★ 리브랜딩**: 코랄+고양이 '꽁이' → 블루(#4F6EF0)+시바 '단이'. 참조 번들 `~/Downloads/mascot-dan/`(palette.css, dan-0X PNG 6종, app-icon.png).
  - `HiFiColors` 전면 교체(이름 유지·값만, 34파일 전파). 매핑: Brand `#4F6EF0`/BrandHover `#3D58D6`/BrandShadow `#3A52C9`/BrandSoft `#EAEEFE` / New·NewShadow·NewSoft `#1FA968`·`#178A55`·`#E7F6EE` / Update `#E89A4A`·`#C9803A`·`#FCEBD6` / Closing `#F0533A`·`#D2432D`·`#FDEAE6` / Text `#241F1B`·`#8A8178`·`#B5AEA4` / Bg `#FAF7F2`·`#FFFFFF`·`#EFEAE1` / Border `#ECE6DD`·`#DCD4C8`.
  - 마스코트 Canvas→PNG 6표정(`res/drawable-nodpi/mascot_*.png`, `MascotRenderer`는 BitmapFactory). MascotExpression enum 6종 이름 유지(23개 호출부·WidgetState 로직 안 건드림): Happy→mascot_happy, Default→mascot_calm, Wave→mascot_wink, Wow→mascot_excited, Sad→mascot_alert(전용 sad 없음→근접), Sleep→mascot_sleepy.
  - 앱 아이콘 블루+시바. '꽁이'→'단이' 텍스트(안드+백엔드) 일괄.
  - JobKind.label(): NEW→"NEW", UPDATE→"변경", CLOSING→"마감임박".
- **★ 원문 직링크 3소스 수정**: greenhouse embed URL(`job-boards.greenhouse.io/embed/job_app?for={토큰}&token={id}`), ALIO `mobile2021/recruit/recruitView.do?idx=`, 서울→고용24 `m.work24.go.kr/.../empDetailAuthView.do?wantedAuthNo={JO_REGIST_NO}&infoTypeCd=VALIDATION&...`. 기존 행 Flyway **V4**로 보정.
- **★ 직군 분류 91%**: 영문 키워드 대폭 보강 + 본문/오염 키워드 정밀화(finance 448 폭증 수정) + "기타(etc)" 폴백 + 재분류 API. (95%는 과다태깅 위험이라 멈춤, AI분류 v0.2.)
- **★ 메인 today 재설계**: 표시 kind 재계산(NEW=오늘 첫등장 or 안 본 공고 / 마감임박 D-3 / 마감 지난 건 제외 / 진입마다 now 기준 최신화). 오늘 칩 NEW/UPDATE/Hurry up!(카드 배지는 '마감임박'). 마감임박 마감순 정렬.
- **★ 위젯 자체 fetch**(`WidgetUpdater`): 앱 안 열어도 ~30분 주기로 백엔드 today 호출해 관심 기준 새 공고 수 갱신. 오늘 탭과 같은 기준(안 본 공고 포함)으로 통일.
- **★ 알림(다이제스트) = 오늘 규칙**: 아침=오늘 첫등장, 저녁=마감 D-3, 둘 다 **관심 직군+규모** 필터. 규모는 기기 등록에 추가(devices.interest_sizes, Flyway **V5**).
- **★ 첫 로드 502 대응**: today limit 1000→200, 목록 본문 160자로 자름(toDetailDto는 전체), today 풀 3000→2000, OkHttp 타임아웃 25s, 첫로드 자동 재시도 5회(2·4·6·8초 점증), 앱시작 warmup 핑(`MainActivity.warmUpServer` jobsToday(limit=1) 최대 6회 3초 간격).
- **★ keep-alive**: UptimeRobot(무료) 5분마다 `GET /actuator/health` 핑 — 사용자가 직접 설정 완료(Up/100%). GitHub Actions cron(`.github/workflows/keepalive.yml`)도 시도했으나 git/gh 토큰에 `workflow` 스코프 없어 push 거부 → UptimeRobot으로 대체.
- **위젯 크기별 핀**: Small/Medium/Large 별도 provider(`open class` 필요 — 빌드실패로 발견, Kotlin 클래스 기본 final). 마감캘린더 월 이동·날짜클릭·근무지역·NEW만 배지·upcoming 70일. 관심편집 라우팅 분리(EditJobCategory/EditCompanySize, editMode=true 저장 후 popBackStack)+이전/완료 모드. 찾아보기 라벨제거·밝은 블루·더블탭 좋아요·제목 자동축소·마감 제외. 알림 종 복원+빈 상태. 공유 카카오톡(`#FEE500` 유지). 회사 통계 허위정보(합격률·올해신규 라벨) 정리. 온보딩 산업군 제거·직군 '어디든'·건너뛰기=전체·1회만. 첫진입 도움말(오늘·마감·관심기업·찾아보기, `HelpDialog`+HelpIconButton).
- **제작 과정 기록**: `jobalert/PROJECT_HISTORY.md`(이 문서) 생성.

**이어갈 지점/주의**:
- 무료 박스 한계: cold start/OOM/재배포로 가끔 502. warmup·재시도·경량화·keep-alive로 가렸지만 근본은 유료 or 더 가벼운 today. **개발 중 잦은 재배포 자체가 502의 큰 원인** — 사용자 테스트 중엔 push 자제(하루 ~15번 재배포, 각 1~2분 다운).
- **NEW 정의는 "오늘+안 본"** — 신규 설치 시 안 본 게 많아 NEW가 크게 잡힘(브라우징하면 줆). limit 200이라 표시·카운트가 200으로 캡.
- 분류 측정 시 겹친 재수집(isRunning)·배포 지연·`job_categories`=영문코드(한글 라벨 아님) 주의.

**미완/다음**: Play Store 미등록(개발자 $25, 현재 사이드로드 디버그 `com.jobalert.app.debug`) · 푸시 실발송 검증(PUSH_ENABLED·FCM_ENABLED 미설정 시 폰 푸시 안 감, 앱 히스토리엔 보임) · v0.2(AI 직군분류 95%+, 검색 오타교정, 산업군 실필터, 위젯 FCM 즉시갱신, today 추가 경량화/캐싱).

---

## 4. 핵심 도메인 규칙 (코드 기준)

- **diff 라벨링**(`JobPersistenceService.applyDiff`): INSERT=NEW / 제목·마감 변경=UPDATE / 마감 임박(D-3)=CLOSING / 변화없음=ACTIVE / 사라짐=만료(isActive=false). 0건 받은 소스는 만료 스윕 제외. reactivate(만료→재수집 시 복귀) 처리. originalUrl 갱신(line 190).
- **회사 미스 시 자동생성**(isApproved=false) + 정규화명 캐시(matchOrNull 재매칭)로 중복생성 방지.
- **inferSize**: 공공기관→public, 서울→small, greenhouse/lever→large_corp. 신규+기존 size=null 회사 재수집 시 보정.
- **메인 표시 kind 재계산**(`JobService.today`→`displayKind`): 응답 시 메모리로 재계산 — 오늘(KST) 첫 등장=NEW(하루 유지), 마감 D-3 이내=CLOSING. 마감 지난 공고 제외(now 기준). JPA 관리 엔티티 직접 mutate는 위험(readOnly라 flush는 안 됐지만 주의).
- **JobMapper**: 목록 `toDto`는 `description?.take(160)`(미리보기), 상세 `toDetailDto`는 전체. D-day는 DB 저장 안 하고 매 응답 KST 계산(마감없음="상시", days<0="마감", 0="D-Day", else "D-N").
- **관심 ↔ 필터 2-tier 분리**: 관심(영속, 온보딩·내정보·푸시 개인화) vs 세션 필터(비영속, 일회성, 시작 시 관심으로 초기화).

## 5. 운영·보안 메모

- **/admin/* 토큰 인증**(`AdminAuthInterceptor`+`WebConfig`): `ADMIN_TOKEN` 설정 시 `X-Admin-Token` 헤더 일치 요구(불일치 401), 빈값이면 개방+경고로그. spring-security 안 씀(경로만 매핑). ADMIN_TOKEN=`09c92bec8b56d0d7e1dc914212fd17e9`.
- **시크릿(미커밋)**: `backend/secrets/fcm-service-account.json`(gitignore), `keystore.properties`+`release.jks`(release 서명, 비번 백업 필수), data.go.kr·서울 API 키(Railway Variables).
- **푸시 활성화 조건**: `PUSH_ENABLED=true` + `FCM_ENABLED=true`(+서비스계정 키). 기본 false라 안 켜져 있으면 실제 폰 푸시는 안 나감(앱 내 알림 히스토리엔 보임). Firebase `jobjob-533ca`. 서비스계정키 분실 시 Firebase 콘솔→프로젝트설정→서비스계정→새 키.
- **수집 활성화**: `COLLECTOR_ENABLED`, `PUBINST_ENABLED`+키, `SEOUL_ENABLED`+키(+`SEOUL_MAX_ROWS`). 실행: `JOBALERT_PUBINST_KEY=<키> PUBINST_ENABLED=true JOBALERT_SEOUL_KEY=<키> SEOUL_ENABLED=true ./gradlew bootRun` → `POST /api/v1/admin/collect`.
- **keep-alive**: UptimeRobot(무료)가 5분마다 `GET /actuator/health` 핑 → 박스 콜드/유휴 완화. actuator/health는 DB SELECT 1 수준이라 가벼움(무거운 today를 핑하면 역효과). 비용 무시 수준(박스는 어차피 24시간 가동).
- **무료 박스 한계 대응**: today limit 200·목록 본문 160자·today 풀 2000으로 응답/메모리 경량화 + 앱 warmup·재시도로 콜드스타트 흡수.

## 6. 알려진 함정·주의사항 (Compose/Kotlin/배포)

- Compose에 box-shadow 없음 → 3D 버튼은 그림자박스+본체 2겹 스택(`HiFiButton.kt`).
- `Double.dp`는 컴파일 에러 → `1.4f.dp` 또는 `2.dp`.
- 음수 `padding`도 에러 → `offset` 사용.
- `FlowRow`는 `@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)` 필요.
- Kotlin 클래스 기본 `final` → 상속하려면 `open class`(위젯 provider 빌드실패로 발견).
- 마스코트는 (초기) Canvas SVG path 재현 → (06-09) PNG 6표정으로 교체.
- (옛 정보, 현재 무효) "웹 샌드박스에선 Android Gradle 빌드 불가" → 06-09부터 개발 머신에서 직접 `./gradlew assembleDebug` 가능.
- Railway 함정: ①DATABASE_URL은 jdbc 아님(직접 박음) ②content=true 메모리과부하 ③무거운 동기 엔드포인트 502→@Async ④변수 수정 후 Deploy 버튼 ⑤적재는 맨끝 한번에 점프 ⑥total_estimate는 limit에 묶임.

## 7. 데이터 소스 등급표 (CLAUDE.md 기준)

- 🟢 **Greenhouse/Lever 공개 API**(인증 불필요): `boards-api.greenhouse.io/v1/boards/{token}/jobs`, `api.lever.co/v0/postings/{company}`. 한국 회사=쿠팡·크래프톤·당근·몰로코·센드버드(검증된 5곳이 사실상 전부). Lever 한국 회사 0.
- 🟢 **기재부 공공기관 채용 API**(data.go.kr/15125273): 라이선스 제한 없음 + 개발단계 자동승인 + 개인 가능 + JSON. 공공기관 340곳+. 상세 API `recruitment/detail?sn=`로 본문. 공공누리라 본문 자유.
- 🟢 **서울시 일자리포털**(data.seoul.go.kr OA-13341): 공공누리 1유형(상업 OK). 급여 제공 첫 소스. 노인·중장년 위주라 `isElderlyOrCareJob` 네거티브 필터.
- 🟡 **워크넷/고용24**(data.go.kr/3038225): 공공누리 4유형(비영리 무료앱 가능)이나 접근이 기업회원전용+영리법인불가+담당자심사라 불확실. 보류.
- 🟡 **원티드 OpenAPI**: 약관상 상업이용·개인신청 가부 불명.
- 🟠 **국내 대기업 자체 채용페이지**: robots 80%는 안 막으나 robots≠허락. 출처표기+원본링크 전제, 회사별 약관 확인 후만.
- **막힌 소스**: 그리팅(두들린, 고객사 키 필요, v0.5 제휴) · 잡코리아·점핏·잡다·로켓펀치(공개 API 없음). **잡코리아 크롤링 절대 금지 — 2017다224395 대법원 판례(합의금 120억, DB권+부정경쟁).**

## 8. 폐기·완화된 결정

- **AI 한줄요약 폐기(2026-06-06)**: Anthropic API 비용 부담. 비전 5번 무효. DTO·mock·안드 잔재 제거, DB 컬럼·엔티티 `Job.summary`만 null로 잔존.
- **약관 원칙 완화(2026-06-06)**: 기존 "메타데이터만/원문 복제 금지" → "출처표기+원본링크 유지하며 본문 활용"(포트폴리오·검증 단계, 분쟁 시 즉시 제거).
- **온보딩 산업군 섹션 제거(2026-06-09)**: 어떤 소스도 산업군 데이터를 안 줘서 회사 ~95% null, 실제 필터에도 안 쓰임 → 직군으로 대체.
- **온보딩 회사규모 6→3개(2026-06-07)**: 데이터 0인 규모 정리(대기업·공기업·중소만).

## 9. 만든 기능 인벤토리

- **수집·분류**: 4개 소스 수집, 직군 21개+기타 자동분류(`JobCategoryClassifier`), 경력(ExperienceClassifier)/규모 분류, 본문 수집(greenhouse content / 공공기관 상세 API / 서울 DTY_CN).
- **조회 API**: today(개인화·회사 다양성 interleave), search(직군필터+단어분해), discover(인스타 탐색 랭킹), upcoming(마감 캘린더 70일), similar(직군 겹침), by-ids(본 공고), companies/{id}/page.
- **화면**: 온보딩(직군·규모·위젯), 오늘(메인), 찾아보기(Reels VerticalPager), 마감 캘린더(월 이동·날짜클릭), 회사 상세, 공고 상세, 관심 기업, 마이페이지, 저장/본 공고, 검색, 공유(카카오톡), 알림 히스토리, 관심 편집.
- **푸시**: FCM 토큰 등록 + 매일 9시(새 공고)·21시(마감임박) 다이제스트(관심 직군+규모 필터, 듀오링고풍 문구 다종 순환).
- **위젯**: 새 공고 수 + 단이 표정(0건 Sleep/3일+미방문 Sad/5개+ Wow/그외 Happy). 크기 3종 핀. 위젯 자체 백엔드 fetch(30분 주기).
- **첫 진입 도움말**: 오늘·마감캘린더·관심기업·찾아보기 1회 다이얼로그(+'?' 재보기).

## 10. 전체 커밋 타임라인 (요약)

- **05-26~27**: HTML 프로토타입 26화면 → 안드 화면 20개 + 백엔드 Phase 1 스캐폴드(15 mock API) + bootRun 검증(`d1a0529`, `307c4e1`).
- **05-27~28**: 사람인 RealClient·회사명 정규화·Clearbit 로고(Phase 3 사전, `74c4a85`·`c346dae`·`88304be`·`4704a23`). 단위테스트 39.
- **06-03~05**: 사람인 거절 → 하이브리드 전환(`9097385`) → 공공기관·서울 소스(`b78c93e`·`f18b9e3`) → 수집→DB→API→앱 첫 엔드투엔드(`adfed35`·`a12ab17`·`99d4ac1`) → 직군분류·필터·관심기업·온보딩직군·FCM·다이제스트·푸시 스케줄(`cd17d1d`·`47ec03b`·`85d4ddd`·`c5fa89a`~`86028e8`).
- **06-06**: UX 개선 6건(`0662e36`·`cda0ba9`·`c1a06c8`·`bec5186`·`54f0218`) + 본문 수집(`8017227`·`c7bb3fb`) + 서울 소스(`7950821`·`7378fe8`) + 한줄요약 폐기 + 관심기업 UX(`9e87735`·`d608998`).
- **06-07**: 검색·찾아보기·알림·홈 위젯(꽁이 표정) 대개편(`f80760a`~`6579307`) + 온보딩 규모 정합화(`c35b639`) + Railway 클라우드 첫 배포(`ffd20e8`·`d8df879`·`47b2484`) + 데이터 1,343건 + /admin 인증(`21f3ef4`).
- **06-08**: 정식 release 서명 빌드(`3df8948`) + UX 13건 + 관심↔필터 분리(`479fec8`) + 찾아보기 인스타 랭킹(`82eb8f0`·`f46855a`·`0337bfa`) + 원문 직링크 + by-ids(`079f7c6`).
- **06-09**: 리브랜딩(`6f0a864`) + 메인 NEW 재정의·마감임박 D-3 + 첫진입 도움말 + 캘린더 월이동 + 관심편집 모드 + 위젯 자체 fetch + 알림 관심(직군+규모) + 회사통계 정리 + 첫로드 502 대응(`1ef48c3`) + keep-alive(UptimeRobot) + 원문 직링크 3소스(Flyway V4·V5) + 직군분류 91% + `PROJECT_HISTORY.md`.
- **06-09 밤~10 (FCM 푸시 가동 + 수집 OOM 사투 + 로딩 개선)**: 실사용 중 발견한 문제를 하나씩 추적해 잡은 긴 세션.
  - **FCM 푸시 첫 실가동**: 키를 파일이 아니라 **환경변수 JSON**(`FCM_CREDENTIALS_JSON`)으로 주입 가능하게(`1cabbaf`) → Railway에 `PUSH_ENABLED`·`FCM_ENABLED`·키 설정 → **테스트 푸시 폰 실제 도착**. 진단용 `GET /admin/push-status`(`8e9cf42`·`e1e88a5`). digest-all 비동기화(502 회피).
  - **수집 OOM "빠짐없이 vs 빠름" 사투**: 전체 한 번에 적재 → 무료 박스 OOM(완전 다운 실측). ①소스별 순차(`69cae74` 전신) → ②공공기관 단독도 OOM → ③**페이지(100건) 스트리밍 적재**(`69cae74`): `JobSource.fetchInBatches` + `persist(sweep=false)` + 소스 끝나면 `sweepExpiredForSource`로 **만료 분리**(안 받은 페이지를 마감 처리하는 사고 방지). 완전다운→회복가능. 수집 06·18시로 이동+공공기관 30분 뒤 단독.
  - **오늘 탭 NEW 규칙 재설계**(`8a9664b`): 관심 설정 **당일=조건 맞는 전체**·**다음날부터=신규만**. 공고별 seen 기록이 아니라 "관심 설정일" 날짜 하나만 저장(`ActiveFilter.showAllToday`).
  - **로딩 속도 3겹 해결**: today limit 200→1500(전체, 마감임박 누락 해결)로 응답이 커지자 → ①**목록 경량화**(`9ed0aa9`, `JobMapper.DtoScope`: 홈/검색은 본문·태그·급여 등 제거, 찾아보기는 풀 필드) today 790KB→431KB·0.93초 ②**메모리 캐시**(`b069c13`, 탭 왕복 즉시) ③**영속 캐시**(`bcda1ec`, `FeedCache`+stale-while-revalidate): **서버가 죽어도 마지막 화면 즉시 표시** 후 백그라운드 갱신.
  - **★ 이 세션의 교훈(함정)**: ①Railway 변수는 화면 표시(false)가 옛 캐시일 수 있음 → 실제값은 `/admin/push-status`로 확인(1시간 헤맴). ②keep-alive(UptimeRobot)는 **sleep 방지지 OOM 방지가 아님** — 무거운 호출 연속이면 박스가 죽음(Restart로 회복). ③공공기관 소스 ID는 `public-institution`(`pubinst` 아님). ④부팅 직후 박스는 약해 무거운 호출(limit 1500)에 또 크래시 — 검증도 가볍게.

## 11. 현재 상태 & 남은 것

- ✅ 라이브 배포·자동수집(페이지 스트리밍, 06·18시)·4소스·직군분류 91%·리브랜딩·위젯 자체갱신·도움말·관심(직군+규모) 푸시·**FCM 실발송 가동**·원문 직링크·**목록 경량화**·**오늘 피드 영속 캐시**(서버 다운에도 즉시 표시).
- ⚠️ **Play Store 미등록**(개발자 계정 $25 필요) — 현재 사이드로드(카톡으로 폰 설치, 디버그 패키지 `com.jobalert.app.debug`).
- ⚠️ **트라이얼 박스 불안정**: 무거운 부하(대량 호출 연속)에 OOM 크래시(Restart로 복구). 영속 캐시로 앱은 빈 화면 방지하나, **근본 해결은 출시 시 유료 전환**. keep-alive는 sleep만 막고 OOM은 못 막음.
- 📋 v0.2 후보: AI 직군분류(95%+), 검색 오타교정(형태소), 산업군 실필터, 위젯 FCM 즉시 갱신, 알림 실발송 매일 검증(내일 9시~), 알림에 마스코트 표정, 찾아보기 머문시간 학습, 박스 유료화.

## 12. claude.ai에서 가공할 때 참고

- 더 깊은 의사결정·운영 함정: 저장소 루트 `CLAUDE.md`(영속 프로젝트 기억), `API_CONTRACT.md`, `PHASE_PLAN.md`, `~/.claude/projects/.../memory/session_end_*.md`(세션별).
- 코드 인용 시: 안드 `ui/screens/*`·`widget/*`, 백엔드 `service/JobService.kt`·`service/JobPersistenceService.kt`·`service/JobCategoryClassifier.kt`·`service/JobMapper.kt`·`client/source/*`.
- 톤: 1인 코딩 초보가 "원리 이해 후 응용"하며 시행착오로 배운 과정 — 회고/포트폴리오에 적합.
