// screens-v2-subpages.jsx — 마이페이지 서브페이지들

// 공통: 뒤로가기 + 타이틀 헬퍼
function SubHeader({ title, action }) {
  const nav = useNav();
  return {
    title,
    action,
    leading: <span style={{cursor:'pointer', marginRight: 4}} onClick={() => nav.back()}><SkIcon name="chev-l" size={22}/></span>
  };
}

// ── 1. 저장한 공고 ──────────────────────────────────
function V2_SavedPostings() {
  const nav = useNav();
  return (
    <AndroidPhone {...SubHeader({ title: '저장한 공고' })}>
      <div style={{ padding: '4px 18px 8px' }}>
        <div className="t-sm muted2">북마크한 공고 <b style={{ color:'var(--update)' }}>14개</b></div>
        <div className="row" style={{ gap: 6, marginTop: 8 }}>
          <span className="chip sm on">전체 14</span>
          <span className="chip sm">진행중 9</span>
          <span className="chip sm">마감 5</span>
        </div>
      </div>
      <div style={{ flex: 1, overflowY: 'auto', padding: '4px 16px 12px', display:'flex', flexDirection:'column', gap: 8 }}>
        <V2_JobRow kind="new" company="삼성전자" role="2026 상반기 신입공채" logo="삼성" dday={{ text:'D-24', date:'~6/15' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="new" company="네이버" role="신입 백엔드 개발자" logo="N" dday={{ text:'D-19', date:'~6/10' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="update" company="카카오" role="경력 프론트엔드 개발자" logo="카카오" dday={{ text:'D-6', date:'~5/28' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="new" company="LG에너지솔루션" role="연구개발(R&D) 신입" logo="LG" dday={{ text:'D-16', date:'~6/7' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="closing" company="현대자동차" role="신입사원 일반공채" logo="현대" dday={{ text:'D-1', date:'내일' }} onClick={() => nav.go('detail')}/>
        <V2_JobRow kind="new" company="쿠팡" role="백엔드 엔지니어" logo="쿠팡" dday={{ text:'D-12', date:'~6/3' }} onClick={() => nav.go('detail')}/>

        <div className="sk-box dashed" style={{ marginTop: 4, padding: 12, textAlign:'center' }}>
          <div className="note muted2">+ 8개 더 있어요</div>
        </div>
      </div>
    </AndroidPhone>
  );
}

// ── 2. 좋아요한 공고 ──────────────────────────────────
function V2_LikedPostings() {
  const nav = useNav();
  return (
    <AndroidPhone {...SubHeader({ title: '좋아요한 공고' })}>
      <div style={{ padding: '4px 18px 8px' }}>
        <div className="row" style={{ alignItems:'center', gap: 8 }}>
          <Mascot size={36} expression="happy"/>
          <div className="t-sm muted2">
            찾아보기에서 ❤️ 누른 <b style={{ color:'var(--brand)' }}>23개</b>
          </div>
        </div>
      </div>
      <div style={{ flex: 1, overflowY: 'auto', padding: '4px 16px 12px', display:'flex', flexDirection:'column', gap: 8 }}>
        {/* 좋아요 한 공고들 - heart icon 강조 */}
        {[
          ['두산','두산에너빌리티','2026 신입공채 (기계/전기/화학)','D-22','~6/13','new'],
          ['KT','KT','AI Lab 연구원 (신입)','D-18','~6/9','new'],
          ['아모','아모레퍼시픽','마케팅·브랜드 매니저','D-14','~6/5','new'],
          ['한화','한화시스템','SW 엔지니어 (신입)','D-25','~6/16','new'],
          ['쿠팡','쿠팡','백엔드 엔지니어 (신입)','D-12','~6/3','new'],
        ].map(([logo, comp, role, dday, date, kind],i) =>
          <div key={i} className="card" style={{ padding: '10px 12px', cursor:'pointer', position:'relative' }} onClick={() => nav.go('detail')}>
            <div style={{ position:'absolute', top: 8, right: 10 }}>
              <SkIcon name="heart" size={16} color="var(--brand)"/>
            </div>
            <div className="row" style={{ gap: 10 }}>
              <div className="logo">{logo}</div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div className="t-xs muted">{comp}</div>
                <div className="t-md b" style={{ marginTop: 2, lineHeight: 1.15 }}>{role}</div>
              </div>
              <div style={{ textAlign:'right' }}>
                <div className="t-sm b" style={{ color:'var(--new)' }}>{dday}</div>
                <div className="t-xs muted">{date}</div>
              </div>
            </div>
          </div>
        )}

        <div className="sk-box dashed" style={{ marginTop: 4, padding: 12, textAlign:'center' }}>
          <div className="note muted2">+ 18개 더 있어요</div>
        </div>
      </div>
    </AndroidPhone>
  );
}

// ── 3. 알림 설정 ──────────────────────────────────
function V2_NotifSettings() {
  return (
    <AndroidPhone {...SubHeader({ title: '알림 설정' })}>
      <div style={{ flex: 1, overflowY: 'auto', padding: '8px 18px 14px' }}>
        <div className="row" style={{ gap: 10, alignItems:'center' }}>
          <Mascot size={50} expression="default"/>
          <div className="t-sm muted2" style={{ flex: 1, lineHeight: 1.25 }}>
            매일 같은 시간에 알려드려요. 시간은 자동 설정이에요.
          </div>
        </div>

        <div className="t-md b muted" style={{ marginTop: 16 }}>매일 정기 알림</div>
        <div className="col" style={{ gap: 0, marginTop: 6 }}>
          <div className="row" style={{ padding: '12px 0', borderBottom: '1.5px dashed var(--ink-3)' }}>
            <div style={{ flex: 1 }}>
              <div className="t-md b">☀️ 아침 9:00</div>
              <div className="t-xs muted">어제 새로 뜬 공고 요약</div>
            </div>
            <span className="toggle on-brand on"/>
          </div>
          <div className="row" style={{ padding: '12px 0', borderBottom: '1.5px dashed var(--ink-3)' }}>
            <div style={{ flex: 1 }}>
              <div className="t-md b">🌙 저녁 21:00</div>
              <div className="t-xs muted">마감 임박 공고 알림</div>
            </div>
            <span className="toggle on-brand on"/>
          </div>
        </div>

        <div className="t-md b muted" style={{ marginTop: 16 }}>이벤트 알림</div>
        <div className="col" style={{ gap: 0, marginTop: 6 }}>
          {[
            ['🆕','관심기업 새 공고','등록 즉시 푸시', true],
            ['🔥','마감 D-1','저장한 공고 하루 전', true],
            ['📝','공고 내용 변경','자격/마감일 수정됨', false],
            ['🎯','AI 추천 공고','관심사 매칭 공고', false],
          ].map(([e,t,sub,on],i) =>
            <div key={i} className="row" style={{ padding: '12px 0', borderBottom: '1.5px dashed var(--ink-3)' }}>
              <div style={{ flex: 1 }}>
                <div className="t-md b row" style={{ gap: 6 }}>{e} {t}</div>
                <div className="t-xs muted">{sub}</div>
              </div>
              <span className={'toggle on-brand' + (on ? ' on' : '')}/>
            </div>
          )}
        </div>

        <div className="t-md b muted" style={{ marginTop: 16 }}>방해 금지</div>
        <div className="row" style={{ padding: '12px 0' }}>
          <div style={{ flex: 1 }}>
            <div className="t-md b">22:00 ~ 다음날 7:00</div>
            <div className="t-xs muted">이 시간엔 알림 안 와요</div>
          </div>
          <span className="toggle"/>
        </div>
      </div>
    </AndroidPhone>
  );
}

// ── 4. 바탕화면 위젯 설정 ──────────────────────────────────
function V2_WidgetSettings() {
  const [size, setSize] = React.useState('medium');
  const PreviewWidget = () => (
    <div style={{
      background: '#fffdf7',
      border: '2px solid #1a1a1a',
      borderRadius: 14,
      padding: 10,
      fontFamily: "'Gaegu', sans-serif",
      width: size === 'small' ? 110 : size === 'medium' ? 240 : 240,
      height: size === 'small' ? 100 : size === 'medium' ? 80 : 160,
      display: 'flex', flexDirection: 'column'
    }}>
      {size === 'small' ? (
        <>
          <div className="row" style={{ gap: 4, alignItems:'center' }}>
            <Mascot size={22} expression="happy"/>
            <div style={{ fontSize: 10, opacity: 0.7 }}>오늘 새 공고</div>
          </div>
          <div style={{ fontSize: 24, fontWeight: 700, color:'var(--brand)', lineHeight: 1, marginTop: 4 }}>17</div>
          <div style={{ fontSize: 10, opacity: 0.6 }}>마감 3건</div>
        </>
      ) : (
        <div className="row" style={{ gap: 8, alignItems:'center', flex: 1 }}>
          <Mascot size={36} expression="happy"/>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 10, opacity: 0.6 }}>오늘</div>
            <div className="row" style={{ gap: 4, alignItems:'baseline' }}>
              <span style={{ fontSize: 22, fontWeight: 700, color:'var(--brand)', lineHeight: 1 }}>17</span>
              <span style={{ fontSize: 11 }}>+ 마감 3</span>
            </div>
            {size === 'large' && (
              <div className="t-xs muted" style={{ marginTop: 4 }}>삼성·네이버·LG 포함</div>
            )}
          </div>
        </div>
      )}
    </div>
  );
  return (
    <AndroidPhone {...SubHeader({ title: '바탕화면 위젯' })}>
      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 18px 14px' }}>
        <div className="row" style={{ justifyContent:'space-between' }}>
          <div className="t-md b">위젯 사용</div>
          <span className="toggle on-brand on"/>
        </div>

        <div className="t-md b muted" style={{ marginTop: 16 }}>크기 선택</div>
        <div className="col" style={{ gap: 8, marginTop: 8 }}>
          {[
            ['small', 'Small · 2×1', '카운터만'],
            ['medium', 'Medium · 4×1', '한 줄 요약'],
            ['large', 'Large · 4×2', '핵심 공고 3개'],
          ].map(([id, t, sub]) =>
            <div
              key={id}
              onClick={() => setSize(id)}
              className={'sk-box row' + (size === id ? ' fill-brand' : '')}
              style={{ padding: 10, gap: 10, cursor:'pointer', borderColor: size === id ? 'var(--brand)' : 'var(--ink)' }}
            >
              <div className="chk" style={{
                background: size === id ? 'var(--brand)' : 'transparent',
                borderColor: size === id ? 'var(--brand)' : 'var(--ink)'
              }}>
                {size === id && <SkIcon name="check" size={12} color="#fff"/>}
              </div>
              <div style={{ flex: 1 }}>
                <div className="t-sm b">{t}</div>
                <div className="t-xs muted">{sub}</div>
              </div>
            </div>
          )}
        </div>

        <div className="t-md b muted" style={{ marginTop: 16 }}>미리보기</div>
        <div className="sk-box" style={{
          padding: 16, background:'linear-gradient(135deg, #2a1f3d 0%, #4a3457 100%)',
          display:'flex', justifyContent:'center', alignItems:'center', minHeight: 180
        }}>
          <PreviewWidget/>
        </div>

        <div className="sk-box dashed" style={{ marginTop: 14, padding: 10 }}>
          <div className="t-xs b row" style={{ gap: 6 }}>
            <SkIcon name="sparkle" size={12} color="var(--brand)"/>
            바탕화면에 위젯 추가하기
          </div>
          <div className="t-xs muted" style={{ marginTop: 2 }}>홈 길게 누르기 → 위젯 → 채용알리미</div>
        </div>
      </div>
    </AndroidPhone>
  );
}

// ── 5. 관심 직군 ──────────────────────────────────
function V2_JobInterests() {
  const allJobs = [
    '기획·전략', '마케팅·홍보·조사', '회계·세무·재무',
    '인사·노무·HRD', '총무·법무·사무', 'IT개발·데이터',
    '디자인', '영업·판매·무역', '고객상담·TM',
    '구매·자재·물류', '상품기획·MD', '운전·운송·배송',
    '서비스', '생산', '건설·건축',
    '의료', '연구·R&D', '교육',
    '미디어·문화·스포츠', '금융·보험', '공공·복지'
  ];
  const selectedIdx = [2, 5, 6]; // 회계·세무·재무, IT개발·데이터, 디자인
  const selected = selectedIdx.map(i => allJobs[i]);
  return (
    <AndroidPhone {...SubHeader({ title: '관심 직군' })}>
      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 18px 14px' }}>
        <div className="note muted2">선택한 직군 위주로 새 공고를 알려드려요</div>

        <div className="t-md b muted" style={{ marginTop: 14 }}>현재 선택 ({selected.length}개)</div>
        <div className="row" style={{ gap: 6, marginTop: 6, flexWrap:'wrap' }}>
          {selected.map(j =>
            <span key={j} className="chip sm on-brand row" style={{ gap: 4 }}>{j} <SkIcon name="close" size={10} color="#fff"/></span>
          )}
        </div>

        <div className="t-md b muted" style={{ marginTop: 18 }}>전체 직군 (21)</div>
        <div style={{ display:'grid', gridTemplateColumns: '1fr 1fr', gap: 6, marginTop: 8 }}>
          {allJobs.map((j, i) =>
            <div key={j} className={'chip sm' + (selectedIdx.includes(i) ? ' on-brand' : '')}
                 style={{ justifyContent:'center', padding: '10px 6px', fontSize: 12, textAlign:'center', lineHeight: 1.15 }}>
              {j}
            </div>
          )}
        </div>

        <div className="sk-box fill-note" style={{ marginTop: 18, padding: 10 }}>
          <div className="t-sm b">💡 팁</div>
          <div className="t-xs muted2" style={{ marginTop: 2, lineHeight: 1.3 }}>
            너무 많이 선택하면 공고가 쏟아져요. 3~5개가 적당해요.
          </div>
        </div>
      </div>
    </AndroidPhone>
  );
}

// ── 6. 피드백 보내기 ──────────────────────────────────
function V2_Feedback() {
  const [type, setType] = React.useState('idea');
  return (
    <AndroidPhone {...SubHeader({ title: '피드백' })}>
      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 18px 14px' }}>
        <div className="row" style={{ gap: 10, alignItems:'center' }}>
          <Mascot size={50} expression="wave"/>
          <div className="t-sm muted2" style={{ flex: 1, lineHeight: 1.25 }}>
            꽁이가 듣고 있어요! 뭐든 편하게 말해주세요.
          </div>
        </div>

        <div className="t-md b muted" style={{ marginTop: 16 }}>종류</div>
        <div className="row" style={{ gap: 6, marginTop: 6 }}>
          {[
            ['idea', '💡 아이디어'],
            ['bug', '🐛 버그'],
            ['etc', '✏️ 기타'],
          ].map(([id, t]) =>
            <span
              key={id}
              onClick={() => setType(id)}
              className={'chip' + (type === id ? ' on-brand' : '')}
              style={{ cursor:'pointer' }}>{t}</span>
          )}
        </div>

        <div className="t-md b muted" style={{ marginTop: 16 }}>내용</div>
        <div className="sk-box" style={{ padding: 12, minHeight: 140 }}>
          <div className="note muted" style={{ lineHeight: 1.4 }}>
            예) "○○ 기업이 추가됐으면 좋겠어요"<br/>
            예) "관심없는 공고도 너무 많이 떠요"
          </div>
        </div>

        <div className="t-md b muted" style={{ marginTop: 16 }}>연락처 (선택)</div>
        <div className="sk-box" style={{ padding: '10px 12px' }}>
          <div className="t-sm muted">답변 받을 이메일</div>
        </div>

        <button className="btn brand block" style={{ marginTop: 18 }}>보내기</button>

        <div className="note muted2 center" style={{ marginTop: 8 }}>
          꽁이가 24시간 안에 답변드려요 ✏️
        </div>
      </div>
    </AndroidPhone>
  );
}

// ── 7. 앱 정보 ──────────────────────────────────
function V2_About() {
  return (
    <AndroidPhone {...SubHeader({ title: '앱 정보' })}>
      <div style={{ flex: 1, overflowY: 'auto', padding: '12px 18px 14px' }}>
        <div className="center" style={{ padding: 18 }}>
          <Mascot size={100} expression="happy"/>
          <div className="t-2xl b" style={{ marginTop: 8 }}>채용알리미</div>
          <div className="note muted2">매일 새 공고만, 간단하게.</div>
          <div className="t-xs muted" style={{ marginTop: 4 }}>v0.1.0 · 베타</div>
        </div>

        <div className="sk-box fill-brand" style={{ padding: 12, marginTop: 6 }}>
          <div className="t-md b">📅 매일 자동 수집</div>
          <div className="t-sm muted2" style={{ marginTop: 4, lineHeight: 1.3 }}>
            저녁 6시에 공공데이터·기업 채용사이트에서 신규/변경/마감 공고를 자동으로 모아드려요. AI가 핵심 내용을 요약해줍니다.
          </div>
        </div>

        <div className="col" style={{ marginTop: 14, gap: 0 }}>
          {[
            ['📜', '서비스 이용약관'],
            ['🔒', '개인정보 처리방침'],
            ['📂', '오픈소스 라이선스'],
            ['📢', '공지사항'],
            ['📨', '문의 메일'],
          ].map(([e, t], i) =>
            <div key={i} className="row" style={{ padding: '12px 4px', borderBottom: '1.5px dashed var(--ink-3)', gap: 12 }}>
              <span style={{ fontSize: 16 }}>{e}</span>
              <div style={{ flex: 1 }} className="t-sm b">{t}</div>
              <SkIcon name="chev" size={14} color="#8a8a8a"/>
            </div>
          )}
        </div>

        <div className="center muted t-xs" style={{ marginTop: 16, lineHeight: 1.4 }}>
          © 2026 채용알리미<br/>
          Made with ☕ and Claude
        </div>
      </div>
    </AndroidPhone>
  );
}

Object.assign(window, {
  V2_SavedPostings, V2_LikedPostings, V2_NotifSettings,
  V2_WidgetSettings, V2_JobInterests, V2_Feedback, V2_About,
});
