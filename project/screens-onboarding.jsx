// screens-onboarding.jsx — 온보딩 3종

// A: 3-step 페이지형 (직군 → 관심기업 → 알림시간)
function OnboardingA() {
  const jobs = ['개발', '디자인', '마케팅', '기획', '영업', '재무', '인사', 'R&D'];
  return (
    <Phone showHeader={true} title="채용알리미">
      <div style={{ padding: 16, flex: 1, display: 'flex', flexDirection: 'column' }}>
        {/* progress */}
        <div className="row" style={{ gap: 4, marginBottom: 16 }}>
          <span className="dot on"/><span className="dot"/><span className="dot"/>
        </div>
        <Mascot size={70} expression="wave" />
        <div className="t-2xl b" style={{ marginTop: 8, lineHeight: 1.1 }}>
          어떤 일을<br/>찾고 계세요?
        </div>
        <div className="note" style={{ marginTop: 6 }}>관심 직군을 골라주세요 (복수선택)</div>

        <div style={{ marginTop: 16, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
          {jobs.map((j, i) => (
            <div key={j} className={'chip' + ([0,3,5].includes(i) ? ' toss-on' : '')} style={{ justifyContent:'center', padding: '12px 0' }}>
              {j}
            </div>
          ))}
        </div>

        <div className="spacer"/>
        <button className="btn primary block">다음으로 →</button>
        <div className="center muted t-sm" style={{ marginTop: 6 }}>나중에 설정하기</div>
      </div>
    </Phone>
  );
}

// B: 스크롤 단일 페이지 체크리스트
function OnboardingB() {
  return (
    <Phone showHeader={true} title="시작하기">
      <div style={{ padding: 16, overflowY: 'auto', flex: 1 }}>
        <div className="row" style={{ gap: 10 }}>
          <Mascot size={56} expression="happy"/>
          <div>
            <div className="t-xl b">반가워요!</div>
            <div className="note">3분이면 끝나요 ✏️</div>
          </div>
        </div>

        <div className="sk-box fill-toss" style={{ marginTop: 14 }}>
          <div className="t-md b">① 관심 직군</div>
          <div className="row" style={{ gap: 6, marginTop: 8, flexWrap: 'wrap' }}>
            {['개발','디자인','기획','마케팅','영업'].map((j,i) =>
              <span key={j} className={'chip sm' + (i<2 ? ' on' : '')}>{j}</span>
            )}
            <span className="chip sm">+ 더보기</span>
          </div>
        </div>

        <div className="sk-box" style={{ marginTop: 12 }}>
          <div className="t-md b">② 기업 규모</div>
          <div className="row" style={{ gap: 6, marginTop: 8, flexWrap:'wrap' }}>
            {['대기업','중견기업','중소기업','공기업','외국계'].map((j,i) =>
              <span key={j} className={'chip sm' + ([0,3].includes(i) ? ' on' : '')}>{j}</span>
            )}
          </div>
        </div>

        <div className="sk-box" style={{ marginTop: 12 }}>
          <div className="t-md b">③ 산업군</div>
          <div className="row" style={{ gap: 6, marginTop: 8, flexWrap:'wrap' }}>
            {['IT','금융','제조','유통','바이오'].map(j => <span key={j} className="chip sm">{j}</span>)}
          </div>
        </div>

        <div className="sk-box" style={{ marginTop: 12 }}>
          <div className="t-md b">④ 매일 알림 받을 시간</div>
          <div className="row" style={{ gap: 8, marginTop: 8 }}>
            <span className="chip on">아침 9시</span>
            <span className="chip">저녁 6시</span>
            <span className="chip">밤 10시</span>
          </div>
        </div>

        <button className="btn primary block" style={{ marginTop: 16 }}>시작하기</button>
      </div>
    </Phone>
  );
}

// C: 카드 스와이프 (Tinder-style 관심 기업 선택)
function OnboardingC() {
  return (
    <Phone showHeader={true} title="관심 기업 고르기">
      <div style={{ padding: 16, flex: 1, display: 'flex', flexDirection: 'column' }}>
        <div className="row" style={{ justifyContent: 'space-between', alignItems:'center' }}>
          <div className="t-md b">8 / 40 선택됨</div>
          <span className="chip sm">건너뛰기</span>
        </div>
        <div className="bar" style={{ marginTop: 8 }}><i style={{ width: '20%' }}/></div>

        {/* 카드 스택 */}
        <div style={{ position: 'relative', flex: 1, marginTop: 18, marginBottom: 16 }}>
          <div className="sk-box" style={{
            position:'absolute', inset: '14px 30px 0 30px',
            transform: 'rotate(3deg)', background: 'var(--paper-2)', height: 'calc(100% - 20px)'
          }}/>
          <div className="sk-box" style={{
            position:'absolute', inset: '7px 18px 0 18px',
            transform: 'rotate(-2deg)', background: 'var(--paper)', height: 'calc(100% - 14px)'
          }}/>
          <div className="sk-box thick" style={{
            position:'absolute', inset: 0, padding: 16, display:'flex', flexDirection:'column'
          }}>
            <div className="logo lg" style={{ alignSelf: 'center', marginTop: 12 }}>삼성</div>
            <div className="t-xl b center" style={{ marginTop: 12 }}>삼성전자</div>
            <div className="t-sm muted center">대기업 · IT/제조</div>
            <div className="div"/>
            <div className="t-sm">최근 1개월</div>
            <div className="t-2xl b" style={{ color: 'var(--toss)' }}>공고 12건</div>
            <div className="note" style={{ marginTop: 4 }}>주로 개발·디자인 분야</div>
            <div className="spacer"/>
            <div className="row" style={{ justifyContent: 'space-around' }}>
              <div className="sk-box" style={{ width: 50, height: 50, borderRadius: 999, display:'flex', alignItems:'center', justifyContent:'center', borderColor:'var(--closing)' }}>
                <SkIcon name="close" size={22} color="var(--closing)"/>
              </div>
              <div className="sk-box" style={{ width: 50, height: 50, borderRadius: 999, display:'flex', alignItems:'center', justifyContent:'center', borderColor:'var(--toss)', background:'var(--toss)' }}>
                <SkIcon name="heart" size={22} color="#fff"/>
              </div>
            </div>
          </div>
        </div>

        <div className="note center muted2">← 스와이프해서 관심 기업 고르기 →</div>
      </div>
    </Phone>
  );
}

window.OnboardingA = OnboardingA;
window.OnboardingB = OnboardingB;
window.OnboardingC = OnboardingC;
