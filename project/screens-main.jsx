// screens-main.jsx — 메인 피드 3종 (신규/변경/마감)

// 더미 데이터
const NEW_JOBS = [
  { company: '삼성전자', role: '2026 상반기 신입공채', region: '서울/수원', exp: '신입', edu: '학사이상', dday: { date: '06.15', text: 'D-24' }, summary: 'DS/메모리/파운드리·DX 부문 통합 모집', tags:['반도체','대기업','공채'], logo: '삼성' },
  { company: '네이버', role: '신입 백엔드 개발자', region: '판교', exp: '신입', edu: '학사', dday: { date: '06.10', text: 'D-19' }, summary: 'Java/Kotlin · 대규모 서비스 트래픽 처리', tags:['IT','개발'], logo: 'N' },
  { company: 'LG에너지솔루션', role: '연구개발(R&D) 신입', region: '대전', exp: '신입', edu: '석사이상', dday: { date: '06.07', text: 'D-16' }, summary: '배터리 셀·소재·공정 R&D 통합 모집', tags:['배터리','R&D'], logo: 'LG' },
];
const UPDATE_JOBS = [
  { company: '카카오', role: '경력 프론트엔드 개발자', region: '판교', exp: '3년+', edu: '무관', dday: { date: '05.28', text: 'D-6' }, summary: '모집 분야 확대 · Web Platform 팀 추가', tags:['IT','경력','React'], logo: '카카오' },
  { company: 'SK하이닉스', role: '2026 신입사원 채용', region: '이천/청주', exp: '신입', edu: '학사이상', dday: { date: '06.02', text: 'D-11' }, summary: '서류 마감일 6/2 → 6/5로 변경됨', tags:['반도체'], logo: 'SK' },
];
const CLOSING_JOBS = [
  { company: '현대자동차', role: '신입사원 일반공채', region: '서울/울산', exp: '신입', edu: '학사', dday: { date: '내일', text: 'D-1' }, summary: '내일 18시 서류 마감', tags:['자동차','대기업'], logo: '현대' },
  { company: 'CJ', role: 'CJ제일제당 신입공채', region: '서울', exp: '신입', edu: '학사', dday: { date: '오늘', text: 'D-0' }, summary: '오늘 23시 59분 마감', tags:['식품','FMCG'], logo: 'CJ' },
];

// A: 3섹션 가로 스와이프 (페이지 닷)
function MainA() {
  return (
    <Phone showHeader title="오늘의 채용">
      <div style={{ padding: '12px 16px 4px' }}>
        <div className="row" style={{ justifyContent:'space-between', alignItems:'flex-start' }}>
          <div>
            <div className="note muted2">5월 22일 목요일</div>
            <div className="t-xl b" style={{ lineHeight: 1.1 }}>오늘 새 공고 <span style={{ color:'var(--toss)' }}>17건</span></div>
          </div>
          <div style={{ position:'relative' }}>
            <Mascot size={50} expression="happy"/>
            <div style={{
              position: 'absolute', top: -4, right: -4,
              width: 22, height: 22, borderRadius: 999,
              background: 'var(--closing)', color: '#fff',
              fontSize: 12, fontWeight: 700,
              display:'flex', alignItems:'center', justifyContent:'center',
              border: '1.6px solid #1a1a1a'
            }}>17</div>
          </div>
        </div>
      </div>

      {/* 페이지 닷 */}
      <div className="row" style={{ justifyContent:'center', gap: 6, marginBottom: 6 }}>
        <span className="chip sm toss-on" style={{ background:'var(--new)', borderColor:'var(--new)' }}>● NEW 17</span>
        <span className="chip sm">UPDATE 4</span>
        <span className="chip sm">CLOSING 3</span>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 16px 12px' }}>
        {NEW_JOBS.map((j, i) => <JobCard key={i} kind="new" {...j}/>)}
        <div className="note center muted2" style={{ marginTop: 8 }}>← 옆으로 넘기면 UPDATE 섹션 →</div>
      </div>
      <TabBar active="home"/>
    </Phone>
  );
}

// B: Segmented control + 통합 리스트
function MainB() {
  return (
    <Phone showHeader title="오늘의 채용">
      <div style={{ padding: '10px 16px 0' }}>
        <div className="row" style={{ gap: 8, alignItems:'center' }}>
          <Mascot size={40} expression="default"/>
          <div className="t-md b" style={{ flex: 1 }}>오늘 새 공고 <span className="sk-uline">17건</span> 떴어요!</div>
        </div>
      </div>

      {/* segmented control */}
      <div style={{ padding: '10px 16px 0' }}>
        <div className="sk-box" style={{ padding: 3, display:'flex', borderRadius: 10, background:'var(--paper-2)' }}>
          <div className="t-sm b center" style={{ flex:1, padding:'7px 0', background:'var(--paper)', border:'1.6px solid var(--ink)', borderRadius: 7, color:'var(--new)' }}>NEW · 17</div>
          <div className="t-sm b center" style={{ flex:1, padding:'7px 0', color:'var(--ink-3)' }}>UPDATE · 4</div>
          <div className="t-sm b center" style={{ flex:1, padding:'7px 0', color:'var(--ink-3)' }}>CLOSING · 3</div>
        </div>
      </div>

      {/* 칩 필터 */}
      <div className="chip-row">
        <span className="chip sm on">전체</span>
        <span className="chip sm">개발</span>
        <span className="chip sm">디자인</span>
        <span className="chip sm">대기업</span>
        <span className="chip sm">서울</span>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 16px 12px' }}>
        {NEW_JOBS.map((j, i) => <JobCard key={i} kind="new" {...j}/>)}
      </div>
      <TabBar active="home"/>
    </Phone>
  );
}

// C: 상단 마스코트 카드 + 한 섹션씩 (오늘은 NEW가 강조)
function MainC() {
  return (
    <Phone showHeader title="채용알리미">
      <div style={{ flex: 1, overflowY: 'auto' }}>
        {/* 마스코트 큰 카드 */}
        <div className="sk-box fill-toss" style={{ margin: 12, padding: 14, borderRadius: 16 }}>
          <div className="row" style={{ alignItems:'flex-start', gap: 10 }}>
            <Mascot size={64} expression="wow"/>
            <div style={{ flex: 1 }}>
              <div className="note muted2">5/22 (목)</div>
              <div className="t-xl b" style={{ lineHeight:1.15 }}>
                <span className="sk-uline">NEW 17건</span><br/>
                + UPDATE 4건, 마감 3건
              </div>
            </div>
          </div>
          <div className="row" style={{ gap: 6, marginTop: 10 }}>
            <span className="label new">🔥 오늘 핵심: 삼성·네이버·LG</span>
          </div>
        </div>

        {/* NEW 섹션 헤더 */}
        <div className="row" style={{ padding: '4px 16px', justifyContent:'space-between' }}>
          <div className="t-lg b row" style={{ gap: 6 }}>
            <span className="label new">NEW</span> 오늘 새로 뜬 공고
          </div>
          <span className="note muted">전체보기 →</span>
        </div>
        <div style={{ padding: '4px 16px' }}>
          {NEW_JOBS.slice(0,2).map((j,i) => <JobCard key={i} kind="new" {...j}/>)}
        </div>

        {/* UPDATE 섹션 */}
        <div className="row" style={{ padding: '12px 16px 4px', justifyContent:'space-between' }}>
          <div className="t-lg b row" style={{ gap: 6 }}>
            <span className="label update">UPDATE</span> 변경된 공고
          </div>
          <span className="note muted">전체보기 →</span>
        </div>
        <div style={{ padding: '4px 16px' }}>
          <JobCard kind="update" compact {...UPDATE_JOBS[0]}/>
        </div>

        {/* CLOSING */}
        <div className="row" style={{ padding: '12px 16px 4px', justifyContent:'space-between' }}>
          <div className="t-lg b row" style={{ gap: 6 }}>
            <span className="label closing">CLOSING</span> 곧 마감
          </div>
          <span className="note muted">전체보기 →</span>
        </div>
        <div style={{ padding: '4px 16px 14px' }}>
          <JobCard kind="closing" compact {...CLOSING_JOBS[0]}/>
        </div>
      </div>
      <TabBar active="home"/>
    </Phone>
  );
}

window.MainA = MainA;
window.MainB = MainB;
window.MainC = MainC;
window.NEW_JOBS = NEW_JOBS;
window.UPDATE_JOBS = UPDATE_JOBS;
window.CLOSING_JOBS = CLOSING_JOBS;
