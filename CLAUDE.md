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
- 🟠 **회색지대 — 국내 대기업 자체 채용페이지(메타데이터+원본링크만)**: robots 실측 결과 80%(현대·KT·쿠팡 등)는 robots로 안 막음, 카카오식 강차단은 ~7%(카카오 401·롯데 WAF). **단 robots≠허락** — 출처표기+원본링크 유지하며 본문 활용(2026-06-06 사용자 결정). 카카오·삼성처럼 봇 막는 곳은 제외. 회사별 약관 확인 후에만.

#### 막힌 소스 (확인 완료)
- **그리팅(두들린)**: 공개 API 있으나(`oapi.greetinghr.com`) **고객사 API Key 필요** → 3자 집계 불가. v0.5 제휴 문의 대상.
- **잡코리아·점핏·잡다·로켓펀치**: 공개 API 없음. 잡코리아 크롤링은 **2017다224395 대법원 판례(합의금 120억, DB권+부정경쟁)** 직격 — 절대 안 함.
- **한국산업인력공단·커리어넷 API**: 채용공고 아님(자격증·직업정보).

#### 핵심 개념 메모 (세션 학습)
- robots.txt는 **기술적 잠금이 아니라 신사협정(팻말)**. 무시해도 대부분 서버는 응답(카카오 robots 401인데 공고 API는 JSON 줌). "기술적으로 되냐"와 "해도 되냐(약관·재배포)"는 별개.
- 채용 "사실"(제목·마감일·직군)은 저작권 대상 아님. **공공기관 데이터는 공공누리(이용허락 제한 없음)라 본문도 자유 활용 OK.** 민간(Greenhouse 등) "본문 글"은 어문저작물이지만 — **출처표기 전제로 본문 활용함(2026-06-06 사용자 결정: 포트폴리오·검증 단계, 분쟁 시 즉시 제거).** 타 채용 플랫폼(잡코리아 등) 크롤링만 절대 금지(판례).
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
- **본문 활용 (2026-06-06 사용자 결정 — 기존 "메타데이터만/원문 복제 금지" 폐기)**: 공공기관(공공누리)은 본문 자유 활용. 민간(Greenhouse/Lever·국내 대기업 페이지)은 **출처표기 + 원본 링크 유지하며 본문 활용**. robots.txt·약관 자동수집 금지 조항은 회사별 계속 확인(스크래핑 자체의 적법성은 별개 이슈). 지원은 원본으로 보냄.
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
- **★★ 리브랜딩 + UX 40여건 + 분류91% + 502대응 + keep-alive (2026-06-09)** — 상세는 `memory/session_end_2026-06-09.md` + 저장소 루트 `PROJECT_HISTORY.md`(제작 전 과정 기록). 핵심:
  - **리브랜딩**: 코랄+고양이'꽁이' → **블루(#4F6EF0)+시바'단이'**. HiFiColors 전면 교체, 마스코트 Canvas→PNG 6표정(`res/drawable-nodpi/mascot_*`), 앱아이콘, 텍스트 '꽁이'→'단이'(안드+백엔드).
  - **원문 직링크 3소스 수정**: greenhouse embed(`job-boards.greenhouse.io/embed/job_app?for=토큰&token=id`), ALIO `mobile2021/recruit/recruitView.do?idx=`, 서울→고용24 `m.work24.go.kr ...wantedAuthNo=JO_REGIST_NO`. 기존행 Flyway V4.
  - **직군 분류 91%**: 영문 키워드 보강+오염키워드 정밀화(finance 448폭증 수정)+**"기타(etc)" 폴백**+재분류 API. (95%는 과다태깅 위험·AI분류 v0.2.)
  - **메인 today 재설계**: 표시 kind 재계산 — **NEW=오늘(KST) 첫등장 또는 아직 안 본 공고**(필터·관심 바꾸면 새 매칭), 마감임박=D-3, 마감 지난 건 제외, 진입마다 now 기준. 오늘칩 NEW/UPDATE/Hurry up!(카드 배지는 '마감임박'). 위젯·오늘·알림 셋 다 같은 관심 기준(직군+규모).
  - **위젯 자체 fetch**(`WidgetUpdater`): 앱 안 열어도 ~30분 주기로 today 호출해 갱신. **알림 다이제스트도 오늘 규칙(관심 직군+규모)** — 규모는 `devices.interest_sizes`(Flyway V5).
  - **★ 무료박스 502 대응**: today limit 1000→200, 목록 본문 160자로 자름(상세는 전체), today풀 3000→2000, OkHttp 타임아웃 25s, **첫로드 자동 재시도 5회 + 앱시작 warmup 핑**, **keep-alive=UptimeRobot 5분마다 `/actuator/health`**. **개발 중 잦은 재배포(커밋마다 1~2분 다운)도 502의 큰 원인 → 테스트 중 push 자제.**
  - 그 외: 위젯 크기별 핀(`open class`), 캘린더 월이동·근무지역·NEW만, 관심편집 분리(이전/완료), 찾아보기 라벨제거·밝은블루·더블탭, 회사통계 허위정보(합격률·올해신규 라벨)정리, 온보딩 산업군제거·직군'어디든', 첫진입 도움말(HelpDialog), 공유 카카오톡.
  - **★ 빌드: 이제 개발 머신에서 직접 `./gradlew assembleDebug`로 검증**(아래 함정 정정 참고).
- **★ 클라우드 배포 (2026-06-07, Railway)** — 백엔드가 로컬→**24시간 클라우드 서버**로 첫 배포. 공개주소 `https://dsadjk22-production.up.railway.app`(health UP, jobs/today 399건). 매일 18시 KST 자동수집 cron 가동. 프로젝트 `enchanting-wisdom` + 관리형 Postgres(DB명 `railway`), Redis 제거. repo `hkforwork5260-tech/dsadjk22`, **Root Directory=`backend`**, push시 자동재배포. 절차=`backend/DEPLOY_RAILWAY.md`. 함정 3개: ①Railway `DATABASE_URL`은 `postgresql://`(jdbc아님)→env에 `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/railway` 직접 박음 ②수집 멈춤=Greenhouse `content=true`가 메모리작은 박스서 과부하→`content=false` 기본(그래서 **greenhouse 본문 현재 미수집**)+힙75% ③무거운 동기 `/admin/collect`는 프록시 502→`@Async`(202). 커밋 `ffd20e8`·`d8df879`·`47b2484`.
- **★ 클라우드 데이터 확장 + 운영 보안 (2026-06-07, 같은 날 후속)** — 위 배포의 "미설정" 3개를 전부 처리. 커밋 `21f3ef4`.
  - **데이터 399 → 1,343건**: Railway Variables에 `PUBINST_ENABLED=true`+`JOBALERT_PUBINST_KEY`, `SEOUL_ENABLED=true`+`JOBALERT_SEOUL_KEY`(+`SEOUL_MAX_ROWS=1000`) 추가. 라이브 실측 = **공공기관 500 + 서울 445 + greenhouse 398, 회사 534곳**. 매일 cron도 이 키로 자동수집.
  - **`/admin/*` 토큰 인증**(`AdminAuthInterceptor`+`WebConfig`): `ADMIN_TOKEN`(=`jobalert.admin.token`) 설정 시 `X-Admin-Token` 헤더 일치 요구(불일치 401). spring-security 안 씀(공개 API 보호 위해 경로만 매핑). 빈값이면 개방+경고로그(로컬용). 수동 수집 시 `curl -X POST -H "X-Admin-Token: <토큰>" .../api/v1/admin/collect`.
  - **안드 `ApiClient.BASE_URL`** = `https://dsadjk22-production.up.railway.app/`(로컬은 주석). 빌드는 본인 PC.
  - **★ Railway 함정 (오늘 1시간 헤맴)**: 변수를 고친 뒤 **`Deploy` 버튼(또는 Apply changes)을 눌러야 반영**됨. 안 누르면 "N Changes" 대기 상태로 옛 값 유지 → 키가 안 먹은 것처럼 보임. 적재는 모든 소스 fetch 후 **맨 끝 한 번에**(`HybridCollectorService`) 일어나 공공기관 상세 500여 호출 끝날 때 399→1,343으로 점프. `jobs/search?limit=N`의 `total_estimate`는 **반환 개수**(limit에 묶임)지 총량 아님 — 총량 보려면 limit 크게.
- **★ 안드로이드 출시 빌드 + UX 대개편 + 찾아보기 인스타 랭킹 (2026-06-08)** — 사용자 실사용 피드백 반복 반영. 안드 빌드/설치는 본인 PC(카톡으로 폰 설치). 커밋 다수(주요: 출시준비 `3df8948`, ALIO `idx`+Flyway `V3`, `/by-ids` `079f7c6`, `/discover` `82eb8f0`·`f46855a`, 찾아보기 프론트 `0337bfa`).
  - **정식 release 빌드**: `keystore.properties`(gitignore) 기반 release 서명(`release.jks`, alias `jobalert-release`, **비번 백업 필수**). google-services.json에 release 패키지 `com.jobalert.app` 추가(프로젝트 `jobjob-533ca` — 처음에 실수로 새 프로젝트 `-91b81` 생성→폐기). lint `InvalidFragmentVersionForActivityResult` 오탐 제외(lintVitalRelease 차단). 서명 APK `CN=JobAlert`. **Play Console 미등록**(개발자 계정 $25 필요) — 지금은 사이드로드.
  - **런처 아이콘**: 눈·코 없던 기괴한 placeholder → **꽁이 얼굴**(`ic_launcher_foreground.xml`, 마스코트 팔레트).
  - **위젯**: 온보딩·마이페이지 "지금 추가하기" = `requestPinAppWidget`(WidgetPinner, 한 번 탭 추가). 2x1은 "채용알리미" 자리에 **새 공고 수 숫자**.
  - **UI 정리**: 가짜 상태바(9:41)·하단 제스처막대 = `HiFiStatusBar`/`HiFiGestureNav` 본문 비워 23개 화면 일괄 제거(루트 `windowInsetsPadding(systemBars)`가 이미 처리). 찾아보기 카드 우측 버튼 영역(72dp gutter)+제목 3줄 제한. **카드 로고 자리=근무지역**(`Job.regionShort`). 내정보 "관심"(관심기업 제거·부제 제거·톱니바퀴 제거·규모 실제값).
  - **온보딩 1회만**: `ActiveFilter.onboardingDone` 플래그(처음 설치 1회→이후 메인 직행). 직군·규모 미리선택 제거(빈→직접 고름, 내정보 수정 시 저장값 반영).
  - **★ 관심↔필터 2-tier 분리**(`ActiveFilter`): **관심**(interestCategories/interestSizes, 영속, 온보딩·내정보·푸시개인화) ↔ **세션 필터**(categories/experiences/sizes/deadlineDays, 비영속, 시작 시 관심으로 초기화, 피드가 사용). `setInterest`(온보딩·내정보) vs `setFilter`(필터 다이얼로그·일회성). 필터 적용해도 관심 안 바뀜·앱 재시작 시 관심으로 리셋.
  - **필터 작동 수정**: today() 카운트를 **필터된 후보에서 계산**(필터 시 헤더·칩 숫자 반영). **마감일 필터**(`deadlineDays`, N일 이내). 검색 `total_estimate`=자르기 전 실제 매칭 수(공고(50) 하드코딩 해소). 오늘·검색 limit↑(사실상 전부). FilterScreen 더미 기본값·"17건" 제거.
  - **공공기관 원문 직링크**: ALIO 파라미터 `recrutPblntSn`→**`idx`**(WebSearch로 확인 — recrutPblntSn은 무시돼 통합목록으로 빠졌음). 기존 행은 **Flyway `V3__fix_alio_url.sql`**로 일괄 보정(재수집이 트라이얼 박스서 불안정해 마이그레이션이 결정적).
  - **본 공고 기록함**: 백엔드 `GET /jobs/by-ids`(여러 ID, 입력순) + `SeenJobsScreen`. 상세 진입 시 `SeenJobs.markSeen`(영속, LinkedHashSet). 내정보 "본 공고 N ›" 탭→목록. 카운트 하드코딩 87 제거.
  - **★ 찾아보기 인스타 탐색 랭킹** `GET /jobs/discover`(오늘 필터와 **완전 분리**, 필터버튼 없음): 점수(관심기업+5·관심직군+3·저장취향+2·최신+2/+1·마감임박+1) + **소스+회사 2단 라운드로빈**(검증: 공공·gh·서울 1/3씩 번갈아) + **관심:발견 3:1**(발견 25%) + 그룹 난수 셔플. 본 공고 후순위는 클라(로컬 SeenJobs). 미구현=머문시간 학습(4번, 앱 계측 필요).
  - **⚠️ 트라이얼 박스 불안정**: 무료 Railway 박스가 수집·대량응답(오늘 limit 1000)에 **OOM 크래시** 반복(앱 502 + 서버 000). 데이터는 Postgres라 안전, push 재배포로 부활. **출시 전 유료 플랜 or 페이지네이션 필요.** 미푸시 커밋 `0337bfa`(프론트는 push해도 백엔드 무관하나 Railway가 재배포돼서 보류).
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
- ~~Claude Haiku 한줄 요약~~ — **폐기 (2026-06-06, 사용자 결정: Anthropic API 비용 부담)**. 실제 호출 코드는 원래 없었음. DTO·mock·안드 잔재 전부 제거, DB 컬럼·엔티티 `Job.summary`만 null로 잔존(되돌리기 비용). 비전 5번 항목도 무효.
- 회사 시드 — placeholder 57개. 하이브리드 전환으로 "Greenhouse/Lever 토큰 보유 회사" + "수집 가능 국내 대기업" 리스트로 재정의 필요
- Play Store — 미작업 (Phase 5)

### 📋 다음 단계 (2026-06-05 기준 우선순위)
1. **안드로이드 화면 백엔드 연결** — ✅ 메인·검색·캘린더·회사상세·직군필터·**관심기업** 연결 완료(Repository+ViewModel+UiState, 실 DB). 관심기업은 익명 기기ID(SharedPreferences UUID + X-Device-Id 인터셉터) 기반 user_favorites DB, 별표 토글+목록+회사page isFavorited 반영. 회사상세 `/companies/{id}/page`, 직군필터 `ActiveFilter`+`?categories=`, 검색 BasicTextField.
   ✅ **온보딩 관심직군**(고른 직군→메인 피드 기본필터, SharedPreferences 영속) + **알림 히스토리**(다이제스트 생성·저장·조회) + **FCM 푸시**(백엔드 firebase-admin 발송 + 앱 토큰등록·수신) 연결 완료.
   ✅ **(2026-06-06) 나머지 화면·필터 거의 전부 연결**: 공고상세(/jobs/{id})·지원버튼(원본URL)·찾아보기·비슷한공고·온보딩추천회사·알림설정토글·관심직군표시·검색(회사명)·알림읽음·회사상세평균마감. 필터는 **직군+경력+규모** 작동(경력=ExperienceClassifier 공공기관recrutSeNm+제목, 규모=출처기준 public). **신입 필터 404건**.
   **미연결**: 꽁이요약(비용보류+본문미수집), 메인빈상태(중복), 마이페이지 위젯·피드백(백엔드없음), 필터 지역·마감(데이터 형식편차). 온보딩추천회사·MainEmpty 외 mock 거의 없음.
   **안드로이드 빌드 검증은 사용자 PC에서만**(이 환경 Google Maven 차단). 필터는 직군만 적용(규모·경력·지역·마감 facet은 백엔드 미지원).

### ✅ 검색·찾아보기·알림·위젯 대개편 (2026-06-07, 커밋 f80760a~6579307)
- **검색**: 직군별 둘러보기 = 직군 코드 필터(`/jobs/search?categories=`), 검색어 = 공백 단어분해 제목·회사명 부분일치(OR). 오타교정은 v0.2(형태소/검색엔진 필요).
- **찾아보기**: 좋아요→관심기업(회사기준 서버연동), 저장 서버연동(마이페이지 저장공고와 동일 소스), 본 공고 후순위(`SeenJobs` 로컬), 카드 제목정제(`displayRole`)+급여칩+근무조건 태그+본문 미리보기.
- **"오늘 새 공고" NEW 누적 버그 수정**: `JobKind.ACTIVE` 도입. applyDiff UNCHANGED→ACTIVE(NEW는 INSERT만). 메인=오늘 변화(NEW/UPDATE/CLOSING), 찾아보기=전체 진행중(ACTIVE 포함). 메인/캘린더/MainEmpty when에 ACTIVE 분기.
- **알림**: 듀오링고풍 간단 문구 + 다양 템플릿(`NotificationService` 아침5·저녁4·빈날3), dayOfYear 순환. "꽁이가 새 공고 N개 찾았어요 🐱" 식.
- **회사상세**: description 없으면 산업·규모·근무지·공고수로 소개문 자동생성(`buildAbout`).
- **★ 홈 위젯**: 새 공고 수 + 꽁이(상황별 표정 — 0건 Sleep/3일+미방문 Sad/5개+ Wow/그외 Happy). `Mascot`을 `drawMascot`(DrawScope) 추출 → `MascotRenderer`로 Bitmap 렌더(RemoteViews, Glance 미사용). 크기 3레이아웃(tiny 1x1/wide 2x1·4x1/세로 2x2·4x2, getAppWidgetOptions 분기). 큰 위젯에 마감임박. `WidgetState`(SharedPreferences). **위젯 빌드·실기기 검증은 사용자 PC에서 완료(꽁이 정상 렌더 확인)**. 임계 110dp는 기기별 근사.

### ✅ 온보딩 회사규모 ↔ 데이터 정합화 (2026-06-07, 커밋 c35b639)
- 온보딩 규모 6개 중 5개가 데이터 0(중소=서울 size null, 중견·외국계·스타트업 없음)이라 정리. 직군 21개는 데이터 다 있어 유지.
- 백엔드 `JobPersistenceService.inferSize`: 소스 기반 규모 보정(공공기관→public, 서울→small, greenhouse/lever→large_corp). 신규+기존 size=null 회사 재수집 시 보정. `JobService.today` pool 1000→3000(규모 필터 누락 방지). 라이브: large_corp 399·public 500·small 441.
- 온보딩 `OnboardingCompanySizeScreen` 3개(대기업·공기업·중소)로 + 선택→코드 변환 ActiveFilter 저장(기존엔 선택이 저장 안 됐음).

### ✅ 데이터 소스 확장 + 관심기업 UX (2026-06-06 후반)
- **서울시 일자리포털 소스 추가** (커밋 7950821·7378fe8) — `SeoulJobSource`. 총 23,145건(공공누리 1유형, 상업OK). **급여(HOPE_WAGE)를 주는 첫 소스** + 본문·학력·경력. 활성화: `SEOUL_ENABLED=true JOBALERT_SEOUL_KEY=<data.seoul.go.kr 키> SEOUL_MAX_ROWS=1000`. 서울 API가 Content-Type을 xml로 잘못 보내 String 받아 직접 파싱. 최근 진행중 위주(마감 지난 건 제외).
  - **⚠️ 서울시 데이터 실체**: 서울일자리센터 알선이라 요양보호사·경비·청소 등 노인·중장년 일자리 위주(취준생 타겟과 불일치). → `isElderlyOrCareJob` 네거티브 필터로 노인·중장년 전용 제외(1000→441건). 남은 건 용접·영업·사무·생산관리·기사 등 나이무관 직무. 고스펙 대졸 신입 공채는 아님(서울 중소 한계).
  - **★ 전체 양**: 활성 1,340건(공공기관 500 + 서울 441 + greenhouse 399), 회사 530곳. 더 늘리려면 SEOUL_MAX_ROWS↑(단 노인 비율 높아 실익 체감↓).
  - 실행(두 키 함께): `JOBALERT_PUBINST_KEY=<키> PUBINST_ENABLED=true JOBALERT_SEOUL_KEY=<키> SEOUL_ENABLED=true ./gradlew bootRun`
- **★ 리서치 확정 (중요, 재시도 금지)**: 토종 대기업(토스·배민·네이버·카카오·무신사·컬리·야놀자·직방·두나무 등)은 **Greenhouse/Lever 공개 보드 미사용**(35개 후보 전부 404). 자체 ATS/그리팅 사용. → GH/Lever로 한국 대기업 발굴 불가. 양 확보는 서울시 등 공공 소스가 답. 워크넷은 라이선스 상업금지라 보류.
- **관심기업 UX** (커밋 9e87735) — 찾아보기 "좋아요"→"관심기업"(회사 기준 서버연동), 공고상세 앱바 관심기업 하트(is_favorited). 기업추가는 검색 경로 유지.
- **공공기관 지원링크** (커밋 d608998) — 기관별 srcUrl 부정확 → JOB-ALIO 통합 URL(`job.alio.go.kr/recruitview.do?recrutPblntSn=`)로 통일.

### ✅ UX 개선 6건 (2026-06-06, 커밋 0662e36·cda0ba9·c1a06c8·bec5186·54f0218) — 사용자 실사용 피드백 반영
- **G1 찾아보기 진행바 제거** + **검색 기업탭 제거(공고만)** + **한줄요약 흔적 정리**
- **G3 쿠팡 편중 해소 + 개인화 피드** — `JobService.today()`에 회사 라운드로빈 interleave(한 회사 연속 노출 차단) + 관심기업(+2)·관심직군(+1) 가점. `/jobs/today`에 X-Device-Id 선택 헤더. 라이브: 30건 전부 다른 회사·연속중복 0, 관심기업 11→1위.
- **G4 관심기업 추가 버그** — 관심기업 화면 "기업 추가" 버튼이 빈 람다(NavGraph TODO)였음 → 검색 연결. setFavorite runCatching silent 실패 → 결과 콜백+롤백+토스트. ⚠️ **남은 트레이드오프**: 검색 결과에서 회사 섹션을 빼서, "기업 추가"→검색 시 회사가 안 나옴(공고만). 회사 검색 전용 경로는 미구현.
- **G5 저장한 공고(북마크) 풀스택 신규** — `saved_jobs` 테이블(V2) + SavedJob 엔티티/repo/service/controller(`/users/me/saved`). JobDetailDto.isSaved. 안드: 상세 북마크 서버연동, `SavedJobsScreen` 신규, 마이페이지 "저장한 공고"→실화면. 마이페 카운트는 🔖 아이콘(실수치 추후).
- **G2 카드/상세 충실화** — JobDto.jobCategories + CompanyEmbedDto.size 추가. 찾아보기 카드·공고상세에 직군·회사규모 배지(빈 학력/태그 대신 채워진 것만).
- **G6 공고 본문 수집 완료 (2026-06-06, 커밋 8017227·c7bb3fb)** — 양 소스 모두 본문 채움(899/899).
  - **Greenhouse**: `content=true`로 본문(content)+부서(departments). `SourceUtil.htmlToText`(Jsoup) HTML→평문. 399/399, 쿠팡 2730자.
  - **공공기관**: 목록엔 본문 없지만 **상세 API `recruitment/detail?sn=`**에 응시자격(aplyQlfcCn)·전형방법(scrnprcdrMthdExpln)·우대·학력(acbgCondNmLst) 텍스트 제공 → 공고당 상세 1회 호출로 `description`+`education` 채움. 500/500. **공공누리라 본문 자유 활용**. 상세호출=목록건수(500=505회/일, 개발계정 1000 한도 내).
  - 상세화면 "📄 상세 내용" 섹션(8줄 접기+더보기). `JobPersistenceService`에 **reactivate**(만료→재수집 시 복귀) 버그도 수정.
  - **급여는 여전히 미수집**(Greenhouse pay_input_ranges=null, 공공기관 상세에도 급여 텍스트 없음 — 소스에 데이터 자체가 없음).
  - 온보딩 관심회사 고르기(onb3) 제거(커밋 9a5f205). 본문 길어 상세 접기(c60d6d5). 약관 본문활용 허용으로 완화(d5368c8).

### 🔥 FCM 푸시 운영 메모 (2026-06-05)
- Firebase 프로젝트 `jobjob-533ca`(Spark 무료). `google-services.json`(패키지 `com.jobalert.app.debug`) 앱에 배치·커밋됨.
- 서비스계정 키: `backend/secrets/fcm-service-account.json` (**gitignore, 미커밋**). 분실 시 Firebase 콘솔→프로젝트설정→서비스계정→새 키.
- 백엔드 발송 활성화: `FCM_ENABLED=true`(+키 경로 기본 secrets/). bootRun에 환경변수로.
- 실 발송 흐름: 앱 시작 시 FCM 토큰을 `/devices/register`로 등록(X-Device-Id+fcm_token) → `POST /admin/digest`(또는 매일 cron) → 그 기기 토큰으로 push. 에뮬레이터는 **Google Play/APIs 이미지**라야 FCM 수신됨.
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
- **(정정 2026-06-09)** 개발 머신(사용자 맥)에서 **`cd ~/jobalert/android-app && ./gradlew assembleDebug`로 직접 빌드 가능**(deps 캐시됨). 과거 "웹 샌드박스 빌드 불가"는 옛 정보 — 이제 매 변경 후 직접 빌드 검증하고 APK(`app/build/outputs/apk/debug/app-debug.apk`) 제공(카톡 사이드로드). `open class` 누락 같은 컴파일 에러도 직접 잡음.

## 연락처·자원

- 사람인 OpenAPI: https://oapi.saramin.co.kr/
- 사람인 약관 (주의사항): https://oapi.saramin.co.kr/caution
- 공정위 공시대상기업집단: https://www.ftc.go.kr/
- Clearbit Logo API: https://clearbit.com/logo
- Anthropic API 문서: https://docs.anthropic.com/
