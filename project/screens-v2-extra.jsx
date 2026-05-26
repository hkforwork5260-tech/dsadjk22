// screens-v2-extra.jsx — 빈상태, 푸시알림, 에러, 검색결과

// ── 1. 빈 상태 메인 (오늘 새 공고 0건) ──────────────────────
function V2_MainEmpty() {
  const nav = useNav();
  return (
    <AndroidPhone title="채용알리미" action={<span style={{cursor:'pointer'}} onClick={() => nav.go('filter')}><SkIcon name="filter" size={20}/></span>}>
      <div style={{ padding: '4px 18px 6px' }}>
        <div className="row" style={{ justifyContent:'space-between', alignItems:'flex-start' }}>
          <div>
            <div className="note muted2">5월 22일 목요일</div>
            <div className="t-xl b" style={{ lineHeight: 1.1 }}>
              오늘은 <span className="sk-uline brand">조용한</span> 날
            </div>
          </div>
          <Mascot size={56} expression="sleep"/>
        </div>
      </div>

      <div className="row" style={{ justifyContent:'center', gap: 6, marginTop: 8, padding: '0 16px' }}>
        <span className="chip sm">NEW 0</span>
        <span className="chip sm">UPDATE 1</span>
        <span className="chip sm">CLOSING 2</span>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 16px 12px', display:'flex', flexDirection:'column' }}>
        {/* 안내 카드 */}
        <div className="sk-box fill-brand" style={{ padding: 16, textAlign:'center' }}>
          <Mascot size={70} expression="default"/>
          <div className="t-lg b" style={{ marginTop: 8, lineHeight: 1.2 }}>
            오늘 새 공고는 없어요
          </div>
          <div className="note muted2" style={{ marginTop: 4, lineHeight: 1.3 }}>
            대부분 기업이 휴식 중이에요.<br/>
            관심 기업을 더 추가하면 더 자주 볼 수 있어요!
          </div>
          <button className="btn brand sm" style={{ marginTop: 10, padding:'8px 14px' }} onClick={() => nav.go('fav')}>
            + 관심 기업 추가
          </button>
        </div>

        {/* 그래도 있는 것들 */}
        <div className="t-md b" style={{ marginTop: 16 }}>📌 챙겨봐야 할 공고</div>
        <div className="col" style={{ gap: 8, marginTop: 8 }}>
          <V2_JobRow kind="closing" company="현대자동차" role="신입사원 일반공채" logo="현대" dday={{ text:'D-1', date:'내일' }} onClick={() => nav.go('detail')}/>
          <V2_JobRow kind="closing" company="CJ제일제당" role="CJ 신입공채" logo="CJ" dday={{ text:'D-0', date:'오늘' }} onClick={() => nav.go('detail')}/>
          <V2_JobRow kind="update" company="SK하이닉스" role="2026 신입사원 채용" logo="SK" dday={{ text:'D-11', date:'~6/2' }} onClick={() => nav.go('detail')}/>
        </div>

        <div className="sk-box dashed" style={{ marginTop: 14, padding: 10 }}>
          <div className="t-sm b row" style={{ gap: 6 }}>
            <SkIcon name="refresh" size={14}/> 다음 자동 수집: 내일 9:00
          </div>
        </div>
      </div>
      <TabBarV2 active="home"/>
    </AndroidPhone>
  );
}

// ── 2. 에러 상태 (수집 실패) ──────────────────────────
function V2_MainError() {
  const nav = useNav();
  return (
    <AndroidPhone title="채용알리미" action={<span style={{cursor:'pointer'}} onClick={() => nav.go('filter')}><SkIcon name="filter" size={20}/></span>}>
      <div style={{ padding: '4px 18px 6px' }}>
        <div className="row" style={{ justifyContent:'space-between', alignItems:'flex-start' }}>
          <div>
            <div className="note muted2">5월 22일 목요일</div>
            <div className="t-xl b" style={{ lineHeight: 1.1 }}>
              오늘 새 공고 <span style={{ color:'var(--brand)' }}>14건</span>
            </div>
          </div>
          <Mascot size={56} expression="sad"/>
        </div>
      </div>

      {/* 에러 배너 */}
      <div style={{ padding: '8px 16px 0' }}>
        <div className="sk-box" style={{
          padding: 12,
          background: 'var(--closing-soft)',
          borderColor: 'var(--closing)'
        }}>
          <div className="row" style={{ gap: 8, alignItems:'flex-start' }}>
            <span style={{ fontSize: 22 }}>⚠️</span>
            <div style={{ flex: 1 }}>
              <div className="t-md b" style={{ color: 'var(--closing)' }}>
                일부 기업 수집 실패
              </div>
              <div className="t-sm muted2" style={{ marginTop: 2 }}>
                현대차·포스코·CJ 등 3곳에서 사이트 접속 실패.<br/>
                다시 시도하거나 원본 사이트에서 직접 확인해보세요.
              </div>
              <div className="row" style={{ gap: 6, marginTop: 8 }}>
                <button className="btn sm" onClick={() => nav.go('main')}>다시 시도</button>
                <button className="btn sm">자세히 보기</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="row" style={{ justifyContent:'center', gap: 6, marginTop: 10, padding: '0 16px' }}>
        <span className="chip sm" style={{ background:'var(--new)', borderColor:'var(--new)', color:'#fff' }}>● NEW 14</span>
        <span className="chip sm">UPDATE 3</span>
        <span className="chip sm">CLOSING 2</span>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 16px 12px', display:'flex', flexDirection:'column', gap: 8 }}>
        <V2_JobRow kind="new" company="삼성전자" role="2026 상반기 신입공채" logo="삼성" dday={{ text:'D-24', date:'~6/15' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="new" company="네이버" role="신입 백엔드 개발자" logo="N" dday={{ text:'D-19', date:'~6/10' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="new" company="LG에너지솔루션" role="연구개발(R&D) 신입" logo="LG" dday={{ text:'D-16', date:'~6/7' }} onClick={() => nav.go('detail')}/>

        {/* 실패한 기업 위치에 placeholder */}
        <div className="sk-box dashed" style={{ padding: 12, opacity: 0.7 }}>
          <div className="row" style={{ gap: 10 }}>
            <div className="logo" style={{ background: 'var(--paper-2)' }}>?</div>
            <div style={{ flex: 1 }}>
              <div className="t-xs muted">현대자동차 · 수집 실패</div>
              <div className="t-md b" style={{ lineHeight: 1.15 }}>
                <span className="muted2">공고를 가져올 수 없어요</span>
              </div>
            </div>
            <SkIcon name="refresh" size={18} color="#8a8a8a"/>
          </div>
        </div>

        <V2_JobRow kind="new" company="카카오" role="신입 안드로이드 개발자" logo="카카오" dday={{ text:'D-14', date:'~6/5' }} onClick={() => nav.go('detail')}/>
      </div>
      <TabBarV2 active="home"/>
    </AndroidPhone>
  );
}

// ── 3. 푸시 알림 — 잠금화면 ──────────────────────────
function V2_LockScreen() {
  return (
    <div className="phone phone-android android-wallpaper">
      <div className="and-status">
        <span className="t-xs b">9:00</span>
        <span className="spacer"/>
        <span className="t-xs">📶</span>
        <span className="t-xs">▮</span>
      </div>

      <div className="and-clock">
        <div style={{ fontSize: 60, fontWeight: 700, lineHeight: 1 }}>9:00</div>
        <div style={{ fontSize: 14, opacity: 0.85, marginTop: 4 }}>5월 22일 목요일</div>
      </div>

      {/* 푸시 알림 (single, large) */}
      <div style={{ padding: '32px 14px 0' }}>
        <div className="notif-card" style={{ background:'rgba(255,255,255,0.96)', padding: 14 }}>
          <div className="ic-box" style={{ width: 38, height: 38, background:'var(--brand)' }}>
            <Mascot size={28} expression="wave"/>
          </div>
          <div style={{ flex: 1 }}>
            <div className="row" style={{ justifyContent:'space-between' }}>
              <div className="t-sm b">채용알리미</div>
              <div className="t-xs muted">지금</div>
            </div>
            <div className="t-md b" style={{ marginTop: 4, lineHeight: 1.2 }}>
              ☀️ 좋은 아침! 오늘 새 공고 17건
            </div>
            <div className="t-sm muted2" style={{ marginTop: 2, lineHeight: 1.25 }}>
              삼성전자 · 네이버 · LG에너지솔루션 외 14곳
            </div>
          </div>
        </div>

        {/* 다른 알림 */}
        <div className="notif-card" style={{ marginTop: 8, padding: 10, opacity: 0.85 }}>
          <div className="ic-box" style={{ background:'#5b8def', width: 32, height: 32 }}>💬</div>
          <div style={{ flex: 1 }}>
            <div className="row" style={{ justifyContent:'space-between' }}>
              <div className="t-sm b">메시지</div>
              <div className="t-xs muted">8:42</div>
            </div>
            <div className="t-sm muted2">엄마: 오늘 점심 뭐 먹어?</div>
          </div>
        </div>
      </div>

      {/* 안내 하단 */}
      <div style={{
        position:'absolute', bottom: 30, left: 0, right: 0,
        textAlign:'center', color:'#fff', fontSize: 13, opacity: 0.7,
        fontFamily: "'Gaegu', sans-serif"
      }}>
        ↑ 위로 밀어 잠금 해제
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

// ── 4. 푸시 알림 — 알림 센터 (펼쳐서) ──────────────────────────
function V2_NotifCenter() {
  return (
    <div className="phone phone-android" style={{
      background: 'rgba(20, 20, 30, 0.85)',
      backdropFilter: 'blur(20px)',
      borderRadius: 36,
      border: '2.4px solid var(--ink)',
      overflow: 'hidden',
      position: 'relative'
    }}>
      <div className="and-status" style={{ color:'#fff' }}>
        <span className="t-xs b">9:01</span>
        <span className="spacer"/>
        <span className="t-xs">📶</span>
        <span className="t-xs">▮</span>
      </div>

      {/* 빠른 설정 토글 4개 */}
      <div style={{ padding: '10px 14px 6px', display:'flex', gap: 8 }}>
        {[['📶','Wi-Fi'], ['🌙','방해금지'], ['🔦','조명'], ['📷','카메라']].map(([e,l]) =>
          <div key={l} style={{
            flex: 1,
            background: 'rgba(255,255,255,0.15)',
            border: '1px solid rgba(255,255,255,0.2)',
            borderRadius: 12,
            padding: '8px 4px',
            textAlign:'center',
            color:'#fff',
            fontFamily: "'Gaegu', sans-serif"
          }}>
            <div style={{ fontSize: 18 }}>{e}</div>
            <div style={{ fontSize: 10, opacity: 0.85, marginTop: 2 }}>{l}</div>
          </div>
        )}
      </div>

      {/* 날짜 */}
      <div style={{ padding: '8px 14px 4px', color:'#fff', fontFamily:"'Gaegu', sans-serif", fontSize: 13, opacity: 0.85 }}>
        오늘 · 5월 22일 목요일
      </div>

      {/* 알림 그룹 */}
      <div style={{ flex: 1, overflow: 'auto', padding: '4px 14px 14px' }}>
        {/* 채용알리미 그룹 헤더 */}
        <div className="row" style={{ gap: 8, padding: '6px 4px', color:'#fff', fontFamily:"'Gaegu', sans-serif" }}>
          <div style={{ width: 18, height: 18, borderRadius: 5, background:'var(--brand)', display:'flex', alignItems:'center', justifyContent:'center' }}>
            <Mascot size={16} expression="default"/>
          </div>
          <span className="t-sm b">채용알리미</span>
          <span className="t-xs muted" style={{ color:'rgba(255,255,255,0.6)' }}>3개 알림</span>
        </div>

        {/* 푸시 1 — 아침 브리핑 */}
        <div className="notif-card" style={{ marginTop: 6, padding: 12 }}>
          <div className="ic-box" style={{ width: 34, height: 34 }}>
            <Mascot size={24} expression="wave"/>
          </div>
          <div style={{ flex: 1 }}>
            <div className="row" style={{ justifyContent:'space-between' }}>
              <div className="t-sm b">아침 브리핑</div>
              <div className="t-xs muted">9:00</div>
            </div>
            <div className="t-md b" style={{ marginTop: 2, lineHeight: 1.2 }}>
              ☀️ 오늘 새 공고 17건
            </div>
            <div className="t-sm muted2" style={{ marginTop: 1 }}>
              삼성 · 네이버 · LG 외 14곳
            </div>
          </div>
        </div>

        {/* 푸시 2 — 관심기업 즉시 */}
        <div className="notif-card" style={{ marginTop: 6, padding: 12 }}>
          <div className="ic-box" style={{ width: 34, height: 34, background:'var(--new)' }}>⭐</div>
          <div style={{ flex: 1 }}>
            <div className="row" style={{ justifyContent:'space-between' }}>
              <div className="t-sm b">관심기업 새 공고</div>
              <div className="t-xs muted">7:23</div>
            </div>
            <div className="t-md b" style={{ marginTop: 2, lineHeight: 1.2 }}>
              네이버 — 신입 백엔드 개발자
            </div>
            <div className="t-sm muted2" style={{ marginTop: 1 }}>
              마감 6/10 · 판교 · 학사+
            </div>
          </div>
        </div>

        {/* 푸시 3 — 어제 저녁 마감임박 */}
        <div className="notif-card" style={{ marginTop: 6, padding: 12, opacity: 0.85 }}>
          <div className="ic-box" style={{ width: 34, height: 34, background:'var(--update)' }}>⏰</div>
          <div style={{ flex: 1 }}>
            <div className="row" style={{ justifyContent:'space-between' }}>
              <div className="t-sm b">저녁 마감 알림</div>
              <div className="t-xs muted">어제 21:00</div>
            </div>
            <div className="t-md b" style={{ marginTop: 2, lineHeight: 1.2 }}>
              ⏰ 저장한 2개 공고 마감 임박
            </div>
            <div className="t-sm muted2" style={{ marginTop: 1 }}>
              현대차 D-1 · CJ제일제당 D-0
            </div>
          </div>
        </div>

        {/* 다른 앱 알림 그룹 */}
        <div className="row" style={{ gap: 8, padding: '14px 4px 6px', color:'#fff', fontFamily:"'Gaegu', sans-serif" }}>
          <div style={{ width: 18, height: 18, borderRadius: 5, background:'#5b8def', display:'flex', alignItems:'center', justifyContent:'center', fontSize: 11 }}>💬</div>
          <span className="t-sm b">메시지</span>
        </div>
        <div className="notif-card" style={{ padding: 10, opacity: 0.85 }}>
          <div className="ic-box" style={{ background:'#5b8def', width: 30, height: 30, fontSize: 14 }}>💬</div>
          <div style={{ flex: 1 }}>
            <div className="row" style={{ justifyContent:'space-between' }}>
              <div className="t-sm b">엄마</div>
              <div className="t-xs muted">8:42</div>
            </div>
            <div className="t-sm muted2">오늘 점심 뭐 먹어?</div>
          </div>
        </div>
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

// ── 5. 검색 결과 ──────────────────────────
function V2_SearchResults() {
  const nav = useNav();
  return (
    <AndroidPhone
      title=""
      leading={<span style={{cursor:'pointer', marginRight: 4}} onClick={() => nav.back()}><SkIcon name="chev-l" size={22}/></span>}
    >
      <div style={{ padding: '0 16px 8px' }}>
        <div className="sk-box row" style={{ padding: '8px 12px', gap: 8 }}>
          <SkIcon name="search" size={16}/>
          <span className="t-md b">삼성</span>
          <span className="spacer"/>
          <SkIcon name="close" size={14} color="#8a8a8a"/>
        </div>
      </div>

      <div className="chip-row" style={{ borderBottom: '1.5px dashed var(--ink-3)' }}>
        <span className="chip sm on-brand">전체 12</span>
        <span className="chip sm">기업 4</span>
        <span className="chip sm">공고 8</span>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 16px' }}>
        <div className="t-sm b muted">기업 (4)</div>
        <div className="col" style={{ gap: 4, marginTop: 6 }}>
          {[
            ['삼성','삼성전자', 3, true],
            ['삼성','삼성SDS', 1, false],
            ['삼성','삼성바이오로직스', 0, false],
            ['삼성','삼성생명', 0, false],
          ].map(([l,n,b,fav],i) =>
            <div key={i} className="row" style={{ padding: 8, borderBottom: '1.5px dashed var(--ink-3)', gap: 10 }}>
              <div className="logo sm">{l}</div>
              <div style={{ flex: 1 }}>
                <div className="t-sm"><b className="sk-uline brand">삼성</b>{n.slice(2)}</div>
                {b > 0 && <div className="t-xs muted">오늘 공고 {b}건</div>}
              </div>
              {fav ? (
                <SkIcon name="heart" size={18} color="var(--brand)"/>
              ) : (
                <button className="btn sm" style={{ padding: '4px 8px' }}>관심+</button>
              )}
            </div>
          )}
        </div>

        <div className="t-sm b muted" style={{ marginTop: 16 }}>공고 (8)</div>
        <div className="col" style={{ gap: 8, marginTop: 6 }}>
          <V2_JobRow kind="new" company="삼성전자" role="2026 상반기 신입공채" logo="삼성" dday={{ text:'D-24', date:'~6/15' }} onClick={() => nav.go('detail')}/>
          <V2_JobRow kind="new" company="삼성SDS" role="클라우드 신입사원" logo="삼성" dday={{ text:'D-20', date:'~6/11' }} onClick={() => nav.go('detail')}/>
          <V2_JobRow kind="update" company="삼성전자" role="DS부문 경력직" logo="삼성" dday={{ text:'D-30', date:'~6/21' }} onClick={() => nav.go('detail')}/>
        </div>
      </div>
      <TabBarV2 active="search"/>
    </AndroidPhone>
  );
}

Object.assign(window, {
  V2_MainEmpty, V2_MainError, V2_LockScreen, V2_NotifCenter, V2_SearchResults
});

// ── 회사 정보 + 채용공고 ───────────────────────────────
// 관심기업 그리드/리스트에서 회사 클릭 시 진입
function V2_CompanyPage({ data }) {
  const nav = useNav();
  const hasPostings = data.postings && data.postings.length > 0;

  return (
    <AndroidPhone
      title=""
      leading={<span style={{cursor:'pointer', marginRight: 4}} onClick={() => nav.back()}><SkIcon name="chev-l" size={22}/></span>}
      action={<SkIcon name="share" size={18}/>}
    >
      <div style={{ flex: 1, overflowY: 'auto' }}>
        {/* 회사 헤더 */}
        <div style={{ padding: '0 18px 16px', textAlign:'center' }}>
          <div className="logo lg" style={{ width: 80, height: 80, fontSize: 24, margin: '0 auto', background:'var(--brand-soft)' }}>
            {data.logo}
          </div>
          <div className="t-2xl b" style={{ marginTop: 10, lineHeight: 1.1 }}>{data.name}</div>
          <div className="t-sm muted2" style={{ marginTop: 2 }}>{data.sector}</div>
          <div className="row" style={{ justifyContent:'center', gap: 6, marginTop: 8, flexWrap:'wrap' }}>
            <span className="chip sm">{data.size}</span>
            <span className="chip sm">📍 {data.region}</span>
          </div>
          <div className="row" style={{ justifyContent:'center', gap: 8, marginTop: 12 }}>
            <button className={'btn sm' + (data.starred ? ' brand' : '')} style={{ padding: '6px 14px' }}>
              {data.starred ? '✓ 관심기업' : '+ 관심기업'}
            </button>
            <button className="btn sm" style={{ padding: '6px 14px' }}>
              <SkIcon name="link" size={14}/> 홈페이지
            </button>
          </div>
        </div>

        {/* 회사 소개 */}
        <div className="sk-box" style={{ margin: '0 18px 14px' }}>
          <div className="t-xs b muted" style={{ marginBottom: 4 }}>회사 소개</div>
          <div className="t-sm" style={{ lineHeight: 1.4 }}>{data.about}</div>
        </div>

        {/* 통계 */}
        <div className="row" style={{ padding: '0 18px 14px', gap: 8 }}>
          <div className="sk-box" style={{ flex: 1, textAlign:'center', padding: 10 }}>
            <div className="t-xs muted">올해 신규</div>
            <div className="t-xl b" style={{ color:'var(--brand)' }}>{data.stats.thisYear}건</div>
          </div>
          <div className="sk-box" style={{ flex: 1, textAlign:'center', padding: 10 }}>
            <div className="t-xs muted">평균 마감</div>
            <div className="t-xl b">{data.stats.avgClose}</div>
          </div>
          <div className="sk-box" style={{ flex: 1, textAlign:'center', padding: 10 }}>
            <div className="t-xs muted">합격률</div>
            <div className="t-xl b" style={{ color:'var(--good)' }}>{data.stats.rate}</div>
          </div>
        </div>

        {/* 진행중인 공고 */}
        <div style={{ padding: '0 18px 8px' }}>
          <div className="row" style={{ justifyContent:'space-between' }}>
            <div className="t-md b">진행중인 공고</div>
            {hasPostings && <span className="t-sm muted2">{data.postings.length}건</span>}
          </div>
        </div>

        {hasPostings ? (
          <div style={{ padding: '0 16px 16px', display:'flex', flexDirection:'column', gap: 8 }}>
            {data.postings.map((p, i) =>
              <div key={i} className={`card hl-${p.kind}`} style={{ padding: 12, cursor:'pointer' }} onClick={() => nav.go('detail')}>
                <div className="row" style={{ gap: 6 }}>
                  <span className={`label ${p.kind}`} style={{ fontSize: 10, padding: '1px 6px' }}>{p.kind === 'new' ? 'NEW' : p.kind === 'update' ? 'UPDATE' : 'CLOSING'}</span>
                  <span className="t-xs muted">{p.region}</span>
                  <span className="spacer"/>
                  <span className={`label outline-${p.kind} b`}>{p.dday}</span>
                </div>
                <div className="t-md b" style={{ marginTop: 4, lineHeight: 1.15 }}>{p.role}</div>
                <div className="row" style={{ justifyContent:'space-between', marginTop: 6 }}>
                  <div className="row" style={{ gap: 6 }}>
                    {p.tags.map(t => <span key={t} className="chip sm">{t}</span>)}
                  </div>
                  <div className="row" style={{ gap: 4, alignItems:'center' }}>
                    <SkIcon name="link" size={12} color="#8a8a8a"/>
                    <span className="t-xs muted">원본</span>
                  </div>
                </div>
              </div>
            )}
          </div>
        ) : (
          // 빈 상태
          <div style={{ padding: '4px 18px 20px' }}>
            <div className="sk-box dashed" style={{ padding: 20, textAlign:'center' }}>
              <Mascot size={64} expression="sleep"/>
              <div className="t-md b" style={{ marginTop: 8 }}>지금은 채용 공고가 없어요</div>
              <div className="note muted2" style={{ marginTop: 4, lineHeight: 1.3 }}>
                새 공고가 뜨면 알려드릴게요.<br/>
                관심기업으로 등록되어 있어요 ✓
              </div>
              <button className="btn sm" style={{ marginTop: 12, padding: '6px 14px' }}>
                <SkIcon name="link" size={12}/> 채용 사이트 직접 보기
              </button>
            </div>

            <div className="t-md b" style={{ marginTop: 18 }}>최근 채용 이력</div>
            <div className="col" style={{ gap: 6, marginTop: 8 }}>
              {data.history && data.history.map((h, i) =>
                <div key={i} className="row sk-box" style={{ padding: 10, gap: 8, opacity: 0.7 }}>
                  <div style={{ flex: 1 }}>
                    <div className="t-sm">{h.role}</div>
                    <div className="t-xs muted">{h.period}</div>
                  </div>
                  <span className="t-xs muted">마감</span>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </AndroidPhone>
  );
}

const SAMSUNG = {
  logo: '삼성',
  name: '삼성전자',
  sector: '반도체 · IT · 가전',
  region: '서울/수원/화성',
  size: '대기업',
  starred: true,
  about: '반도체(DS), 디스플레이, 모바일·생활가전(DX) 등 사업부문을 운영. 글로벌 1위 메모리 반도체 기업.',
  stats: { thisYear: 24, avgClose: '3주', rate: '약 4%' },
  postings: [
    { kind: 'new', role: '2026 상반기 신입공채', region: '서울/수원', dday: 'D-24', tags: ['#신입공채', '#대규모'] },
    { kind: 'new', role: 'DS 부문 메모리 R&D', region: '화성', dday: 'D-20', tags: ['#반도체', '#R&D'] },
    { kind: 'update', role: 'DX 부문 SW 경력직', region: '수원', dday: 'D-35', tags: ['#경력', '#SW'] },
    { kind: 'new', role: '글로벌 마케팅 (3년+)', region: '서울', dday: 'D-18', tags: ['#마케팅', '#경력'] },
  ],
};

const DOOSAN_EMPTY = {
  logo: '두산',
  name: '두산밥캣',
  sector: '건설기계 · 산업기계',
  region: '인천',
  size: '대기업',
  starred: true,
  about: '소형 건설장비 글로벌 1위. 북미 시장 매출 비중 70%.',
  stats: { thisYear: 8, avgClose: '2주', rate: '약 6%' },
  postings: [],
  history: [
    { role: '2026 상반기 신입공채', period: '5/1 ~ 5/14 마감' },
    { role: '재무 경력직', period: '4/15 ~ 4/30 마감' },
    { role: '글로벌 영업 (5년+)', period: '3/10 ~ 4/3 마감' },
  ]
};

function V2_CompanyDetail() { return <V2_CompanyPage data={SAMSUNG}/>; }
function V2_CompanyDetailEmpty() { return <V2_CompanyPage data={DOOSAN_EMPTY}/>; }

window.V2_CompanyDetail = V2_CompanyDetail;
window.V2_CompanyDetailEmpty = V2_CompanyDetailEmpty;

// ── 6. 찾아보기 (Discover) — 릴스/인스타 스타일 ──────────────
const DISCOVER_DECK = [
  { logo:'두산', name:'두산에너빌리티', sector:'에너지·중공업', region:'창원', size:'대기업', recent:5,
    posting:{ kind:'new', role:'2026 신입공채 (기계/전기/화학)', dday:'D-22', date:'~6/13 18:00',
      summary:'배터리·원전·풍력 등 다양한 분야에서 신입 모집. 학사 이상.',
      exp:'신입', edu:'학사+', loc:'창원' },
    tags:['친환경', '원전', '터빈'] },
  { logo:'KT', name:'KT', sector:'통신·IT', region:'서울/판교', size:'대기업', recent:3,
    posting:{ kind:'new', role:'AI Lab 연구원 (신입)', dday:'D-18', date:'~6/9 23:59',
      summary:'GPT 기반 한국어 LLM 연구·개발. 석사+ 우대.',
      exp:'신입', edu:'학사+', loc:'판교' },
    tags:['AI', '5G', '클라우드'] },
  { logo:'아모', name:'아모레퍼시픽', sector:'화장품·뷰티', region:'서울', size:'대기업', recent:2,
    posting:{ kind:'new', role:'마케팅·브랜드 매니저', dday:'D-14', date:'~6/5 18:00',
      summary:'설화수·라네즈 등 메인 브랜드 마케팅 담당. 3년+ 경력.',
      exp:'3년+', edu:'학사+', loc:'서울 용산' },
    tags:['뷰티', '글로벌', 'K-beauty'] },
  { logo:'한화', name:'한화시스템', sector:'방산·IT', region:'성남', size:'대기업', recent:4,
    posting:{ kind:'new', role:'SW 엔지니어 (신입)', dday:'D-25', date:'~6/16 18:00',
      summary:'위성·방산 SW 개발. C++/Python 기본. 보안의식 필수.',
      exp:'신입', edu:'학사+', loc:'성남' },
    tags:['방산', '위성', '보안'] },
  { logo:'쿠팡', name:'쿠팡', sector:'커머스·물류', region:'서울', size:'대기업', recent:7,
    posting:{ kind:'new', role:'백엔드 엔지니어 (신입)', dday:'D-12', date:'~6/3 23:59',
      summary:'대규모 트래픽 처리. Java/Kotlin · MSA. 영어 가능자 우대.',
      exp:'신입', edu:'학사+', loc:'서울 송파' },
    tags:['커머스', '대규모', '성장'] },
];

function V2_Discover() {
  const [companyFav, setCompanyFav] = React.useState({});
  const [postingSaved, setPostingSaved] = React.useState({});
  const favCount = Object.values(companyFav).filter(Boolean).length;
  const saveCount = Object.values(postingSaved).filter(Boolean).length;

  return (
    <AndroidPhone
      title="찾아보기"
      action={<span style={{cursor:'pointer'}}><SkIcon name="filter" size={20}/></span>}
    >
      <div style={{ padding: '0 18px 8px' }}>
        <div className="row" style={{ alignItems:'center' }}>
          <div className="note muted2" style={{ flex: 1 }}>관심사 매칭으로 추천해드려요</div>
          <span className="chip sm on-brand" style={{ fontSize: 11 }}>❤️ {favCount} · 🔖 {saveCount}</span>
        </div>
      </div>

      <div style={{
        flex: 1,
        overflowY: 'scroll',
        scrollSnapType: 'y mandatory',
        scrollBehavior: 'smooth'
      }}>
        {DISCOVER_DECK.map((data, i) => {
          const fav = !!companyFav[data.name];
          const saved = !!postingSaved[data.posting.role];
          return (
            <div key={i} style={{
              height: '100%',
              minHeight: '100%',
              scrollSnapAlign: 'start',
              scrollSnapStop: 'always',
              padding: '4px 14px 14px',
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
                <button className="btn block sm" style={{ marginTop: 10 }}>상세 보기</button>
                <div className="note center muted2" style={{ marginTop: 6, fontSize: 13, opacity: i === DISCOVER_DECK.length - 1 ? 0 : 0.7 }}>↓ 다음 공고로 스크롤</div>
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
          <div className="t-2xl b" style={{ marginTop: 10, color: 'var(--brand-deep)' }}>오늘은 여기까지!</div>
          <div className="note muted2" style={{ marginTop: 4 }}>내일 또 새로운 공고가 기다릴게요</div>
          <div className="row" style={{ gap: 14, marginTop: 16 }}>
            <div className="sk-box" style={{ padding: 10, textAlign:'center', minWidth: 80 }}>
              <SkIcon name="heart" size={18} color="var(--brand)"/>
              <div className="t-2xl b" style={{ color:'var(--brand)', marginTop: 4 }}>{favCount}</div>
              <div className="t-xs muted">관심기업</div>
            </div>
            <div className="sk-box" style={{ padding: 10, textAlign:'center', minWidth: 80 }}>
              <SkIcon name="bookmark" size={18} color="var(--update)"/>
              <div className="t-2xl b" style={{ color:'var(--update)', marginTop: 4 }}>{saveCount}</div>
              <div className="t-xs muted">공고 저장</div>
            </div>
          </div>
        </div>
      </div>

      <TabBarV2 active="discover"/>
    </AndroidPhone>
  );
}

// 토스트 애니메이션 (한 번만)
if (typeof document !== 'undefined' && !document.getElementById('discover-styles')) {
  const s = document.createElement('style');
  s.id = 'discover-styles';
  s.textContent = `
    @keyframes protoToast {
      0% { opacity: 0; transform: translateY(-10px); }
      15% { opacity: 1; transform: translateY(0); }
      85% { opacity: 1; transform: translateY(0); }
      100% { opacity: 0; transform: translateY(-6px); }
    }
  `;
  document.head.appendChild(s);
}

window.V2_Discover = V2_Discover;
