// prototype.jsx — 인터랙티브 프로토타입
// 단일 폰 화면 + 화면 간 네비게이션 상태 관리

const SCREEN_REGISTRY = {
  // Hi-Fi 화면들 (우선)
  h_onb1: { Comp: () => <HiFi_Onb1/>, label: '🎨 Hi-Fi 온보딩 ①' },
  h_onb2: { Comp: () => <HiFi_Onb2/>, label: '🎨 Hi-Fi 온보딩 ② 기업/산업' },
  h_onb2swipe: { Comp: () => <HiFi_OnbSwipe/>, label: '🎨 Hi-Fi 온보딩 ③ 스와이프' },
  h_onb4: { Comp: () => <HiFi_Onb4Widget/>, label: '🎨 Hi-Fi 온보딩 ④ 위젯' },
  h_main: { Comp: () => <HiFi_Main/>, label: '🎨 Hi-Fi 메인' },
  h_mainEmpty: { Comp: () => <HiFi_MainEmpty/>, label: '🎨 Hi-Fi 메인 (빈 상태)' },
  h_detail: { Comp: () => <HiFi_Detail/>, label: '🎨 Hi-Fi 상세' },
  h_discover: { Comp: () => <HiFi_Discover/>, label: '🎨 Hi-Fi 찾아보기' },
  h_filter: { Comp: () => <HiFi_Filter/>, label: '🎨 Hi-Fi 필터' },
  h_fav: { Comp: () => <HiFi_Favorites/>, label: '🎨 Hi-Fi 관심기업' },
  h_search: { Comp: () => <HiFi_Search/>, label: '🎨 Hi-Fi 검색' },
  h_searchResults: { Comp: () => <HiFi_SearchResults/>, label: '🎨 Hi-Fi 검색 결과' },
  h_company: { Comp: () => <HiFi_CompanyDetail/>, label: '🎨 Hi-Fi 회사 상세' },
  h_companyEmpty: { Comp: () => <HiFi_CompanyDetailEmpty/>, label: '🎨 Hi-Fi 회사 상세 (공고없음)' },
  h_mypage: { Comp: () => <HiFi_MyPage/>, label: '🎨 Hi-Fi 마이' },
  h_saved: { Comp: () => <HiFi_SavedPostings/>, label: '🎨 Hi-Fi 저장한 공고' },
  h_notifSettings: { Comp: () => <HiFi_NotifSettings/>, label: '🎨 Hi-Fi 알림 설정' },
  h_widgetSettings: { Comp: () => <HiFi_WidgetSettings/>, label: '🎨 Hi-Fi 위젯 설정' },
  h_jobInterests: { Comp: () => <HiFi_JobInterests/>, label: '🎨 Hi-Fi 관심 직군' },
  h_feedback: { Comp: () => <HiFi_Feedback/>, label: '🎨 Hi-Fi 피드백' },
  h_lock: { Comp: () => <HiFi_LockScreen/>, label: '🎨 Hi-Fi 잠금' },
  h_widget: { Comp: () => <HiFi_Widget/>, label: '🎨 Hi-Fi 위젯' },
  h_notifHistory: { Comp: () => <HiFi_NotifHistory/>, label: '🆕 알림 히스토리' },
  h_calendar: { Comp: () => <HiFi_Calendar/>, label: '🆕 마감 캘린더' },
  h_share: { Comp: () => <HiFi_ShareSheet/>, label: '🆕 공유 시트' },
  h_similar: { Comp: () => <HiFi_Similar/>, label: '🆕 비슷한 공고' },
  // ── v2 와이어프레임 ──
  onb1: { Comp: () => <V2_Onb1/>, label: '온보딩 ①' },
  onb2: { Comp: () => <V2_Onb2/>, label: '온보딩 ②' },
  onb3: { Comp: () => <V2_Onb3Swipe/>, label: '온보딩 ③' },
  onb4: { Comp: () => <V2_Onb4Widget/>, label: '온보딩 ④' },
  main: { Comp: () => <V2_Main/>, label: '메인' },
  detail: { Comp: () => <V2_Detail/>, label: '공고 상세' },
  filter: { Comp: () => <V2_Filter/>, label: '필터' },
  fav: { Comp: () => <V2_Favorites/>, label: '관심 기업' },
  company: { Comp: () => <V2_CompanyDetail/>, label: '· 회사 상세 (삼성)' },
  companyEmpty: { Comp: () => <V2_CompanyDetailEmpty/>, label: '· 회사 상세 (공고없음)' },
  search: { Comp: () => <V2_Search/>, label: '검색' },
  searchResults: { Comp: () => <V2_SearchResults/>, label: '· 검색 결과' },
  mypage: { Comp: () => <V2_MyPage/>, label: '마이페이지' },
  saved: { Comp: () => <V2_SavedPostings/>, label: '· 저장한 공고' },
  notifSettings: { Comp: () => <V2_NotifSettings/>, label: '· 알림 설정' },
  widgetSettings: { Comp: () => <V2_WidgetSettings/>, label: '· 위젯 설정' },
  jobInterests: { Comp: () => <V2_JobInterests/>, label: '· 관심 직군' },
  feedback: { Comp: () => <V2_Feedback/>, label: '· 피드백' },
  about: { Comp: () => <V2_About/>, label: '· 앱 정보' },
  mainEmpty: { Comp: () => <V2_MainEmpty/>, label: '메인 (빈 상태)' },
  discover: { Comp: () => <V2_Discover/>, label: '🌟 찾아보기' },
  lockScreen: { Comp: () => <V2_LockScreen/>, label: '🔒 잠금화면' },
  notifCenter: { Comp: () => <V2_NotifCenter/>, label: '🔔 알림 센터' },
};

function Prototype() {
  const [history, setHistory] = React.useState(['onb1']);
  const [animKey, setAnimKey] = React.useState(0);
  const current = history[history.length - 1];

  const nav = React.useMemo(() => ({
    current,
    go: (s) => {
      if (!SCREEN_REGISTRY[s]) {
        console.warn('prototype: no screen', s);
        return;
      }
      setHistory(h => [...h, s]);
      setAnimKey(k => k + 1);
    },
    back: () => {
      setHistory(h => h.length > 1 ? h.slice(0, -1) : h);
      setAnimKey(k => k + 1);
    },
    reset: () => {
      setHistory(['onb1']);
      setAnimKey(k => k + 1);
    },
  }), [current]);

  const Screen = SCREEN_REGISTRY[current].Comp;

  return (
    <NavCtx.Provider value={nav}>
      <div style={{
        display: 'flex', gap: 16, padding: 14,
        width: '100%', height: '100%', boxSizing:'border-box',
        alignItems:'flex-start'
      }}>
        {/* 좌측: 폰 */}
        <div style={{ display:'flex', flexDirection:'column', gap: 8 }}>
          {/* 컨트롤 */}
          <div style={{
            display: 'flex', alignItems: 'center', gap: 6,
            fontFamily: "'Gaegu', sans-serif", fontSize: 13,
          }}>
            <span className="note" style={{ color:'#666' }}>
              현재: <b>{SCREEN_REGISTRY[current].label}</b>
            </span>
            <span style={{ flex: 1 }}/>
            <button
              onClick={nav.back}
              disabled={history.length <= 1}
              style={{
                fontFamily:"'Gaegu', sans-serif", fontSize: 12,
                padding:'4px 10px', borderRadius: 6,
                border:'1.5px solid #1a1a1a', background:'#fff', cursor:'pointer',
                opacity: history.length <= 1 ? 0.35 : 1
              }}>← 뒤로</button>
            <button
              onClick={nav.reset}
              style={{
                fontFamily:"'Gaegu', sans-serif", fontSize: 12,
                padding:'4px 10px', borderRadius: 6,
                border:'1.5px solid #1a1a1a', background:'#fff', cursor:'pointer'
              }}>↺ 처음으로</button>
          </div>
          <div key={animKey} className="proto-screen">
            <Screen/>
          </div>
        </div>

        {/* 우측: 점프 메뉴 */}
        <div style={{
          width: 130,
          display:'flex', flexDirection:'column', gap: 4,
          fontFamily:"'Gaegu', sans-serif", fontSize: 13,
          paddingTop: 32
        }}>
          <div className="note" style={{ color:'#888', marginBottom: 4 }}>↪ 화면 점프</div>
          {Object.entries(SCREEN_REGISTRY).map(([id, { label }]) => (
            <button
              key={id}
              onClick={() => nav.go(id)}
              style={{
                fontFamily:"'Gaegu', sans-serif", fontSize: 13,
                padding:'5px 10px', borderRadius: 6,
                border: '1.5px solid #1a1a1a',
                background: id === current ? 'var(--brand)' : '#fffdf7',
                color: id === current ? '#fff' : '#1a1a1a',
                cursor:'pointer', textAlign:'left',
                fontWeight: id === current ? 700 : 400
              }}>
              {label}
            </button>
          ))}

          <div className="note" style={{ color:'#888', marginTop: 12, fontSize: 13, lineHeight: 1.3 }}>
            💡 폰의 버튼/카드를 직접 탭하거나, 점프로 임의 화면 이동
          </div>
        </div>
      </div>
    </NavCtx.Provider>
  );
}

// 페이드 전환 애니메이션 + 점프 메뉴 스타일
if (typeof document !== 'undefined' && !document.getElementById('proto-styles')) {
  const s = document.createElement('style');
  s.id = 'proto-styles';
  s.textContent = `
    .proto-screen {
      animation: protoFade 0.22s ease;
    }
    @keyframes protoFade {
      from { opacity: 0; transform: translateX(8px); }
      to { opacity: 1; transform: translateX(0); }
    }
  `;
  document.head.appendChild(s);
}

window.Prototype = Prototype;
