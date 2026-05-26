// screens-search.jsx — 검색 3종

// A: 최근/추천 키워드 + 검색바
function SearchA() {
  return (
    <Phone showHeader title="검색">
      <div style={{ padding: 14 }}>
        <div className="sk-box row" style={{ padding: '10px 12px', gap: 8 }}>
          <SkIcon name="search" size={18} color="#1a1a1a"/>
          <span className="t-md muted">기업명·직무·키워드</span>
          <span className="spacer"/>
          <SkIcon name="mic" size={18} color="#8a8a8a"/>
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 14px 14px' }}>
        <div className="row" style={{ justifyContent:'space-between' }}>
          <div className="t-md b">최근 검색</div>
          <span className="note muted">전체삭제</span>
        </div>
        <div className="row" style={{ gap: 6, marginTop: 8, flexWrap:'wrap' }}>
          {['삼성전자','UX 디자이너','대전','신입 개발','LG'].map(k =>
            <span key={k} className="chip sm row" style={{ gap: 4 }}>{k} <SkIcon name="close" size={10} color="#8a8a8a"/></span>
          )}
        </div>

        <div className="t-md b" style={{ marginTop: 18 }}>🔥 지금 인기 검색어</div>
        <div className="col" style={{ gap: 6, marginTop: 8 }}>
          {[
            ['1','삼성전자 상반기 공채','up'],
            ['2','네이버 백엔드','up'],
            ['3','LG에너지 R&D','new'],
            ['4','현대차','same'],
            ['5','카카오 경력','down'],
          ].map(([n,k,t]) =>
            <div key={n} className="row" style={{ padding: '4px 0' }}>
              <span className="t-md b" style={{ width: 22, color: 'var(--toss)' }}>{n}</span>
              <span className="t-md" style={{ flex: 1 }}>{k}</span>
              <span className="t-xs muted">{t === 'up' ? '▲' : t === 'down' ? '▼' : t === 'new' ? 'NEW' : '-'}</span>
            </div>
          )}
        </div>

        <div className="t-md b" style={{ marginTop: 18 }}>추천 키워드</div>
        <div className="row" style={{ gap: 6, marginTop: 8, flexWrap:'wrap' }}>
          {['#신입공채','#수시채용','#재택가능','#복지좋은','#성과급','#연봉상위','#스타트업'].map(k =>
            <span key={k} className="chip sm">{k}</span>
          )}
        </div>
      </div>
      <TabBar active="search"/>
    </Phone>
  );
}

// B: 자동완성 풍 결과 미리보기
function SearchB() {
  return (
    <Phone showHeader title="검색">
      <div style={{ padding: 14 }}>
        <div className="sk-box row" style={{ padding: '10px 12px', gap: 8 }}>
          <SkIcon name="search" size={18}/>
          <span className="t-md b">삼성</span>
          <span style={{ width: 1, height: 16, background:'var(--toss)' }}/>
          <span className="spacer"/>
          <SkIcon name="close" size={16} color="#8a8a8a"/>
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 14px 14px' }}>
        <div className="t-sm b muted">기업</div>
        <div className="col" style={{ gap: 4, marginTop: 6 }}>
          {[
            ['삼성','삼성전자', 3],
            ['삼성','삼성SDS', 1],
            ['삼성','삼성바이오로직스', 0],
            ['삼성','삼성생명', 0],
          ].map(([l,n,b],i) =>
            <div key={i} className="row" style={{ padding: 8, borderBottom: '1.5px dashed var(--ink-3)', gap: 10 }}>
              <div className="logo sm">{l}</div>
              <div style={{ flex: 1 }}>
                <div className="t-sm"><b className="sk-uline">삼성</b>{n.slice(2)}</div>
              </div>
              {b > 0 && <span className="label outline-new" style={{ fontSize: 10 }}>+{b}</span>}
              {b === 0 && <span className="t-xs muted">관심추가</span>}
            </div>
          )}
        </div>

        <div className="t-sm b muted" style={{ marginTop: 16 }}>오늘 공고</div>
        <div className="col" style={{ gap: 6, marginTop: 6 }}>
          <JobCard kind="new" compact {...NEW_JOBS[0]}/>
        </div>

        <div className="t-sm b muted" style={{ marginTop: 16 }}>연관 키워드</div>
        <div className="row" style={{ gap: 6, marginTop: 6, flexWrap:'wrap' }}>
          {['삼성전자 DS','삼성 신입','삼성 SDS','삼성디스플레이'].map(k =>
            <span key={k} className="chip sm">{k}</span>
          )}
        </div>
      </div>
      <TabBar active="search"/>
    </Phone>
  );
}

// C: 음성 검색 강조 + 빠른 진입
function SearchC() {
  return (
    <Phone showHeader title="검색">
      <div style={{ flex: 1, overflowY: 'auto', padding: 14, display:'flex', flexDirection:'column' }}>
        <div className="t-2xl b" style={{ lineHeight: 1.1 }}>
          뭐 찾으세요? 🔍
        </div>
        <div className="note muted2">기업, 직무, 지역, 키워드 다 가능</div>

        <div className="sk-box thick row" style={{ padding: '12px 14px', gap: 10, marginTop: 12 }}>
          <SkIcon name="search" size={20}/>
          <span className="t-md muted" style={{ flex:1 }}>예) "판교 신입 개발자"</span>
        </div>

        {/* 음성 검색 큰 버튼 */}
        <div className="center" style={{ marginTop: 18 }}>
          <div className="sk-box" style={{
            width: 90, height: 90, borderRadius: 999,
            background:'var(--toss)', borderColor:'var(--toss)',
            display:'inline-flex', alignItems:'center', justifyContent:'center',
            boxShadow: '3px 3px 0 var(--ink)'
          }}>
            <SkIcon name="mic" size={36} color="#fff" strokeWidth={2.2}/>
          </div>
          <div className="note" style={{ marginTop: 8 }}>탭해서 말로 검색</div>
        </div>

        <div className="div"/>

        <div className="t-md b">빠른 카테고리</div>
        <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap: 8, marginTop: 8 }}>
          {[
            ['🆕','오늘 새공고'],
            ['⏰','마감 임박'],
            ['💼','신입공채'],
            ['🌟','관심기업만'],
          ].map(([e,t]) =>
            <div key={t} className="sk-box row" style={{ padding: 10, gap: 8 }}>
              <span style={{ fontSize: 20 }}>{e}</span>
              <span className="t-sm b">{t}</span>
            </div>
          )}
        </div>

        <div className="t-md b" style={{ marginTop: 14 }}>인기</div>
        <div className="row" style={{ gap: 6, marginTop: 6, flexWrap:'wrap' }}>
          {['삼성전자','네이버','LG에너지','카카오','현대'].map((k,i) =>
            <span key={k} className={'chip sm' + (i<2 ? ' toss-on' : '')}>{k}</span>
          )}
        </div>
      </div>
      <TabBar active="search"/>
    </Phone>
  );
}

window.SearchA = SearchA;
window.SearchB = SearchB;
window.SearchC = SearchC;
