# 채용알리미(JobAlert) — 제작 전 과정 기록

> 임현경 / 코딩 초보 1인 개발 / Claude Code와 함께. 2026-05-26 ~ 2026-06-09.
> 이 문서는 "이 앱을 만든 전 과정(코드·의사결정·시행착오)"을 한 곳에 정리한 것. claude.ai에 가져가 블로그·회고·포트폴리오로 가공하기 위한 원자료.

---

## 0. 한 줄 요약
취준생을 위한 **매일 1분 채용 알리미 앱**. 듀오링고처럼 짧게 자주 들르고, 매일 새 공고를 푸시·위젯으로 알려준다. 안드로이드(Jetpack Compose) + Spring Boot 백엔드 + 여러 공공/공개 채용 API 하이브리드 수집. 마스코트 시바견 **'단이'**, 신뢰의 블루 컬러.

- 안드로이드 코드 70개 .kt / 백엔드 82개 .kt / 커밋 ~140개.
- 라이브: `https://dsadjk22-production.up.railway.app` (Railway 무료 박스 + 관리형 Postgres). 활성 공고 ~1,350건, 회사 ~530곳.

---

## 1. 비전 · 타겟 · 핵심가치
- **비전**: 매일 1분, 짧게 자주 — 잠금화면 푸시 + 홈 위젯으로 새 공고를 놓치지 않게.
- **타겟**: 고스펙 대기업·중견·빅테크·공기업 지망 취준생. 자기 직군 새 공고를 놓치기 싫은 사람.
- **핵심가치**: ①자동 수집·자동 직군분류 ②고정 시간 푸시(9시·21시, 사용자가 시간설정 안 함=귀찮음 제거) ③잠금화면 풀스크린 푸시 ④홈 위젯(듀오링고 스트릭처럼 매일 활성) ⑤(폐기) AI 한줄요약.
- **디자인 톤**: 듀오링고 + 한국 모바일 UX. 3D 깊이감 버튼(Compose엔 box-shadow 없어 그림자박스+본체 2겹 스택). 폰트 Pretendard.

## 2. 기술 스택 (확정)
- **안드로이드**: Kotlin 2.0.21 + Jetpack Compose(BOM 2024.10) + Navigation Compose + Retrofit/OkHttp/kotlinx.serialization. 패키지 `com.jobalert.app`.
- **백엔드**: Kotlin + Spring Boot 3.5 + PostgreSQL + Flyway. Java 17. Layered(controller→service→repository→client/source). DTO↔Entity 분리. 모든 시간 UTC 저장, 노출 시 KST 변환. 패키지 `com.jobalert.backend`.
- **푸시**: Firebase Cloud Messaging (프로젝트 `jobjob-533ca`).
- **호스팅**: Railway(무료) + 관리형 Postgres. repo `hkforwork5260-tech/dsadjk22`, Root Directory=`backend`, push 시 자동 재배포.
- **로고**: Clearbit Logo API + 한글 회사명 사전 + fallback 첫 글자.

---

## 3. 가장 큰 전환점 — 데이터 소스 (★ 핵심 시행착오)
처음엔 **사람인 OpenAPI 단독**으로 "1000개 대기업 전체 자동수집"을 노렸다. 그런데:

1. **2026-06-04 사람인 거절**: 담당자가 "개인 프로젝트는 운영정책상 승인 불가(API는 비영리 공공기관에 한함)" 회신. → 사람인 단독 전략 완전 폐기. (재신청해도 자격 미달)
2. **합법 무료 데이터소스 전수조사(2026-06-04~05)** 결론: **"1000개 대기업 전체 자동수집"은 개인이 합법으로 불가**(삼성·네이버·카카오 직접 공채 주는 합법 무료 API 없음). 대신 **여러 합법 소스를 합치는 하이브리드**로 충분. 비전을 "대기업 올인"→"채용 알리미(대기업은 부분)"로 조정.
3. **확정한 v0.1 데이터 스택**:
   - 🟢 **Greenhouse/Lever 공개 API**(인증 불필요): 쿠팡·크래프톤·당근·몰로코·센드버드 등 한국 빅테크. (실측: 쿠팡 261건 등)
   - 🟢 **기재부 공공기관 채용 API**(data.go.kr/15125273): 라이선스 제한 없음·개발단계 자동승인·개인 가능. 공공기관 340곳+(한전·인국공·국민연금 등). 공기업 타겟 핵심.
   - 🟢 **서울시 일자리포털**(공공누리 1유형, 상업 OK): 급여(HOPE_WAGE) 주는 첫 소스.
   - 🟠 국내 대기업 자체 채용페이지(출처표기+원본링크 전제, 회사별 robots/약관 확인 후).
4. **막힌 소스**: 그리팅(고객사 키 필요)·잡코리아/점핏/원티드(공개 API 없음). **잡코리아 크롤링은 절대 금지 — 2017다224395 대법원 판례(합의금 120억).**
5. **시행착오로 확정한 사실들**:
   - **토종 대기업(토스·배민·네이버·카카오·무신사·컬리·야놀자·직방·두나무)은 Greenhouse/Lever 공개 보드 미사용** — 35개 후보 전부 404. 자체 ATS/그리팅 사용. → GH/Lever로 한국 대기업 발굴 불가 **확정(재시도 금지)**. 양 확보는 공공 소스가 답.
   - **서울시 데이터 실체**: 서울일자리센터 알선이라 요양보호사·경비·청소 등 **노인·중장년 일자리 위주**(취준생 타겟과 불일치) → `isElderlyOrCareJob` 네거티브 필터로 제외(1000→441건).
   - robots.txt는 기술적 잠금이 아니라 **신사협정(팻말)**. "기술적으로 되냐"와 "해도 되냐(약관·재배포)"는 별개.
   - 채용 "사실"(제목·마감일)은 저작권 대상 아님. 공공기관은 공공누리라 본문 자유. 민간 본문은 어문저작물이지만 **출처표기 전제로 활용(2026-06-06 사용자 결정, 분쟁 시 즉시 제거)**.
- 소스 어댑터 패턴: `JobSource` 인터페이스 + `RawJobPosting` 공통 모델 + `HybridCollectorService`로 통합.

## 4. 폐기·완화된 결정들
- **AI 한줄요약(꽁이/단이의 한줄 요약) 폐기(2026-06-06)**: Anthropic API 비용 부담. 비전 5번 항목 무효. DTO·mock 잔재 제거.
- **약관 원칙 완화(2026-06-06)**: 기존 "메타데이터만/원문 복제 금지" → "출처표기+원본링크 유지하며 본문 활용".
- **온보딩 산업군 섹션 제거(2026-06-09)**: 어떤 소스도 산업군 데이터를 안 줘서 회사 ~95%가 null, 실제 필터에도 안 쓰임 → 직군으로 대체.

---

## 5. 아키텍처 개요
### 폴더
```
android-app/app/src/main/kotlin/com/jobalert/app/
  ui/theme/        디자인 토큰(HiFiColors, HiFiType, Theme)
  ui/components/   재사용(HiFiButton 3D, HiFiJobCard, Mascot, HelpDialog 등)
  ui/screens/      화면별(main, discover, calendar, company, detail, favorites, mypage, onboarding, settings, search, share, notif, saved, seen, similar, filter)
  nav/NavGraph.kt  라우트
  data/            model, api(Retrofit), fcm, SeenJobs/SavedJobs/AppStats/HelpState 로컬상태
  widget/          홈 위젯(Provider, MascotRenderer, WidgetState, WidgetUpdater, WidgetPinner)
backend/src/main/kotlin/com/jobalert/backend/
  controller / service / repository / entity / dto
  client/source/   greenhouse, lever, publicinst, seoul (+ JobSource, RawJobPosting, SourceRegistry)
  scheduler/       CollectorScheduler(매일 18시 KST 수집), PushScheduler(9시·21시)
  resources/db/migration/  Flyway V1~V5
```
### 데이터 흐름
수집(소스 fetch) → `HybridCollectorService` → `JobPersistenceService`(회사 매칭/자동생성, diff 라벨링 NEW/UPDATE/CLOSING/ACTIVE, 직군분류, 만료 스윕) → DB → 조회 API → 앱(Retrofit) / 위젯(자체 fetch).

### 핵심 도메인 규칙
- **diff 라벨링**: INSERT=NEW / 제목·마감 변경=UPDATE / 마감 임박=CLOSING / 변화없음=ACTIVE / 사라짐=만료(isActive=false). 0건 받은 소스는 만료 스윕 제외(API 장애로 전체 닫는 사고 방지).
- **회사 미스 시 자동생성**(isApproved=false) + 정규화명 캐시로 중복생성 방지.
- **메인 표시 kind 재계산(2026-06-09)**: today()가 응답 시 메모리로 재계산 — **오늘(KST) 첫 등장=NEW(하루 유지)**, 마감 D-3 이내=CLOSING(앱 '마감임박'). 마감 지난 공고 제외(now 기준).
- **관심 ↔ 필터 2-tier 분리**: 관심(영속, 온보딩·내정보·푸시 개인화) vs 세션 필터(비영속, 일회성, 시작 시 관심으로 초기화).

---

## 6. 만든 기능 (인벤토리)
- **수집·분류**: 4개 소스 수집, 직군 21개+기타 자동분류(`JobCategoryClassifier`), 경력/규모 분류, 본문 수집(greenhouse content / 공공기관 상세 API / 서울 DTY_CN).
- **조회 API**: today(개인화·회사 다양성 interleave), search(직군필터+단어분해), discover(인스타 탐색 랭킹: 관심+다양성+발견25%+셔플), upcoming(마감 캘린더 70일), similar(직군 겹침), by-ids(본 공고), companies/{id}/page.
- **화면**: 온보딩(직군·규모·위젯), 오늘(메인), 찾아보기(Reels VerticalPager), 마감 캘린더(월 이동·날짜클릭), 회사 상세, 공고 상세, 관심 기업, 마이페이지, 저장/본 공고, 검색, 공유(카카오톡), 알림 히스토리, 관심 편집.
- **푸시**: FCM 토큰 등록 + 매일 9시(새 공고)·21시(마감임박) 다이제스트(관심 직군+규모 필터, 듀오링고풍 문구 다종 순환).
- **위젯**: 새 공고 수 + 단이 표정(0건 Sleep/3일+미방문 Sad/5개+ Wow/그외 Happy). 크기 3종(Small 2×1/Medium 4×2/Large 4×4) 핀. **위젯 자체 백엔드 fetch**(앱 안 열어도 30분 주기로 관심 기준 새 공고 수 갱신).
- **첫 진입 도움말**: 오늘·마감캘린더·관심기업·찾아보기 1회 다이얼로그(+'?' 재보기).

---

## 7. 시행착오 & 배운 것 (★ 회고 핵심)
1. **사람인 단독 → 거절 → 하이브리드 전환**(위 3장). 가장 큰 방향 전환.
2. **토종 대기업 GH/Lever 35개 전부 404** — 며칠 헤매다 "불가" 확정. 헛수고를 줄이려 "재시도 금지"로 못박음.
3. **원문 링크가 공고 아닌 홍보/목록으로 빠짐(2026-06-08~09)**: 실데이터로 일일이 열어보며 소스별 정답 URL 발굴 —
   - 당근/쿠팡 greenhouse `absolute_url`은 회사 홍보사이트로 빠짐 → **greenhouse embed URL**(`job-boards.greenhouse.io/embed/job_app?for={토큰}&token={id}`)로 우회.
   - 공공기관 ALIO 데스크톱 `recruitview.do?idx=`는 폰에서 모바일 리다이렉트로 idx 잃고 목록 → **`mobile2021/recruit/recruitView.do?idx=`**.
   - 서울은 모든 공고가 같은 검색목록 하드코딩 → 실은 **고용24 알선 공고**라 `JO_REGIST_NO`로 `m.work24.go.kr` 직링크.
   - 기존 행은 재수집 없이 **Flyway 마이그레이션**으로 일괄 보정(트라이얼 박스 OOM 회피).
4. **직군 분류 정확도 사투(2026-06-08~09)** — 가장 길게 헤맴:
   - 키워드 규칙 기반 분류 → 영문 제목(greenhouse 27%) 미분류 많음.
   - 키워드 보강하며 **tags(4대보험)·본문 보일러플레이트("우수 인재", "투자 유치")를 신호로 썼다가 `finance_insurance`가 15→448로 폭증**(오염). → 신호를 제목+부서+키워드로 한정, 오탐 키워드(보험·투자·인재·assistant) 정밀화.
   - "기타(etc)" 폴백 도입(미분류 0%) + DB 재분류 API.
   - **제목만 재분류는 70%로 떨어짐**(공공기관·서울은 부서/직종명 신호가 결정적인데 DB 미저장) → **소스 재수집**으로 부서 신호 복구 85% → 실제 미분류 제목 보고 키워드 2차 보강 91%.
   - **측정이 계속 꼬임**: Railway 배포 지연 + 여러 백그라운드 재수집이 `isRunning`으로 서로 막혀 실제 재수집이 안 됨 + `job_categories`가 한글 라벨이 아닌 영문 코드인 걸 헷갈림. → "겹침 없이 한 번씩" 깨끗하게 측정해야 함을 배움.
   - 최종 91% 실분류 + 9% 기타로 마무리(95%는 포괄 단어 매핑=과다태깅 위험이라 멈춤. 정밀하게는 AI 분류 v0.2).
5. **위젯 크기 1x1 통일** — 안드로이드는 핀 크기를 widget XML(targetCell)로만 정함 → 크기별 별도 provider 3개 필요. 그 과정에서 **`open class` 누락으로 빌드 실패**(Kotlin 클래스 기본 final) → 사용자가 "바뀐 게 없다"고 함. **이때 이 맥에서 직접 빌드가 된다는 걸 발견**(CLAUDE.md의 "웹 샌드박스 빌드 불가"는 옛 정보) → 이후 빌드 검증을 직접 수행.
6. **NEW 정의 시행착오**: "firstSeenAt이 오늘"만으로 정의 → 오늘 insert된 공고가 없어 NEW 빈칸. (재수집 테스트가 NEW를 ACTIVE로 바꾼 것도 원인.) → **"오늘 올라온 OR 아직 안 본 공고"**로 재정의(필터·관심 바꾸면 새 매칭이 NEW로). 위젯·오늘·알림 셋 다 같은 관심 기준으로 통일.
7. **리브랜딩(2026-06-08~09)**: 코랄+고양이 '꽁이' → 블루(#4F6EF0)+시바 '단이'. PNG 6표정으로 Canvas 드로잉 교체. google-services에 release 패키지 추가하다 **새 Firebase 프로젝트를 실수로 생성→폐기**. lint `InvalidFragmentVersionForActivityResult` 오탐으로 release 빌드 차단→제외.
8. **클라우드 배포 함정(Railway)**: ①`DATABASE_URL`은 `postgresql://`(jdbc 아님)→env에 jdbc URL 직접 박음 ②Greenhouse `content=true`가 작은 박스서 OOM→`content=false` 기본 ③무거운 동기 `/admin/collect`는 프록시 502→`@Async`(202) ④**변수 수정 후 `Deploy` 버튼 눌러야 반영**(안 누르면 옛 값, 1시간 헤맴) ⑤적재는 모든 소스 fetch 후 맨 끝 한 번에 점프 ⑥`total_estimate`는 limit에 묶인 반환 개수.
9. **트라이얼 박스 OOM 반복**: 무료 박스가 수집·대량 응답(limit 1000)에 크래시. 데이터는 Postgres라 안전, push 재배포로 부활. **단, 크래시 대부분은 개발 중 과도한 테스트(수십 번 재수집·큰 쿼리) 탓**이라, 실사용자 부하가 적으면 무료로도 충분할 수 있음(추후 today 캐싱·페이지네이션).
10. **컴파일 못 하던 시절의 흔적**: 초기엔 웹 샌드박스라 안드 빌드 불가 → "사용자 PC에서 검증"으로 진행. Compose 함정들 학습(box-shadow 없음=2겹 스택, `1.4.dp` 불가→`1.4f.dp`, 음수 padding 불가→offset, FlowRow @OptIn 필요).

---

## 8. 운영·보안 메모
- **/admin/* 토큰 인증**(`AdminAuthInterceptor`): `ADMIN_TOKEN` 설정 시 `X-Admin-Token` 헤더 일치 요구. 수동 수집/재분류 시 사용.
- **시크릿(미커밋)**: `backend/secrets/fcm-service-account.json`(gitignore), `keystore.properties`+`release.jks`(release 서명, 비번 백업 필수), data.go.kr·서울 API 키(Railway Variables).
- **푸시 활성화 조건**: `PUSH_ENABLED=true` + `FCM_ENABLED=true`(+서비스계정 키). 기본 false라 안 켜져 있으면 실제 폰 푸시는 안 나감(앱 내 알림 히스토리엔 보임).
- **수집 활성화**: `COLLECTOR_ENABLED`, `PUBINST_ENABLED`+키, `SEOUL_ENABLED`+키.

## 9. 현재 상태 & 남은 것
- ✅ 라이브 배포·자동수집·4소스·직군분류 91%·리브랜딩·위젯 자체갱신·도움말·관심(직군+규모) 푸시·원문 직링크.
- ⚠️ **Play Store 미등록**(개발자 계정 $25 필요) — 현재 사이드로드(카톡으로 폰 설치, 디버그 패키지 `com.jobalert.app.debug`).
- ⚠️ 트라이얼 박스 안정성(출시 전 유료 or today 캐싱·페이지네이션 검토).
- 📋 v0.2 후보: AI 직군분류(95%+), 검색 오타교정(형태소), 산업군 실필터, 위젯 FCM 즉시 갱신, 푸시 실발송 검증, 알림에 마스코트 표정.

---

## 10. 전체 커밋 타임라인 (요약)
- **05-26~27**: HTML 프로토타입 26화면 → 안드 화면 20개(온보딩·메인·검색·회사·관심기업·마이페이지·캘린더·찾아보기) + 백엔드 Phase 1 스캐폴드(15 mock API) + bootRun 검증.
- **05-27~28**: 사람인 RealClient·회사명 정규화·Clearbit 로고(Phase 3 사전). 단위테스트 39.
- **06-03~05**: 사람인 거절 → 하이브리드 전환 → 공공기관·서울 소스 → **수집→DB→API→앱 첫 엔드투엔드**(에뮬레이터에 실 쿠팡 공고) → 직군분류·필터·관심기업·온보딩직군·FCM·다이제스트·푸시 스케줄.
- **06-06**: UX 개선 6건 + 본문 수집(카드/상세 충실화) + 서울 소스 + 한줄요약 폐기 + 관심기업 UX.
- **06-07**: 검색·찾아보기·알림(듀오링고풍)·**홈 위젯(꽁이 표정)** 대개편 + NEW 누적버그(ACTIVE 도입) + **Railway 클라우드 첫 배포** + 데이터 1,343건 + /admin 인증.
- **06-08**: 정식 release 서명 빌드 + UX 13건 + **리브랜딩(시바 단이·블루)** + 관심↔필터 분리 + 찾아보기 인스타 랭킹(/discover) + 원문 직링크 3소스 + 직군분류 사투.
- **06-09**: 메인 NEW 재정의·마감임박 D-3·진입 최신화 + 첫진입 도움말 + 캘린더 월이동 + 관심편집 분리 + 위젯 자체 fetch + 알림 관심(직군+규모) + 회사통계 허위정보 정리.

---

## 11. claude.ai에서 가공할 때 참고
- 더 깊은 의사결정·운영 함정은 저장소 루트 `CLAUDE.md`(영속 프로젝트 기억), `API_CONTRACT.md`, `PHASE_PLAN.md`, 그리고 `~/.claude/projects/.../memory/session_end_*.md`에 세션별로 남아 있음.
- 코드 인용 시: 안드 `ui/screens/*`·`widget/*`, 백엔드 `service/JobService.kt`·`service/JobPersistenceService.kt`·`service/JobCategoryClassifier.kt`·`client/source/*` 위주.
- 톤: 1인 코딩 초보가 "원리 이해 후 응용"하며 시행착오로 배운 과정 — 회고/포트폴리오에 적합.
