// screens-v2.jsx — 선택안 통합 흐름 (안드로이드, 코랄 브랜드)

// ── 1. 온보딩 STEP 1 — 직군 선택 ──────────────────────────
function V2_Onb1() {
  const nav = useNav();
  const jobs = [
    '기획·전략', '마케팅·홍보·조사', '회계·세무·재무',
    '인사·노무·HRD', '총무·법무·사무', 'IT개발·데이터',
    '디자인', '영업·판매·무역', '고객상담·TM',
    '구매·자재·물류', '상품기획·MD', '운전·운송·배송',
    '서비스', '생산', '건설·건축',
    '의료', '연구·R&D', '교육',
    '미디어·문화·스포츠', '금융·보험', '공공·복지'
  ];
  const selected = [2, 5, 6];
  return (
    <AndroidPhone showAppBar={false}>
      <div style={{ padding: '14px 18px 16px', flex: 1, display: 'flex', flexDirection: 'column' }}>
        <div className="row" style={{ gap: 4, marginBottom: 10 }}>
          <span className="dot on" style={{ background:'var(--brand)' }}/>
          <span className="dot"/><span className="dot"/><span className="dot"/>
        </div>
        <div className="row" style={{ gap: 10, alignItems:'center' }}>
          <Mascot size={56} expression="wave"/>
          <div style={{ flex: 1 }}>
            <div className="t-xl b" style={{ lineHeight: 1.1 }}>
              어떤 일을 찾고 있어?
            </div>
            <div className="note muted2" style={{ marginTop: 2 }}>복수 선택 OK</div>
          </div>
        </div>

        <div style={{ flex: 1, overflowY: 'auto', marginTop: 12, paddingRight: 2 }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 6 }}>
            {jobs.map((j, i) => (
              <div key={j} className={'chip sm' + (selected.includes(i) ? ' on-brand' : '')}
                   style={{ justifyContent:'center', padding: '10px 6px', fontSize: 12, textAlign:'center', lineHeight:1.15 }}>
                {j}
              </div>
            ))}
          </div>
        </div>

        <button className="btn brand block" style={{ marginTop: 10 }} onClick={() => nav.go('onb2')}>다음 (3개 선택됨) →</button>
        <div className="center muted note" style={{ marginTop: 6, fontSize: 14, cursor:'pointer' }} onClick={() => nav.go('main')}>나중에 설정하기</div>
      </div>
    </AndroidPhone>
  );
}

// ── 2. 온보딩 STEP 2 — 기업 규모/산업 ──────────────────────
function V2_Onb2() {
  const nav = useNav();
  return (
    <AndroidPhone showAppBar={false}>
      <div style={{ padding: '20px 18px 16px', flex: 1, display: 'flex', flexDirection: 'column' }}>
        <div className="row" style={{ gap: 4, marginBottom: 14 }}>
          <span className="dot" style={{ background:'var(--brand)', width:16, borderRadius:3 }}/>
          <span className="dot on" style={{ background:'var(--brand)' }}/>
          <span className="dot"/><span className="dot"/>
        </div>
        <Mascot size={70} expression="default"/>
        <div className="t-2xl b" style={{ marginTop: 8, lineHeight: 1.1 }}>
          어떤 회사들이<br/>궁금해?
        </div>

        <div className="t-md b muted2" style={{ marginTop: 18 }}>기업 규모</div>
        <div className="row" style={{ gap: 6, marginTop: 8, flexWrap:'wrap' }}>
          {['대기업','공기업','중견기업','중소기업','외국계','스타트업'].map((j,i) =>
            <span key={j} className={'chip' + ([0,1].includes(i) ? ' on-brand' : '')}>{j}</span>
          )}
        </div>

        <div className="t-md b muted2" style={{ marginTop: 18 }}>산업군</div>
        <div className="row" style={{ gap: 6, marginTop: 8, flexWrap:'wrap' }}>
          {['IT/플랫폼','반도체','금융','자동차','바이오','화학/소재','유통','+'].map((j,i) =>
            <span key={j} className={'chip' + ([0,1].includes(i) ? ' on-brand' : '')}>{j}</span>
          )}
        </div>

        <div className="spacer"/>
        <div className="row" style={{ gap: 8 }}>
          <button className="btn" style={{ flex: 1 }} onClick={() => nav.back()}>← 이전</button>
          <button className="btn brand" style={{ flex: 2 }} onClick={() => nav.go('onb3')}>다음 →</button>
        </div>
      </div>
    </AndroidPhone>
  );
}

// ── 3. 온보딩 STEP 3 — 회사 스와이프 (Reels) ────────
const ONB_DECK = [
  { logo:'삼성', name:'삼성전자', sector:'반도체·IT', region:'서울/수원', size:'대기업', recent:12,
    posting:{ kind:'new', role:'2026 상반기 신입공채', dday:'D-24', date:'~6/15 18:00',
      summary:'DS·DX 부문 통합 모집. 학사 이상.', exp:'신입', edu:'학사+', loc:'수원' },
    tags:['반도체', '공채', '대기업'] },
  { logo:'N', name:'네이버', sector:'IT·플랫폼', region:'판교', size:'대기업', recent:8,
    posting:{ kind:'new', role:'신입 백엔드 개발자', dday:'D-19', date:'~6/10 23:59',
      summary:'Java/Kotlin 대규모 트래픽 처리.', exp:'신입', edu:'학사+', loc:'판교' },
    tags:['IT', 'Java', '백엔드'] },
  { logo:'LG', name:'LG에너지솔루션', sector:'배터리', region:'대전', size:'대기업', recent:6,
    posting:{ kind:'new', role:'연구개발(R&D) 신입', dday:'D-16', date:'~6/7 18:00',
      summary:'배터리 셀·소재·공정 R&D.', exp:'신입', edu:'석사+', loc:'대전' },
    tags:['배터리', 'R&D', '석사'] },
];

function V2_Onb3Swipe() {
  const nav = useNav();
  const [companyFav, setCompanyFav] = React.useState({});
  const [postingSaved, setPostingSaved] = React.useState({});
  const favCount = Object.values(companyFav).filter(Boolean).length;

  return (
    <AndroidPhone
      showAppBar={true}
      title="관심 회사 고르기"
      action={<span style={{cursor:'pointer', fontFamily:"'Gaegu',sans-serif", fontWeight:700, fontSize:13, color:'var(--ink-2)'}} onClick={() => nav.go('onb4')}>건너뛰기</span>}
    >
      <div style={{ padding: '0 16px 8px' }}>
        <div className="row" style={{ gap: 4, marginBottom: 8 }}>
          <span className="dot" style={{ background:'var(--brand)', width:16, borderRadius:3 }}/>
          <span className="dot" style={{ background:'var(--brand)', width:16, borderRadius:3 }}/>
          <span className="dot on" style={{ background:'var(--brand)' }}/>
          <span className="dot"/>
        </div>
        <div className="row" style={{ alignItems:'center' }}>
          <div className="note muted2" style={{ flex: 1 }}>❤️ 누르면 관심기업으로 추가 돼요</div>
          <span className="chip sm on-brand" style={{ fontSize: 11 }}>{favCount}개 추가됨</span>
        </div>
      </div>

      <div style={{
        flex: 1,
        overflowY: 'scroll',
        scrollSnapType: 'y mandatory',
        scrollBehavior: 'smooth'
      }}>
        {ONB_DECK.map((data, i) => {
          const fav = !!companyFav[data.name];
          const saved = !!postingSaved[data.posting.role];
          return (
            <div key={i} style={{
              height: '100%',
              minHeight: '100%',
              scrollSnapAlign: 'start',
              scrollSnapStop: 'always',
              padding: '8px 14px 14px',
              position: 'relative',
              display: 'flex', flexDirection: 'column'
            }}>
              {/* 상단: 회사 */}
              <div className="sk-box" style={{ padding: 12 }}>
                <div className="row" style={{ gap: 10, alignItems:'flex-start' }}>
                  <div className="logo" style={{ width: 56, height: 56, fontSize: 18, background:'var(--brand-soft)' }}>{data.logo}</div>
                  <div style={{ flex: 1 }}>
                    <div className="t-xs muted">{data.size} · {data.region}</div>
                    <div className="t-xl b" style={{ lineHeight: 1.1, marginTop: 2 }}>{data.name}</div>
                    <div className="t-sm muted2">{data.sector}</div>
                  </div>
                </div>
                <div className="row" style={{ gap: 4, marginTop: 8, flexWrap:'wrap' }}>
                  {data.tags.map(t => <span key={t} className="chip sm">#{t}</span>)}
                  <span className="chip sm" style={{ background:'var(--brand-soft)' }}>최근 {data.recent}건</span>
                </div>
              </div>

              {/* 하단: 공고 */}
              <div className="sk-box" style={{ padding: 12, marginTop: 8, flex: 1, display:'flex', flexDirection:'column' }}>
                <div className="row" style={{ gap: 6 }}>
                  <span className={`label ${data.posting.kind}`} style={{ fontSize: 10 }}>NEW</span>
                  <span className="t-sm b" style={{ color: 'var(--new)' }}>{data.posting.dday}</span>
                  <span className="spacer"/>
                  <span className="t-xs muted">~ {data.posting.date}</span>
                </div>
                <div className="t-lg b" style={{ marginTop: 8, lineHeight: 1.15 }}>{data.posting.role}</div>
                <div className="row" style={{ gap: 4, marginTop: 8, flexWrap:'wrap' }}>
                  <span className="chip sm">📍 {data.posting.loc}</span>
                  <span className="chip sm">🎓 {data.posting.edu}</span>
                  <span className="chip sm">💼 {data.posting.exp}</span>
                </div>
                <div className="sk-box fill-brand" style={{ padding: 10, marginTop: 10 }}>
                  <div className="note" style={{ fontSize: 14, lineHeight: 1.3 }}>{data.posting.summary}</div>
                </div>
                <div className="spacer"/>
                <div className="note center muted2" style={{ marginTop: 8, fontSize: 14, opacity: i === ONB_DECK.length - 1 ? 0 : 0.7 }}>↓ 다음 공고로 스크롤</div>
              </div>

              {/* 오른쪽 플로팅 액션 */}
              <div style={{
                position:'absolute', right: 18, bottom: 100,
                display:'flex', flexDirection:'column', gap: 12, alignItems:'center'
              }}>
                <div style={{ display:'flex', flexDirection:'column', alignItems:'center', gap: 2 }}>
                  <button
                    onClick={() => setCompanyFav(s => ({ ...s, [data.name]: !s[data.name] }))}
                    style={{
                      width: 44, height: 44, borderRadius: 999,
                      background: fav ? 'var(--brand)' : 'var(--paper)',
                      border: '1.8px solid var(--ink)', cursor:'pointer',
                      boxShadow: fav ? '2px 2px 0 var(--brand-deep)' : '2px 2px 0 var(--ink-3)',
                      display:'flex', alignItems:'center', justifyContent:'center'
                    }}>
                    <SkIcon name="heart" size={20} color={fav ? '#fff' : 'var(--ink)'} strokeWidth={2.2}/>
                  </button>
                  <span className="t-xs b" style={{ color: fav ? 'var(--brand)' : 'var(--ink-3)' }}>관심기업</span>
                </div>
                <div style={{ display:'flex', flexDirection:'column', alignItems:'center', gap: 2 }}>
                  <button
                    onClick={() => setPostingSaved(s => ({ ...s, [data.posting.role]: !s[data.posting.role] }))}
                    style={{
                      width: 44, height: 44, borderRadius: 999,
                      background: saved ? 'var(--update)' : 'var(--paper)',
                      border: '1.8px solid var(--ink)', cursor:'pointer',
                      boxShadow: saved ? '2px 2px 0 var(--ink)' : '2px 2px 0 var(--ink-3)',
                      display:'flex', alignItems:'center', justifyContent:'center'
                    }}>
                    <SkIcon name="bookmark" size={20} color={saved ? '#fff' : 'var(--ink)'} strokeWidth={2.2}/>
                  </button>
                  <span className="t-xs b" style={{ color: saved ? 'var(--update)' : 'var(--ink-3)' }}>공고 저장</span>
                </div>
              </div>
            </div>
          );
        })}

        {/* 완료 카드 */}
        <div style={{
          height: '100%', minHeight: '100%', scrollSnapAlign: 'start',
          padding: '24px 18px', display:'flex', flexDirection:'column',
          alignItems:'center', justifyContent:'center', textAlign:'center',
          background: 'var(--brand-soft)'
        }}>
          <Mascot size={84} expression="happy"/>
          <div className="t-2xl b" style={{ marginTop: 10, color: 'var(--brand-deep)' }}>
            {favCount > 0 ? `${favCount}개 관심기업!` : '관심 회사를 골라봐'}
          </div>
          <div className="note muted2" style={{ marginTop: 4 }}>매일 새 공고를 알려드릴게요</div>
          <button className="btn brand block" style={{ marginTop: 18 }} onClick={() => nav.go('onb4')}>완료 →</button>
        </div>
      </div>
    </AndroidPhone>
  );
}

window.V2_OnbDeck = ONB_DECK;

// ── 4. 온보딩 STEP 4 — 위젯 + 알림 유도 ────────
// 사용자 요구: 별도 시간설정 X. 그냥 허용만 받으면 9시·21시 자동
function V2_Onb4Widget() {
  const nav = useNav();
  return (
    <AndroidPhone showAppBar={false}>
      <div style={{ padding: '20px 18px 16px', flex: 1, display: 'flex', flexDirection: 'column' }}>
        <div className="row" style={{ gap: 4, marginBottom: 14 }}>
          <span className="dot" style={{ background:'var(--brand)', width:16, borderRadius:3 }}/>
          <span className="dot" style={{ background:'var(--brand)', width:16, borderRadius:3 }}/>
          <span className="dot" style={{ background:'var(--brand)', width:16, borderRadius:3 }}/>
          <span className="dot on" style={{ background:'var(--brand)' }}/>
        </div>

        <div className="t-2xl b" style={{ lineHeight: 1.1 }}>
          마지막!<br/>
          <span className="sk-uline brand">잊지 않게</span> 챙겨줄게
        </div>
        <div className="note muted2" style={{ marginTop: 4 }}>
          매일 아침 9시, 저녁 9시에 새 공고를 알려줄게요
        </div>

        {/* 위젯 프리뷰 */}
        <div className="sk-box fill-brand" style={{ marginTop: 18, padding: 14 }}>
          <div className="t-sm b row" style={{ gap: 6 }}>
            📱 바탕화면 위젯 미리보기
          </div>
          {/* 모의 위젯 */}
          <div style={{
            marginTop: 10,
            background: '#1a1a1a',
            border: '2px solid #1a1a1a',
            borderRadius: 16,
            padding: 12,
            color: '#fff',
            fontFamily: "'Gaegu', sans-serif"
          }}>
            <div className="row" style={{ gap: 8 }}>
              <Mascot size={42} expression="happy" tint="#ff9b85"/>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 13, opacity: 0.7 }}>오늘 새 공고</div>
                <div style={{ fontSize: 26, fontWeight: 700, lineHeight: 1, color:'var(--brand)' }}>
                  17건 <span style={{ fontSize: 14, color:'#fff', opacity:0.6 }}>+ 마감 3</span>
                </div>
              </div>
              <div style={{
                width: 28, height: 28, borderRadius: 999,
                background: 'var(--alert)', color:'#fff',
                display:'flex', alignItems:'center', justifyContent:'center',
                fontSize: 13, fontWeight: 700,
                border: '2px solid #fff'
              }}>17</div>
            </div>
          </div>
        </div>

        {/* 핵심 안내 */}
        <div className="col" style={{ gap: 8, marginTop: 14 }}>
          <div className="row" style={{ gap: 8 }}>
            <span className="chk on" style={{ background:'var(--brand)', borderColor:'var(--brand)' }}><SkIcon name="check" size={12} color="#fff"/></span>
            <span className="t-sm">아침 9:00 · 새 공고 요약</span>
          </div>
          <div className="row" style={{ gap: 8 }}>
            <span className="chk on" style={{ background:'var(--brand)', borderColor:'var(--brand)' }}><SkIcon name="check" size={12} color="#fff"/></span>
            <span className="t-sm">저녁 9:00 · 마감 임박 공고</span>
          </div>
          <div className="row" style={{ gap: 8 }}>
            <span className="chk on" style={{ background:'var(--brand)', borderColor:'var(--brand)' }}><SkIcon name="check" size={12} color="#fff"/></span>
            <span className="t-sm">바탕화면 위젯으로 한눈에</span>
          </div>
        </div>

        <div className="spacer"/>
        <button className="btn brand block" onClick={() => nav.go('main')}>알림 허용하고 위젯 추가</button>
        <div className="center muted note" style={{ marginTop: 6, fontSize: 14, cursor:'pointer' }} onClick={() => nav.go('main')}>나중에 설정에서 끌 수 있어요</div>
      </div>
    </AndroidPhone>
  );
}

// ── 5. 메인 피드 — 가로 스와이프 3섹션 ──────────────────────
// 간소화된 카드: 로고 + 회사·직무 + D-day만
function V2_JobRow({ kind = 'new', company, role, dday, logo, saved = false, onClick }) {
  const labelKlass = kind === 'new' ? 'new' : kind === 'update' ? 'update' : 'closing';
  return (
    <div className="card" style={{ padding: '10px 12px', cursor: onClick ? 'pointer' : 'default' }} onClick={onClick}>
      <div className="row" style={{ gap: 10 }}>
        <div className="logo">{logo || (company||'').slice(0,2)}</div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div className="row" style={{ gap: 6 }}>
            <span className={`label ${labelKlass}`} style={{ fontSize: 10, padding: '1px 6px' }}>
              {kind === 'new' ? 'NEW' : kind === 'update' ? 'UPDATE' : 'CLOSING'}
            </span>
            <span className="t-xs muted" style={{ overflow:'hidden', textOverflow:'ellipsis', whiteSpace:'nowrap' }}>{company}</span>
          </div>
          <div className="t-md b" style={{ marginTop: 2, lineHeight: 1.15, overflow:'hidden', textOverflow:'ellipsis', whiteSpace:'nowrap' }}>{role}</div>
        </div>
        <div style={{ textAlign:'right' }}>
          <div className={`t-sm b`} style={{ color: `var(--${labelKlass})` }}>{dday?.text}</div>
          <div className="t-xs muted">{dday?.date}</div>
        </div>
      </div>
    </div>
  );
}

function V2_Main() {
  const nav = useNav();
  return (
    <AndroidPhone title="채용알리미" action={<span style={{cursor:'pointer'}} onClick={() => nav.go('filter')}><SkIcon name="filter" size={20}/></span>}>
      <div style={{ padding: '4px 18px 6px' }}>
        <div className="row" style={{ justifyContent:'space-between', alignItems:'flex-start' }}>
          <div>
            <div className="note muted2">5월 22일 목요일</div>
            <div className="t-xl b" style={{ lineHeight: 1.1 }}>
              오늘 새 공고 <span style={{ color:'var(--brand)' }}>17건</span>
            </div>
          </div>
          <div style={{ position:'relative' }}>
            <Mascot size={56} expression="happy"/>
            <div style={{
              position: 'absolute', top: -2, right: -2,
              minWidth: 22, height: 22, padding: '0 5px',
              borderRadius: 999,
              background: 'var(--alert)', color: '#fff',
              fontSize: 11, fontWeight: 700,
              display:'flex', alignItems:'center', justifyContent:'center',
              border: '1.6px solid #1a1a1a'
            }}>17</div>
          </div>
        </div>
      </div>

      {/* 페이지 인디케이터 (가로 스와이프 섹션) */}
      <div className="row" style={{ justifyContent:'center', gap: 6, marginTop: 8, padding: '0 16px' }}>
        <span className="chip sm" style={{ background:'var(--new)', borderColor:'var(--new)', color:'#fff' }}>● NEW 17</span>
        <span className="chip sm">UPDATE 4</span>
        <span className="chip sm">CLOSING 3</span>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 16px 12px', display:'flex', flexDirection:'column', gap: 8 }}>
        <V2_JobRow kind="new" company="삼성전자" role="2026 상반기 신입공채" logo="삼성" dday={{ text:'D-24', date:'~6/15' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="new" company="네이버" role="신입 백엔드 개발자" logo="N" dday={{ text:'D-19', date:'~6/10' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="new" company="LG에너지솔루션" role="연구개발(R&D) 신입" logo="LG" dday={{ text:'D-16', date:'~6/7' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="new" company="카카오" role="신입 안드로이드 개발자" logo="카카오" dday={{ text:'D-14', date:'~6/5' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="new" company="포스코" role="2026 상반기 신입공채" logo="포스" dday={{ text:'D-12', date:'~6/3' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="new" company="현대모비스" role="기계 R&D 신입" logo="HM" dday={{ text:'D-10', date:'~6/1' }} onClick={() => nav.go('detail')}/>
        <div className="note center muted2" style={{ marginTop: 4 }}>← 옆으로 넘기면 UPDATE 섹션 →</div>
      </div>
      <TabBarV2 active="home"/>
    </AndroidPhone>
  );
}

// ── 6. 공고 상세 — 탭 분리 ────────────────────
function V2_Detail() {
  const nav = useNav();
  return (
    <AndroidPhone
      title=""
      showAppBar={true}
      leading={<span style={{cursor:'pointer', marginRight: 4}} onClick={() => nav.back()}><SkIcon name="chev-l" size={22}/></span>}
      action={<><SkIcon name="bookmark" size={20}/><SkIcon name="share" size={18}/></>}
    >
      <div style={{ padding: 14 }}>
        <div className="row" style={{ gap: 10 }}>
          <div className="logo lg">N</div>
          <div style={{ flex: 1 }}>
            <span className="label new">NEW</span>
            <div className="t-lg b" style={{ marginTop: 4, lineHeight: 1.15 }}>
              신입 백엔드 개발자
            </div>
            <div className="t-sm muted">네이버 · 판교</div>
          </div>
        </div>
        <div className="row" style={{ gap: 6, marginTop: 10, flexWrap:'wrap' }}>
          <span className="label outline-new b">D-19</span>
          <span className="chip sm">신입</span>
          <span className="chip sm">학사</span>
          <span className="chip sm">Java</span>
        </div>
      </div>

      <div className="row" style={{ padding: '0 14px', borderBottom: '1.5px solid var(--ink-3)' }}>
        {[
          ['요약', true],
          ['원문', false],
          ['회사', false],
          ['비슷한', false],
        ].map(([t,on]) => (
          <div key={t} className="t-md b" style={{
            padding: '8px 12px',
            borderBottom: on ? '2.5px solid var(--brand)' : 'none',
            color: on ? 'var(--brand)' : 'var(--ink-3)',
            marginBottom: -1
          }}>{t}</div>
        ))}
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: 14 }}>
        <div className="sk-box fill-brand">
          <div className="t-sm b row" style={{ gap: 4 }}>
            <SkIcon name="sparkle" size={14} color="var(--brand)"/> 꽁이 한줄 요약
          </div>
          <div className="t-md" style={{ marginTop: 4, lineHeight: 1.3 }}>
            검색·커머스 백엔드. Java/Kotlin 대규모 트래픽 처리. 학사 이상, 신입 가능.
          </div>
        </div>

        <div className="t-md b" style={{ marginTop: 14 }}>📋 핵심 정보</div>
        <div className="col" style={{ gap: 6, marginTop: 6 }}>
          <div className="row t-sm"><span className="muted" style={{ width: 70 }}>마감</span><span className="b">6월 10일 (수) 23:59</span></div>
          <div className="row t-sm"><span className="muted" style={{ width: 70 }}>자격</span><span className="b">학사 이상, 신입~3년</span></div>
          <div className="row t-sm"><span className="muted" style={{ width: 70 }}>근무지</span><span className="b">분당구 정자동</span></div>
          <div className="row t-sm"><span className="muted" style={{ width: 70 }}>전형</span><span className="b">서류 → 코딩 → 면접</span></div>
        </div>

        <div className="t-md b" style={{ marginTop: 14 }}>🎯 우대사항</div>
        <ul className="t-sm" style={{ paddingLeft: 18, lineHeight: 1.6, margin: '4px 0' }}>
          <li>대규모 분산 시스템 경험</li>
          <li>Spring Boot / Kotlin 능숙자</li>
          <li>오픈소스 기여 경험</li>
        </ul>

        <div className="sk-box dashed" style={{ marginTop: 10 }}>
          <div className="t-sm b row" style={{ gap: 4 }}>
            <SkIcon name="link" size={14}/> 원본: recruit.navercorp.com/...
          </div>
        </div>
      </div>

      <div style={{ padding: 12, borderTop: '1.8px solid var(--ink)' }}>
        <button className="btn brand block" onClick={() => alert('실제 앱: 네이버 채용 사이트로 이동')}>지원하러 가기 →</button>
      </div>
    </AndroidPhone>
  );
}

// ── 7. 필터 — 풀스크린 ────────────────────
function V2_Filter() {
  const nav = useNav();
  const Sec = ({ title, sub, children }) => (
    <div style={{ marginBottom: 16 }}>
      <div className="row" style={{ justifyContent:'space-between' }}>
        <div className="t-md b">{title}</div>
        {sub && <div className="note muted">{sub}</div>}
      </div>
      <div className="row" style={{ gap: 6, marginTop: 6, flexWrap: 'wrap' }}>{children}</div>
    </div>
  );
  return (
    <AndroidPhone
      title="필터"
      leading={<span style={{cursor:'pointer', marginRight: 4}} onClick={() => nav.back()}><SkIcon name="close" size={22}/></span>}
      action={<span className="note">초기화</span>}
    >
      <div style={{ flex: 1, overflowY: 'auto', padding: '8px 18px 12px' }}>
        <Sec title="직군" sub="3개 선택">
          {[
            '기획·전략', '마케팅·홍보·조사', '회계·세무·재무',
            '인사·노무·HRD', '총무·법무·사무', 'IT개발·데이터',
            '디자인', '영업·판매·무역', '고객상담·TM',
            '구매·자재·물류', '상품기획·MD', '운전·운송·배송',
            '서비스', '생산', '건설·건축',
            '의료', '연구·R&D', '교육',
            '미디어·문화·스포츠', '금융·보험', '공공·복지'
          ].map((j,i) =>
            <span key={j} className={'chip sm' + ([2,5,6].includes(i) ? ' on-brand' : '')} style={{ fontSize: 12 }}>{j}</span>
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
          {['오늘','내일','D-3','D-7','D-14'].map(j => <span key={j} className="chip">{j}</span>)}
        </Sec>
      </div>
      <div style={{ padding: 12, borderTop: '1.8px solid var(--ink)' }}>
        <button className="btn brand block" onClick={() => nav.go('main')}>17건 결과 보기</button>
      </div>
    </AndroidPhone>
  );
}

// ── 8. 관심 기업 — 로고 그리드 ────────────────────
function V2_Favorites() {
  const nav = useNav();
  const companies = [
    ['삼성','삼성전자', 3, true],
    ['N','네이버', 2, true],
    ['카카오','카카오', 1, false],
    ['LG','LG에너지', 1, true],
    ['SK','SK하이닉스', 0, false],
    ['현대','현대차', 2, true],
    ['CJ','CJ', 0, false],
    ['포스','포스코', 1, false],
    ['+','추가', null, false],
  ];
  return (
    <AndroidPhone title="관심 기업" action={<SkIcon name="plus" size={20}/>}>
      <div style={{ padding: '6px 18px 0' }}>
        <div className="row" style={{ justifyContent:'space-between' }}>
          <div className="t-md b">관심 기업 8 · 오늘 새공고 8건</div>
        </div>
        <div className="note muted2">로고 우상단 빨간 점 = 오늘 새 공고</div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: 14 }}>
        <div style={{ display:'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 10 }}>
          {companies.map(([logo, name, badge, onAlarm], i) =>
            <div key={i} className={'sk-box' + (i===8 ? ' dashed' : '')} style={{
              padding: 10, display:'flex', flexDirection:'column', alignItems:'center', gap: 4, position: 'relative',
              cursor: i === 8 ? 'pointer' : 'pointer'
            }}
            onClick={() => {
              if (i === 8) return; // 추가 버튼은 별도
              if (badge === 0) nav.go('companyEmpty'); // 채용중 없음
              else nav.go('company');
            }}>
              <div className="logo" style={{ width: 44, height: 44, fontSize: 14, background: badge > 0 ? 'var(--brand-soft)' : 'var(--paper-2)' }}>{logo}</div>
              <div className="t-sm b center" style={{ lineHeight: 1.1 }}>{name}</div>
              <div className="t-xs muted">{badge != null ? `공고 ${badge}` : ''}</div>
              {badge > 0 && (
                <div style={{
                  position:'absolute', top:6, right:6,
                  width: 20, height: 20, borderRadius: 999,
                  background:'var(--alert)', color:'#fff',
                  fontSize: 11, fontWeight: 700,
                  display:'flex', alignItems:'center', justifyContent:'center',
                  border: '1.5px solid var(--ink)'
                }}>{badge}</div>
              )}
              {onAlarm && (
                <div style={{ position:'absolute', bottom: 4, left:4, fontSize: 9, color:'var(--brand)' }}>🔔</div>
              )}
            </div>
          )}
        </div>

        <div className="sk-box fill-note" style={{ marginTop: 14, padding: 10 }}>
          <div className="row" style={{ gap: 8 }}>
            <Mascot size={42} expression="default"/>
            <div className="note" style={{ flex: 1, fontSize: 14, lineHeight: 1.2 }}>
              로고 누르면 그 회사 공고만 모아볼 수 있어요.<br/>
              꾹 누르면 알림 끄기 / 삭제 ✏️
            </div>
          </div>
        </div>
      </div>
      <TabBarV2 active="fav"/>
    </AndroidPhone>
  );
}

// ── 9. 검색 — 최근/인기 ────────────────────
function V2_Search() {
  const nav = useNav();
  return (
    <AndroidPhone title="검색">
      <div style={{ padding: '4px 18px 0' }}>
        <div className="sk-box row" style={{ padding: '10px 12px', gap: 8, cursor:'pointer' }} onClick={() => nav.go('searchResults')}>
          <SkIcon name="search" size={18}/>
          <span className="t-md muted">기업명·직무·키워드</span>
          <span className="spacer"/>
          <SkIcon name="mic" size={18} color="#8a8a8a"/>
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 18px 12px' }}>
        <div className="row" style={{ justifyContent:'space-between' }}>
          <div className="t-md b">최근 검색</div>
          <span className="note muted">전체삭제</span>
        </div>
        <div className="row" style={{ gap: 6, marginTop: 8, flexWrap:'wrap' }}>
          {['삼성전자','UX 디자이너','대전','신입 개발','LG'].map(k =>
            <span key={k} className="chip sm row" style={{ gap: 4, cursor:'pointer' }} onClick={() => nav.go('searchResults')}>{k} <SkIcon name="close" size={10} color="#8a8a8a"/></span>
          )}
        </div>

        <div className="t-md b" style={{ marginTop: 18 }}>🔥 지금 인기 검색어</div>
        <div className="col" style={{ gap: 2, marginTop: 8 }}>
          {[
            ['1','삼성전자 상반기 공채','▲'],
            ['2','네이버 백엔드','▲'],
            ['3','LG에너지 R&D','NEW'],
            ['4','현대차','-'],
            ['5','카카오 경력','▼'],
          ].map(([n,k,t]) =>
            <div key={n} className="row" style={{ padding: '6px 0' }}>
              <span className="t-md b" style={{ width: 22, color: 'var(--brand)' }}>{n}</span>
              <span className="t-md" style={{ flex: 1 }}>{k}</span>
              <span className="t-xs muted">{t}</span>
            </div>
          )}
        </div>

        <div className="t-md b" style={{ marginTop: 18 }}>직군별 둘러보기</div>
        <div style={{ display:'grid', gridTemplateColumns: '1fr 1fr', gap: 6, marginTop: 8 }}>
          {[
            'IT개발·데이터', '디자인', '마케팅·홍보·조사',
            '기획·전략', '회계·세무·재무', '인사·노무·HRD',
            '영업·판매·무역', '연구·R&D'
          ].map(j =>
            <div key={j} className="chip sm" style={{ justifyContent:'center', padding: '10px 6px', fontSize: 12, cursor:'pointer' }} onClick={() => nav.go('searchResults')}>
              {j}
            </div>
          )}
          <div className="chip sm" style={{ justifyContent:'center', padding: '10px 6px', fontSize: 12, cursor:'pointer' }}>
            + 전체 21개
          </div>
        </div>

        <div className="t-md b" style={{ marginTop: 18 }}>추천 키워드</div>
        <div className="row" style={{ gap: 6, marginTop: 8, flexWrap:'wrap' }}>
          {['#신입공채','#수시채용','#재택가능','#복지좋은','#성과급','#스타트업'].map(k =>
            <span key={k} className="chip sm">{k}</span>
          )}
        </div>
      </div>
      <TabBarV2 active="search"/>
    </AndroidPhone>
  );
}

// ── 10. 마이페이지 — 미니멀 (스트릭 + 메뉴) ──────────────
function V2_MyPage() {
  const nav = useNav();
  return (
    <AndroidPhone title="내 정보">
      <div style={{ flex: 1, overflowY: 'auto', padding: 14 }}>
        <div className="sk-box fill-brand" style={{ padding: 14 }}>
          <div className="row" style={{ gap: 10 }}>
            <Mascot size={66} expression="happy"/>
            <div style={{ flex: 1 }}>
              <div className="t-lg b">잘하고 있어요! 🔥</div>
              <div className="note muted2">꽁이가 12일째 챙겨주는 중</div>
            </div>
          </div>
          <div className="div"/>
          <div className="row" style={{ justifyContent:'space-around', textAlign:'center' }}>
            <div style={{ flex: 1 }}>
              <div className="t-2xl b" style={{ color:'var(--ink-2)' }}>87</div>
              <div className="t-xs muted">본 공고</div>
            </div>
            <div
              style={{ flex: 1, cursor:'pointer', borderLeft:'1.5px dashed var(--ink-3)' }}
              onClick={() => nav.go('saved')}>
              <div className="t-2xl b" style={{ color:'var(--update)' }}>14</div>
              <div className="t-xs muted">저장한 공고 ›</div>
            </div>
          </div>
        </div>

        <div className="col" style={{ marginTop: 14, gap: 0 }}>
          {[
            ['🔔','알림 설정','매일 9시 / 21시', 'notifSettings'],
            ['📱','바탕화면 위젯','켜짐 (Medium)', 'widgetSettings'],
            ['🎯','관심 직군','개발 · 디자인 · 데이터', 'jobInterests'],
            ['💬','피드백 보내기','', 'feedback'],
            ['ℹ️','앱 정보','v0.1.0 · 베타', 'about'],
          ].map(([e,t,sub,route],i,arr) =>
            <div key={i} className="row" style={{ padding: '14px 4px', borderBottom: i < arr.length - 1 ? '1.5px dashed var(--ink-3)' : 'none', gap: 12, cursor:'pointer' }} onClick={() => nav.go(route)}>
              <span style={{ fontSize: 18 }}>{e}</span>
              <div style={{ flex: 1 }}>
                <div className="t-sm b">{t}</div>
                {sub && <div className="t-xs muted">{sub}</div>}
              </div>
              <SkIcon name="chev" size={14} color="#8a8a8a"/>
            </div>
          )}
        </div>
      </div>
      <TabBarV2 active="me"/>
    </AndroidPhone>
  );
}

// ── 11. 바탕화면 위젯 (안드로이드 홈스크린) ──────────────
// size: 'small' (2x1) | 'medium' (4x1) | 'large' (4x2)
function V2_Widget({ size = 'medium' }) {
  // 위젯 그리드 점유 (4컬럼 기준)
  const widgetStyle = {
    small:  { gridColumn: 'span 2', gridRow: 'span 1' },
    medium: { gridColumn: 'span 4', gridRow: 'span 1' },
    large:  { gridColumn: 'span 4', gridRow: 'span 2' },
  }[size];

  const widget = (
    <div style={{
      ...widgetStyle,
      background: '#fffdf7',
      border: '2px solid #1a1a1a',
      borderRadius: 18,
      padding: size === 'small' ? 10 : 12,
      fontFamily: "'Gaegu', sans-serif",
      boxShadow: '3px 3px 0 rgba(0,0,0,0.25)',
      display: 'flex',
      flexDirection: 'column',
      minHeight: size === 'large' ? 168 : 0
    }}>
      {size === 'small' ? (
        <>
          <div className="row" style={{ gap: 6, alignItems:'center' }}>
            <Mascot size={28} expression="happy"/>
            <div style={{ flex: 1, fontSize: 11, opacity: 0.7 }}>오늘 새 공고</div>
          </div>
          <div style={{ fontSize: 28, fontWeight: 700, color:'var(--brand)', lineHeight: 1, marginTop: 4 }}>17</div>
          <div style={{ fontSize: 11, opacity: 0.7 }}>마감 3건</div>
        </>
      ) : size === 'medium' ? (
        <div className="row" style={{ gap: 10, alignItems:'center', flex: 1 }}>
          <Mascot size={42} expression="happy"/>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 11, opacity: 0.65 }}>5/22 · 오늘 새 공고</div>
            <div className="row" style={{ gap: 6, marginTop: 2, alignItems:'baseline' }}>
              <span style={{ fontSize: 28, fontWeight: 700, color:'var(--brand)', lineHeight: 1 }}>17</span>
              <span style={{ fontSize: 12 }}>+ UPDATE 4 · 마감 3</span>
            </div>
          </div>
          <div style={{
            minWidth: 24, height: 24, padding: '0 6px',
            borderRadius: 999, background:'var(--alert)', color:'#fff',
            display:'flex', alignItems:'center', justifyContent:'center',
            fontSize: 11, fontWeight: 700,
            border:'1.5px solid #1a1a1a'
          }}>17</div>
        </div>
      ) : (
        <>
          <div className="row" style={{ gap: 10 }}>
            <Mascot size={52} expression="happy"/>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 11, opacity: 0.65 }}>5/22 (목)</div>
              <div style={{ fontSize: 16, fontWeight: 700, lineHeight: 1.05 }}>오늘 새 공고</div>
              <div className="row" style={{ gap: 8, marginTop: 2, alignItems:'baseline' }}>
                <span style={{ fontSize: 32, fontWeight: 700, color:'var(--brand)', lineHeight: 1 }}>17</span>
                <span style={{ fontSize: 12 }}>+ UPDATE 4 · 마감 3</span>
              </div>
            </div>
            <div style={{
              minWidth: 26, height: 26, padding: '0 6px',
              borderRadius: 999, background:'var(--alert)', color:'#fff',
              display:'flex', alignItems:'center', justifyContent:'center',
              fontSize: 12, fontWeight: 700,
              border:'1.5px solid #1a1a1a'
            }}>17</div>
          </div>
          <div style={{ marginTop: 10, paddingTop: 8, borderTop: '1.5px dashed var(--ink-3)' }}>
            <div style={{ fontSize: 11, opacity: 0.7, marginBottom: 4 }}>오늘 핵심</div>
            <div className="col" style={{ gap: 4 }}>
              {[
                ['삼성전자', '신입공채', 'D-24'],
                ['네이버', '백엔드', 'D-19'],
                ['LG에너지', 'R&D', 'D-16'],
              ].map(([c, r, d]) =>
                <div key={c} className="row" style={{ gap: 8, fontSize: 13 }}>
                  <span className="b" style={{ width: 70 }}>{c}</span>
                  <span style={{ flex: 1, opacity: 0.8 }}>{r}</span>
                  <span className="b" style={{ color:'var(--brand)' }}>{d}</span>
                </div>
              )}
            </div>
          </div>
        </>
      )}
    </div>
  );

  return (
    <div className="phone phone-android android-wallpaper">
      <div className="and-status">
        <span className="t-xs b">9:41</span>
        <span className="spacer"/>
        <span className="t-xs">📶</span>
        <span className="t-xs">▮</span>
      </div>

      <div className="and-clock">
        <div style={{ fontSize: 48, fontWeight: 700, lineHeight: 1 }}>9:41</div>
        <div style={{ fontSize: 14, opacity: 0.85, marginTop: 4 }}>5월 22일 목요일</div>
      </div>

      {/* 위젯이 위치하는 홈스크린 그리드 */}
      <div style={{ padding: '24px 14px 0', flex: 1 }}>
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(4, 1fr)',
          gap: 10,
          alignContent: 'start'
        }}>
          {widget}

          {/* 다른 위젯 아이콘들로 빈 칸 채우기 */}
          {size === 'small' && (
            <>
              <div className="app-icon" style={{ gridColumn: 'span 1' }}><div className="ic-box">🕐</div><span>시계</span></div>
              <div className="app-icon" style={{ gridColumn: 'span 1' }}><div className="ic-box">🌤️</div><span>날씨</span></div>
            </>
          )}
        </div>
      </div>

      {/* 도크 (앱 아이콘 행) */}
      <div className="and-icons">
        <div className="app-icon brand">
          <div className="ic-box" style={{ position:'relative' }}>
            <Mascot size={32} expression="default"/>
            <div style={{
              position:'absolute', top:-4, right:-4,
              minWidth: 22, height: 22, padding: '0 5px',
              borderRadius: 999, background:'var(--alert)', color:'#fff',
              fontSize: 11, fontWeight: 700,
              display:'flex', alignItems:'center', justifyContent:'center',
              border: '1.5px solid #1a1a1a'
            }}>17</div>
          </div>
          <span>채용알리미</span>
        </div>
        <div className="app-icon"><div className="ic-box">💬</div><span>메시지</span></div>
        <div className="app-icon"><div className="ic-box">📷</div><span>카메라</span></div>
        <div className="app-icon"><div className="ic-box">🗺️</div><span>지도</span></div>
      </div>

      {/* nav pill */}
      <div style={{
        position:'absolute', left: 0, right: 0, bottom: 6,
        display:'flex', justifyContent:'center'
      }}>
        <div style={{ width: 100, height: 4, background:'#fff', opacity: 0.8, borderRadius: 2 }}/>
      </div>
    </div>
  );
}

Object.assign(window, {
  V2_Onb1, V2_Onb2, V2_Onb3Swipe, V2_Onb4Widget,
  V2_Main, V2_Detail, V2_Filter, V2_Favorites, V2_Search, V2_MyPage, V2_Widget
});
