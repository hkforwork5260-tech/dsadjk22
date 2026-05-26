# 채용알리미 디자인 핸드오프

> 매일 자동으로 채용공고를 수집해서 신규/변경/마감만 보여주는 모바일 미니앱

## 📌 개요

**채용알리미**는 취준생(공채 중심·대기업·중견기업)을 타겟으로 한 안드로이드 미니앱입니다. 핵심 가치는 **체류시간 짧지만 매일 노출** — 듀오링고처럼 매일 잠깐 들러서 새 공고만 빠르게 확인하는 경험.

### 핵심 기능
- **매일 18:00 자동 수집** (공공데이터 API + 크롤링)
- **어제 대비 자동 분류** (NEW / UPDATE / CLOSING)
- **AI 분석** (이미지/PDF/HWP 공고 → Claude로 텍스트화 + 한줄 요약)
- **매일 9시·21시 자동 푸시** (시간 설정 X, 허용만 받으면 고정)
- **바탕화면 위젯** (듀오링고 스트릭처럼 매일 시각화)

### 타겟 플랫폼
**안드로이드** (네이티브 또는 React Native 권장). 토스 미니앱 출시 계획은 폐기되었습니다.

---

## 📂 이 패키지에 대해

이 폴더의 HTML/JSX 파일은 **디자인 레퍼런스**입니다. React + Babel inline JSX로 만들어진 프로토타입으로, **의도된 룩앤필과 인터랙션을 보여줍니다 — 그대로 프로덕션에 복붙하는 게 아닙니다**.

핵심 작업은 **이 디자인을 타겟 코드베이스의 환경(Jetpack Compose / React Native / SwiftUI 등)으로 재구현**하는 것. 기존 디자인 시스템이 있다면 그 패턴을 따르되, 본 패키지가 정의한 컬러·타이포·인터랙션 톤은 유지해주세요.

### 디자인 충실도
**High-fidelity (hifi)** — Pretendard 폰트·정제된 컴포넌트·최종 컬러·인터랙션이 정의된 상태. 픽셀 단위로 가깝게 재현해주세요. 단, 손그림 와이어프레임 흔적(Gaegu/Nanum 폰트, 거친 선)은 모두 제거됨.

---

## 🎨 디자인 시스템

### 컬러 토큰

| 용도 | 토큰 | 값 |
|---|---|---|
| **브랜드 (코랄)** | `--h-brand` | `#FF6B35` |
| 브랜드 호버 | `--h-brand-hover` | `#FF5722` |
| 브랜드 딥 (그림자) | `--h-brand-shadow` | `#D9532A` |
| 브랜드 다크 | `--h-brand-dark` | `#E5522A` |
| 브랜드 소프트 (배경) | `--h-brand-soft` | `#FFF0E8` |
| **NEW (초록)** | `--h-new` | `#58CC02` |
| NEW 그림자 | `--h-new-shadow` | `#4AB801` |
| NEW 소프트 | `--h-new-soft` | `#E7F9D6` |
| **UPDATE (옐로)** | `--h-update` | `#FFC800` |
| UPDATE 그림자 | `--h-update-shadow` | `#E0A800` |
| UPDATE 소프트 | `--h-update-soft` | `#FFF7D6` |
| **CLOSING (빨강)** | `--h-closing` | `#FF4B4B` |
| CLOSING 그림자 | `--h-closing-shadow` | `#E63D3D` |
| CLOSING 소프트 | `--h-closing-soft` | `#FFE1E1` |
| **INFO (블루)** | `--h-info` | `#1CB0F6` |
| INFO 소프트 | `--h-info-soft` | `#E1F3FC` |
| **텍스트 1** | `--h-text` | `#3C3C3C` |
| 텍스트 2 | `--h-text-2` | `#777777` |
| 텍스트 3 (보조) | `--h-text-3` | `#AFAFAF` |
| **배경 1** | `--h-bg` | `#FFFFFF` |
| 배경 2 (카드 호버) | `--h-bg-2` | `#F7F8FA` |
| 배경 3 (비활성) | `--h-bg-3` | `#EFEFF1` |
| **보더** | `--h-border` | `#E5E5E5` |
| 보더 다크 | `--h-border-dark` | `#D1D1D1` |

### 타이포그래피
모두 **Pretendard Variable** (한글 산세리프, fallback: -apple-system, Roboto)

| 클래스 | 크기 | weight | line-height | letter-spacing |
|---|---|---|---|---|
| `.h-display` | 32px | 800 | 1.1 | -0.5 |
| `.h-title` | 22px | 800 | 1.2 | -0.3 |
| `.h-h2` | 17px | 700 | 1.3 | - |
| `.h-body` | 15px | 500 | 1.4 | - |
| `.h-body-2` | 14px | 500 (color #777) | 1.4 | - |
| `.h-caption` | 12px | 700 (color #AFAFAF) | uppercase | 0.4 |

### Spacing
4의 배수 권장: **4 / 8 / 12 / 14 / 16 / 18 / 20 / 22 / 24 / 32**

### Border Radius
- 칩(chip): `999px` (pill)
- 작은 버튼/카드: `10–14px`
- 카드: `16–18px`
- 시트(bottom sheet): `24px`
- 폰 프레임: `44px`

### Shadow (듀오링고풍 깊이감)
버튼은 단순 box-shadow가 아닌 **bottom-only offset shadow**로 3D 느낌:
```css
.h-btn.primary { box-shadow: 0 4px 0 #D9532A; }
.h-btn.primary:active { transform: translateY(2px); box-shadow: 0 2px 0 #D9532A; }
```

### 마스코트
**꽁이** (고양이 기본 / 강아지 토글) — `Mascot` 컴포넌트는 `shared.jsx`에 SVG 인라인. 표정 6종: default / happy / sleep / sad / wow / wave. 출시 시에는 일러스트레이터에게 의뢰해서 정식 일러스트로 교체 권장.

---

## 📱 화면 목록 (총 26개)

각 화면 스크린샷은 `screenshots/` 폴더에 있습니다. 화면당 소스 위치는 `source/screens-hifi*.jsx`에서 함수명으로 찾으세요.

### 1. 온보딩 (4단계)
4단계 페이지 이동. 진행 dot 인디케이터 상단, 모든 단계에 우상단 "건너뛰기" 버튼.

| # | 화면 | 함수 | 스크린샷 |
|---|---|---|---|
| 1 | 직군 선택 (21개 카테고리) | `HiFi_Onb1` | `01-onboarding-1-job-categories.png` |
| 2 | 기업 규모 + 산업군 | `HiFi_Onb2` | `02-onboarding-2-company-scale.png` |
| 3 | 회사 스와이프 (Reels 스타일) | `HiFi_OnbSwipe` | `03-onboarding-3-company-swipe.png` |
| 4 | 위젯 + 알림 권한 유도 | `HiFi_Onb4Widget` | `04-onboarding-4-widget-permission.png` |

**21개 직군 카테고리** (사람인 기준):
기획·전략 / 마케팅·홍보·조사 / 회계·세무·재무 / 인사·노무·HRD / 총무·법무·사무 / IT개발·데이터 / 디자인 / 영업·판매·무역 / 고객상담·TM / 구매·자재·물류 / 상품기획·MD / 운전·운송·배송 / 서비스 / 생산 / 건설·건축 / 의료 / 연구·R&D / 교육 / 미디어·문화·스포츠 / 금융·보험 / 공공·복지

### 2. 메인 흐름

| # | 화면 | 함수 | 스크린샷 |
|---|---|---|---|
| 5 | 메인 피드 (NEW/UPDATE/CLOSING 토글) | `HiFi_Main` | `05-main-feed.png` |
| 6 | 메인 빈 상태 | `HiFi_MainEmpty` | `06-main-empty-state.png` |
| 7 | 공고 상세 (탭 4종) | `HiFi_Detail` | `07-job-detail.png` |
| 8 | 필터 (풀스크린, 21직군 + 6그룹) | `HiFi_Filter` | `08-filter.png` |
| 9 | 검색 (최근/인기/직군별) | `HiFi_Search` | `09-search.png` |
| 10 | 검색 결과 (기업/공고 분리) | `HiFi_SearchResults` | `10-search-results.png` |

### 3. 찾아보기 / 관심기업

| # | 화면 | 함수 | 스크린샷 |
|---|---|---|---|
| 11 | 찾아보기 (인스타 릴스 스타일 세로 스크롤) | `HiFi_Discover` | `11-discover-reels.png` |
| 12 | 관심 기업 그리드 | `HiFi_Favorites` | `12-favorites-grid.png` |
| 13 | 회사 상세 (공고 있음) | `HiFi_CompanyDetail` | `13-company-detail.png` |
| 14 | 회사 상세 (공고 없음) | `HiFi_CompanyDetailEmpty` | `14-company-detail-empty.png` |

### 4. 마이페이지

| # | 화면 | 함수 | 스크린샷 |
|---|---|---|---|
| 15 | 마이페이지 (스트릭 + 메뉴) | `HiFi_MyPage` | `15-mypage.png` |
| 16 | 저장한 공고 | `HiFi_SavedPostings` | `16-saved-postings.png` |
| 17 | 알림 설정 (9시·21시 고정) | `HiFi_NotifSettings` | `17-notif-settings.png` |
| 18 | 위젯 설정 (실시간 미리보기) | `HiFi_WidgetSettings` | `18-widget-settings.png` |
| 19 | 관심 직군 (21개 토글) | `HiFi_JobInterests` | `19-job-interests.png` |
| 20 | 피드백 보내기 | `HiFi_Feedback` | `20-feedback.png` |

### 5. OS 노출 (잠금화면 / 위젯)
듀오링고 스트릭처럼 앱을 열지 않아도 매일 노출되는 진입점.

| # | 화면 | 함수 | 스크린샷 |
|---|---|---|---|
| 21 | 잠금화면 + 푸시 알림 (코랄 그라데이션 배경) | `HiFi_LockScreen` | `21-lockscreen-push.png` |
| 22 | 바탕화면 위젯 (Large 4×2) | `HiFi_Widget` | `22-homescreen-widget.png` |

### 6. 추가 기능

| # | 화면 | 함수 | 스크린샷 |
|---|---|---|---|
| 23 | 알림 히스토리 (오늘/어제/이번 주) | `HiFi_NotifHistory` | `23-notif-history.png` |
| 24 | 마감 캘린더 (월 그리드) | `HiFi_Calendar` | `24-calendar.png` |
| 25 | 공유 시트 (카카오톡/라인 등) | `HiFi_ShareSheet` | `25-share-sheet.png` |
| 26 | 비슷한 공고 추천 (AI 매칭률) | `HiFi_Similar` | `26-similar-postings.png` |

---

## 🧩 핵심 컴포넌트

### 폰 프레임 (HiFiPhone)
- 360 × 740, 8px 검은 베젤, 44px 라운드
- 30px 상태바 + 옵셔널 앱바 + 컨텐츠 영역 + 22px 제스처 네비 pill (안드로이드)

### 하단 탭바 (HiFiTabBar)
5탭 고정: **오늘 / 검색 / 찾아보기 / 관심기업 / 내정보**
활성 탭은 코랄 컬러, 아이콘 위에 작은 라벨.

### JobCard (HiFiJobCard)
- 좌: 로고 (48×48, 라운드 14px)
- 중: NEW 라벨 + 회사명 + 직무명 (한 줄)
- 우: D-day + 날짜

### 듀오링고풍 버튼 (.h-btn)
- 기본: bottom shadow `0 4px 0 #D1D1D1`
- 클릭: `translateY(2px)`로 내려가고 shadow는 `0 2px 0 #D1D1D1`
- Primary는 코랄 배경 + 코랄 딥 shadow
- Green은 NEW 컬러로 동일 패턴

### 칩 (.h-chip)
Pill(`999px`), 14px 폰트, `outline`/`on` variant 지원

---

## 🎬 핵심 인터랙션

### 1. 찾아보기 (릴스 스타일)
세로 스크롤 + scroll-snap. 한 카드 = 한 회사 + 한 공고. 오른쪽 플로팅 액션:
- **❤️ 관심기업** (heart, 코랄) — 회사 단위 즐겨찾기 토글
- **🔖 공고 저장** (bookmark, 옐로) — 개별 공고 저장 토글

```css
.hifi-reel-feed {
  scroll-snap-type: y mandatory;
  overflow-y: scroll;
}
.hifi-reel-card {
  height: 100%;
  scroll-snap-align: start;
  scroll-snap-stop: always;
}
```

### 2. 공고 상세 탭
요약 / 원문 / 회사 / 비슷한 → 4번째 탭(비슷한)은 별도 페이지로 라우팅

### 3. 위젯 크기 변경
설정에서 Small (2×1) / Medium (4×1) / Large (4×2) 라디오 → 미리보기 영역에서 실시간 반영

### 4. 알림 자동 시간 (9시·21시 고정)
별도 시간 설정 UI 없음. 알림 권한만 받으면 자동. 끄기만 가능.

### 5. 검색 결과 칩 토글
전체 / 기업 / 공고 — 세그먼트로 결과 필터

---

## 🔀 네비게이션 흐름

```
[온보딩 1~4] → [메인 피드]
                  ↓ (filter icon)
                [필터] → 결과는 메인으로
                  ↓ (search tab)
                [검색] → [검색 결과] → [공고 상세]
                  ↓ (job card click)
                [공고 상세] → [share icon] → [공유 시트]
                            → [비슷한 탭] → [비슷한 공고]
                  ↓ (discover tab)
                [찾아보기 릴스]
                  ↓ (fav tab)
                [관심 기업] → [회사 상세] (공고 있음/없음)
                  ↓ (me tab)
                [마이페이지] → 저장한 공고 / 알림 설정 / 위젯 설정 /
                              관심 직군 / 피드백 / 알림 히스토리 / 마감 캘린더
                  ↓ (bell icon)
                [알림 히스토리]
```

---

## 🗂 상태 관리 요구사항

### 로컬 상태
- 관심기업 목록 (회사 ID 배열)
- 저장 공고 목록 (공고 ID 배열)
- 좋아한 공고 카운트 (찾아보기 통계용)
- 본 공고 카운트 + 스트릭 일수 (마이페이지)
- 알림 설정 (각 타입별 토글, 9시/21시 ON/OFF, 방해금지)
- 위젯 크기 (small/medium/large)
- 관심 직군 (21개 중 다중 선택)
- 알림 히스토리 (서버에서 N일치 fetch)

### 서버 데이터 (백엔드)
- 매일 18:00 수집된 공고 (대기업·중견기업 리스트)
- 어제 대비 diff (NEW / UPDATE / CLOSING)
- AI 분석 결과 (한줄 요약 + 신뢰도)
- 회사 메타데이터 (로고, 산업, 규모, 최근 채용 이력)
- 매칭 점수 (사용자 직군/저장 이력 기반)

---

## 📦 폴더 구조

```
design_handoff_jobalert/
├── README.md                  # 이 파일
├── screenshots/               # 26개 화면 스크린샷 PNG
└── source/                    # 원본 HTML 디자인 파일들
    ├── index.html             # 디자인 캔버스 (모든 화면 펼침)
    ├── styles.css             # v2 와이어프레임 스타일 (참고용)
    ├── styles-hifi.css        # ★ 하이파이 디자인 시스템
    ├── shared.jsx             # 공용 컴포넌트 (Mascot, SkIcon, Phone 등)
    ├── shared-hifi.jsx        # ★ 하이파이 공용 컴포넌트
    ├── screens-hifi.jsx       # ★ 메인 흐름 7화면
    ├── screens-hifi-extra.jsx # ★ 보조 화면 13화면
    ├── screens-hifi-new.jsx   # ★ 추가 기능 4화면
    ├── prototype.jsx          # 인터랙티브 네비 라우터
    ├── app.jsx                # 디자인 캔버스 컴포지션
    ├── design-canvas.jsx      # 캔버스 뷰어 (참고용, 재구현 불필요)
    └── tweaks-panel.jsx       # 마스코트 토글 패널 (참고용)
```

**★ 표시한 파일이 핵심**입니다. 나머지는 컨테이너/뷰어 코드라 재구현 불필요.

### 시작하는 법
1. `source/index.html`을 브라우저로 열어서 캔버스 뷰 보기 (모든 화면 + 인터랙티브 프로토타입)
2. 캔버스 좌상단 "📲 인터랙티브 프로토타입" 카드를 풀스크린으로 열면 실제 탭하며 흐름 체험 가능
3. 화면별 코드는 `source/screens-hifi*.jsx`에서 함수명으로 찾기

---

## ✅ 구현 시 체크리스트

- [ ] 디자인 토큰 → 타겟 플랫폼 토큰 시스템에 매핑 (Compose Theme / RN StyleSheet / Tailwind config 등)
- [ ] Pretendard 폰트 설치 (or 시스템 폰트로 fallback)
- [ ] 듀오링고풍 3D 버튼 (눌렀을 때 내려가는 인터랙션) 재현
- [ ] 마스코트 SVG → 일러스트레이터에게 의뢰해서 정식 일러스트로 교체
- [ ] 찾아보기 scroll-snap → 네이티브에서는 ViewPager / PagerView (vertical)
- [ ] 위젯은 별도 작업 (Android App Widget API)
- [ ] 푸시 알림은 FCM 또는 OneSignal 등
- [ ] 21개 직군 카테고리는 enum으로 관리
- [ ] 회사·공고 데이터 모델 정의 (백엔드 API 스펙 별도)

---

## 💡 의사결정 기록 (왜 이렇게 디자인했나)

- **빨강 톤다운**: CLOSING 컬러를 #FF4B4B 정도로 유지하되, 뱃지·알림 등 일상적 빨강은 코랄(#FF6B35)로 대체. 피로감 줄이기.
- **알림 시간 자동화**: 사용자가 시간 고르는 단계 제거. "허용만 받으면 9시·21시 자동" — 진입 마찰 감소.
- **❤️와 🔖 분리**: heart = 회사 즐겨찾기, bookmark = 개별 공고 저장. 인스타·핀터레스트 패턴과 일치.
- **좋아요 페이지 제거**: 저장과 통일. 마이페이지가 너무 복잡해지는 것 방지.
- **관심기업 메뉴 제거**: 하단 탭바에 이미 있으니 마이페이지 메뉴에서 빼서 중복 정리.
- **찾아보기 = Reels 스크롤**: 탭 카드 스택보다 익숙하고 빠름. 패스 버튼 없이 그냥 스크롤.
- **마스코트(꽁이)**: 듀오링고 부엉이처럼 매일 노출되는 친근한 페르소나. 종(species)은 Tweaks로 고양이/강아지 전환 가능.

---

## 📞 문의

디자인 의도가 불명확한 부분은 원본 디자인 파일을 직접 열어서 확인하거나, 디자이너에게 문의해주세요.
