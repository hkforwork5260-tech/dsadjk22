// screens-hifi-new.jsx — 추가 기능 4종

// ── 1. 알림 히스토리 ──────────────────────────
function HiFi_NotifHistory() {
  const nav = useNav();
  const Group = ({ date, children }) => (
    <div style={{ marginBottom: 18 }}>
      <div className="h-caption" style={{ marginBottom: 8 }}>{date}</div>
      <div className="h-col" style={{ gap: 8 }}>{children}</div>
    </div>
  );
  const Item = ({ icon, bg, time, title, sub, unread, onClick }) => (
    <div onClick={onClick} style={{
      display: 'flex', gap: 12, padding: 14,
      background: unread ? 'var(--h-brand-soft)' : 'var(--h-bg)',
      border: '1px solid ' + (unread ? 'var(--h-brand)' : 'var(--h-border)'),
      borderRadius: 14, cursor: 'pointer', position: 'relative'
    }}>
      <div style={{
        width: 38, height: 38, borderRadius: 12, background: bg,
        display:'flex', alignItems:'center', justifyContent:'center',
        flexShrink: 0, fontSize: 18
      }}>{icon}</div>
      <div className="h-grow">
        <div className="h-row" style={{ justifyContent:'space-between' }}>
          <div className="h-body" style={{ fontWeight: 700, fontSize: 14 }}>{title}</div>
          <div className="h-body-2" style={{ fontSize: 11 }}>{time}</div>
        </div>
        <div className="h-body-2" style={{ marginTop: 2, fontSize: 13, lineHeight: 1.35 }}>{sub}</div>
      </div>
      {unread && <div style={{
        position:'absolute', top: 12, right: 12,
        width: 8, height: 8, borderRadius: 999, background: 'var(--h-brand)'
      }}/>}
    </div>
  );

  return (
    <HiFiPhone
      title="알림"
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
      action={<button className="h-btn ghost sm" style={{ fontSize: 12 }}>모두 읽음</button>}
    >
      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 16px' }}>
        <Group date="오늘 · 5/22">
          <Item icon={<Mascot size={26} expression="wave"/>} bg="var(--h-brand-soft)"
            time="9:00" unread
            title="아침 브리핑"
            sub="☀️ 오늘 새 공고 17건이 떴어요. 삼성·네이버·LG 외 14곳"
            onClick={() => nav.go('h_main')}/>
          <Item icon="⭐" bg="#ffe7b3" time="7:23" unread
            title="네이버에 새 공고"
            sub="신입 백엔드 개발자 (D-19, 판교)"
            onClick={() => nav.go('h_detail')}/>
        </Group>

        <Group date="어제 · 5/21">
          <Item icon="🔥" bg="var(--h-update-soft)" time="21:00"
            title="저녁 마감 알림"
            sub="저장한 2개 공고 마감 임박 — 현대차 D-1 · CJ제일제당 D-0"
            onClick={() => nav.go('h_saved')}/>
          <Item icon="📝" bg="var(--h-info-soft)" time="14:32"
            title="공고 내용 변경"
            sub="SK하이닉스 - 서류 마감일이 6/2 → 6/5로 변경됐어요"
            onClick={() => nav.go('h_detail')}/>
          <Item icon={<Mascot size={26} expression="happy"/>} bg="var(--h-brand-soft)"
            time="9:00"
            title="아침 브리핑"
            sub="☀️ 어제 새 공고 12건. 카카오·포스코·CJ 포함"
            onClick={() => nav.go('h_main')}/>
        </Group>

        <Group date="이번 주">
          <Item icon="🎯" bg="var(--h-new-soft)" time="5/20"
            title="딱맞는 공고 발견"
            sub="네 관심사(개발·디자인) 매칭률 92% — 토스 프론트엔드"
            onClick={() => nav.go('h_detail')}/>
          <Item icon="🔥" bg="var(--h-update-soft)" time="5/19"
            title="삼성전자 새 공고"
            sub="DS 부문 메모리 R&D (D-20)"
            onClick={() => nav.go('h_detail')}/>
        </Group>

        <div className="h-card flat" style={{
          padding: 16, marginTop: 8, textAlign:'center',
          border: '2px dashed var(--h-border-dark)', background: 'transparent'
        }}>
          <div className="h-body-2">+ 31개 더 있어요</div>
        </div>
      </div>
    </HiFiPhone>
  );
}

// ── 2. 공고 캘린더 ──────────────────────────
function HiFi_Calendar() {
  const nav = useNav();
  // 5월 캘린더 - 마감 표시
  const closings = {
    22: [{ company: 'CJ', kind: 'closing', count: 1 }],
    23: [{ company: '현대', kind: 'closing', count: 1 }],
    28: [{ company: '카', kind: 'update', count: 1 }],
    30: [{ company: '카', kind: 'new', count: 2 }],
  };
  const today = 22;

  const days = ['일', '월', '화', '수', '목', '금', '토'];
  const firstDay = 4; // 5/1 = 목요일
  const totalDays = 31;
  const cells = Array.from({ length: 42 }, (_, i) => {
    const d = i - firstDay + 1;
    return d > 0 && d <= totalDays ? d : null;
  });

  return (
    <HiFiPhone
      title="마감 캘린더"
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
      action={<HiFiIconBtn name="settings" size={18}/>}
    >
      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 16px' }}>
        {/* 월 네비 */}
        <div className="h-row" style={{ justifyContent:'center', padding: '4px 0 12px', gap: 16 }}>
          <SkIcon name="chev-l" size={16} color="var(--h-text-2)"/>
          <div className="h-h2">2026년 5월</div>
          <SkIcon name="chev" size={16} color="var(--h-text-2)"/>
        </div>

        {/* 요일 헤더 */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 4 }}>
          {days.map((d, i) =>
            <div key={d} className="h-body-2" style={{
              textAlign:'center', fontSize: 11, fontWeight: 700, padding: 4,
              color: i === 0 ? 'var(--h-closing)' : i === 6 ? 'var(--h-info)' : 'var(--h-text-2)'
            }}>{d}</div>
          )}
        </div>

        {/* 날짜 셀 */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 4, marginTop: 4 }}>
          {cells.map((d, i) => {
            const closing = d ? closings[d] : null;
            const isToday = d === today;
            const dow = i % 7;
            return (
              <div key={i} style={{
                aspectRatio: '1 / 1.2',
                background: closing ? 'var(--h-brand-soft)' : 'transparent',
                border: '1.5px solid ' + (isToday ? 'var(--h-brand)' : 'transparent'),
                borderRadius: 8,
                padding: 4,
                display: 'flex', flexDirection: 'column',
                gap: 2,
                cursor: closing ? 'pointer' : 'default'
              }}>
                {d && (
                  <>
                    <div style={{
                      fontSize: 12, fontWeight: isToday ? 800 : 600,
                      color: isToday ? 'var(--h-brand)' :
                             dow === 0 ? 'var(--h-closing)' :
                             dow === 6 ? 'var(--h-info)' : 'var(--h-text)',
                      textAlign: 'center'
                    }}>{d}</div>
                    {closing && closing.map((c, j) =>
                      <div key={j} style={{
                        fontSize: 9, fontWeight: 800,
                        padding: '1px 3px',
                        background: `var(--h-${c.kind})`,
                        color: '#fff',
                        borderRadius: 4,
                        textAlign:'center',
                        lineHeight: 1.3
                      }}>{c.company}{c.count > 1 ? ` +${c.count - 1}` : ''}</div>
                    )}
                  </>
                )}
              </div>
            );
          })}
        </div>

        <div className="h-row" style={{ gap: 12, marginTop: 14, justifyContent:'center' }}>
          <div className="h-row" style={{ gap: 4 }}>
            <span style={{ width: 8, height: 8, borderRadius: 2, background:'var(--h-new)' }}/>
            <span className="h-body-2" style={{ fontSize: 11 }}>새공고</span>
          </div>
          <div className="h-row" style={{ gap: 4 }}>
            <span style={{ width: 8, height: 8, borderRadius: 2, background:'var(--h-update)' }}/>
            <span className="h-body-2" style={{ fontSize: 11 }}>변경</span>
          </div>
          <div className="h-row" style={{ gap: 4 }}>
            <span style={{ width: 8, height: 8, borderRadius: 2, background:'var(--h-closing)' }}/>
            <span className="h-body-2" style={{ fontSize: 11 }}>마감</span>
          </div>
        </div>

        {/* 오늘 마감 */}
        <div className="h-card brand" style={{ padding: 14, marginTop: 22 }}>
          <div className="h-caption" style={{ color: 'var(--h-brand-dark)' }}>오늘 마감 · 5/22</div>
          <div className="h-h2" style={{ marginTop: 4 }}>1개 공고</div>
          <div className="h-col" style={{ gap: 8, marginTop: 10 }}>
            <HiFiJobCard kind="closing" logo="CJ" company="CJ제일제당" role="CJ 신입공채" dday="D-0" dateText="23:59" onClick={() => nav.go('h_detail')}/>
          </div>
        </div>

        {/* 곧 마감 */}
        <div className="h-h2" style={{ marginTop: 22 }}>이번 주 마감 (3건)</div>
        <div className="h-col" style={{ gap: 8, marginTop: 10 }}>
          <HiFiJobCard kind="closing" logo="현" company="현대자동차" role="신입사원 일반공채" dday="D-1" dateText="5/23" onClick={() => nav.go('h_detail')}/>
          <HiFiJobCard kind="update" logo="카" company="카카오" role="경력 프론트엔드" dday="D-6" dateText="5/28" onClick={() => nav.go('h_detail')}/>
        </div>
      </div>
    </HiFiPhone>
  );
}

// ── 3. 공유 시트 (카카오톡 등) ──────────────────────────
function HiFi_ShareSheet() {
  const nav = useNav();
  return (
    <div className="hifi" style={{ position: 'relative' }}>
      <div className="hifi-phone" style={{ background: '#1a1a1a' }}>
        {/* 상태바 (다크) */}
        <div className="hifi-status" style={{ color: '#fff' }}>
          <span>9:41</span>
          <span style={{ flex: 1 }}/>
          <span className="right">
            <span style={{ fontSize: 12 }}>📶</span>
            <span style={{
              display: 'inline-block', width: 22, height: 11, borderRadius: 2,
              border: '1.4px solid #fff', position: 'relative'
            }}>
              <span style={{ position:'absolute', left:1, top:1, bottom:1, width:'80%', background:'#fff', borderRadius: 1 }}/>
            </span>
          </span>
        </div>

        {/* 배경: 공고 상세 살짝 보임 */}
        <div style={{
          flex: 1, position: 'relative',
          background: 'rgba(255,255,255,0.08)',
          display: 'flex', flexDirection: 'column'
        }}>
          <div style={{ padding: 20, opacity: 0.4 }}>
            <div style={{ height: 28, background: '#fff', borderRadius: 8, width: '60%', marginBottom: 12 }}/>
            <div style={{ height: 18, background: '#fff', borderRadius: 6, width: '80%', marginBottom: 8 }}/>
            <div style={{ height: 18, background: '#fff', borderRadius: 6, width: '70%' }}/>
          </div>

          {/* 시트 */}
          <div style={{
            position: 'absolute', left: 0, right: 0, bottom: 0,
            background: '#fff',
            borderTopLeftRadius: 24, borderTopRightRadius: 24,
            padding: '14px 20px 20px',
            maxHeight: '70%', overflowY: 'auto'
          }}>
            <div style={{ width: 40, height: 4, background: 'var(--h-border-dark)', borderRadius: 2, margin: '0 auto 16px' }}/>

            {/* 미리보기 카드 */}
            <div className="h-card brand" style={{ padding: 12 }}>
              <div className="h-row" style={{ gap: 10 }}>
                <div className="h-logo">N</div>
                <div className="h-grow">
                  <div className="h-caption" style={{ color: 'var(--h-brand-dark)' }}>NEW · D-19</div>
                  <div className="h-body" style={{ fontWeight: 700, marginTop: 2 }}>네이버 · 신입 백엔드 개발자</div>
                  <div className="h-body-2" style={{ fontSize: 12 }}>~ 6/10 23:59 · 판교</div>
                </div>
              </div>
            </div>

            <div className="h-h2" style={{ marginTop: 18 }}>공유하기</div>
            <div style={{
              display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)',
              gap: 12, marginTop: 14
            }}>
              {[
                ['💛', '카카오톡', '#FEE500', '#3C1E1E'],
                ['🟢', '라인', '#00C300', '#fff'],
                ['💬', '문자', '#5B8DEF', '#fff'],
                ['📧', '이메일', '#FF6B35', '#fff'],
                ['📋', '링크 복사', '#f0f0f0', '#3c3c3c'],
                ['📷', '인스타', 'linear-gradient(135deg,#833AB4,#FD1D1D,#FCB045)', '#fff'],
                ['🔗', 'URL', '#f0f0f0', '#3c3c3c'],
                ['…', '더보기', '#f0f0f0', '#3c3c3c'],
              ].map(([icon, label, bg, fg]) =>
                <div key={label} style={{ display:'flex', flexDirection:'column', alignItems:'center', gap: 6, cursor:'pointer' }}>
                  <div style={{
                    width: 54, height: 54, borderRadius: 16,
                    background: bg, color: fg,
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontSize: 24,
                    boxShadow: '0 2px 6px rgba(0,0,0,0.08)'
                  }}>{icon}</div>
                  <div className="h-body-2" style={{ fontSize: 11, fontWeight: 700, textAlign: 'center' }}>{label}</div>
                </div>
              )}
            </div>

            <div className="h-h2" style={{ marginTop: 22 }}>옵션</div>
            <div className="h-col" style={{ gap: 0, marginTop: 8 }}>
              <div className="h-row" style={{ padding: '12px 0', borderBottom: '1px solid var(--h-border)', gap: 10 }}>
                <span style={{ fontSize: 18 }}>💬</span>
                <div className="h-body h-grow" style={{ fontWeight: 600 }}>한 줄 메시지 추가</div>
                <SkIcon name="chev" size={14} color="var(--h-text-3)"/>
              </div>
              <div className="h-row" style={{ padding: '12px 0', borderBottom: '1px solid var(--h-border)', gap: 10 }}>
                <span style={{ fontSize: 18 }}>👥</span>
                <div className="h-body h-grow" style={{ fontWeight: 600 }}>같이 보는 친구 만들기</div>
                <div className="h-chip sm" style={{ background:'var(--h-brand-soft)', color:'var(--h-brand-dark)', fontSize: 10 }}>NEW</div>
              </div>
              <div className="h-row" style={{ padding: '12px 0', gap: 10 }}>
                <span style={{ fontSize: 18 }}>🔒</span>
                <div className="h-body h-grow" style={{ fontWeight: 600 }}>익명으로 공유</div>
                <div className="h-toggle"/>
              </div>
            </div>

            <button className="h-btn block" style={{ marginTop: 16, background: 'var(--h-bg-2)' }} onClick={() => nav.back()}>
              취소
            </button>
          </div>
        </div>

        <div className="hifi-nav">
          <div className="hifi-nav-pill" style={{ background:'#fff' }}/>
        </div>
      </div>
    </div>
  );
}

// ── 4. 비슷한 공고 추천 ──────────────────────────
function HiFi_Similar() {
  const nav = useNav();
  return (
    <HiFiPhone
      title="비슷한 공고"
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
    >
      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 16px' }}>
        {/* 원본 공고 */}
        <div className="h-card" style={{ padding: 12 }}>
          <div className="h-caption">이 공고 기준</div>
          <div className="h-row" style={{ gap: 10, marginTop: 6 }}>
            <div className="h-logo">N</div>
            <div className="h-grow">
              <div className="h-body" style={{ fontWeight: 700 }}>네이버 · 신입 백엔드 개발자</div>
              <div className="h-body-2" style={{ fontSize: 12 }}>판교 · Java/Kotlin · 학사+</div>
            </div>
          </div>
        </div>

        {/* AI 매칭 안내 */}
        <div className="h-card brand" style={{ padding: 12, marginTop: 14 }}>
          <div className="h-row" style={{ gap: 8 }}>
            <span style={{ fontSize: 18 }}>✨</span>
            <div className="h-body" style={{ fontWeight: 700, lineHeight: 1.4 }}>
              꽁이가 직무·지역·스택을 비교해서 비슷한 공고 8개를 찾았어요
            </div>
          </div>
        </div>

        {/* 매칭률 높은 공고 */}
        <div className="h-h2" style={{ marginTop: 22 }}>매칭률 높은 공고</div>
        <div className="h-col" style={{ gap: 10, marginTop: 10 }}>
          {[
            { logo:'카', name:'카카오', role:'신입 백엔드 개발자', loc:'판교', stack:'Java/Spring', dday:'D-14', match:96 },
            { logo:'쿠', name:'쿠팡', role:'백엔드 엔지니어 (신입)', loc:'송파', stack:'Kotlin/Spring', dday:'D-12', match:91 },
            { logo:'토', name:'토스', role:'백엔드 신입 개발자', loc:'역삼', stack:'Kotlin/Spring', dday:'D-9', match:88 },
          ].map((p, i) =>
            <div key={i} className="h-card" style={{ padding: 12, cursor:'pointer' }} onClick={() => nav.go('h_detail')}>
              <div className="h-row" style={{ gap: 12 }}>
                <div className="h-logo">{p.logo}</div>
                <div className="h-grow">
                  <div className="h-row" style={{ gap: 6 }}>
                    <span className="h-label new">NEW</span>
                    <span className="h-body-2" style={{ fontSize: 11 }}>{p.name}</span>
                  </div>
                  <div className="h-body" style={{ fontWeight: 700, marginTop: 2 }}>{p.role}</div>
                  <div className="h-body-2" style={{ fontSize: 12, marginTop: 2 }}>📍 {p.loc} · {p.stack}</div>
                </div>
                <div style={{ textAlign:'right' }}>
                  <div style={{
                    fontSize: 14, fontWeight: 800, color: 'var(--h-brand)',
                    background: 'var(--h-brand-soft)', padding: '2px 8px', borderRadius: 999
                  }}>{p.match}%</div>
                  <div className="h-mono-num" style={{ fontSize: 13, marginTop: 4, color:'var(--h-new-shadow)' }}>{p.dday}</div>
                </div>
              </div>
              {/* 매칭 사유 */}
              <div className="h-row" style={{ gap: 4, marginTop: 8, flexWrap:'wrap' }}>
                {p.match >= 90 && <span className="h-chip sm" style={{ background:'var(--h-new-soft)', color:'var(--h-new-shadow)' }}>같은 직무</span>}
                {p.loc.includes('판교') || p.loc.includes('역삼') || p.loc.includes('송파') ? <span className="h-chip sm">같은 권역</span> : null}
                <span className="h-chip sm">같은 스택</span>
              </div>
            </div>
          )}
        </div>

        {/* 같은 산업 */}
        <div className="h-h2" style={{ marginTop: 22 }}>같은 산업 (IT/플랫폼)</div>
        <div className="h-col" style={{ gap: 10, marginTop: 10 }}>
          <HiFiJobCard kind="new" logo="당" company="당근마켓" role="플랫폼 백엔드 (신입)" dday="D-21" dateText="~6/12" onClick={() => nav.go('h_detail')}/>
          <HiFiJobCard kind="new" logo="배" company="배달의민족" role="서버 개발자 (신입)" dday="D-17" dateText="~6/8" onClick={() => nav.go('h_detail')}/>
        </div>

        {/* 사용자 데이터 기반 */}
        <div className="h-h2" style={{ marginTop: 22 }}>네 관심사 기반 추천</div>
        <div className="h-body-2" style={{ marginTop: 4, fontSize: 12 }}>
          관심 직군: IT개발·데이터 / 좋아한 공고 23개 분석
        </div>
        <div className="h-col" style={{ gap: 10, marginTop: 10 }}>
          <HiFiJobCard kind="new" logo="라" company="라인" role="신입 서버 사이드 엔지니어" dday="D-25" dateText="~6/16" onClick={() => nav.go('h_detail')}/>
          <HiFiJobCard kind="new" logo="삼" company="삼성SDS" role="클라우드 백엔드 신입" dday="D-20" dateText="~6/11" onClick={() => nav.go('h_detail')}/>
        </div>
      </div>
    </HiFiPhone>
  );
}

Object.assign(window, {
  HiFi_NotifHistory, HiFi_Calendar, HiFi_ShareSheet, HiFi_Similar
});
