# 채용알리미 (JobAlert)

> **Future Claude sessions read this first.** This is the project's durable memory across sessions. Keep it accurate when major decisions change.

## 비전 한 줄

취준생을 위한 매일 1분 채용 정보 앱. 듀오링고처럼 짧게 자주 들르고, 매일 새 공고 푸시로 잠금화면에 노출된다.

## 타겟 사용자

고스펙 대기업·중견·빅테크 지망 취준생. 자기 직군의 새 공채/수시 공고를 놓치기 싫어한다. 한 번에 길게 보지 않고 짧게 자주 확인하는 패턴.

## 핵심 가치

1. **자동 수집·자동 분류** — 매일 18시 KST 사람인 OpenAPI로 수집, 어제 대비 diff로 NEW / UPDATE / CLOSING 자동 라벨링
2. **고정 시간 푸시** — 매일 9시·21시 자동 발송. 사용자가 시간 설정 안 함 (귀찮음 제거)
3. **잠금화면 풀스크린 푸시** — 알림에서 바로 공고 보고 지원 이탈 가능
4. **홈 위젯** — 듀오링고 스트릭처럼 매일 활성. v0.2 예정
5. **AI 한줄 요약** — 공고 본문을 Claude Haiku로 요약. "꽁이의 한줄 요약" 카드로 노출

## 디자인 톤

듀오링고 + 한국 모바일 UX. 코랄 브랜드 (#FF6B35) + 3D 깊이감 버튼 + 마스코트 '꽁이' (고양이). 폰트는 Pretendard.

상세 디자인 토큰은 `project/index.html` 내부 React 컴포넌트의 inline CSS 변수 + `android-app/app/src/main/kotlin/com/jobalert/app/ui/theme/` 참고.

## 의사결정 기록

### 데이터 소스 (2026-06-04 전면 수정 — 사람인 거절로 하이브리드 전환)

> **중대 변경**: 2026-06-04 사람인 API 담당자(최호성)가 거절 회신. "운영정책상 개인 프로젝트는 승인 불가. API는 비영리 목적(학교·정부기관 등 공공 비영리 단체)에 한해 제공." → **사람인 단독 전략 폐기.** 재신청해도 자격 미달(개인 포트폴리오)이라 무의미.
>
> 2026-06-04~05 대안 데이터소스 전수조사 완료. 결론: **"1000개 대기업 전체 자동수집"은 개인이 합법으로 어렵다**(삼성·LG·네이버·카카오 직접 공채를 주는 합법 무료 API 없음). 대신 **여러 합법 무료 소스를 합치는 하이브리드**로 충분히 굴러가는 앱 가능. 비전을 "대기업 올인"→"취준생 채용 알리미(대기업은 부분)"로 조정.

#### v0.1 합법·무료 데이터 스택 (등급별, 2026-06-05 검증 기준)
- 🟢 **즉시·깨끗 — Greenhouse / Lever 공개 API** (인증 불필요): `GET boards-api.greenhouse.io/v1/boards/{token}/jobs`, `GET api.lever.co/v0/postings/{company}`.
  - **실측 확인(2026-06-05)**: 쿠팡(`coupang`) 한국 공고 261건(신입 포함), 크래프톤(`krafton`) 50건 → **한국 빅테크/대기업 실 커버됨**. databricks·spotify 등 글로벌 빅테크 한국지사도. 코드 구현 완료(`client/source/`), 토큰만 추가하면 동작.
- 🟢 **즉시·깨끗 — 기재부 공공기관 채용정보 API**: `data.go.kr/data/15125273`. **라이선스 제한 없음 + 개발단계 자동승인 + 개인 가능 + JSON**. 공공기관 340곳+(한전·인국공·국민연금·코레일…). 고스펙 취준생 핵심 타겟(공기업) 커버. → 가장 깨끗한 메인 소스 후보.
- 🟢 깨끗 — **서울시 일자리포털**(`data.seoul.go.kr OA-13341`): 공공누리 **1유형**(상업이용도 OK). 서울 중소·중견.
- 🟡 **조건부 — 워크넷/고용24 채용정보 API**(`data.go.kr/data/3038225`): 라이선스는 공공누리 4유형이라 **비영리 무료앱은 사용 가능**. BUT 접근이 ⓐ work24 **기업회원 전용** ⓑ 약관에 **"영리법인(개인사업자) 사용 불가"** ⓒ **담당자 심사**(자동승인 아님) → 개인 비영리 신청 통과 여부 **불확실**. 문의처: 043-870-8556 / workmaster@keis.or.kr. 응답스펙은 풍부(coTp=01 대기업필터, 상세에 연매출·근로자수·직무본문). 통과 시 좋은 보조 소스.
- 🟡 조건부 — **원티드 OpenAPI**(`openapi.wanted.jobs`): 인증키 신청(3영업일). 빅테크·스타트업 강점. 약관상 상업이용·개인신청 가부 **불명** → 문의 필요.
- 🟠 **회색지대 — 국내 대기업 자체 채용페이지(메타데이터+원본링크만)**: robots 실측 결과 80%(현대·KT·쿠팡 등)는 robots로 안 막음, 카카오식 강차단은 ~7%(카카오 401·롯데 WAF). **단 robots≠허락** — 본문 복제 금지·제목/마감일/링크만·출처표기 시 저위험(구글검색식). 카카오·삼성처럼 봇 막는 곳은 제외. 회사별 약관 확인 후에만.

#### 막힌 소스 (확인 완료)
- **그리팅(두들린)**: 공개 API 있으나(`oapi.greetinghr.com`) **고객사 API Key 필요** → 3자 집계 불가. v0.5 제휴 문의 대상.
- **잡코리아·점핏·잡다·로켓펀치**: 공개 API 없음. 잡코리아 크롤링은 **2017다224395 대법원 판례(합의금 120억, DB권+부정경쟁)** 직격 — 절대 안 함.
- **한국산업인력공단·커리어넷 API**: 채용공고 아님(자격증·직업정보).

#### 핵심 개념 메모 (세션 학습)
- robots.txt는 **기술적 잠금이 아니라 신사협정(팻말)**. 무시해도 대부분 서버는 응답(카카오 robots 401인데 공고 API는 JSON 줌). "기술적으로 되냐"와 "해도 되냐(약관·재배포)"는 별개.
- 채용 "사실"(제목·마감일·직군)은 저작권 대상 아님 / 공고 "본문 글"은 어문저작물. → **사실+링크만 쓰면 저작권 회피, 본문 복붙은 위험.**
- "회사에 이득"은 실질 위험을 낮추지만(실제로 잘 안 싸움) 법적 방패는 아님.

### 회사 풀 (확정)
- v0.1 시작: **1000개**
- 시드: 공정거래위원회 공시대상기업집단 (88개 그룹의 약 800~1000개 계열사) + 빅테크·금융·공기업 보충
- 자동 성장: 매일 수집 중 새 회사 발견 → admin 검토 → 자동 추가
- 메타데이터: 각 소스 응답 + Clearbit 로고 API
- **회사 풀 재정의 필요(2026-06-04)**: 사람인 폐기로 "1000개 일괄"보다 **소스별 회사 리스트**로 전환 — Greenhouse/Lever 토큰 보유 한국기업(쿠팡·크래프톤 등) + 기재부 공공기관 340곳 + 워크넷·서울시 자동 유입. `SourceRegistry`에서 관리.

### 영리화 (확정)
- **v0.1 광고/결제 ❌** — 무료 공개 API(Greenhouse/Lever)·메타데이터 수집 단계에선 재판매·상업화 분쟁 소지를 피하려 광고/결제 일체 없음
- **v1.0** 정식 제휴(그리팅 등) 확보 후 광고 도입 검토
- 현재는 포트폴리오·사용자 검증·시장 학습 목적

### 기술 스택 (확정)
- **안드로이드**: Kotlin + Jetpack Compose + Navigation Compose. Compose BOM 2024.10, Kotlin 2.0.21, AGP 8.6
- **백엔드**: Kotlin + Spring Boot 3.x + PostgreSQL + Redis (큐). Java 17+
- **AI 요약**: Claude Haiku (`claude-haiku-4-5-20251001`) via Anthropic SDK
- **푸시**: Firebase Cloud Messaging (FCM)
- **호스팅**: Railway 또는 Fly.io (v0.1). 매출 발생 시 AWS/GCP 이전 검토
- **로고**: Clearbit Logo API (`logo.clearbit.com/{domain}`) — 무료, fallback은 회사 첫 글자 텍스트

### 약관·법적 제약
- **출처 표기**: 공고마다 출처 소스 명시(예: "출처: Greenhouse", "출처: ○○ 채용", 원본 링크). 사람인 전제 폐기.
- **Greenhouse/Lever 공개 API**: 인증 불필요·공개 엔드포인트. 과도한 호출 자제(회사당 1일 1~2회), User-Agent 명시 권장.
- **국내 대기업 페이지 수집(③)**: robots.txt 준수 + 약관에 자동수집 금지 조항 없는지 회사별 확인 + **메타데이터(제목·직군·마감일·원본 URL)만**, 원문 복제 금지. 지원은 원본으로 보냄.
- 잡코리아·캐치·원티드·점핏 등 타 채용 플랫폼 크롤링은 **법적 위험 (잡코리아 vs 사람인 2017다224395 대법원 판례)** — 절대 안 함

## 폴더 구조

```
.
├── CLAUDE.md                    # ← 이 파일. 영속적 프로젝트 기억
├── PHASE_PLAN.md                # 단계별 작업 계획
├── API_CONTRACT.md              # REST API 스펙 (frontend·backend 공유 계약)
├── README.md                    # 디자인 핸드오프 번들 README
├── chats/                       # 디자인 단계 채팅 기록
├── project/                     # HTML 프로토타입 (26개 화면, React+Babel inline)
│   └── index.html               # 인터랙티브 프로토타입 본체
├── android-app/                 # 안드로이드 앱 (Jetpack Compose)
│   ├── app/src/main/kotlin/com/jobalert/app/
│   │   ├── ui/theme/            # 디자인 시스템 (Color, Type, Theme)
│   │   ├── ui/components/       # 재사용 컴포넌트 (HiFiButton, JobCard, Mascot 등)
│   │   ├── ui/screens/          # 화면별 컴포저블
│   │   ├── nav/                 # NavGraph
│   │   └── data/                # 모델·샘플·Repository
│   └── README.md                # 안드로이드 모듈 README
└── backend/                     # Spring Boot 백엔드 (다음 세션부터)
    └── (TBD)
```

## 현재 상태

### ✅ 완료
- HTML 프로토타입 (`project/index.html`) — 26개 화면 전부, jsdom 검증 26/26
- 의사결정: 데이터 소스 / 회사 풀 / 영리화 / 기술 스택 모두 확정
- **Phase 1 백엔드 코어 (`backend/`)** — Spring Boot 3.5 + Kotlin 2.0 스캐폴드 / Flyway 스키마 10개 테이블 / JPA Entity·Repository / REST API 15개 엔드포인트 mock 응답 / 사람인 mock client / 수집 cron 골격 / 회사 시드 57개 placeholder / Docker Compose. `./gradlew compileKotlin` 통과.
  - **bootRun 실 검증 (2026-05-27)**: postgres+redis 도커 + Spring Boot 2.3초 부팅 + 주요 5개 엔드포인트 200. `NoResourceFoundException` 404 매핑 fix (`d1a0529`).
- **Phase 3 사전 작업 (2026-05-28, 백엔드 v3 세션)** — 사람인 키 도착 전 키 없이 가능한 작업 풀스택 사전 구현 (단위 테스트 39개 PASS):
  - 카테고리 코드 키 sync — 백엔드↔안드로이드 6개 코드 키 통일, API_CONTRACT에 정식 21개 표 추가 (`74c4a85`)
  - SaraminRealClient 본구현 — 공식 명세 정확 반영 / RestClient 3s·7s 타임아웃 / 4xx·5xx + 사람인 자체 에러코드(1/2/3/4/99) 매핑 / ApiCallLogger 연동 / JobCollectorService 페이지네이션 + 5xx 1회 재시도 + 일일 한도 도달 시 중단 (`c346dae`)
  - 회사명 정규화 + dedup 매처 — `(주)`·`㈜`·`주식회사`·`Corp` 등 마커 안정상태까지 제거 + NFKC + 영문 lowercase, exact match → alias fallback (`88304be`)
  - Clearbit 로고 리졸버 — homepage URL > 한글 회사명 dictionary(30+) > 영문 slug 휴리스틱 (`4704a23`)
  - 의존성: MockK 1.13.13 추가 (testImplementation, Kotlin 친화 모킹)
- **Phase 2.A + 2.B + 2.C 안드로이드 화면 20개 완료** (`android-app/`):
  - 온보딩 ①②③④, 메인, 메인 빈 상태, 공고 상세, 필터, 검색, 검색 결과,
    회사 상세 (공고 있음/없음 2종), 관심기업, 마이페이지, 알림 히스토리, 마감 캘린더
  - **2.C (2026-05-27)** — 찾아보기 Reels (VerticalPager), 공유 시트 (BottomSheet),
    비슷한 공고, 마이페이지 서브 4개 (알림 설정 / 위젯 설정 / 관심 직군 / 피드백)
  - `data/api/MockApi.kt` — API_CONTRACT.md 형식 그대로 mock 응답 (백엔드 붙기 전 임시)
  - 디자인 시스템 컴포넌트 10개 + 라우트 16개
- **★ 수집→DB→API→앱 엔드투엔드 연결 (2026-06-05, 백엔드 v5 + FE 세션)** — 4개 부품이 처음으로 끝까지 실연결. 단위 57개 + 라이브 검증 PASS. 커밋 3개(`adfed35`·`a12ab17`·`99d4ac1`):
  - **적재·diff 파이프라인** (`JobPersistenceService`) — RawJobPosting → jobs upsert + 어제 대비 diff(INSERT=NEW / 제목·마감변경=UPDATE / 마감3일내=CLOSING우선 / 사라짐=만료 isActive=false). 회사 매칭 미스 시 **자동 생성(isApproved=false)** — 사장님 결정. 안전장치: 회사 중복생성 방지(정규화명 캐시+재매칭), **0건 받은 소스는 만료 스윕 제외**(API 장애가 전체 공고 닫는 사고 방지). `TimeConfig` Clock 빈 주입.
  - **조회 API 실 DB화** — `JobService`·`CompanyService`가 MockDataProvider→Repository. `JobMapper`(엔티티→DTO, 회사임베드 N+1회피, D-day KST 파생계산·마감없으면 "상시"). `AdminController` POST `/api/v1/admin/collect` 수동 수집 트리거(v0.1 무인증).
  - **안드로이드 메인 화면 백엔드 연결** — Retrofit+OkHttp+kotlinx.serialization 도입. `ApiClient`(Json SnakeCase 네이밍전략으로 camelCase↔snake_case 자동, BASE_URL=10.0.2.2:8080 에뮬레이터), `ApiService`(jobs 엔드포인트만), `JobRepository`(DTO→도메인), `MainViewModel`(Loading/Success/Error, @JvmOverloads로 viewModel() 호환), `MainScreen` 3상태 렌더. ApiModels @Serializable + deadline/postingDate nullable.
  - **라이브 검증**: docker postgres + Greenhouse 실호출 → 388건 적재(쿠팡268·크래프톤51·당근40·Moloco20·Sendbird9), 회사3곳 자동생성, 2회차 멱등성(inserted=0 unchanged=388), **에뮬레이터 메인 화면에 실 쿠팡 공고 표시 성공**.
  - **★ 기재부 공공기관 소스 활성화 (2026-06-05 같은 세션)** — `JOBALERT_PUBINST_KEY` + `PUBINST_ENABLED=true`로 켜서 검증. 500건 수집 → **회사 5곳→188곳**(근로복지공단·국립공원공단·한전KPS·대한적십자사·국립중앙의료원 등 공기업 대거 자동생성, isApproved=false). 마감일 있어 `/jobs/upcoming` 캘린더 실동작(14일내 369건, D-day 정확). 단 Greenhouse 한국 토큰은 추측 발굴 실패 — 검증 5곳이 사실상 전부(회사별 채용페이지 수동 확인 필요).

### 🚧 진행
- **데이터 소스 하이브리드 전환 (2026-06-04 진행 중)** — 사람인 거절로 ①Greenhouse/Lever 공개 API + ③국내 대기업 페이지(검증 후) 합치는 수집기로 재설계. 소스 어댑터 패턴(`JobSource` 인터페이스 + `RawJobPosting` 공통 모델).
- 사람인 코드(`SaraminRealClient` 등) — 폐기 아님, 한 소스로 격하/비활성. 사업자등록 후 재신청 시 재사용 가능.
- 회사명 매칭(Normalizer/Matcher), Clearbit 로고 Resolver — 소스 무관 자산이라 그대로 재활용
- ③ 국내 대기업 robots/약관 검증 — 미착수 (코딩 전 선행 필요)
- FCM·푸시 — 미작업 (Phase 3)
- Claude Haiku 한줄 요약 — 미작업 (Phase 3). Job 엔티티 summary 필드는 있으나 null
- 회사 시드 — placeholder 57개. 하이브리드 전환으로 "Greenhouse/Lever 토큰 보유 회사" + "수집 가능 국내 대기업" 리스트로 재정의 필요
- Play Store — 미작업 (Phase 5)

### 📋 다음 단계 (2026-06-05 기준 우선순위)
1. **안드로이드 화면 백엔드 연결** — ✅ 메인·검색·캘린더·회사상세·직군필터·**관심기업** 연결 완료(Repository+ViewModel+UiState, 실 DB). 관심기업은 익명 기기ID(SharedPreferences UUID + X-Device-Id 인터셉터) 기반 user_favorites DB, 별표 토글+목록+회사page isFavorited 반영. 회사상세 `/companies/{id}/page`, 직군필터 `ActiveFilter`+`?categories=`, 검색 BasicTextField.
   **미연결**: ⓐ알림=Firebase FCM 설정(사장님 계정·google-services.json·서버키)+푸시발송 선행, ⓑ온보딩 추천회사=공고많은회사 쿼리+logo DTO(저가치). MainEmptyScreen은 mock 유지.
   **안드로이드 빌드 검증은 사용자 PC에서만**(이 환경 Google Maven 차단). 필터는 직군만 적용(규모·경력·지역·마감 facet은 백엔드 미지원).
2. **수집 소스 확장** — ✅ 기재부 공공기관 활성화 완료(188곳). 남은 것: Greenhouse 한국 토큰은 추측 불가(검증 5곳이 전부) → 회사별 채용페이지 수동 확인하거나 보류. pubinst는 max-pages 5(500건) 캡 — 더 받으려면 늘리기. **활성화 방법**: `JOBALERT_PUBINST_KEY=<data.go.kr 키> PUBINST_ENABLED=true ./gradlew bootRun` (키는 사용자 보유, 저장소엔 미커밋).
3. ✅ **직군 분류 완료** — `JobCategoryClassifier`(제목+부서+키워드 규칙 매칭)로 21개 직군 자동 태깅. 적재 시 newJob/applyDiff에서 분류. `/jobs/today?categories=`로 필터. 21개 직군 전부 채워짐(미분류 14%). 정확도 개선(AI 분류)은 v0.2. **FE 필터 화면 연결은 미완**(나머지 화면 작업에 포함).
4. FCM 푸시 / Haiku 한줄요약 (Phase 3 잔여).

수동 수집: `POST /api/v1/admin/collect`. 일일 자동수집 cron은 `jobalert.collector.enabled=true`로 활성(기본 false).

## 컨벤션

### Kotlin/Compose
- 패키지: `com.jobalert.app`
- 컴포넌트 네이밍: `HiFi*` 접두사 (디자인 시스템 컴포넌트), 화면은 `*Screen`
- 디자인 토큰은 `ui/theme/` 안에서만 정의. 컴포넌트는 토큰을 import해서 사용
- `MaterialTheme`은 일부 슬롯만 매핑. 컴포넌트는 거의 다 커스텀 (디자인 충실도 우선)

### 백엔드 (예정)
- 패키지: `com.jobalert.backend`
- Layered: `controller` → `service` → `repository` → `client(saramin, claude)`
- DTO ↔ Entity 분리. Entity는 JPA, DTO는 controller-service 경계
- 모든 시간은 `Instant` 또는 `OffsetDateTime` (UTC) DB 저장. 사용자 노출 시점에 KST 변환
- API 응답 envelope: `{ data: {...}, meta: { ... } }` 또는 단순 객체. `API_CONTRACT.md` 따름

### Git
- 한 세션 = 하나 이상의 의미 있는 commit
- commit 메시지: 한국어 OK. 제목 + 본문 (왜) 구조
- main 브랜치 직접 push (1인 프로젝트)

## 새 세션 시작 시 권장 프롬프트

```
1. CLAUDE.md, PHASE_PLAN.md, API_CONTRACT.md를 먼저 읽어주세요.
2. git log --oneline -20 으로 최근 진행 상황 파악하세요.
3. 그리고 [Phase X — 작업 이름]을 시작하겠습니다.
```

## 알려진 함정·주의사항

- **Compose에 box-shadow 없음** — 듀오링고풍 3D 버튼은 그림자 박스 + 본체 박스 2겹 스택으로 구현 (`HiFiButton.kt` 참고)
- **`Double.dp`는 컴파일 에러** — `1.4.dp` 대신 `1.4f.dp` 또는 `2.dp` 사용
- **음수 `padding`도 에러** — `offset` 사용
- **마스코트 꽁이**는 현재 Canvas로 SVG path 재현. 출시 전에 일러스트레이터 의뢰 → VectorDrawable 또는 Lottie로 교체 권장
- **이 웹 샌드박스에선 Android Gradle 빌드 불가** (Google Maven 차단). 빌드 검증은 사용자 PC에서.

## 연락처·자원

- 사람인 OpenAPI: https://oapi.saramin.co.kr/
- 사람인 약관 (주의사항): https://oapi.saramin.co.kr/caution
- 공정위 공시대상기업집단: https://www.ftc.go.kr/
- Clearbit Logo API: https://clearbit.com/logo
- Anthropic API 문서: https://docs.anthropic.com/
