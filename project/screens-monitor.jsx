// screens-monitor.jsx — 수집 모니터링 3종 (변수 많은 정보서칭 대응)
// 사용자 우려: "공고 스타일이 다양해서 템플릿 분석보다 Claude가 유기적으로 분석해야"
// → 매 수집의 성공/실패/신뢰도를 투명하게 노출하는 화면

// A: 오늘 수집 현황 (성공/실패/대기 한눈에)
function MonitorA() {
  return (
    <Phone showHeader title="수집 현황">
      <div style={{ flex: 1, overflowY: 'auto', padding: 14 }}>
        <div className="row" style={{ alignItems:'flex-start', gap: 10 }}>
          <Mascot size={50} expression="default"/>
          <div style={{ flex: 1 }}>
            <div className="t-lg b">오늘 18:00 수집 완료</div>
            <div className="note muted2">자동으로 매일 새 공고를 찾아드려요</div>
          </div>
        </div>

        {/* 통계 카드 3개 */}
        <div className="row" style={{ gap: 8, marginTop: 14 }}>
          <div className="sk-box fill-toss center" style={{ flex: 1, padding: 8 }}>
            <div className="t-2xl b" style={{ color: 'var(--toss)' }}>17</div>
            <div className="t-xs b">신규</div>
          </div>
          <div className="sk-box fill-update center" style={{ flex: 1, padding: 8 }}>
            <div className="t-2xl b" style={{ color: 'var(--update)' }}>4</div>
            <div className="t-xs b">변경</div>
          </div>
          <div className="sk-box fill-closing center" style={{ flex: 1, padding: 8 }}>
            <div className="t-2xl b" style={{ color: 'var(--closing)' }}>3</div>
            <div className="t-xs b">마감</div>
          </div>
        </div>

        {/* 신뢰도 바 */}
        <div className="sk-box" style={{ marginTop: 12 }}>
          <div className="row" style={{ justifyContent:'space-between' }}>
            <div className="t-sm b">AI 분석 신뢰도</div>
            <div className="t-sm b" style={{ color:'var(--good)' }}>94%</div>
          </div>
          <div className="bar" style={{ marginTop: 6 }}><i style={{ width: '94%', background: 'var(--good)' }}/></div>
          <div className="t-xs muted" style={{ marginTop: 4 }}>이미지 공고 5건, PDF 첨부 7건 분석됨</div>
        </div>

        {/* 기업별 진행 상황 */}
        <div className="t-md b" style={{ marginTop: 16 }}>기업별 수집 상태</div>
        <div className="col" style={{ gap: 6, marginTop: 8 }}>
          {[
            ['삼성','삼성전자', 'ok', '3건 발견 · 17:58'],
            ['네이버','네이버', 'ok', '2건 발견 · 17:58'],
            ['LG','LG에너지', 'ok', '1건 발견 · 17:59'],
            ['SK','SK하이닉스', 'warn', '이미지만 있음 · AI 분석'],
            ['CJ','CJ제일제당', 'ok', '변경 감지 · 17:59'],
            ['현대','현대자동차', 'fail', '사이트 접속 실패 · 재시도'],
            ['포스','포스코', 'pending', '대기 중...'],
          ].map(([l, n, s, msg], i) => {
            const color = s === 'ok' ? 'var(--good)' : s === 'warn' ? 'var(--update)' : s === 'fail' ? 'var(--closing)' : 'var(--ink-3)';
            const icon = s === 'ok' ? '✓' : s === 'warn' ? '!' : s === 'fail' ? '×' : '…';
            return (
              <div key={i} className="row sk-box" style={{ padding: 8, gap: 8 }}>
                <div className="logo sm">{l}</div>
                <div style={{ flex: 1 }}>
                  <div className="t-sm b">{n}</div>
                  <div className="t-xs muted">{msg}</div>
                </div>
                <div style={{
                  width: 22, height: 22, borderRadius: 999, border: '1.6px solid '+color,
                  color, display:'flex', alignItems:'center', justifyContent:'center',
                  fontWeight: 700, fontSize: 13, fontFamily:'Gaegu', background: 'var(--paper)'
                }}>{icon}</div>
              </div>
            );
          })}
        </div>

        <div className="sk-box dashed" style={{ marginTop: 12, padding: 10 }}>
          <div className="t-sm b row" style={{ gap: 6 }}><SkIcon name="refresh" size={14}/> 다음 수집: 내일 오전 9:00</div>
        </div>
      </div>
    </Phone>
  );
}

// B: 분석 신뢰도 중심 (Claude가 잘 못 읽었을 수도 있어요)
function MonitorB() {
  const Conf = ({ pct }) => {
    const color = pct >= 90 ? 'var(--good)' : pct >= 70 ? 'var(--update)' : 'var(--closing)';
    return (
      <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
        <div style={{ width: 50, height: 6, border: '1.4px solid var(--ink)', borderRadius: 4, background: 'var(--paper)', overflow: 'hidden' }}>
          <div style={{ width: pct + '%', height: '100%', background: color }}/>
        </div>
        <span className="t-xs b" style={{ color }}>{pct}%</span>
      </div>
    );
  };
  return (
    <Phone showHeader title="AI 분석 리포트">
      <div style={{ flex: 1, overflowY: 'auto', padding: 14 }}>
        <div className="t-xl b">AI 분석 결과 ✨</div>
        <div className="note muted2">Claude가 공고를 어떻게 읽었는지 확인하세요</div>

        <div className="sk-box fill-note" style={{ marginTop: 12, padding: 10 }}>
          <div className="t-sm b">⚠️ 검토 필요 (3건)</div>
          <div className="t-xs muted2" style={{ marginTop: 2 }}>분석 신뢰도가 낮아 사람 확인이 좋아요</div>
        </div>

        <div className="col" style={{ gap: 8, marginTop: 12 }}>
          {[
            ['SK하이닉스','이미지 공고', 62, 'warn'],
            ['두산에너빌','HWP 첨부만', 71, 'warn'],
            ['CJ대한통운','PDF 분석', 88, 'ok'],
            ['삼성전자','HTML 직접추출', 98, 'ok'],
            ['네이버','HTML 직접추출', 99, 'ok'],
            ['LG에너지','PDF 분석', 95, 'ok'],
          ].map(([n, src, pct, s], i) =>
            <div key={i} className="sk-box" style={{ padding: 10 }}>
              <div className="row" style={{ justifyContent:'space-between', alignItems:'flex-start' }}>
                <div>
                  <div className="t-sm b">{n}</div>
                  <div className="t-xs muted">소스: {src}</div>
                </div>
                {pct < 75 && <span className="chip sm" style={{ borderColor:'var(--update)', color:'var(--update)' }}>확인필요</span>}
              </div>
              <div style={{ marginTop: 8 }}><Conf pct={pct}/></div>
              {pct < 75 && (
                <div className="t-xs muted2" style={{ marginTop: 6, padding: 6, background:'var(--paper-2)', borderRadius: 6 }}>
                  💬 "이미지에서 마감일을 정확히 못 읽었어요. 원본 확인 추천"
                </div>
              )}
            </div>
          )}
        </div>

        <button className="btn block" style={{ marginTop: 14 }}>모두 다시 분석 🔄</button>
      </div>
    </Phone>
  );
}

// C: 미니멀 - 카드 한 장 요약 (일반 사용자용)
function MonitorC() {
  return (
    <Phone showHeader title="내 정보">
      <div style={{ flex: 1, overflowY: 'auto', padding: 14 }}>
        {/* 프로필 카드 */}
        <div className="sk-box fill-toss" style={{ padding: 14 }}>
          <div className="row" style={{ gap: 10 }}>
            <Mascot size={60} expression="happy"/>
            <div style={{ flex: 1 }}>
              <div className="t-lg b">잘하고 있어요!</div>
              <div className="note muted2">12일째 매일 확인 중 🔥</div>
            </div>
          </div>
          <div className="div"/>
          <div className="row" style={{ justifyContent:'space-around', textAlign:'center' }}>
            <div>
              <div className="t-2xl b" style={{ color:'var(--toss)' }}>87</div>
              <div className="t-xs muted">본 공고</div>
            </div>
            <div>
              <div className="t-2xl b" style={{ color:'var(--update)' }}>14</div>
              <div className="t-xs muted">저장</div>
            </div>
            <div>
              <div className="t-2xl b" style={{ color:'var(--closing)' }}>5</div>
              <div className="t-xs muted">지원</div>
            </div>
          </div>
        </div>

        {/* 오늘 수집 요약 */}
        <div className="sk-box" style={{ marginTop: 12 }}>
          <div className="row" style={{ justifyContent:'space-between' }}>
            <div className="t-md b">오늘 18:00 자동 수집</div>
            <span className="label" style={{ background:'var(--good-soft)', borderColor:'var(--good)', color:'var(--good)' }}>✓ 완료</span>
          </div>
          <div className="t-sm muted2" style={{ marginTop: 4 }}>40개 기업 · 17개 신규공고 발견</div>
        </div>

        {/* 메뉴 리스트 */}
        <div className="col" style={{ marginTop: 12, gap: 0 }}>
          {[
            ['🔔','알림 설정','매일 9시 · 18시'],
            ['🎯','관심 직군','개발 · 디자인'],
            ['⭐','관심 기업','8개 기업'],
            ['📊','수집 상세 리포트','신뢰도 94%'],
            ['💬','피드백 보내기',''],
            ['ℹ️','앱 정보','v0.1.0 · 베타'],
          ].map(([e,t,sub],i) =>
            <div key={i} className="row" style={{ padding: '12px 4px', borderBottom: '1.5px dashed var(--ink-3)', gap: 12 }}>
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
      <TabBar active="me"/>
    </Phone>
  );
}

window.MonitorA = MonitorA;
window.MonitorB = MonitorB;
window.MonitorC = MonitorC;
