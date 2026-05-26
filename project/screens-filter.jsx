// screens-filter.jsx — 필터 3종

// A: 상단 칩 가로 스크롤 (선택한 사항이 메인 화면에 인라인으로 적용)
function FilterA() {
  return (
    <Phone showHeader title="오늘의 채용">
      {/* 상단 칩 스크롤 (사용자가 선택한 필터들) */}
      <div className="chip-row" style={{ borderBottom: '1.5px dashed var(--ink-3)' }}>
        <span className="chip sm on row" style={{ gap: 4 }}>
          전체 <SkIcon name="chev-d" size={12}/>
        </span>
        <span className="chip sm toss-on">개발 ×</span>
        <span className="chip sm toss-on">디자인 ×</span>
        <span className="chip sm">대기업</span>
        <span className="chip sm">서울</span>
        <span className="chip sm">D-7이내</span>
        <span className="chip sm">신입</span>
      </div>

      <div style={{ padding: '10px 14px 4px' }}>
        <div className="row" style={{ justifyContent:'space-between' }}>
          <div className="t-sm b">2개 필터 적용 · 8건</div>
          <div className="note muted2">초기화</div>
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 14px 14px' }}>
        {NEW_JOBS.slice(0,2).map((j,i) => <JobCard key={i} kind="new" {...j}/>)}
        <JobCard kind="update" compact {...UPDATE_JOBS[0]}/>
      </div>

      <TabBar active="home"/>

      {/* floating action - 더 많은 필터 */}
      <div style={{
        position:'absolute', right: 14, bottom: 70,
        padding: '8px 14px',
        background: 'var(--ink)', color: '#fff',
        borderRadius: 999, fontWeight: 700, fontSize: 13, fontFamily: 'Gaegu',
        boxShadow: '2px 2px 0 var(--toss)'
      }}>
        + 필터 추가
      </div>
    </Phone>
  );
}

// B: 풀스크린 필터 (카테고리별 그룹)
function FilterB() {
  const Sec = ({ title, children, sub }) => (
    <div style={{ marginBottom: 16 }}>
      <div className="row" style={{ justifyContent:'space-between' }}>
        <div className="t-md b">{title}</div>
        {sub && <div className="note muted">{sub}</div>}
      </div>
      <div className="row" style={{ gap: 6, marginTop: 6, flexWrap: 'wrap' }}>{children}</div>
    </div>
  );
  return (
    <Phone showHeader title="필터">
      <div style={{ padding: '12px 16px 0', display:'flex', justifyContent:'space-between', alignItems:'center' }}>
        <div className="t-xl b">필터 ✏️</div>
        <span className="note">초기화</span>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 16px 12px' }}>
        <Sec title="직군" sub="3개 선택">
          {['개발','디자인','기획','마케팅','영업','재무','인사','데이터'].map((j,i) =>
            <span key={j} className={'chip' + ([0,1,7].includes(i) ? ' toss-on' : '')}>{j}</span>
          )}
        </Sec>

        <Sec title="기업 규모">
          {['대기업','공기업','중견','중소','외국계','스타트업'].map((j,i) =>
            <span key={j} className={'chip' + ([0,1].includes(i) ? ' on' : '')}>{j}</span>
          )}
        </Sec>

        <Sec title="산업">
          {['IT/인터넷','금융','제조','유통','바이오','자동차','+더보기'].map(j => <span key={j} className="chip">{j}</span>)}
        </Sec>

        <Sec title="경력">
          {['신입','1~3년','3~5년','5년+','무관'].map((j,i) =>
            <span key={j} className={'chip' + (i===0 ? ' on' : '')}>{j}</span>
          )}
        </Sec>

        <Sec title="지역">
          {['서울','경기/인천','대전','부산','광주','대구','+'].map((j,i) =>
            <span key={j} className={'chip' + ([0,1].includes(i) ? ' on' : '')}>{j}</span>
          )}
        </Sec>

        <Sec title="마감일">
          {['오늘 마감','내일 마감','D-3 이내','D-7 이내','D-14 이내'].map(j => <span key={j} className="chip">{j}</span>)}
        </Sec>
      </div>

      <div style={{ padding: 12, borderTop: '1.8px solid var(--ink)' }}>
        <button className="btn primary block">17건 결과 보기</button>
      </div>
    </Phone>
  );
}

// C: 봇툼시트 형태 (화면 절반)
function FilterC() {
  return (
    <Phone showHeader title="오늘의 채용">
      {/* 뒷 배경 */}
      <div style={{ flex: 1, background:'var(--paper-2)', position:'relative', overflow:'hidden' }}>
        <div style={{ padding: 12, opacity: 0.4 }}>
          {[1,2,3].map(i =>
            <div key={i} className="card"><div className="t-md b">공고 카드 {i}</div></div>
          )}
        </div>
        <div style={{ position:'absolute', inset:0, background:'rgba(0,0,0,0.3)' }}/>

        <div style={{
          position:'absolute', left:0, right:0, bottom:0,
          background:'var(--paper)',
          borderTop:'2px solid var(--ink)',
          borderTopLeftRadius: 24, borderTopRightRadius: 24,
          padding:16, height: '55%', overflowY: 'auto'
        }}>
          <div style={{ width: 40, height: 4, background:'var(--ink-3)', borderRadius:2, margin:'0 auto 12px' }}/>

          <div className="row" style={{ justifyContent:'space-between' }}>
            <div className="t-lg b">빠른 필터</div>
            <SkIcon name="close" size={20}/>
          </div>

          <div className="row" style={{ marginTop: 10, gap: 6, overflowX:'auto' }}>
            <span className="chip sm toss-on">⚡ 마감임박</span>
            <span className="chip sm">📍 내 지역</span>
            <span className="chip sm">⭐ 관심기업만</span>
            <span className="chip sm">🆕 오늘 새공고</span>
          </div>

          <div className="t-md b" style={{ marginTop: 14 }}>직군</div>
          <div className="row" style={{ gap: 6, marginTop: 6, flexWrap:'wrap' }}>
            {['개발','디자인','기획','마케팅','영업'].map((j,i) =>
              <span key={j} className={'chip sm' + (i<2 ? ' on' : '')}>{j}</span>
            )}
          </div>

          <div className="t-md b" style={{ marginTop: 12 }}>마감일까지</div>
          <div className="row" style={{ gap: 6, marginTop: 6 }}>
            <span className="chip sm">D-3</span>
            <span className="chip sm on">D-7</span>
            <span className="chip sm">D-14</span>
            <span className="chip sm">전체</span>
          </div>

          <div className="row" style={{ gap: 8, marginTop: 16 }}>
            <button className="btn sm" style={{ flex: 1 }}>초기화</button>
            <button className="btn primary sm" style={{ flex: 2 }}>적용 (17건)</button>
          </div>
        </div>
      </div>
    </Phone>
  );
}

window.FilterA = FilterA;
window.FilterB = FilterB;
window.FilterC = FilterC;
