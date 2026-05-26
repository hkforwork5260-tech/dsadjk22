// screens-favorites.jsx — 즐겨찾기 기업 3종

// A: 그리드 (로고 카드)
function FavoritesA() {
  const companies = [
    ['삼성','삼성전자', 3, true],
    ['네이버','네이버', 2, true],
    ['카카오','카카오', 1, false],
    ['LG','LG에너지', 1, true],
    ['SK','SK하이닉스', 0, false],
    ['현대','현대차', 2, true],
    ['CJ','CJ', 0, false],
    ['포스','포스코', 1, false],
    ['+','추가', null, false],
  ];
  return (
    <Phone showHeader title="관심 기업">
      <div style={{ padding: '12px 16px 0' }}>
        <div className="row" style={{ justifyContent:'space-between' }}>
          <div className="t-xl b">관심 기업 8</div>
          <div className="note">+ 기업 추가</div>
        </div>
        <div className="note muted2">로고 우상단 빨간 점 = 오늘 새 공고</div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: 14 }}>
        <div style={{ display:'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 10 }}>
          {companies.map(([logo, name, badge, onTrack], i) =>
            <div key={i} className={'sk-box' + (i===8 ? ' dashed' : '')} style={{
              padding: 10, display:'flex', flexDirection:'column', alignItems:'center', gap: 4, position: 'relative'
            }}>
              <div className="logo" style={{ width: 44, height: 44, fontSize: 14 }}>{logo}</div>
              <div className="t-sm b center" style={{ lineHeight: 1.1 }}>{name}</div>
              <div className="t-xs muted">{badge != null ? `공고 ${badge}` : ''}</div>
              {badge > 0 && (
                <div style={{
                  position:'absolute', top:6, right:6,
                  width: 18, height: 18, borderRadius: 999,
                  background:'var(--closing)', color:'#fff',
                  fontSize: 10, fontWeight: 700,
                  display:'flex', alignItems:'center', justifyContent:'center',
                  border: '1.5px solid var(--ink)'
                }}>{badge}</div>
              )}
              {onTrack && <span className="label sm" style={{ fontSize: 9, padding: '0 4px', background: 'var(--good-soft)', borderColor:'var(--good)', color:'var(--good)' }}>알림 ON</span>}
            </div>
          )}
        </div>
      </div>
      <TabBar active="fav"/>
    </Phone>
  );
}

// B: 리스트 + 검색 + 알파벳/가나다 인덱스
function FavoritesB() {
  return (
    <Phone showHeader title="관심 기업">
      <div style={{ padding: 14 }}>
        <div className="sk-box row" style={{ padding: '8px 12px', gap: 8 }}>
          <SkIcon name="search" size={16} color="#8a8a8a"/>
          <span className="t-sm muted">기업 검색...</span>
        </div>
        <div className="row" style={{ justifyContent:'space-between', marginTop: 12, alignItems:'center' }}>
          <div className="t-md b">12개 기업</div>
          <div className="row" style={{ gap: 6 }}>
            <span className="chip sm on">전체</span>
            <span className="chip sm">새공고만</span>
          </div>
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 14px 12px' }}>
        {/* 그룹: ㄱ */}
        <div className="note muted" style={{ marginBottom: 4 }}>ㄱ</div>
        {[
          ['CJ','CJ제일제당', 0, false],
          ['GS','GS칼텍스', 1, true],
        ].map(([l,n,b,on],i) => (
          <div key={i} className="row sk-box" style={{ padding: 10, marginBottom: 6, gap: 10 }}>
            <div className="logo sm">{l}</div>
            <div style={{ flex:1 }}>
              <div className="t-sm b">{n}</div>
              {b > 0 && <span className="label outline-new sm" style={{ fontSize: 10 }}>NEW {b}</span>}
            </div>
            <span className={'toggle' + (on ? ' on' : '')}/>
          </div>
        ))}

        <div className="note muted" style={{ marginTop: 8, marginBottom: 4 }}>ㄴ</div>
        <div className="row sk-box" style={{ padding: 10, marginBottom: 6, gap: 10 }}>
          <div className="logo sm">N</div>
          <div style={{ flex:1 }}>
            <div className="t-sm b">네이버</div>
            <span className="label outline-new sm" style={{ fontSize: 10 }}>NEW 2</span>
          </div>
          <span className="toggle on"/>
        </div>

        <div className="note muted" style={{ marginTop: 8, marginBottom: 4 }}>ㄷ</div>
        <div className="row sk-box" style={{ padding: 10, marginBottom: 6, gap: 10 }}>
          <div className="logo sm">D</div>
          <div style={{ flex:1 }}>
            <div className="t-sm b">두산</div>
            <span className="t-xs muted">5/22 공고 없음</span>
          </div>
          <span className="toggle"/>
        </div>

        <div className="note muted" style={{ marginTop: 8, marginBottom: 4 }}>ㄹ</div>
        <div className="row sk-box" style={{ padding: 10, marginBottom: 6, gap: 10 }}>
          <div className="logo sm">LG</div>
          <div style={{ flex:1 }}>
            <div className="t-sm b">LG에너지솔루션</div>
            <span className="label outline-new sm" style={{ fontSize: 10 }}>NEW 1</span>
          </div>
          <span className="toggle on"/>
        </div>
      </div>

      {/* 사이드 가나다 인덱스 */}
      <div style={{
        position:'absolute', right: 4, top: 100,
        display:'flex', flexDirection:'column', gap: 2,
        fontSize: 10, color: 'var(--ink-3)', fontWeight: 700
      }}>
        {['ㄱ','ㄴ','ㄷ','ㄹ','ㅁ','ㅂ','ㅅ','ㅇ','ㅈ','ㅊ','A','S'].map(c => <span key={c}>{c}</span>)}
      </div>

      <TabBar active="fav"/>
    </Phone>
  );
}

// C: 카테고리 탭 (대기업 / 공기업 / 스타트업)
function FavoritesC() {
  return (
    <Phone showHeader title="관심 기업">
      <div style={{ padding: '12px 14px 0' }}>
        <div className="row" style={{ gap: 10, alignItems:'flex-start' }}>
          <Mascot size={50} expression="default"/>
          <div style={{ flex: 1 }}>
            <div className="t-lg b">담아둔 기업 8곳</div>
            <div className="note muted2">오늘 새 공고 3건 있어요!</div>
          </div>
        </div>
      </div>

      <div className="row" style={{ borderBottom:'1.5px solid var(--ink-3)', padding: '12px 14px 0', gap: 12 }}>
        {[
          ['전체 8', true],
          ['대기업 5', false],
          ['공기업 2', false],
          ['스타트업 1', false]
        ].map(([t,on]) =>
          <div key={t} className="t-sm b" style={{
            padding: '6px 4px',
            borderBottom: on ? '2.5px solid var(--toss)' : '2.5px solid transparent',
            color: on ? 'var(--toss)' : 'var(--ink-3)',
            marginBottom: -1
          }}>{t}</div>
        )}
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: 14 }}>
        <div className="note muted" style={{ marginBottom: 4 }}>📌 새 공고 있는 곳</div>
        {[
          ['삼성','삼성전자','반도체·IT', 3],
          ['네이버','네이버','IT·플랫폼', 2],
          ['LG','LG에너지솔루션','배터리', 1],
        ].map(([l,n,t,b],i) => (
          <div key={i} className="card hl-new" style={{ padding: 10, marginBottom: 8 }}>
            <div className="row" style={{ gap: 10 }}>
              <div className="logo">{l}</div>
              <div style={{ flex: 1 }}>
                <div className="t-md b">{n}</div>
                <div className="t-xs muted">{t}</div>
              </div>
              <span className="label new">+{b}</span>
            </div>
          </div>
        ))}

        <div className="note muted" style={{ marginBottom: 4, marginTop: 8 }}>조용한 기업</div>
        {[
          ['SK','SK하이닉스','반도체'],
          ['현대','현대자동차','자동차'],
        ].map(([l,n,t],i) => (
          <div key={i} className="row sk-box" style={{ padding: 10, marginBottom: 6, gap: 10, opacity: 0.7 }}>
            <div className="logo sm">{l}</div>
            <div style={{ flex: 1 }}>
              <div className="t-sm b">{n}</div>
              <div className="t-xs muted">{t}</div>
            </div>
            <span className="t-xs muted">조용해요</span>
          </div>
        ))}
      </div>

      <TabBar active="fav"/>
    </Phone>
  );
}

window.FavoritesA = FavoritesA;
window.FavoritesB = FavoritesB;
window.FavoritesC = FavoritesC;
