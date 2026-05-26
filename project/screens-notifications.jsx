// screens-notifications.jsx — 알림 설정 3종

// A: 단순 토글 + 시간 선택 리스트
function NotifA() {
  const Item = ({ icon, title, sub, on }) => (
    <div className="row" style={{ padding: '12px 0', borderBottom: '1.5px dashed var(--ink-3)' }}>
      <div style={{ flex: 1 }}>
        <div className="t-md b row" style={{ gap: 6 }}>{icon} {title}</div>
        {sub && <div className="t-xs muted">{sub}</div>}
      </div>
      <span className={'toggle' + (on ? ' on' : '')}/>
    </div>
  );
  return (
    <Phone showHeader title="알림 설정">
      <div style={{ flex: 1, overflowY: 'auto', padding: '14px 16px' }}>
        <div className="t-xl b">알림 설정 ⚙️</div>
        <div className="note muted2">언제 어떤 알림을 받을지 골라주세요</div>

        <div className="t-sm b muted" style={{ marginTop: 16 }}>매일 정기 알림</div>
        <Item icon="🌅" title="아침 브리핑" sub="매일 오전 9시 · 어제 새공고 요약" on={true}/>
        <Item icon="🌙" title="저녁 마감 알림" sub="매일 오후 6시 · 마감 임박 공고" on={true}/>

        <div className="t-sm b muted" style={{ marginTop: 16 }}>이벤트 알림</div>
        <Item icon="🆕" title="관심기업 새 공고" sub="등록 즉시 푸시" on={true}/>
        <Item icon="🔥" title="마감 임박 (D-1)" sub="저장한 공고 마감 하루 전" on={true}/>
        <Item icon="📝" title="공고 내용 변경" sub="자격/마감일 등 수정됨" on={false}/>
        <Item icon="🎯" title="딱맞는 공고 발견" sub="AI 추천 (실험)" on={false}/>

        <div className="sk-box fill-note" style={{ marginTop: 16 }}>
          <div className="row" style={{ gap: 8 }}>
            <Mascot size={40} expression="happy"/>
            <div className="note" style={{ flex: 1, fontSize: 14 }}>
              듀오링고처럼 매일 알림으로 챙겨줄게요! 안 보면 슬퍼해요...
            </div>
          </div>
        </div>
      </div>
    </Phone>
  );
}

// B: 카드형 (카테고리별 큰 카드)
function NotifB() {
  return (
    <Phone showHeader title="알림">
      <div style={{ flex: 1, overflowY: 'auto', padding: 14 }}>
        <div className="t-xl b">알림 관리</div>

        {/* 카드 1 */}
        <div className="sk-box fill-toss" style={{ marginTop: 14 }}>
          <div className="row" style={{ justifyContent:'space-between' }}>
            <div className="t-md b row" style={{ gap: 6 }}>📅 매일 알림</div>
            <span className="toggle on"/>
          </div>
          <div className="t-sm muted2" style={{ marginTop: 4 }}>매일 같은 시간 요약 받기</div>
          <div className="div"/>
          <div className="row" style={{ gap: 6, flexWrap:'wrap' }}>
            <span className="chip sm toss-on">아침 9:00</span>
            <span className="chip sm">점심 12:30</span>
            <span className="chip sm toss-on">저녁 18:00</span>
            <span className="chip sm">밤 22:00</span>
            <span className="chip sm">+ 사용자 설정</span>
          </div>
        </div>

        {/* 카드 2 */}
        <div className="sk-box" style={{ marginTop: 12 }}>
          <div className="row" style={{ justifyContent:'space-between' }}>
            <div className="t-md b row" style={{ gap: 6 }}>⭐ 관심기업 알림</div>
            <span className="toggle on"/>
          </div>
          <div className="t-sm muted2" style={{ marginTop: 4 }}>등록한 8개 기업 새 공고 즉시</div>
          <div className="div"/>
          <div className="row" style={{ gap: 6, alignItems:'center' }}>
            <span className="chk on"><SkIcon name="check" size={12} color="#fff"/></span>
            <span className="t-sm">새 공고만 (UPDATE는 제외)</span>
          </div>
        </div>

        {/* 카드 3 */}
        <div className="sk-box" style={{ marginTop: 12 }}>
          <div className="row" style={{ justifyContent:'space-between' }}>
            <div className="t-md b row" style={{ gap: 6 }}>⏰ 마감임박 알림</div>
            <span className="toggle on"/>
          </div>
          <div className="t-sm muted2" style={{ marginTop: 4 }}>저장한 공고의 마감 전 알림</div>
          <div className="div"/>
          <div className="row" style={{ gap: 6 }}>
            <span className="chip sm on">D-3</span>
            <span className="chip sm on">D-1</span>
            <span className="chip sm">당일</span>
          </div>
        </div>

        {/* 카드 4 */}
        <div className="sk-box" style={{ marginTop: 12 }}>
          <div className="row" style={{ justifyContent:'space-between' }}>
            <div className="t-md b row" style={{ gap: 6 }}>🔕 방해금지</div>
            <span className="toggle"/>
          </div>
          <div className="t-sm muted2" style={{ marginTop: 4 }}>22시 ~ 다음날 8시 알림 꺼둠</div>
        </div>
      </div>
    </Phone>
  );
}

// C: 캐릭터 안내 풍 (단계적 + 친근)
function NotifC() {
  return (
    <Phone showHeader title="알림 설정">
      <div style={{ flex: 1, overflowY: 'auto', padding: 14 }}>
        <div className="sk-box fill-note" style={{ padding: 14, textAlign:'center' }}>
          <Mascot size={70} expression="wave"/>
          <div className="t-lg b" style={{ marginTop: 6 }}>알림으로 챙겨드릴까요?</div>
          <div className="note muted2">매일 안 보면 까먹기 쉬워요 ✏️</div>
        </div>

        {/* 단계 1 */}
        <div className="row" style={{ marginTop: 14, alignItems:'flex-start', gap: 10 }}>
          <div className="logo sm" style={{ background:'var(--toss)', color:'#fff', borderColor:'var(--toss)' }}>1</div>
          <div style={{ flex: 1 }}>
            <div className="t-md b">언제 받을까요?</div>
            <div className="row" style={{ gap: 6, marginTop: 6, flexWrap:'wrap' }}>
              <span className="chip sm toss-on">🌅 아침 9시</span>
              <span className="chip sm">☀️ 점심</span>
              <span className="chip sm toss-on">🌙 저녁 6시</span>
            </div>
          </div>
        </div>

        {/* 단계 2 */}
        <div className="row" style={{ marginTop: 14, alignItems:'flex-start', gap: 10 }}>
          <div className="logo sm" style={{ background:'var(--toss)', color:'#fff', borderColor:'var(--toss)' }}>2</div>
          <div style={{ flex: 1 }}>
            <div className="t-md b">어떤 걸 알려드릴까요?</div>
            <div className="col" style={{ gap: 6, marginTop: 6 }}>
              <div className="row sk-box" style={{ padding: 8, gap: 8 }}>
                <span className="chk on"><SkIcon name="check" size={12} color="#fff"/></span>
                <span className="t-sm" style={{ flex: 1 }}>관심기업에 새 공고가 떴을 때</span>
              </div>
              <div className="row sk-box" style={{ padding: 8, gap: 8 }}>
                <span className="chk on"><SkIcon name="check" size={12} color="#fff"/></span>
                <span className="t-sm" style={{ flex: 1 }}>저장한 공고가 마감되기 전 (D-1)</span>
              </div>
              <div className="row sk-box" style={{ padding: 8, gap: 8 }}>
                <span className="chk"/>
                <span className="t-sm" style={{ flex: 1 }}>공고 내용이 변경됐을 때</span>
              </div>
            </div>
          </div>
        </div>

        {/* 단계 3 */}
        <div className="row" style={{ marginTop: 14, alignItems:'flex-start', gap: 10 }}>
          <div className="logo sm" style={{ background:'var(--paper-2)' }}>3</div>
          <div style={{ flex: 1 }}>
            <div className="t-md b muted2">바탕화면 위젯 (선택)</div>
            <div className="note muted2">바로가기 앱에서 빨간 점으로 표시</div>
          </div>
        </div>

        <button className="btn primary block" style={{ marginTop: 16 }}>설정 저장</button>
      </div>
    </Phone>
  );
}

window.NotifA = NotifA;
window.NotifB = NotifB;
window.NotifC = NotifC;
