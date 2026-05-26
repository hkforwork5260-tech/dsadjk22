// screens-hifi-extra.jsx — 남은 화면들 하이파이 변환

// ── 1. 온보딩 ② 기업 규모/산업 ──────────────────────
function HiFi_Onb2() {
  const nav = useNav();
  const [scale, setScale] = React.useState({ 0: true, 1: true });
  const [sector, setSector] = React.useState({ 0: true, 1: true });
  const scales = ['대기업', '공기업', '중견기업', '중소기업', '외국계', '스타트업'];
  const sectors = ['IT/플랫폼', '반도체', '금융', '자동차', '바이오', '화학/소재', '유통', '+ 더보기'];
  const toggle = (state, set) => (i) => set(s => ({ ...s, [i]: !s[i] }));

  return (
    <HiFiPhone showAppBar={false}>
      <div className="h-pad" style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        <div className="h-row" style={{ gap: 6 }}>
          <span className="h-dot" style={{ background:'var(--h-brand)', width: 24, borderRadius: 4 }}/>
          <span className="h-dot on"/>
          <span className="h-dot"/>
          <span className="h-dot"/>
          <span style={{ flex: 1 }}/>
          <button className="h-btn ghost sm" onClick={() => nav.go('h_main')}>건너뛰기</button>
        </div>

        <div className="h-row" style={{ gap: 12, alignItems:'center', marginTop: 12 }}>
          <Mascot size={56} expression="default"/>
          <div>
            <div className="h-display" style={{ fontSize: 24 }}>어떤 회사들이 궁금해?</div>
            <div className="h-body-2" style={{ marginTop: 2 }}>매칭 정확도 ↑</div>
          </div>
        </div>

        <div className="h-body-2" style={{ marginTop: 18, fontWeight: 700, color: 'var(--h-text-2)' }}>기업 규모</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8, marginTop: 8 }}>
          {scales.map((s, i) =>
            <button key={s} onClick={() => toggle(scale, setScale)(i)}
              className={'h-btn sm' + (scale[i] ? ' primary' : '')}
              style={{ padding: '10px 6px', fontSize: 13, textTransform: 'none' }}>{s}</button>
          )}
        </div>

        <div className="h-body-2" style={{ marginTop: 22, fontWeight: 700, color: 'var(--h-text-2)' }}>산업군</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8, marginTop: 8 }}>
          {sectors.map((s, i) =>
            <button key={s} onClick={() => toggle(sector, setSector)(i)}
              className={'h-btn sm' + (sector[i] ? ' primary' : '')}
              style={{ padding: '12px 8px', fontSize: 14, textTransform: 'none' }}>{s}</button>
          )}
        </div>

        <div style={{ flex: 1 }}/>
        <div className="h-row" style={{ gap: 10 }}>
          <button className="h-btn" style={{ flex: 1 }} onClick={() => nav.back()}>← 이전</button>
          <button className="h-btn primary" style={{ flex: 2 }} onClick={() => nav.go('h_onb2swipe')}>다음 →</button>
        </div>
      </div>
    </HiFiPhone>
  );
}

// ── 2. 온보딩 ④ 위젯 + 알림 유도 ──────────────────────
function HiFi_Onb4Widget() {
  const nav = useNav();
  return (
    <HiFiPhone showAppBar={false}>
      <div className="h-pad" style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        <div className="h-row" style={{ gap: 6 }}>
          <span className="h-dot" style={{ background:'var(--h-brand)', width: 24, borderRadius: 4 }}/>
          <span className="h-dot" style={{ background:'var(--h-brand)', width: 24, borderRadius: 4 }}/>
          <span className="h-dot" style={{ background:'var(--h-brand)', width: 24, borderRadius: 4 }}/>
          <span className="h-dot on"/>
        </div>

        <div className="h-display" style={{ marginTop: 18, fontSize: 28, lineHeight: 1.1 }}>
          마지막!<br/>
          잊지 않게 챙겨줄게
        </div>
        <div className="h-body-2" style={{ marginTop: 6 }}>
          매일 아침 9시, 저녁 9시에 새 공고를 알려드릴게요
        </div>

        {/* 위젯 미리보기 */}
        <div className="h-card brand" style={{ padding: 14, marginTop: 22 }}>
          <div className="h-caption" style={{ color: 'var(--h-brand-dark)' }}>📱 바탕화면 위젯 미리보기</div>
          <div style={{
            marginTop: 10,
            background: '#1a1a1a',
            borderRadius: 16,
            padding: 14,
          }}>
            <div className="h-widget" style={{ boxShadow: 'none' }}>
              <div className="h-row" style={{ gap: 10 }}>
                <Mascot size={40} expression="happy"/>
                <div className="h-grow">
                  <div className="h-body-2" style={{ fontSize: 11 }}>오늘 새 공고</div>
                  <div className="h-row" style={{ gap: 6, marginTop: 2, alignItems:'baseline' }}>
                    <span className="h-mono-num" style={{ fontSize: 26, color:'var(--h-brand)', lineHeight: 1 }}>17</span>
                    <span className="h-body-2" style={{ fontSize: 12 }}>+ 마감 3</span>
                  </div>
                </div>
                <div style={{
                  minWidth: 24, height: 24, padding: '0 6px', borderRadius: 999,
                  background: 'var(--h-brand)', color: '#fff',
                  display:'flex', alignItems:'center', justifyContent:'center',
                  fontSize: 11, fontWeight: 800,
                  border: '2px solid #fff'
                }}>17</div>
              </div>
            </div>
          </div>
        </div>

        {/* 체크리스트 */}
        <div className="h-col" style={{ gap: 10, marginTop: 18 }}>
          {[
            '아침 9:00 · 새 공고 요약',
            '저녁 9:00 · 마감 임박 공고',
            '바탕화면 위젯으로 한눈에',
          ].map((t, i) =>
            <div key={i} className="h-row" style={{ gap: 10 }}>
              <div style={{
                width: 22, height: 22, borderRadius: 999, background: 'var(--h-brand)',
                display:'flex', alignItems:'center', justifyContent:'center'
              }}>
                <SkIcon name="check" size={14} color="#fff" strokeWidth={2.4}/>
              </div>
              <span className="h-body" style={{ fontWeight: 600 }}>{t}</span>
            </div>
          )}
        </div>

        <div style={{ flex: 1 }}/>
        <button className="h-btn primary block lg" onClick={() => nav.go('h_main')}>
          알림 허용하고 위젯 추가
        </button>
        <button className="h-btn ghost block" style={{ marginTop: 6, fontSize: 14 }} onClick={() => nav.go('h_main')}>
          나중에 설정에서 켤 수 있어요
        </button>
      </div>
    </HiFiPhone>
  );
}

// ── 3. 필터 (풀스크린) ──────────────────────
function HiFi_Filter() {
  const nav = useNav();
  const [job, setJob] = React.useState({ 5: true, 6: true });
  const allJobs = HIFI_JOB_CATEGORIES;
  const toggle = (i) => setJob(s => ({ ...s, [i]: !s[i] }));

  const Sec = ({ title, sub, children }) => (
    <div style={{ marginBottom: 22 }}>
      <div className="h-row" style={{ justifyContent:'space-between', marginBottom: 8 }}>
        <div className="h-h2">{title}</div>
        {sub && <div className="h-body-2">{sub}</div>}
      </div>
      {children}
    </div>
  );

  return (
    <HiFiPhone
      title="필터"
      leading={<HiFiIconBtn name="close" size={22} onClick={() => nav.back()}/>}
      action={<button className="h-btn ghost sm">초기화</button>}
    >
      <div style={{ flex: 1, overflowY: 'auto', padding: '8px 20px 16px' }}>
        <Sec title="직군" sub={`${Object.values(job).filter(Boolean).length}개 선택`}>
          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap: 6 }}>
            {allJobs.map((j, i) =>
              <button key={j} onClick={() => toggle(i)}
                className={'h-btn sm' + (job[i] ? ' primary' : '')}
                style={{ padding: '10px 6px', fontSize: 12, textTransform: 'none', lineHeight: 1.15 }}>{j}</button>
            )}
          </div>
        </Sec>
        <Sec title="기업 규모">
          <div className="h-row" style={{ gap: 6, flexWrap:'wrap' }}>
            {['대기업','공기업','중견','중소','외국계','스타트업'].map((j,i) =>
              <span key={j} className={'h-chip outline' + ([0,1].includes(i) ? ' on' : '')}>{j}</span>
            )}
          </div>
        </Sec>
        <Sec title="경력">
          <div className="h-row" style={{ gap: 6, flexWrap:'wrap' }}>
            {['신입','1~3년','3~5년','5년+','무관'].map((j,i) =>
              <span key={j} className={'h-chip outline' + (i===0 ? ' on' : '')}>{j}</span>
            )}
          </div>
        </Sec>
        <Sec title="지역">
          <div className="h-row" style={{ gap: 6, flexWrap:'wrap' }}>
            {['서울','경기/인천','대전','부산','광주','대구','+'].map((j,i) =>
              <span key={j} className={'h-chip outline' + ([0,1].includes(i) ? ' on' : '')}>{j}</span>
            )}
          </div>
        </Sec>
        <Sec title="마감일">
          <div className="h-row" style={{ gap: 6, flexWrap:'wrap' }}>
            {['오늘','내일','D-3','D-7','D-14'].map(j =>
              <span key={j} className="h-chip outline">{j}</span>
            )}
          </div>
        </Sec>
      </div>
      <div style={{ padding: 16, borderTop: '1px solid var(--h-border)' }}>
        <button className="h-btn primary block lg" onClick={() => nav.go('h_main')}>
          17건 결과 보기
        </button>
      </div>
    </HiFiPhone>
  );
}

// ── 4. 관심 기업 (그리드) ──────────────────────
function HiFi_Favorites() {
  const nav = useNav();
  const companies = [
    ['삼성','삼성전자', 3, true, 'company'],
    ['N','네이버', 2, true, 'company'],
    ['카', '카카오', 1, false, 'company'],
    ['LG','LG에너지', 1, true, 'company'],
    ['SK','SK하이닉스', 0, false, 'companyEmpty'],
    ['현','현대차', 2, true, 'company'],
    ['CJ','CJ', 0, false, 'companyEmpty'],
    ['포','포스코', 1, false, 'company'],
  ];

  return (
    <HiFiPhone
      title="관심 기업"
      action={<HiFiIconBtn name="plus" size={20}/>}
    >
      <div style={{ padding: '0 20px 14px' }}>
        <div className="h-h2">관심 기업 8 · <span style={{ color: 'var(--h-brand)' }}>오늘 새공고 8건</span></div>
        <div className="h-body-2" style={{ marginTop: 2 }}>로고 우상단 빨간 점 = 오늘 새 공고</div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 16px 16px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 10 }}>
          {companies.map(([logo, name, badge, hasAlarm, route], i) =>
            <div key={i}
              className="h-card flat"
              style={{ padding: 10, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4, position: 'relative', cursor: 'pointer', borderColor: badge > 0 ? 'var(--h-brand)' : 'var(--h-border)' }}
              onClick={() => nav.go(route)}>
              <div className="h-logo" style={{ background: badge > 0 ? 'var(--h-brand-soft)' : 'var(--h-bg-2)' }}>{logo}</div>
              <div className="h-body" style={{ fontSize: 13, fontWeight: 700, textAlign:'center', lineHeight: 1.1 }}>{name}</div>
              <div className="h-body-2" style={{ fontSize: 11 }}>공고 {badge}</div>
              {badge > 0 && (
                <div style={{
                  position:'absolute', top: 6, right: 6,
                  minWidth: 20, height: 20, padding: '0 5px', borderRadius: 999,
                  background:'var(--h-brand)', color:'#fff',
                  display:'flex', alignItems:'center', justifyContent:'center',
                  fontSize: 11, fontWeight: 800,
                  border: '2px solid #fff', boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                }}>{badge}</div>
              )}
              {hasAlarm && (
                <div style={{ position:'absolute', bottom: 4, left: 4, fontSize: 11 }}>🔔</div>
              )}
            </div>
          )}
          {/* 추가 버튼 */}
          <div className="h-card flat" style={{
            padding: 10, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
            gap: 4, border: '2px dashed var(--h-border-dark)', background: 'transparent', minHeight: 90, cursor: 'pointer'
          }}>
            <SkIcon name="plus" size={20} color="var(--h-text-3)" strokeWidth={2.2}/>
            <div className="h-body-2" style={{ fontSize: 11 }}>기업 추가</div>
          </div>
        </div>

        {/* 안내 카드 */}
        <div className="h-card brand" style={{ padding: 12, marginTop: 16 }}>
          <div className="h-row" style={{ gap: 10 }}>
            <Mascot size={40} expression="default"/>
            <div className="h-grow">
              <div className="h-body" style={{ fontWeight: 700 }}>로고를 누르면 그 회사 공고만 모아볼 수 있어요</div>
              <div className="h-body-2" style={{ fontSize: 12, marginTop: 2 }}>꾹 누르면 알림 끄기 / 삭제</div>
            </div>
          </div>
        </div>
      </div>

      <HiFiTabBar active="fav"/>
    </HiFiPhone>
  );
}

// ── 5. 검색 (최근/인기/직군별) ──────────────────────
function HiFi_Search() {
  const nav = useNav();
  return (
    <HiFiPhone title="검색">
      <div style={{ padding: '0 20px 8px' }}>
        <div onClick={() => nav.go('h_searchResults')} style={{
          display: 'flex', alignItems: 'center', gap: 10,
          padding: '12px 16px',
          background: 'var(--h-bg-2)',
          borderRadius: 14,
          cursor: 'pointer',
          border: '2px solid transparent'
        }}>
          <SkIcon name="search" size={18} color="var(--h-text)"/>
          <span className="h-body" style={{ flex: 1, fontWeight: 600, color: 'var(--h-text-2)' }}>기업명·직무·키워드</span>
          <SkIcon name="mic" size={18} color="var(--h-text-2)"/>
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 20px 16px' }}>
        <div className="h-row" style={{ justifyContent:'space-between' }}>
          <div className="h-h2">최근 검색</div>
          <button className="h-btn ghost sm" style={{ fontSize: 13 }}>전체삭제</button>
        </div>
        <div className="h-row" style={{ gap: 6, marginTop: 8, flexWrap:'wrap' }}>
          {['삼성전자', 'UX 디자이너', '대전', '신입 개발', 'LG'].map(k =>
            <span key={k} className="h-chip outline h-row" style={{ gap: 4, cursor:'pointer' }} onClick={() => nav.go('h_searchResults')}>
              {k} <SkIcon name="close" size={11} color="var(--h-text-3)"/>
            </span>
          )}
        </div>

        <div className="h-h2" style={{ marginTop: 22 }}>🔥 지금 인기 검색어</div>
        <div className="h-col" style={{ gap: 2, marginTop: 10 }}>
          {[
            ['1', '삼성전자 상반기 공채', '▲'],
            ['2', '네이버 백엔드', '▲'],
            ['3', 'LG에너지 R&D', 'NEW'],
            ['4', '현대차', '-'],
            ['5', '카카오 경력', '▼'],
          ].map(([n, k, t]) =>
            <div key={n} className="h-row" style={{ padding: '8px 0' }}>
              <span className="h-mono-num" style={{ width: 24, color: 'var(--h-brand)', fontSize: 15 }}>{n}</span>
              <span className="h-body h-grow" style={{ fontWeight: 600 }}>{k}</span>
              <span style={{
                fontSize: 11, fontWeight: 800,
                color: t === '▲' ? 'var(--h-new-shadow)' : t === '▼' ? 'var(--h-closing)' : t === 'NEW' ? 'var(--h-brand)' : 'var(--h-text-3)'
              }}>{t}</span>
            </div>
          )}
        </div>

        <div className="h-h2" style={{ marginTop: 22 }}>직군별 둘러보기</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 6, marginTop: 10 }}>
          {[
            'IT개발·데이터', '디자인', '마케팅·홍보·조사',
            '기획·전략', '회계·세무·재무', '인사·노무·HRD',
            '영업·판매·무역', '연구·R&D'
          ].map(j =>
            <button key={j} className="h-btn sm" style={{ padding: '10px 6px', fontSize: 12, textTransform: 'none' }}
              onClick={() => nav.go('h_searchResults')}>
              {j}
            </button>
          )}
        </div>

        <div className="h-h2" style={{ marginTop: 22 }}>추천 키워드</div>
        <div className="h-row" style={{ gap: 6, marginTop: 10, flexWrap: 'wrap' }}>
          {['#신입공채', '#수시채용', '#재택가능', '#복지좋은', '#성과급', '#스타트업'].map(k =>
            <span key={k} className="h-chip outline sm">{k}</span>
          )}
        </div>
      </div>
      <HiFiTabBar active="search"/>
    </HiFiPhone>
  );
}

// ── 6. 검색 결과 ──────────────────────
function HiFi_SearchResults() {
  const nav = useNav();
  return (
    <HiFiPhone
      title=""
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
    >
      <div style={{ padding: '0 20px 8px' }}>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 10,
          padding: '10px 16px',
          background: 'var(--h-bg-2)',
          borderRadius: 14,
        }}>
          <SkIcon name="search" size={18}/>
          <span className="h-body" style={{ flex: 1, fontWeight: 700 }}>삼성</span>
          <SkIcon name="close" size={14} color="var(--h-text-2)"/>
        </div>
      </div>

      <div style={{ padding: '12px 20px 0', borderBottom: '1px solid var(--h-border)' }}>
        <div className="h-row" style={{ gap: 6 }}>
          <span className="h-chip on">전체 12</span>
          <span className="h-chip outline">기업 4</span>
          <span className="h-chip outline">공고 8</span>
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '16px 20px' }}>
        <div className="h-caption">기업 (4)</div>
        <div className="h-col" style={{ gap: 0, marginTop: 8 }}>
          {[
            ['삼성', '삼성전자', 3, true],
            ['삼성', '삼성SDS', 1, false],
            ['삼성', '삼성바이오로직스', 0, false],
            ['삼성', '삼성생명', 0, false],
          ].map(([l, n, b, fav], i) =>
            <div key={i} className="h-row" style={{
              padding: '12px 0',
              borderBottom: i < 3 ? '1px solid var(--h-border)' : 'none',
              gap: 12,
              cursor: 'pointer'
            }} onClick={() => nav.go(b > 0 ? 'h_company' : 'h_companyEmpty')}>
              <div className="h-logo sm">{l}</div>
              <div className="h-grow">
                <div className="h-body" style={{ fontWeight: 700 }}>
                  <span style={{ background: 'var(--h-brand-soft)', color: 'var(--h-brand-dark)', padding: '0 2px', borderRadius: 2 }}>삼성</span>
                  {n.slice(2)}
                </div>
                {b > 0 && <div className="h-body-2" style={{ fontSize: 12 }}>오늘 공고 {b}건</div>}
              </div>
              {fav ? (
                <SkIcon name="heart" size={20} color="var(--h-brand)" strokeWidth={2.4}/>
              ) : (
                <button className="h-btn sm" style={{ padding: '6px 12px', fontSize: 12 }}>관심+</button>
              )}
            </div>
          )}
        </div>

        <div className="h-caption" style={{ marginTop: 22 }}>공고 (8)</div>
        <div className="h-col" style={{ gap: 10, marginTop: 8 }}>
          <HiFiJobCard kind="new" logo="삼성" company="삼성전자" role="2026 상반기 신입공채" dday="D-24" dateText="~6/15" onClick={() => nav.go('h_detail')}/>
          <HiFiJobCard kind="new" logo="삼성" company="삼성SDS" role="클라우드 신입사원" dday="D-20" dateText="~6/11" onClick={() => nav.go('h_detail')}/>
          <HiFiJobCard kind="update" logo="삼성" company="삼성전자" role="DS부문 경력직" dday="D-30" dateText="~6/21" onClick={() => nav.go('h_detail')}/>
        </div>
      </div>
      <HiFiTabBar active="search"/>
    </HiFiPhone>
  );
}

// ── 7. 메인 빈 상태 ──────────────────────
function HiFi_MainEmpty() {
  const nav = useNav();
  return (
    <HiFiPhone
      title="채용알리미"
      action={<HiFiIconBtn name="filter" onClick={() => nav.go('h_filter')}/>}
    >
      <div style={{ padding: '0 20px 8px' }}>
        <div className="h-row" style={{ alignItems:'flex-end' }}>
          <div className="h-grow">
            <div className="h-body-2">5월 22일 목요일</div>
            <div className="h-title" style={{ marginTop: 2 }}>
              오늘은 <span style={{ color: 'var(--h-text-2)' }}>조용한 날</span>
            </div>
          </div>
          <Mascot size={60} expression="sleep"/>
        </div>
      </div>

      <div style={{ padding: '6px 20px 10px' }}>
        <div style={{ display: 'flex', gap: 8 }}>
          <button className="h-chip outline" style={{ flex: 1, justifyContent: 'center', color: 'var(--h-text-3)' }}>NEW 0</button>
          <button className="h-chip outline" style={{ flex: 1, justifyContent: 'center' }}>UPDATE 1</button>
          <button className="h-chip outline" style={{ flex: 1, justifyContent: 'center' }}>CLOSING 2</button>
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '8px 20px 16px' }}>
        <div className="h-card brand" style={{ padding: 22, textAlign: 'center' }}>
          <Mascot size={80} expression="default"/>
          <div className="h-h2" style={{ marginTop: 10 }}>오늘 새 공고는 없어요</div>
          <div className="h-body-2" style={{ marginTop: 6, lineHeight: 1.4 }}>
            대부분 기업이 휴식 중이에요.<br/>
            관심 기업을 더 추가하면 더 자주 볼 수 있어요!
          </div>
          <button className="h-btn primary" style={{ marginTop: 16 }} onClick={() => nav.go('h_fav')}>+ 관심 기업 추가</button>
        </div>

        <div className="h-h2" style={{ marginTop: 22 }}>📌 챙겨봐야 할 공고</div>
        <div className="h-col" style={{ gap: 10, marginTop: 10 }}>
          <HiFiJobCard kind="closing" logo="현" company="현대자동차" role="신입사원 일반공채" dday="D-1" dateText="내일" onClick={() => nav.go('h_detail')}/>
          <HiFiJobCard kind="closing" logo="CJ" company="CJ제일제당" role="CJ 신입공채" dday="D-0" dateText="오늘" onClick={() => nav.go('h_detail')}/>
          <HiFiJobCard kind="update" logo="SK" company="SK하이닉스" role="2026 신입사원 채용" dday="D-11" dateText="~6/2" onClick={() => nav.go('h_detail')}/>
        </div>

        <div className="h-card flat" style={{ marginTop: 16, padding: 12, background: 'var(--h-bg-2)', border: 'none' }}>
          <div className="h-row" style={{ gap: 8 }}>
            <SkIcon name="refresh" size={16} color="var(--h-text-2)"/>
            <div className="h-body-2" style={{ fontWeight: 700 }}>다음 자동 수집: 내일 9:00</div>
          </div>
        </div>
      </div>

      <HiFiTabBar active="home"/>
    </HiFiPhone>
  );
}

// ── 8/9. 회사 상세 (공고 있음/없음) ──────────────────────
function HiFi_CompanyPage({ data }) {
  const nav = useNav();
  const hasPostings = data.postings && data.postings.length > 0;
  return (
    <HiFiPhone
      title=""
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
      action={<HiFiIconBtn name="share" size={18}/>}
    >
      <div style={{ flex: 1, overflowY: 'auto' }}>
        {/* 헤더 */}
        <div style={{ padding: '0 20px 16px', textAlign:'center' }}>
          <div className="h-logo lg" style={{ width: 80, height: 80, fontSize: 24, margin: '0 auto', background:'var(--h-brand-soft)', color: 'var(--h-brand-dark)' }}>
            {data.logo}
          </div>
          <div className="h-display" style={{ marginTop: 10, fontSize: 24 }}>{data.name}</div>
          <div className="h-body-2" style={{ marginTop: 4 }}>{data.sector}</div>
          <div className="h-row" style={{ justifyContent:'center', gap: 6, marginTop: 10, flexWrap:'wrap' }}>
            <span className="h-chip outline sm">{data.size}</span>
            <span className="h-chip outline sm">📍 {data.region}</span>
          </div>
          <div className="h-row" style={{ justifyContent:'center', gap: 8, marginTop: 14 }}>
            <button className={'h-btn sm' + (data.starred ? ' primary' : '')} style={{ padding: '8px 14px', fontSize: 13 }}>
              {data.starred ? '✓ 관심기업' : '+ 관심기업'}
            </button>
            <button className="h-btn sm" style={{ padding: '8px 14px', fontSize: 13 }}>
              <SkIcon name="link" size={14}/> 홈페이지
            </button>
          </div>
        </div>

        {/* 소개 */}
        <div style={{ padding: '0 20px 14px' }}>
          <div className="h-card">
            <div className="h-caption">회사 소개</div>
            <div className="h-body" style={{ marginTop: 6, lineHeight: 1.4 }}>{data.about}</div>
          </div>
        </div>

        {/* 통계 */}
        <div style={{ padding: '0 20px 16px' }}>
          <div className="h-row" style={{ gap: 8 }}>
            <div className="h-card flat" style={{ flex: 1, padding: 12, textAlign:'center' }}>
              <div className="h-body-2" style={{ fontSize: 11 }}>올해 신규</div>
              <div className="h-mono-num" style={{ fontSize: 22, color: 'var(--h-brand)', marginTop: 2 }}>{data.stats.thisYear}건</div>
            </div>
            <div className="h-card flat" style={{ flex: 1, padding: 12, textAlign:'center' }}>
              <div className="h-body-2" style={{ fontSize: 11 }}>평균 마감</div>
              <div className="h-mono-num" style={{ fontSize: 22, marginTop: 2 }}>{data.stats.avgClose}</div>
            </div>
            <div className="h-card flat" style={{ flex: 1, padding: 12, textAlign:'center' }}>
              <div className="h-body-2" style={{ fontSize: 11 }}>합격률</div>
              <div className="h-mono-num" style={{ fontSize: 22, color: 'var(--h-new-shadow)', marginTop: 2 }}>{data.stats.rate}</div>
            </div>
          </div>
        </div>

        {/* 진행중 공고 */}
        <div style={{ padding: '0 20px 16px' }}>
          <div className="h-row" style={{ justifyContent:'space-between' }}>
            <div className="h-h2">진행중인 공고</div>
            {hasPostings && <div className="h-body-2" style={{ fontSize: 12, fontWeight: 700 }}>{data.postings.length}건</div>}
          </div>
        </div>

        {hasPostings ? (
          <div style={{ padding: '0 20px 16px', display:'flex', flexDirection:'column', gap: 10 }}>
            {data.postings.map((p, i) =>
              <HiFiJobCard key={i} kind={p.kind} logo={data.logo} company={data.name} role={p.role} dday={p.dday} dateText="" onClick={() => nav.go('h_detail')}/>
            )}
          </div>
        ) : (
          <div style={{ padding: '0 20px 16px' }}>
            <div className="h-card flat" style={{ padding: 22, textAlign:'center', border: '2px dashed var(--h-border-dark)', background: 'transparent' }}>
              <Mascot size={64} expression="sleep"/>
              <div className="h-h2" style={{ marginTop: 8 }}>지금은 채용 공고가 없어요</div>
              <div className="h-body-2" style={{ marginTop: 6, lineHeight: 1.4 }}>
                새 공고가 뜨면 알려드릴게요.<br/>관심기업으로 등록되어 있어요 ✓
              </div>
              <button className="h-btn sm" style={{ marginTop: 14, padding: '8px 14px' }}>
                <SkIcon name="link" size={14}/> 채용 사이트 직접 보기
              </button>
            </div>

            <div className="h-h2" style={{ marginTop: 22 }}>최근 채용 이력</div>
            <div className="h-col" style={{ gap: 6, marginTop: 10 }}>
              {data.history && data.history.map((h, i) =>
                <div key={i} className="h-row" style={{ padding: 12, background: 'var(--h-bg-2)', borderRadius: 12, gap: 10, opacity: 0.85 }}>
                  <div className="h-grow">
                    <div className="h-body" style={{ fontWeight: 700 }}>{h.role}</div>
                    <div className="h-body-2" style={{ fontSize: 12 }}>{h.period}</div>
                  </div>
                  <span className="h-body-2" style={{ fontSize: 12, fontWeight: 700 }}>마감</span>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </HiFiPhone>
  );
}

const HIFI_SAMSUNG = {
  logo: '삼성', name: '삼성전자', sector: '반도체 · IT · 가전',
  region: '서울/수원/화성', size: '대기업', starred: true,
  about: '반도체(DS), 디스플레이, 모바일·생활가전(DX) 등 사업부문 운영. 글로벌 1위 메모리 반도체 기업.',
  stats: { thisYear: 24, avgClose: '3주', rate: '4%' },
  postings: [
    { kind: 'new', role: '2026 상반기 신입공채', dday: 'D-24' },
    { kind: 'new', role: 'DS 부문 메모리 R&D', dday: 'D-20' },
    { kind: 'update', role: 'DX 부문 SW 경력직', dday: 'D-35' },
    { kind: 'new', role: '글로벌 마케팅 (3년+)', dday: 'D-18' },
  ],
};

const HIFI_DOOSAN_EMPTY = {
  logo: '두산', name: '두산밥캣', sector: '건설기계 · 산업기계',
  region: '인천', size: '대기업', starred: true,
  about: '소형 건설장비 글로벌 1위. 북미 시장 매출 비중 70%.',
  stats: { thisYear: 8, avgClose: '2주', rate: '6%' },
  postings: [],
  history: [
    { role: '2026 상반기 신입공채', period: '5/1 ~ 5/14 마감' },
    { role: '재무 경력직', period: '4/15 ~ 4/30 마감' },
    { role: '글로벌 영업 (5년+)', period: '3/10 ~ 4/3 마감' },
  ]
};

function HiFi_CompanyDetail() { return <HiFi_CompanyPage data={HIFI_SAMSUNG}/>; }
function HiFi_CompanyDetailEmpty() { return <HiFi_CompanyPage data={HIFI_DOOSAN_EMPTY}/>; }

// ── 10. 저장한 공고 ──────────────────────
function HiFi_SavedPostings() {
  const nav = useNav();
  return (
    <HiFiPhone
      title="저장한 공고"
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
    >
      <div style={{ padding: '0 20px 12px' }}>
        <div className="h-body-2">북마크한 공고 <b style={{ color: 'var(--h-update-shadow)' }}>14개</b></div>
        <div className="h-row" style={{ gap: 6, marginTop: 10 }}>
          <span className="h-chip on">전체 14</span>
          <span className="h-chip outline">진행중 9</span>
          <span className="h-chip outline">마감 5</span>
        </div>
      </div>
      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 16px', display: 'flex', flexDirection: 'column', gap: 10 }}>
        <HiFiJobCard kind="new" logo="삼성" company="삼성전자" role="2026 상반기 신입공채" dday="D-24" dateText="~6/15" onClick={() => nav.go('h_detail')}/>
        <HiFiJobCard kind="new" logo="N" company="네이버" role="신입 백엔드 개발자" dday="D-19" dateText="~6/10" onClick={() => nav.go('h_detail')}/>
        <HiFiJobCard kind="update" logo="카" company="카카오" role="경력 프론트엔드 개발자" dday="D-6" dateText="~5/28" onClick={() => nav.go('h_detail')}/>
        <HiFiJobCard kind="new" logo="LG" company="LG에너지솔루션" role="연구개발(R&D) 신입" dday="D-16" dateText="~6/7" onClick={() => nav.go('h_detail')}/>
        <HiFiJobCard kind="closing" logo="현" company="현대자동차" role="신입사원 일반공채" dday="D-1" dateText="내일" onClick={() => nav.go('h_detail')}/>
        <HiFiJobCard kind="new" logo="쿠" company="쿠팡" role="백엔드 엔지니어" dday="D-12" dateText="~6/3" onClick={() => nav.go('h_detail')}/>
        <div className="h-card flat" style={{ padding: 14, textAlign:'center', border: '2px dashed var(--h-border-dark)', background: 'transparent' }}>
          <div className="h-body-2">+ 8개 더 있어요</div>
        </div>
      </div>
    </HiFiPhone>
  );
}

// ── 11. 알림 설정 ──────────────────────
function HiFi_NotifSettings() {
  const nav = useNav();
  const Item = ({ icon, title, sub, on = false }) => (
    <div className="h-row" style={{ padding: '14px 0', borderBottom: '1px solid var(--h-border)', gap: 12 }}>
      <span style={{ fontSize: 22 }}>{icon}</span>
      <div className="h-grow">
        <div className="h-body" style={{ fontWeight: 700 }}>{title}</div>
        <div className="h-body-2" style={{ fontSize: 12 }}>{sub}</div>
      </div>
      <div className={'h-toggle' + (on ? ' on' : '')}/>
    </div>
  );
  return (
    <HiFiPhone
      title="알림 설정"
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
    >
      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 16px' }}>
        <div className="h-row" style={{ gap: 12, alignItems:'center', padding: '6px 0 14px' }}>
          <Mascot size={50} expression="default"/>
          <div className="h-body-2" style={{ flex: 1, lineHeight: 1.4 }}>
            매일 같은 시간에 알려드려요. 시간은 자동 설정이에요.
          </div>
        </div>

        <div className="h-caption" style={{ marginTop: 14 }}>매일 정기 알림</div>
        <Item icon="☀️" title="아침 9:00" sub="어제 새로 뜬 공고 요약" on/>
        <Item icon="🌙" title="저녁 21:00" sub="마감 임박 공고 알림" on/>

        <div className="h-caption" style={{ marginTop: 22 }}>이벤트 알림</div>
        <Item icon="🆕" title="관심기업 새 공고" sub="등록 즉시 푸시" on/>
        <Item icon="🔥" title="마감 D-1" sub="저장한 공고 하루 전" on/>
        <Item icon="📝" title="공고 내용 변경" sub="자격/마감일 수정됨"/>
        <Item icon="🎯" title="AI 추천 공고" sub="관심사 매칭 공고"/>

        <div className="h-caption" style={{ marginTop: 22 }}>방해 금지</div>
        <Item icon="🌃" title="22:00 ~ 다음날 7:00" sub="이 시간엔 알림 안 와요"/>
      </div>
    </HiFiPhone>
  );
}

// ── 12. 바탕화면 위젯 설정 ──────────────────────
function HiFi_WidgetSettings() {
  const nav = useNav();
  const [size, setSize] = React.useState('medium');

  const Preview = () => (
    <div className="h-widget" style={{
      width: size === 'small' ? 120 : 260,
      height: size === 'small' ? 110 : size === 'medium' ? 90 : 200,
      display: 'flex', flexDirection: 'column', justifyContent: 'center'
    }}>
      {size === 'small' ? (
        <>
          <div className="h-row" style={{ gap: 6, alignItems:'center' }}>
            <Mascot size={24} expression="happy"/>
            <div className="h-body-2" style={{ fontSize: 10 }}>오늘 새</div>
          </div>
          <div className="h-mono-num" style={{ fontSize: 28, color:'var(--h-brand)', lineHeight: 1, marginTop: 4 }}>17</div>
          <div className="h-body-2" style={{ fontSize: 10 }}>마감 3건</div>
        </>
      ) : size === 'medium' ? (
        <div className="h-row" style={{ gap: 10, alignItems:'center' }}>
          <Mascot size={38} expression="happy"/>
          <div className="h-grow">
            <div className="h-body-2" style={{ fontSize: 10 }}>오늘</div>
            <div className="h-row" style={{ gap: 6, alignItems:'baseline' }}>
              <span className="h-mono-num" style={{ fontSize: 24, color:'var(--h-brand)', lineHeight: 1 }}>17</span>
              <span className="h-body-2" style={{ fontSize: 11 }}>+ 마감 3</span>
            </div>
          </div>
        </div>
      ) : (
        <>
          <div className="h-row" style={{ gap: 10 }}>
            <Mascot size={44} expression="happy"/>
            <div className="h-grow">
              <div className="h-body-2" style={{ fontSize: 11 }}>오늘 새 공고</div>
              <div className="h-row" style={{ gap: 6, alignItems:'baseline' }}>
                <span className="h-mono-num" style={{ fontSize: 28, color:'var(--h-brand)', lineHeight: 1 }}>17</span>
                <span className="h-body-2" style={{ fontSize: 12 }}>+ 마감 3</span>
              </div>
            </div>
          </div>
          <div style={{ height: 1, background: 'var(--h-border)', margin: '10px 0' }}/>
          <div className="h-col" style={{ gap: 4 }}>
            {[['삼성전자','D-24'], ['네이버','D-19'], ['LG에너지','D-16']].map(([c, d]) =>
              <div key={c} className="h-row" style={{ fontSize: 12 }}>
                <span style={{ fontWeight: 700, flex: 1 }}>{c}</span>
                <span style={{ fontWeight: 800, color: 'var(--h-brand)' }}>{d}</span>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );

  return (
    <HiFiPhone
      title="바탕화면 위젯"
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
    >
      <div style={{ flex: 1, overflowY: 'auto', padding: '6px 20px 16px' }}>
        <div className="h-row" style={{ padding: '12px 0' }}>
          <div className="h-body h-grow" style={{ fontWeight: 700 }}>위젯 사용</div>
          <div className="h-toggle on"/>
        </div>

        <div className="h-caption" style={{ marginTop: 14 }}>크기 선택</div>
        <div className="h-col" style={{ gap: 8, marginTop: 8 }}>
          {[
            ['small', 'Small · 2×1', '카운터만'],
            ['medium', 'Medium · 4×1', '한 줄 요약'],
            ['large', 'Large · 4×2', '핵심 공고 3개'],
          ].map(([id, t, sub]) =>
            <div key={id} onClick={() => setSize(id)} className="h-card flat" style={{
              padding: 12,
              display: 'flex', alignItems: 'center', gap: 12,
              borderColor: size === id ? 'var(--h-brand)' : 'var(--h-border)',
              background: size === id ? 'var(--h-brand-soft)' : 'var(--h-bg)',
              cursor: 'pointer'
            }}>
              <div style={{
                width: 22, height: 22, borderRadius: 999,
                background: size === id ? 'var(--h-brand)' : 'transparent',
                border: '2px solid ' + (size === id ? 'var(--h-brand)' : 'var(--h-border-dark)'),
                display: 'flex', alignItems: 'center', justifyContent: 'center'
              }}>
                {size === id && <SkIcon name="check" size={12} color="#fff" strokeWidth={3}/>}
              </div>
              <div className="h-grow">
                <div className="h-body" style={{ fontWeight: 700 }}>{t}</div>
                <div className="h-body-2" style={{ fontSize: 12 }}>{sub}</div>
              </div>
            </div>
          )}
        </div>

        <div className="h-caption" style={{ marginTop: 22 }}>미리보기</div>
        <div style={{
          padding: 24, marginTop: 8,
          background: 'linear-gradient(135deg, #2a1f3d 0%, #4a3457 100%)',
          borderRadius: 18,
          display: 'flex', justifyContent: 'center', alignItems: 'center',
          minHeight: 220
        }}>
          <Preview/>
        </div>

        <div className="h-card brand" style={{ padding: 12, marginTop: 16 }}>
          <div className="h-row" style={{ gap: 8 }}>
            <SkIcon name="sparkle" size={16} color="var(--h-brand)"/>
            <div className="h-body-2" style={{ flex: 1, fontWeight: 700, color: 'var(--h-brand-dark)' }}>
              바탕화면에 위젯 추가: 홈 길게 누르기 → 위젯 → 채용알리미
            </div>
          </div>
        </div>
      </div>
    </HiFiPhone>
  );
}

// ── 13. 관심 직군 ──────────────────────
function HiFi_JobInterests() {
  const nav = useNav();
  const [selected, setSelected] = React.useState({ 2: true, 5: true, 6: true });
  const toggle = (i) => setSelected(s => ({ ...s, [i]: !s[i] }));
  const allJobs = HIFI_JOB_CATEGORIES;
  const count = Object.values(selected).filter(Boolean).length;

  return (
    <HiFiPhone
      title="관심 직군"
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
    >
      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 16px' }}>
        <div className="h-body-2" style={{ padding: '4px 0 14px' }}>선택한 직군 위주로 새 공고를 알려드려요</div>

        <div className="h-caption">현재 선택 ({count}개)</div>
        <div className="h-row" style={{ gap: 6, marginTop: 8, flexWrap:'wrap' }}>
          {Object.entries(selected).filter(([_, v]) => v).map(([i]) =>
            <span key={i} className="h-chip on" style={{ cursor: 'pointer' }} onClick={() => toggle(+i)}>
              {allJobs[+i]} <SkIcon name="close" size={11} color="#fff"/>
            </span>
          )}
        </div>

        <div className="h-caption" style={{ marginTop: 22 }}>전체 직군 (21)</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 6, marginTop: 10 }}>
          {allJobs.map((j, i) =>
            <button key={j} onClick={() => toggle(i)}
              className={'h-btn sm' + (selected[i] ? ' primary' : '')}
              style={{ padding: '10px 6px', fontSize: 12, textTransform: 'none', lineHeight: 1.15 }}>
              {j}
            </button>
          )}
        </div>

        <div className="h-card yellow" style={{ padding: 12, marginTop: 22 }}>
          <div className="h-body" style={{ fontWeight: 700 }}>💡 팁</div>
          <div className="h-body-2" style={{ marginTop: 4, lineHeight: 1.4 }}>
            너무 많이 선택하면 공고가 쏟아져요. 3~5개가 적당해요.
          </div>
        </div>
      </div>
    </HiFiPhone>
  );
}

// ── 14. 피드백 ──────────────────────
function HiFi_Feedback() {
  const nav = useNav();
  const [type, setType] = React.useState('idea');
  return (
    <HiFiPhone
      title="피드백"
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
    >
      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 16px' }}>
        <div className="h-row" style={{ gap: 12, padding: '6px 0 14px', alignItems:'center' }}>
          <Mascot size={50} expression="wave"/>
          <div className="h-body-2" style={{ flex: 1, lineHeight: 1.4 }}>
            꽁이가 듣고 있어요! 뭐든 편하게 말해주세요.
          </div>
        </div>

        <div className="h-caption">종류</div>
        <div className="h-row" style={{ gap: 6, marginTop: 8 }}>
          {[
            ['idea', '💡 아이디어'],
            ['bug', '🐛 버그'],
            ['etc', '✏️ 기타'],
          ].map(([id, label]) =>
            <span key={id} onClick={() => setType(id)}
              className={'h-chip' + (type === id ? ' on' : ' outline')}
              style={{ flex: 1, justifyContent: 'center', cursor:'pointer' }}>
              {label}
            </span>
          )}
        </div>

        <div className="h-caption" style={{ marginTop: 22 }}>내용</div>
        <div className="h-card" style={{ marginTop: 8, minHeight: 130, padding: 14 }}>
          <div className="h-body-2" style={{ lineHeight: 1.5 }}>
            예) "○○ 기업이 추가됐으면 좋겠어요"<br/>
            예) "관심없는 공고도 너무 많이 떠요"
          </div>
        </div>

        <div className="h-caption" style={{ marginTop: 22 }}>연락처 (선택)</div>
        <div className="h-card" style={{ marginTop: 8, padding: 14 }}>
          <div className="h-body-2">답변 받을 이메일</div>
        </div>

        <button className="h-btn primary block lg" style={{ marginTop: 22 }}>
          보내기
        </button>

        <div className="h-body-2" style={{ marginTop: 10, textAlign:'center', fontSize: 12 }}>
          꽁이가 24시간 안에 답변드려요
        </div>
      </div>
    </HiFiPhone>
  );
}

Object.assign(window, {
  HiFi_Onb2, HiFi_Onb4Widget,
  HiFi_Filter, HiFi_Favorites,
  HiFi_Search, HiFi_SearchResults,
  HiFi_MainEmpty,
  HiFi_CompanyDetail, HiFi_CompanyDetailEmpty,
  HiFi_SavedPostings, HiFi_NotifSettings,
  HiFi_WidgetSettings, HiFi_JobInterests, HiFi_Feedback,
});
