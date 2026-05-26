// shared-hifi.jsx — 하이파이 공용 컴포넌트 (듀오링고풍)

// 폰 프레임 (안드로이드 hi-fi)
function HiFiPhone({ children, title, showAppBar = true, leading, action, padTop = false, lockScreen = false }) {
  return (
    <div className={'hifi'}>
      <div className={'hifi-phone' + (lockScreen ? ' h-lock' : '')}>
        {/* 상태바 */}
        <div className="hifi-status" style={lockScreen ? { color: '#fff' } : {}}>
          <span>9:41</span>
          <span style={{ flex: 1 }}/>
          <span className="right">
            <span style={{ fontSize: 12 }}>●●●</span>
            <span style={{ fontSize: 12 }}>📶</span>
            <span style={{
              display: 'inline-block', width: 22, height: 11, borderRadius: 2,
              border: `1.4px solid ${lockScreen ? '#fff' : '#3c3c3c'}`,
              position: 'relative'
            }}>
              <span style={{
                position:'absolute', left: 1, top: 1, bottom: 1, width: '80%',
                background: lockScreen ? '#fff' : '#3c3c3c', borderRadius: 1
              }}/>
            </span>
          </span>
        </div>

        {showAppBar && title !== undefined && (
          <div className="hifi-appbar">
            {leading}
            <div className="title">{title}</div>
            {action}
          </div>
        )}

        <div className="hifi-phone-inner" style={{
          background: lockScreen ? 'transparent' : undefined,
          paddingTop: padTop ? 12 : 0
        }}>
          {children}
        </div>

        <div className="hifi-nav">
          <div className="hifi-nav-pill" style={{ background: lockScreen ? '#fff' : '#1a1a1a', opacity: lockScreen ? 0.8 : 1 }}/>
        </div>
      </div>
    </div>
  );
}

// 둥근 아이콘 버튼
function HiFiIconBtn({ name, size = 20, onClick, ariaLabel }) {
  return (
    <div className="icon-btn" onClick={onClick} aria-label={ariaLabel} role="button">
      <SkIcon name={name} size={size} color="#3c3c3c"/>
    </div>
  );
}

// 탭바
function HiFiTabBar({ active = 'home' }) {
  const nav = useNav();
  const tabs = [
    { id: 'home', label: '오늘', icon: 'home', route: 'h_main' },
    { id: 'search', label: '검색', icon: 'search', route: 'h_search' },
    { id: 'discover', label: '찾아보기', icon: 'compass', route: 'h_discover' },
    { id: 'fav', label: '관심기업', icon: 'heart', route: 'h_fav' },
    { id: 'me', label: '내정보', icon: 'user', route: 'h_mypage' },
  ];
  return (
    <div className="h-tabbar">
      {tabs.map(t => (
        <div key={t.id} className={'tab' + (active === t.id ? ' on' : '')}
             onClick={() => nav.go(t.route)}>
          <div className="ico">
            <SkIcon name={t.icon} size={22} color="currentColor" strokeWidth={2.2}/>
          </div>
          <div>{t.label}</div>
        </div>
      ))}
    </div>
  );
}

// 공고 카드
function HiFiJobCard({ kind = 'new', company, role, logo, dday, dateText, onClick }) {
  const labelKlass = kind === 'new' ? 'new' : kind === 'update' ? 'update' : 'closing';
  const labelText = kind === 'new' ? 'NEW' : kind === 'update' ? 'UPDATE' : 'CLOSING';
  return (
    <div className="h-job-card" onClick={onClick}>
      <div className="h-logo">{logo}</div>
      <div className="h-grow">
        <div className="h-row" style={{ gap: 6 }}>
          <span className={`h-label ${labelKlass}`}>{labelText}</span>
          <span className="h-body-2" style={{ fontSize: 12, fontWeight: 600 }}>{company}</span>
        </div>
        <div className="h-h2" style={{ marginTop: 4, overflow:'hidden', textOverflow:'ellipsis', whiteSpace:'nowrap' }}>{role}</div>
      </div>
      <div style={{ textAlign: 'right', flexShrink: 0 }}>
        <div className="h-mono-num" style={{ fontSize: 16, color: `var(--h-${labelKlass})` }}>{dday}</div>
        <div className="h-body-2" style={{ fontSize: 11 }}>{dateText}</div>
      </div>
    </div>
  );
}

// 듀오링고풍 마스코트 컨테이너 (배경 강조)
function HiFiMascot({ size = 100, expression = 'happy', glow = false }) {
  return (
    <div style={{
      width: size + 24, height: size + 24,
      borderRadius: '50%',
      background: glow ? 'var(--h-brand-soft)' : 'transparent',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      position: 'relative'
    }}>
      <Mascot size={size} expression={expression}/>
    </div>
  );
}

Object.assign(window, { HiFiPhone, HiFiIconBtn, HiFiTabBar, HiFiJobCard, HiFiMascot });
