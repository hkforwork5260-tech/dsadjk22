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

### 데이터 소스 (확정)
- **v0.1 (MVP)**: 사람인 OpenAPI **단독** 사용. 약관 준수 위해 **광고·인앱결제 일체 없음**. "출처: 사람인" 표기 필수
- **v0.5 (3~6개월차)**: 사람인 + 회사 직접 제휴 5~10곳 추가
- **v1.0 (6~12개월차)**: 사람인 정식 비즈니스 파트너십 협상 → 광고 도입 가능
- **회사 직접 크롤링**: robots.txt + 약관 준수 + 메타데이터만 추출 시 회색지대 안전. v0.5+에서 신중히 검토
- **워크넷·공공데이터**: 대기업 커버 약해서 v0.1에서는 미사용

### 회사 풀 (확정)
- v0.1 시작: **1000개**
- 시드: 공정거래위원회 공시대상기업집단 (88개 그룹의 약 800~1000개 계열사) + 빅테크·금융·공기업 보충
- 자동 성장: 매일 수집 중 새 회사 발견 → admin 검토 → 자동 추가
- 메타데이터: 사람인 API 응답 + Clearbit 로고 API
- **중요**: 1000개라도 매일 API 호출 30~60회 (한도 500의 12%). 호출량은 회사 수와 거의 무관 (`co_size` 필터로 일괄 fetch)

### 영리화 (확정)
- **v0.1 광고/결제 ❌** — 사람인 무료 OpenAPI 약관상 사용자/기업/재판매 수익화 모두 금지
- **v1.0** 사람인 의존도 50% 이하 + 정식 파트너십 협상 후 광고 도입 검토
- 현재는 포트폴리오·사용자 검증·시장 학습 목적

### 기술 스택 (확정)
- **안드로이드**: Kotlin + Jetpack Compose + Navigation Compose. Compose BOM 2024.10, Kotlin 2.0.21, AGP 8.6
- **백엔드**: Kotlin + Spring Boot 3.x + PostgreSQL + Redis (큐). Java 17+
- **AI 요약**: Claude Haiku (`claude-haiku-4-5-20251001`) via Anthropic SDK
- **푸시**: Firebase Cloud Messaging (FCM)
- **호스팅**: Railway 또는 Fly.io (v0.1). 매출 발생 시 AWS/GCP 이전 검토
- **로고**: Clearbit Logo API (`logo.clearbit.com/{domain}`) — 무료, fallback은 회사 첫 글자 텍스트

### 약관·법적 제약
- 사람인 OpenAPI 약관: 1일 500 calls / 재판매 금지 / 사용자·기업 대가 수취 금지 / 사칭 금지
- 출처 표기: 앱 어딘가에 "공고 출처: 사람인" 명시
- 잡코리아·캐치 등 다른 채용 플랫폼 크롤링은 **법적 위험 (잡코리아 vs 사람인 2017 대법원 판례)** — 절대 안 함

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
- **Phase 2.A + 2.B 안드로이드 핵심 화면 13개 완료** (`android-app/`):
  - 온보딩 ①②③④, 메인, 메인 빈 상태, 공고 상세, 필터, 검색, 검색 결과,
    회사 상세 (공고 있음/없음 2종), 관심기업, 마이페이지, 알림 히스토리, 마감 캘린더
  - `data/api/MockApi.kt` — API_CONTRACT.md 형식 그대로 mock 응답 (백엔드 붙기 전 임시)
  - 디자인 시스템 컴포넌트 10개 + 라우트 12개

### 🚧 진행
- 안드로이드 placeholder 라우트 — 찾아보기 (Reels), 공유 시트, 비슷한 공고, 마이페이지 서브 (알림설정/위젯설정/관심직군/피드백)
- 사람인 API 키 — 미발급 (Phase 3 시작 조건)
- 사람인 실 API 호출 — `SaraminRealClient` 스텁만 (Phase 3)
- FCM·푸시 — 미작업 (Phase 3)
- Claude Haiku 한줄 요약 — 미작업 (Phase 3)
- 1000개 회사 시드 (공정위 공시) — placeholder만 (Phase 3)
- Play Store — 미작업 (Phase 5)

### 📋 다음 단계
`PHASE_PLAN.md` 참고. 백엔드(Window A)와 안드로이드(Window B)를 병렬 세션으로 진행.

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
