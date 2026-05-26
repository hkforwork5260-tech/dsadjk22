// shared.jsx — 마스코트, 손그림 아이콘, 폰 프레임, 공용 UI

// ── 마스코트: '꽁이' — 몽글몽글 고양이/강아지 ───────────
// species: 'cat' (default) | 'dog'
// expressions: 'default' | 'happy' | 'sleep' | 'sad' | 'wow' | 'wave'
function Mascot({ size = 80, expression = 'default', tint, species }) {
  // 전역 종(species) 설정이 있으면 그걸 사용
  const sp = species || (typeof window !== 'undefined' && window.__mascotSpecies) || 'cat';
  // 기본 색상: 고양이는 따뜻한 베이지/크림, 강아지는 따뜻한 갈색
  const fill = tint || (sp === 'dog' ? '#e8b88a' : '#fad9b0');
  const earInner = sp === 'dog' ? '#c89060' : '#ffb8c2';
  const eyes = {
    default: <>
      <ellipse cx="38" cy="52" rx="4.2" ry="5" fill="#1a1a1a"/>
      <ellipse cx="62" cy="52" rx="4.2" ry="5" fill="#1a1a1a"/>
      <circle cx="39.5" cy="50" r="1.4" fill="#fff"/>
      <circle cx="63.5" cy="50" r="1.4" fill="#fff"/>
    </>,
    happy: <>
      <path d="M34 53 Q38 48 42 53" stroke="#1a1a1a" strokeWidth="2.8" fill="none" strokeLinecap="round"/>
      <path d="M58 53 Q62 48 66 53" stroke="#1a1a1a" strokeWidth="2.8" fill="none" strokeLinecap="round"/>
    </>,
    sleep: <>
      <path d="M33 53 L43 53" stroke="#1a1a1a" strokeWidth="2.6" strokeLinecap="round"/>
      <path d="M57 53 L67 53" stroke="#1a1a1a" strokeWidth="2.6" strokeLinecap="round"/>
    </>,
    sad: <>
      <ellipse cx="38" cy="54" rx="4" ry="4.5" fill="#1a1a1a"/>
      <ellipse cx="62" cy="54" rx="4" ry="4.5" fill="#1a1a1a"/>
      <path d="M33 48 L42 50" stroke="#1a1a1a" strokeWidth="2" strokeLinecap="round"/>
      <path d="M67 48 L58 50" stroke="#1a1a1a" strokeWidth="2" strokeLinecap="round"/>
    </>,
    wow: <>
      <ellipse cx="38" cy="52" rx="5" ry="6" fill="#1a1a1a"/>
      <ellipse cx="62" cy="52" rx="5" ry="6" fill="#1a1a1a"/>
      <circle cx="39.5" cy="50" r="1.6" fill="#fff"/>
      <circle cx="63.5" cy="50" r="1.6" fill="#fff"/>
    </>,
    wave: <>
      <ellipse cx="38" cy="52" rx="4.2" ry="5" fill="#1a1a1a"/>
      <ellipse cx="62" cy="52" rx="4.2" ry="5" fill="#1a1a1a"/>
      <circle cx="39.5" cy="50" r="1.4" fill="#fff"/>
      <circle cx="63.5" cy="50" r="1.4" fill="#fff"/>
    </>,
  }[expression] || null;

  const mouth = {
    default: <path d="M44 65 Q50 70 56 65" stroke="#1a1a1a" strokeWidth="2.4" fill="none" strokeLinecap="round"/>,
    happy: <path d="M40 64 Q50 74 60 64 Q56 70 50 70 Q44 70 40 64 Z" stroke="#1a1a1a" strokeWidth="2.6" fill="#ff6b8a" strokeLinejoin="round"/>,
    sleep: <ellipse cx="50" cy="68" rx="3.5" ry="2.5" fill="none" stroke="#1a1a1a" strokeWidth="2"/>,
    sad: <path d="M44 70 Q50 65 56 70" stroke="#1a1a1a" strokeWidth="2.4" fill="none" strokeLinecap="round"/>,
    wow: <ellipse cx="50" cy="68" rx="4" ry="5" fill="#1a1a1a"/>,
    wave: <path d="M44 65 Q50 70 56 65" stroke="#1a1a1a" strokeWidth="2.4" fill="none" strokeLinecap="round"/>,
  }[expression];

  const arm = expression === 'wave' ? (
    <g transform="translate(82,58) rotate(-25)">
      <ellipse cx="0" cy="0" rx="5" ry="3.5" fill={fill} stroke="#1a1a1a" strokeWidth="2"/>
    </g>
  ) : null;

  return (
    <svg width={size} height={size} viewBox="0 0 100 100" style={{ display: 'block' }}>
      {/* 그림자 */}
      <ellipse cx="50" cy="93" rx="22" ry="2.5" fill="rgba(0,0,0,0.08)"/>
      {/* 작은 발 2개 */}
      <ellipse cx="40" cy="89" rx="6" ry="3.5" fill={fill} stroke="#1a1a1a" strokeWidth="2"/>
      <ellipse cx="60" cy="89" rx="6" ry="3.5" fill={fill} stroke="#1a1a1a" strokeWidth="2"/>

      {/* 귀 — 고양이는 삼각형, 강아지는 둥근 floppy */}
      {sp === 'cat' ? (
        <g>
          <path d="M 26 30 L 22 12 L 38 22 Z" fill={fill} stroke="#1a1a1a" strokeWidth="2.2" strokeLinejoin="round"/>
          <path d="M 74 30 L 78 12 L 62 22 Z" fill={fill} stroke="#1a1a1a" strokeWidth="2.2" strokeLinejoin="round"/>
          <path d="M 27 26 L 26 18 L 32 22 Z" fill={earInner}/>
          <path d="M 73 26 L 74 18 L 68 22 Z" fill={earInner}/>
        </g>
      ) : (
        <g>
          {/* 강아지 floppy 귀 */}
          <ellipse cx="22" cy="32" rx="8" ry="14" fill={fill} stroke="#1a1a1a" strokeWidth="2.2" transform="rotate(-18 22 32)"/>
          <ellipse cx="78" cy="32" rx="8" ry="14" fill={fill} stroke="#1a1a1a" strokeWidth="2.2" transform="rotate(18 78 32)"/>
        </g>
      )}

      {/* 본체 — 몽글한 동그란 형태 */}
      <path
        d="M 50 22
           C 28 22, 20 38, 20 56
           C 20 78, 32 88, 50 88
           C 68 88, 80 78, 80 56
           C 80 38, 72 22, 50 22 Z"
        fill={fill}
        stroke="#1a1a1a"
        strokeWidth="2.4"
        strokeLinejoin="round"
      />
      {/* 하이라이트 */}
      <ellipse cx="35" cy="38" rx="7" ry="4.5" fill="rgba(255,255,255,0.45)" transform="rotate(-25 35 38)"/>

      {eyes}

      {/* 코 (작은 분홍 삼각형) */}
      {sp === 'cat' ? (
        <path d="M 47 60 L 53 60 L 50 64 Z" fill="#ff8fa3" stroke="#1a1a1a" strokeWidth="1.4" strokeLinejoin="round"/>
      ) : (
        <ellipse cx="50" cy="62" rx="3.5" ry="2.5" fill="#1a1a1a"/>
      )}

      {/* 입 / 수염 */}
      {sp === 'cat' ? (
        <g>
          {/* 입 */}
          <path d="M 50 64 Q 50 68 46 68" stroke="#1a1a1a" strokeWidth="2" fill="none" strokeLinecap="round"/>
          <path d="M 50 64 Q 50 68 54 68" stroke="#1a1a1a" strokeWidth="2" fill="none" strokeLinecap="round"/>
          {/* 수염 (좌) */}
          <path d="M 25 64 L 38 62" stroke="#1a1a1a" strokeWidth="1.4" strokeLinecap="round" opacity="0.7"/>
          <path d="M 25 68 L 38 67" stroke="#1a1a1a" strokeWidth="1.4" strokeLinecap="round" opacity="0.7"/>
          {/* 수염 (우) */}
          <path d="M 75 64 L 62 62" stroke="#1a1a1a" strokeWidth="1.4" strokeLinecap="round" opacity="0.7"/>
          <path d="M 75 68 L 62 67" stroke="#1a1a1a" strokeWidth="1.4" strokeLinecap="round" opacity="0.7"/>
        </g>
      ) : (
        <g>
          {/* 강아지 입 */}
          <path d="M 50 64 L 50 67" stroke="#1a1a1a" strokeWidth="2" strokeLinecap="round"/>
          <path d="M 50 67 Q 45 70 42 67" stroke="#1a1a1a" strokeWidth="2" fill="none" strokeLinecap="round"/>
          <path d="M 50 67 Q 55 70 58 67" stroke="#1a1a1a" strokeWidth="2" fill="none" strokeLinecap="round"/>
          {/* 혀 (행복할 때만) */}
          {(expression === 'happy' || expression === 'wave') && (
            <ellipse cx="50" cy="71" rx="3" ry="2" fill="#ff6b8a" stroke="#1a1a1a" strokeWidth="1.4"/>
          )}
        </g>
      )}

      {mouth}

      {/* 볼터치 */}
      <circle cx="28" cy="68" r="4" fill="#ff8fa3" opacity="0.55"/>
      <circle cx="72" cy="68" r="4" fill="#ff8fa3" opacity="0.55"/>

      {arm}
    </svg>
  );
}

// ── 손그림 아이콘 ──────────────────────────────────────────────
function SkIcon({ name, size = 16, color = 'currentColor', strokeWidth = 1.8 }) {
  const paths = {
    home: <path d="M3 10 L10 3 L17 10 M5 9 V17 H15 V9"/>,
    bell: <path d="M5 14 H15 M6 14 V9 Q6 5 10 5 Q14 5 14 9 V14 M8 16 Q10 18 12 16"/>,
    star: <path d="M10 3 L12 8 L17 8.5 L13 12 L14.5 17 L10 14 L5.5 17 L7 12 L3 8.5 L8 8 Z"/>,
    user: <>
      <circle cx="10" cy="7" r="3"/>
      <path d="M4 17 Q4 11 10 11 Q16 11 16 17"/>
    </>,
    search: <>
      <circle cx="8.5" cy="8.5" r="4.5"/>
      <path d="M12 12 L16 16"/>
    </>,
    filter: <path d="M3 5 H17 M5 10 H15 M8 15 H12"/>,
    close: <path d="M5 5 L15 15 M15 5 L5 15"/>,
    chev: <path d="M7 4 L13 10 L7 16"/>,
    'chev-l': <path d="M13 4 L7 10 L13 16"/>,
    'chev-d': <path d="M4 7 L10 13 L16 7"/>,
    'chev-u': <path d="M4 13 L10 7 L16 13"/>,
    plus: <path d="M10 4 V16 M4 10 H16"/>,
    check: <path d="M4 10 L8 14 L16 5"/>,
    list: <path d="M3 5 H17 M3 10 H17 M3 15 H17"/>,
    grid: <path d="M3 3 H9 V9 H3 Z M11 3 H17 V9 H11 Z M3 11 H9 V17 H3 Z M11 11 H17 V17 H11 Z"/>,
    heart: <path d="M10 16 Q3 11 3 7 Q3 4 6 4 Q8 4 10 7 Q12 4 14 4 Q17 4 17 7 Q17 11 10 16 Z"/>,
    clock: <>
      <circle cx="10" cy="10" r="6"/>
      <path d="M10 7 V10 L13 12"/>
    </>,
    calendar: <>
      <rect x="3" y="5" width="14" height="12" rx="1"/>
      <path d="M3 8 H17 M7 3 V6 M13 3 V6"/>
    </>,
    doc: <path d="M5 3 H13 L16 6 V17 H5 Z M13 3 V6 H16 M7 9 H14 M7 12 H14 M7 15 H11"/>,
    link: <path d="M9 7 L7 7 Q3 7 3 11 Q3 14 7 14 L9 14 M11 7 L13 7 Q17 7 17 11 Q17 14 13 14 L11 14 M7 11 H13"/>,
    bookmark: <path d="M5 3 H15 V17 L10 13 L5 17 Z"/>,
    share: <>
      <circle cx="5" cy="10" r="2"/>
      <circle cx="15" cy="5" r="2"/>
      <circle cx="15" cy="15" r="2"/>
      <path d="M7 9 L13 6 M7 11 L13 14"/>
    </>,
    mic: <path d="M10 3 Q12 3 12 5 V10 Q12 12 10 12 Q8 12 8 10 V5 Q8 3 10 3 Z M5 10 Q5 14 10 14 Q15 14 15 10 M10 14 V17"/>,
    refresh: <path d="M5 5 Q5 3 8 3 H12 Q16 3 16 7 V10 M16 7 L14 9 M16 7 L18 9 M15 15 Q15 17 12 17 H8 Q4 17 4 13 V10 M4 13 L2 11 M4 13 L6 11"/>,
    settings: <>
      <circle cx="10" cy="10" r="2.5"/>
      <path d="M10 3 V5 M10 15 V17 M17 10 H15 M5 10 H3 M15 5 L13.5 6.5 M6.5 13.5 L5 15 M15 15 L13.5 13.5 M6.5 6.5 L5 5"/>
    </>,
    fire: <path d="M10 3 Q6 7 6 10 Q6 14 10 17 Q14 14 14 10 Q14 7 12 5 Q11 8 10 8 Q9 6 10 3 Z"/>,
    bag: <path d="M5 7 H15 L16 17 H4 Z M7 7 Q7 3 10 3 Q13 3 13 7"/>,
    sparkle: <path d="M10 3 L11 8 L16 9 L11 10 L10 15 L9 10 L4 9 L9 8 Z M16 3 L16.5 5 L18 5.5 L16.5 6 L16 8 L15.5 6 L14 5.5 L15.5 5 Z"/>,
    compass: <>
      <circle cx="10" cy="10" r="7"/>
      <path d="M10 5 L12 10 L10 15 L8 10 Z" fill={color} fillOpacity="0.15"/>
      <path d="M10 5 L12 10 L10 15 L8 10 Z"/>
    </>,
    arrowR: <path d="M3 10 H17 M12 5 L17 10 L12 15"/>,
    'arrow-down-r': <path d="M3 4 Q12 4 16 16 M12 12 L16 16 L20 12" transform="translate(-2,-1)"/>,
    pdf: <text x="2" y="14" fontFamily="Gaegu" fontSize="9" fontWeight="700" fill={color}>PDF</text>,
  };
  return (
    <svg width={size} height={size} viewBox="0 0 20 20" fill="none" stroke={color} strokeWidth={strokeWidth} strokeLinecap="round" strokeLinejoin="round" style={{ display:'inline-block', verticalAlign:'middle' }}>
      {paths[name]}
    </svg>
  );
}

// ── 손그림 화살표 (artboard 사이 메모용) ──────────────────────
function SkArrow({ d, color = '#4a4a4a', strokeWidth = 2 }) {
  return (
    <svg style={{ position:'absolute', left:0, top:0, overflow:'visible', pointerEvents:'none' }} width="1" height="1">
      <path d={d} stroke={color} strokeWidth={strokeWidth} fill="none" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  );
}

// ── 폰 프레임 ─────────────────────────────────────────
function Phone({ children, title = '채용알리미', showHeader = true }) {
  return (
    <div className="phone">
      {showHeader && (
        <div className="toss-header">
          <span className="x">×</span>
          <span className="ttl">{title}</span>
          <SkIcon name="share" size={14}/>
        </div>
      )}
      <div className="phone-inner">
        {children}
      </div>
    </div>
  );
}

// ── 하단 탭바 ─────────────────────────────────────────
function TabBar({ active = 'home' }) {
  const tabs = [
    { id: 'home', label: '오늘', icon: 'home' },
    { id: 'search', label: '검색', icon: 'search' },
    { id: 'fav', label: '관심기업', icon: 'heart' },
    { id: 'me', label: '내정보', icon: 'user' },
  ];
  return (
    <div className="tabbar">
      {tabs.map(t => (
        <div key={t.id} className={'tab' + (active === t.id ? ' on' : '')}>
          <div className="ic"><SkIcon name={t.icon} size={14}/></div>
          <div>{t.label}</div>
        </div>
      ))}
    </div>
  );
}

// ── 공통 카드: 채용공고 (자세한 정보 노출) ──────────────────────────
function JobCard({ kind = 'new', company, role, region, exp, edu, dday, summary, logo, tags = [], saved = false, compact = false }) {
  const klass = kind === 'new' ? 'hl-new' : kind === 'update' ? 'hl-update' : kind === 'closing' ? 'hl-closing' : '';
  const labelKlass = kind === 'new' ? 'new' : kind === 'update' ? 'update' : 'closing';
  const labelText = kind === 'new' ? 'NEW' : kind === 'update' ? 'UPDATE' : 'CLOSING';
  return (
    <div className={'card ' + klass}>
      <div className="row" style={{ justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div className="row" style={{ gap: 10, flex: 1, minWidth: 0 }}>
          <div className="logo">{logo || (company||'').slice(0,2)}</div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div className="row" style={{ gap: 6 }}>
              <span className={`label ${labelKlass}`}>{labelText}</span>
              <span className="t-xs muted">{company}</span>
            </div>
            <div className="t-lg b" style={{ marginTop: 2, lineHeight: 1.1 }}>{role}</div>
          </div>
        </div>
        <SkIcon name="bookmark" size={18} color={saved ? '#f7a72b' : '#8a8a8a'}/>
      </div>
      {!compact && (
        <>
          <div className="row t-sm muted2" style={{ gap: 12, marginTop: 8, flexWrap: 'wrap' }}>
            {region && <span>📍 {region}</span>}
            {exp && <span>🧑 {exp}</span>}
            {edu && <span>🎓 {edu}</span>}
          </div>
          {summary && <div className="t-sm muted2" style={{ marginTop: 6, lineHeight: 1.3 }}>{summary}</div>}
          {tags.length > 0 && (
            <div className="row" style={{ gap: 4, marginTop: 8, flexWrap: 'wrap' }}>
              {tags.map(t => <span key={t} className="chip sm">#{t}</span>)}
            </div>
          )}
        </>
      )}
      <div className="row" style={{ justifyContent: 'space-between', marginTop: 8, alignItems: 'center' }}>
        <span className="t-xs muted">~ {dday?.date}</span>
        <span className={`label dot outline-${labelKlass} b`}>{dday?.text}</span>
      </div>
    </div>
  );
}

// ── 손그림 별 (장식용) ─────────────────────────────
function SkStar({ size = 14, color = '#f7a72b' }) {
  return (
    <svg width={size} height={size} viewBox="0 0 20 20" fill={color} stroke="#1a1a1a" strokeWidth="1.4" strokeLinejoin="round">
      <path d="M10 2 L12.5 7.5 L18 8.2 L13.8 12 L15 18 L10 15 L5 18 L6.2 12 L2 8.2 L7.5 7.5 Z"/>
    </svg>
  );
}

Object.assign(window, { Mascot, SkIcon, SkArrow, Phone, TabBar, JobCard, SkStar });

// ─────────────────────────────────────────────────────
// v2 — 안드로이드 프레임 + 코랄 브랜드
// ─────────────────────────────────────────────────────

// 안드로이드 폰 (상태바 + 제스처 네비) — 사이드 베젤 없이 컨텐츠만
function AndroidPhone({ children, title, statusDark = false, showAppBar = true, action, leading }) {
  return (
    <div className="phone phone-android">
      {/* 안드로이드 상태바 */}
      <div className="and-status">
        <span className="t-xs b">9:41</span>
        <span className="spacer"/>
        <span className="t-xs">●●●</span>
        <span className="t-xs">📶</span>
        <span className="t-xs">▮</span>
      </div>
      {showAppBar && title !== undefined && (
        <div className="and-appbar">
          {leading}
          <span className="t-lg b">{title}</span>
          <span className="spacer"/>
          {action}
        </div>
      )}
      <div className="phone-inner">{children}</div>
      {/* 제스처 nav pill */}
      <div className="and-nav">
        <div className="and-nav-pill"/>
      </div>
    </div>
  );
}

// v2 탭바 (5탭) — 코랄 active
function TabBarV2({ active = 'home' }) {
  const nav = useNav();
  const tabs = [
    { id: 'home', label: '오늘', icon: 'home', route: 'main' },
    { id: 'search', label: '검색', icon: 'search', route: 'search' },
    { id: 'discover', label: '찾아보기', icon: 'compass', route: 'discover' },
    { id: 'fav', label: '관심기업', icon: 'heart', route: 'fav' },
    { id: 'me', label: '내정보', icon: 'user', route: 'mypage' },
  ];
  return (
    <div className="tabbar tabbar-v2">
      {tabs.map(t => (
        <div key={t.id} className={'tab' + (active === t.id ? ' on-brand' : '')}
             style={{ cursor: 'pointer' }}
             onClick={() => nav.go(t.route)}>
          <div className="ic"><SkIcon name={t.icon} size={14}/></div>
          <div>{t.label}</div>
        </div>
      ))}
    </div>
  );
}

// v2 JobCard — 코랄 강조, NEW=초록
function JobCardV2({ kind = 'new', company, role, region, exp, edu, dday, summary, logo, tags = [], saved = false, compact = false }) {
  const klass = kind === 'new' ? 'hl-new' : kind === 'update' ? 'hl-update' : kind === 'closing' ? 'hl-closing' : '';
  const labelKlass = kind === 'new' ? 'new' : kind === 'update' ? 'update' : 'closing';
  const labelText = kind === 'new' ? 'NEW' : kind === 'update' ? 'UPDATE' : 'CLOSING';
  return (
    <div className={'card ' + klass}>
      <div className="row" style={{ justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div className="row" style={{ gap: 10, flex: 1, minWidth: 0 }}>
          <div className="logo">{logo || (company||'').slice(0,2)}</div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div className="row" style={{ gap: 6 }}>
              <span className={`label ${labelKlass}`}>{labelText}</span>
              <span className="t-xs muted">{company}</span>
            </div>
            <div className="t-lg b" style={{ marginTop: 2, lineHeight: 1.1 }}>{role}</div>
          </div>
        </div>
        <SkIcon name="bookmark" size={18} color={saved ? '#ff7a5a' : '#8a8a8a'}/>
      </div>
      {!compact && (
        <>
          <div className="row t-sm muted2" style={{ gap: 12, marginTop: 8, flexWrap: 'wrap' }}>
            {region && <span>📍 {region}</span>}
            {exp && <span>🧑 {exp}</span>}
            {edu && <span>🎓 {edu}</span>}
          </div>
          {summary && <div className="t-sm muted2" style={{ marginTop: 6, lineHeight: 1.3 }}>{summary}</div>}
          {tags.length > 0 && (
            <div className="row" style={{ gap: 4, marginTop: 8, flexWrap: 'wrap' }}>
              {tags.map(t => <span key={t} className="chip sm">#{t}</span>)}
            </div>
          )}
        </>
      )}
      <div className="row" style={{ justifyContent: 'space-between', marginTop: 8, alignItems: 'center' }}>
        <span className="t-xs muted">~ {dday?.date}</span>
        <span className={`label dot outline-${labelKlass} b`}>{dday?.text}</span>
      </div>
    </div>
  );
}

Object.assign(window, { AndroidPhone, TabBarV2, JobCardV2 });

// ─────────────────────────────────────────────────────
// 네비게이션 컨텍스트 (인터랙티브 프로토타입용)
// 디자인 캔버스에서는 noop이라 영향 없음
// ─────────────────────────────────────────────────────
const NavCtx = React.createContext({ go: () => {}, back: () => {}, reset: () => {}, current: null });
const useNav = () => React.useContext(NavCtx);
window.NavCtx = NavCtx;
window.useNav = useNav;
