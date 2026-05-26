// app.jsx — 디자인 캔버스에 모든 와이어프레임 펼치기

const ART_W = 340;
const ART_H = 700;

// 손글씨 코멘트 노트 (artboard 위에 띄움)
function Stamp({ children, color = 'var(--update)' }) {
  return (
    <div style={{
      display: 'inline-block',
      fontFamily: "'Nanum Pen Script', cursive",
      fontSize: 16,
      padding: '2px 6px',
      border: '1.6px solid '+color,
      color,
      borderRadius: 4,
      transform: 'rotate(-2deg)',
      background: 'rgba(255,255,255,0.85)'
    }}>{children}</div>
  );
}

function App() {
  const TWEAKS = /*EDITMODE-BEGIN*/{
    "showStamps": true,
    "mascot": "cat"
  }/*EDITMODE-END*/;
  const [t, setTweak] = useTweaks(TWEAKS);
  // 마스코트 종(species)을 전역에 sync — Mascot 컴포넌트가 매 렌더마다 읽음
  window.__mascotSpecies = t.mascot;

  // 작은 헬퍼: artboard 하단에 스탬프 코멘트 달기
  const Cap = ({ tone = 'default', children }) => (
    t.showStamps ? (
      <div style={{ position:'absolute', left: 8, bottom: -28, zIndex: 2 }}>
        <Stamp color={
          tone === 'pick' ? 'var(--good)' :
          tone === 'warn' ? 'var(--closing)' : 'var(--update)'
        }>{children}</Stamp>
      </div>
    ) : null
  );

  return (
    <>
      <DesignCanvas>
        {/* ── 하이파이 (듀오링고 풍) ─────────── */}
        <DCSection id="hifi" title="🎨 하이파이 (듀오링고 풍)" subtitle="실제 출시 톤 미리보기 — Pretendard 폰트, 정제된 컴포넌트, 코랄 브랜드 강화">
          <DCArtboard id="hifi-onb1" label="① 온보딩 (직군 선택)" width={400} height={780}>
            <HiFi_Onb1/>
            <Cap tone="pick">버튼에 듀오링고식 깊이감</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-onbswipe" label="①b 온보딩 (회사 스와이프)" width={400} height={780}>
            <HiFi_OnbSwipe/>
            <Cap tone="pick">찾아보기와 동일한 릴스 패턴</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-main" label="② 메인 피드" width={400} height={780}>
            <HiFi_Main/>
            <Cap tone="pick">섹션 토글 (NEW/UPDATE/CLOSING)</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-detail" label="③ 공고 상세" width={400} height={780}>
            <HiFi_Detail/>
            <Cap tone="pick">탭 + AI 요약 카드</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-discover" label="④ 찾아보기 (스와이프)" width={400} height={780}>
            <HiFi_Discover/>
            <Cap tone="pick">3D 버튼 + 토스트</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-mypage" label="⑤ 마이페이지" width={400} height={780}>
            <HiFi_MyPage/>
            <Cap tone="pick">스트릭 카드 강조</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-lock" label="⑥ 잠금화면 + 푸시" width={400} height={780}>
            <HiFi_LockScreen/>
            <Cap tone="pick">코랄 그라데이션 배경</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-widget" label="⑦ 바탕화면 위젯 (Large)" width={400} height={780}>
            <HiFi_Widget/>
            <Cap tone="pick">실제 안드로이드 홈스크린</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 인터랙티브 프로토타입 ─────────── */}
        <DCSection id="prototype" title="📲 인터랙티브 프로토타입" subtitle="실제로 탭하며 흐름 체험 — 카드 우측 상단 ⤢로 풀스크린 추천">
          <DCArtboard id="proto" label="🎮 클릭 가능한 데모" width={520} height={760}>
            <Prototype/>
          </DCArtboard>
        </DCSection>

        {/* ── v2 · 선택안 통합 흐름 (안드로이드, 코랄 브랜드) ─────────── */}
        <DCSection id="v2-flow" title="✨ v2 · 선택안 통합 흐름" subtitle="안드로이드 출시 · 꽁이 마스코트 · 자동 9시/21시 알림 + 바탕화면 위젯">
          <DCArtboard id="v2-onb1" label="① 온보딩 - 직군" width={ART_W} height={ART_H}>
            <V2_Onb1/>
            <Cap tone="pick">3-step 1/4</Cap>
          </DCArtboard>
          <DCArtboard id="v2-onb2" label="② 온보딩 - 기업/산업" width={ART_W} height={ART_H}>
            <V2_Onb2/>
            <Cap tone="pick">3-step 2/4</Cap>
          </DCArtboard>
          <DCArtboard id="v2-onb3" label="③ 온보딩 - 카드 스와이프" width={ART_W} height={ART_H}>
            <V2_Onb3Swipe/>
            <Cap tone="pick">3-step 3/4 — 흥미진진</Cap>
          </DCArtboard>
          <DCArtboard id="v2-onb4" label="④ 위젯+알림 유도" width={ART_W} height={ART_H}>
            <V2_Onb4Widget/>
            <Cap tone="pick">시간 설정 X, 자동 9시/21시</Cap>
          </DCArtboard>
          <DCArtboard id="v2-widget-sm" label="⑤a 위젯 - 2×1 (Small)" width={ART_W} height={ART_H}>
            <V2_Widget size="small"/>
            <Cap>도장 크기 — 빠른 카운터</Cap>
          </DCArtboard>
          <DCArtboard id="v2-widget-md" label="⑤b 위젯 - 4×1 (Medium)" width={ART_W} height={ART_H}>
            <V2_Widget size="medium"/>
            <Cap tone="pick">기본 — 한 줄 요약</Cap>
          </DCArtboard>
          <DCArtboard id="v2-widget-lg" label="⑤c 위젯 - 4×2 (Large)" width={ART_W} height={ART_H}>
            <V2_Widget size="large"/>
            <Cap>핵심 공고 3개 미리보기</Cap>
          </DCArtboard>
          <DCArtboard id="v2-main" label="⑥ 메인 - 가로 스와이프 3섹션" width={ART_W} height={ART_H}>
            <V2_Main/>
            <Cap tone="pick">NEW/UPDATE/CLOSING</Cap>
          </DCArtboard>
          <DCArtboard id="v2-detail" label="⑦ 공고 상세 - 탭 분리" width={ART_W} height={ART_H}>
            <V2_Detail/>
            <Cap tone="pick">요약/원문/회사/비슷한</Cap>
          </DCArtboard>
          <DCArtboard id="v2-filter" label="⑧ 필터 - 풀스크린" width={ART_W} height={ART_H}>
            <V2_Filter/>
            <Cap tone="pick">고급 필터 6그룹</Cap>
          </DCArtboard>
          <DCArtboard id="v2-fav" label="⑨ 관심 기업 - 그리드" width={ART_W} height={ART_H}>
            <V2_Favorites/>
            <Cap tone="pick">빨간점이 핵심</Cap>
          </DCArtboard>
          <DCArtboard id="v2-search" label="⑩ 검색 - 최근/인기" width={ART_W} height={ART_H}>
            <V2_Search/>
            <Cap tone="pick">익숙한 패턴</Cap>
          </DCArtboard>
          <DCArtboard id="v2-mypage" label="⑪ 마이페이지" width={ART_W} height={ART_H}>
            <V2_MyPage/>
            <Cap tone="pick">스트릭 + 메뉴 통합</Cap>
          </DCArtboard>
          <DCArtboard id="v2-discover" label="⑫ 찾아보기 - 카드 스와이프" width={ART_W} height={ART_H}>
            <V2_Discover/>
            <Cap tone="pick">하단 5번째 탭 · 패스/저장/관심+</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 빠진 화면 (C 단계) ─────────── */}
        <DCSection id="v2-edge" title="🧩 빠진 화면 / 엣지케이스" subtitle="빈 상태 · 에러 · 푸시 알림 · 검색 결과">
          <DCArtboard id="v2-empty" label="① 메인 - 빈 상태 (0건)" width={ART_W} height={ART_H}>
            <V2_MainEmpty/>
            <Cap tone="pick">"오늘은 조용한 날" — 관심기업 추가 유도</Cap>
          </DCArtboard>
          <DCArtboard id="v2-lock" label="② 푸시 - 잠금화면 (9:00)" width={ART_W} height={ART_H}>
            <V2_LockScreen/>
            <Cap tone="pick">아침 9시 자동 푸시</Cap>
          </DCArtboard>
          <DCArtboard id="v2-notif" label="③ 푸시 - 알림 센터" width={ART_W} height={ART_H}>
            <V2_NotifCenter/>
            <Cap>3종 푸시 패턴 (아침/관심/마감)</Cap>
          </DCArtboard>
          <DCArtboard id="v2-srchres" label="④ 검색 결과" width={ART_W} height={ART_H}>
            <V2_SearchResults/>
            <Cap>기업 + 공고 분리 노출</Cap>
          </DCArtboard>
          <DCArtboard id="v2-company" label="⑤ 회사 상세 (공고 있음)" width={ART_W} height={ART_H}>
            <V2_CompanyDetail/>
            <Cap tone="pick">관심기업에서 클릭시</Cap>
          </DCArtboard>
          <DCArtboard id="v2-company-empty" label="⑥ 회사 상세 (공고 없음)" width={ART_W} height={ART_H}>
            <V2_CompanyDetailEmpty/>
            <Cap tone="warn">"현재 채용중인 공고 없음"</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 마이페이지 서브 ─────────── */}
        <DCSection id="v2-sub" title="📂 마이페이지 서브페이지" subtitle="각 메뉴 항목 + 저장/좋아요 카운터 클릭시 진입">
          <DCArtboard id="v2-saved" label="① 저장한 공고 (14)" width={ART_W} height={ART_H}>
            <V2_SavedPostings/>
            <Cap tone="pick">저장 카운터 → 진입</Cap>
          </DCArtboard>
          <DCArtboard id="v2-notifSet" label="③ 알림 설정" width={ART_W} height={ART_H}>
            <V2_NotifSettings/>
            <Cap>9시·21시 자동, 끄기만 가능</Cap>
          </DCArtboard>
          <DCArtboard id="v2-widgetSet" label="④ 바탕화면 위젯 설정" width={ART_W} height={ART_H}>
            <V2_WidgetSettings/>
            <Cap>크기 토글 (실시간 미리보기)</Cap>
          </DCArtboard>
          <DCArtboard id="v2-jobInt" label="⑤ 관심 직군" width={ART_W} height={ART_H}>
            <V2_JobInterests/>
            <Cap>대분류 + 세부직군 (개발)</Cap>
          </DCArtboard>
          <DCArtboard id="v2-feedback" label="⑥ 피드백 보내기" width={ART_W} height={ART_H}>
            <V2_Feedback/>
            <Cap>유형 분류 + 자유 입력</Cap>
          </DCArtboard>
          <DCArtboard id="v2-about" label="⑦ 앱 정보" width={ART_W} height={ART_H}>
            <V2_About/>
            <Cap>약관·문의 등 기본 메뉴</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── v1 탐색 (참고용으로 아래에 유지) ───────────────── */}
        <DCSection id="onboarding" title="① 온보딩 (v1 탐색)" subtitle="첫 진입 — 직군/관심기업/알림시간 설정">
          <DCArtboard id="onb-a" label="A · 3-step 페이지" width={ART_W} height={ART_H}>
            <OnboardingA/>
            <Cap>1단계: 직군 선택</Cap>
          </DCArtboard>
          <DCArtboard id="onb-b" label="B · 단일 스크롤" width={ART_W} height={ART_H}>
            <OnboardingB/>
            <Cap tone="pick">한 화면에 다보임</Cap>
          </DCArtboard>
          <DCArtboard id="onb-c" label="C · 카드 스와이프" width={ART_W} height={ART_H}>
            <OnboardingC/>
            <Cap>흥미진진한 첫인상</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 메인 피드 ─────────────────────────── */}
        <DCSection id="main" title="② 메인 피드" subtitle="가장 중요한 화면 — '오늘 새공고'가 강조">
          <DCArtboard id="main-a" label="A · 가로 스와이프 3섹션" width={ART_W} height={ART_H}>
            <MainA/>
            <Cap tone="pick">요청 그대로 — NEW/UPDATE/CLOSING 페이지닷</Cap>
          </DCArtboard>
          <DCArtboard id="main-b" label="B · Segmented + 통합" width={ART_W} height={ART_H}>
            <MainB/>
            <Cap>탭으로 전환, 필터칩까지 한 화면에</Cap>
          </DCArtboard>
          <DCArtboard id="main-c" label="C · 마스코트 큰 카드" width={ART_W} height={ART_H}>
            <MainC/>
            <Cap>강조 + 캐릭터 친근감</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 공고 상세 ─────────────────────────── */}
        <DCSection id="detail" title="③ 공고 상세" subtitle="AI 요약 + 핵심 정보 + 원본 링크">
          <DCArtboard id="det-a" label="A · 풀스크롤" width={ART_W} height={ART_H}>
            <DetailA/>
            <Cap>모든 정보가 한 페이지에</Cap>
          </DCArtboard>
          <DCArtboard id="det-b" label="B · 탭 분리" width={ART_W} height={ART_H}>
            <DetailB/>
            <Cap tone="pick">요약/원문/회사 분리</Cap>
          </DCArtboard>
          <DCArtboard id="det-c" label="C · 봇툼 시트" width={ART_W} height={ART_H}>
            <DetailC/>
            <Cap>메인에서 빠르게 미리보기</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 필터 ─────────────────────────── */}
        <DCSection id="filter" title="④ 필터" subtitle="요청: 상단 칩 가로 스크롤 — 그 변형 포함">
          <DCArtboard id="flt-a" label="A · 인라인 상단 칩" width={ART_W} height={ART_H}>
            <FilterA/>
            <Cap tone="pick">요청 그대로 — 메인 화면에 인라인</Cap>
          </DCArtboard>
          <DCArtboard id="flt-b" label="B · 풀스크린 필터" width={ART_W} height={ART_H}>
            <FilterB/>
            <Cap>고급 필터 (많은 옵션)</Cap>
          </DCArtboard>
          <DCArtboard id="flt-c" label="C · 봇툼 시트" width={ART_W} height={ART_H}>
            <FilterC/>
            <Cap>빠른 토글용 (가벼움)</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 즐겨찾기 ─────────────────────────── */}
        <DCSection id="fav" title="⑤ 관심 기업" subtitle="관리 화면 — 알림 ON/OFF 컨트롤 포함">
          <DCArtboard id="fav-a" label="A · 로고 그리드" width={ART_W} height={ART_H}>
            <FavoritesA/>
            <Cap>한눈에 보임 — 빨간점이 핵심</Cap>
          </DCArtboard>
          <DCArtboard id="fav-b" label="B · 리스트 + 가나다" width={ART_W} height={ART_H}>
            <FavoritesB/>
            <Cap>기업 많을때 (50+)</Cap>
          </DCArtboard>
          <DCArtboard id="fav-c" label="C · 카테고리 탭" width={ART_W} height={ART_H}>
            <FavoritesC/>
            <Cap tone="pick">"새 공고 있는 곳" 우선노출</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 알림 ─────────────────────────── */}
        <DCSection id="notif" title="⑥ 알림 설정" subtitle="듀오링고 챙김의 핵심 — 강도/시간 조절">
          <DCArtboard id="not-a" label="A · 토글 리스트" width={ART_W} height={ART_H}>
            <NotifA/>
            <Cap>iOS 표준 패턴</Cap>
          </DCArtboard>
          <DCArtboard id="not-b" label="B · 카드형" width={ART_W} height={ART_H}>
            <NotifB/>
            <Cap tone="pick">카테고리별 세부 설정</Cap>
          </DCArtboard>
          <DCArtboard id="not-c" label="C · 캐릭터 안내" width={ART_W} height={ART_H}>
            <NotifC/>
            <Cap>온보딩 같은 친근함</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 검색 ─────────────────────────── */}
        <DCSection id="search" title="⑦ 검색" subtitle="기업/직무/키워드 통합 검색">
          <DCArtboard id="srch-a" label="A · 최근/인기" width={ART_W} height={ART_H}>
            <SearchA/>
            <Cap tone="pick">가장 익숙한 패턴</Cap>
          </DCArtboard>
          <DCArtboard id="srch-b" label="B · 자동완성 + 미리보기" width={ART_W} height={ART_H}>
            <SearchB/>
            <Cap>타이핑 중 결과 바로</Cap>
          </DCArtboard>
          <DCArtboard id="srch-c" label="C · 음성 강조" width={ART_W} height={ART_H}>
            <SearchC/>
            <Cap>출퇴근 중 빠른 검색</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 보너스: 수집 모니터링 ─────────────────────────── */}
        <DCSection id="monitor" title="⑧ 수집 모니터링 (보너스)" subtitle="✨ 정보서칭 변수 많은 문제 대응 — Claude 분석 신뢰도 노출">
          <DCArtboard id="mon-a" label="A · 기업별 수집 현황" width={ART_W} height={ART_H}>
            <MonitorA/>
            <Cap tone="warn">관리자/파워유저용</Cap>
          </DCArtboard>
          <DCArtboard id="mon-b" label="B · AI 분석 신뢰도" width={ART_W} height={ART_H}>
            <MonitorB/>
            <Cap tone="warn">'확인 필요' 공고를 사람이 검수</Cap>
          </DCArtboard>
          <DCArtboard id="mon-c" label="C · 미니멀 (내 정보)" width={ART_W} height={ART_H}>
            <MonitorC/>
            <Cap tone="pick">일반 사용자용 (스트릭+요약)</Cap>
          </DCArtboard>
        </DCSection>
      </DesignCanvas>

      <TweaksPanel title="와이어프레임 옵션">
        <TweakSection label="마스코트">
          <TweakRadio
            label="종(species)"
            value={t.mascot}
            options={[{ value: 'cat', label: '🐱 고양이' }, { value: 'dog', label: '🐶 강아지' }]}
            onChange={v => setTweak('mascot', v)}
          />
        </TweakSection>
        <TweakSection label="표시">
          <TweakToggle label="손글씨 코멘트" value={t.showStamps} onChange={v => setTweak('showStamps', v)}/>
        </TweakSection>
        <TweakSection label="안내">
          <div style={{ fontFamily: "'Nanum Pen Script', cursive", fontSize: 15, color: '#4a4a4a', lineHeight: 1.3 }}>
            • 캔버스 빈 공간을 드래그하면 패닝<br/>
            • 휠/핀치로 줌인/줌아웃<br/>
            • 카드 우측 상단 ⤢ 버튼으로 풀스크린<br/>
            • 카드 드래그로 순서 변경
          </div>
        </TweakSection>
      </TweaksPanel>
    </>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App/>);
