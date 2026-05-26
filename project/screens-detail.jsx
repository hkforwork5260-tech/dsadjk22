// screens-detail.jsx — 공고 상세 3종

// A: 풀스크린 스크롤 (위에서 아래로 요약 → 상세 → 첨부)
function DetailA() {
  return (
    <Phone showHeader title="공고 상세">
      <div style={{ flex: 1, overflowY: 'auto', padding: 16 }}>
        <span className="label new">NEW</span>
        <div className="t-xl b" style={{ marginTop: 6, lineHeight:1.15 }}>2026 상반기 신입공채</div>
        <div className="row" style={{ gap: 8, marginTop: 6, alignItems:'center' }}>
          <div className="logo sm">삼성</div>
          <span className="t-sm b">삼성전자</span>
          <span className="t-xs muted">· 대기업</span>
        </div>

        {/* 요약 박스 */}
        <div className="sk-box fill-toss" style={{ marginTop: 12 }}>
          <div className="t-sm b row" style={{ gap: 4 }}>
            <SkIcon name="sparkle" size={14} color="var(--toss)"/> AI 한줄 요약
          </div>
          <div className="t-md" style={{ marginTop: 4, lineHeight: 1.3 }}>
            DS/메모리/파운드리·DX 전 부문 통합 모집. 학사 이상, 6/15 18시 마감.
          </div>
        </div>

        <div className="div"/>

        {/* key-value 정보 */}
        <div className="col" style={{ gap: 8 }}>
          {[
            ['모집부문','반도체·디스플레이·생활가전 외 12개'],
            ['지원자격','학사 이상 (전공 무관)'],
            ['근무지','서울 / 수원 / 화성'],
            ['접수기간','5/26 ~ 6/15 18:00'],
            ['전형','서류 → GSAT → 면접'],
          ].map(([k,v]) => (
            <div key={k} className="row" style={{ alignItems:'flex-start', gap: 10 }}>
              <span className="t-sm muted" style={{ width: 70, flexShrink: 0 }}>{k}</span>
              <span className="t-sm b" style={{ flex: 1 }}>{v}</span>
            </div>
          ))}
        </div>

        <div className="div"/>

        {/* 본문 (placeholder lines) */}
        <div className="t-md b">상세 내용</div>
        {[88, 95, 80, 92, 70].map((w,i) => (
          <div key={i} style={{ height: 8, background:'var(--paper-2)', borderRadius: 4, width: w+'%', marginTop: 6 }}/>
        ))}

        <div className="div"/>

        {/* 첨부 */}
        <div className="t-md b">첨부파일</div>
        <div className="sk-box" style={{ marginTop: 8 }}>
          <div className="row" style={{ gap: 8 }}>
            <div className="placeholder" style={{ width: 36, height: 44, borderRadius: 6 }}>PDF</div>
            <div style={{ flex: 1 }}>
              <div className="t-sm b">채용공고_삼성전자_2026.pdf</div>
              <div className="t-xs muted">2.4MB · 자동 분석됨 ✓</div>
            </div>
            <SkIcon name="chev" size={16}/>
          </div>
        </div>

        <div style={{ height: 80 }}/>
      </div>

      {/* 하단 고정 액션 */}
      <div style={{ padding: 12, borderTop: '1.8px solid var(--ink)', background: 'var(--paper)' }}>
        <div className="row" style={{ gap: 8 }}>
          <SkIcon name="bookmark" size={26} color="#8a8a8a"/>
          <button className="btn primary" style={{ flex: 1 }}>원본 사이트에서 지원하기 →</button>
        </div>
      </div>
    </Phone>
  );
}

// B: 탭 분리 (요약/원문/회사정보)
function DetailB() {
  return (
    <Phone showHeader title="공고 상세">
      {/* 헤더 카드 */}
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
        <div className="row" style={{ gap: 6, marginTop: 10 }}>
          <span className="label outline-new b">D-19</span>
          <span className="chip sm">신입</span>
          <span className="chip sm">학사</span>
          <span className="chip sm">Java</span>
        </div>
      </div>

      {/* 탭 */}
      <div className="row" style={{ padding: '0 14px', borderBottom: '1.5px solid var(--ink-3)' }}>
        {[
          ['요약', true],
          ['원문', false],
          ['회사', false],
          ['비슷한 공고', false],
        ].map(([t,on]) => (
          <div key={t} className="t-md b" style={{
            padding: '8px 12px',
            borderBottom: on ? '2.5px solid var(--toss)' : 'none',
            color: on ? 'var(--toss)' : 'var(--ink-3)',
            marginBottom: -1
          }}>{t}</div>
        ))}
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: 14 }}>
        <div className="sk-box fill-toss">
          <div className="t-sm b row" style={{ gap: 4 }}>
            <SkIcon name="sparkle" size={14}/> AI 요약 (Claude)
          </div>
          <div className="t-md" style={{ marginTop: 4, lineHeight: 1.3 }}>
            검색 / 커머스 백엔드. Java·Kotlin 기반 대규모 트래픽 처리. 학사 이상, 신입 가능.
          </div>
        </div>

        <div className="t-md b" style={{ marginTop: 14 }}>📋 핵심 정보</div>
        <div className="col" style={{ gap: 6, marginTop: 6 }}>
          <div className="row t-sm"><span className="muted" style={{ width: 70 }}>마감</span><span className="b">6월 10일 (수) 23:59</span></div>
          <div className="row t-sm"><span className="muted" style={{ width: 70 }}>지원자격</span><span className="b">학사 이상, 신입~3년</span></div>
          <div className="row t-sm"><span className="muted" style={{ width: 70 }}>근무지</span><span className="b">분당구 정자동</span></div>
          <div className="row t-sm"><span className="muted" style={{ width: 70 }}>전형</span><span className="b">서류 → 코딩테스트 → 면접</span></div>
        </div>

        <div className="t-md b" style={{ marginTop: 14 }}>🎯 우대사항</div>
        <ul className="t-sm" style={{ paddingLeft: 18, lineHeight: 1.5, margin: '6px 0' }}>
          <li>대규모 분산 시스템 경험</li>
          <li>Spring Boot / Kotlin 능숙자</li>
          <li>오픈소스 기여 경험</li>
        </ul>

        <div className="sk-box dashed" style={{ marginTop: 12 }}>
          <div className="t-sm b row" style={{ gap: 4 }}>
            <SkIcon name="link" size={14}/> 원본 공고 사이트
          </div>
          <div className="t-xs muted">recruit.navercorp.com/...</div>
        </div>
      </div>

      <div style={{ padding: 12, borderTop: '1.8px solid var(--ink)' }}>
        <div className="row" style={{ gap: 8 }}>
          <div className="btn sm" style={{ width: 50, padding: 10 }}>
            <SkIcon name="bookmark" size={20}/>
          </div>
          <button className="btn primary" style={{ flex: 1 }}>지원하러 가기</button>
        </div>
      </div>
    </Phone>
  );
}

// C: 봇툼 시트 형태 (메인 위에 떠있는 상세)
function DetailC() {
  return (
    <Phone showHeader title="오늘의 채용">
      {/* 뒤 배경 (메인 살짝 보임) */}
      <div style={{ flex: 1, background: 'var(--paper-2)', position: 'relative', overflow: 'hidden' }}>
        <div style={{ padding: 14, opacity: 0.5 }}>
          <div className="card hl-new"><div className="t-md b">삼성전자 · 2026 신입공채</div></div>
          <div style={{ marginTop: 8 }} className="card"><div className="t-md b">네이버 · 백엔드 개발자</div></div>
        </div>
        <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.25)' }}/>

        {/* 시트 */}
        <div style={{
          position: 'absolute', left: 0, right: 0, bottom: 0,
          background: 'var(--paper)',
          borderTop: '2px solid var(--ink)',
          borderTopLeftRadius: 24, borderTopRightRadius: 24,
          padding: 16, maxHeight: '78%', overflowY: 'auto'
        }}>
          {/* handle */}
          <div style={{ width: 40, height: 4, background: 'var(--ink-3)', borderRadius: 2, margin: '0 auto 12px' }}/>

          <div className="row" style={{ gap: 10 }}>
            <div className="logo lg">LG</div>
            <div style={{ flex: 1 }}>
              <span className="label new">NEW</span>
              <div className="t-lg b" style={{ lineHeight: 1.1, marginTop: 4 }}>R&D 신입사원</div>
              <div className="t-sm muted">LG에너지솔루션 · 대전</div>
            </div>
            <SkIcon name="bookmark" size={22} color="#8a8a8a"/>
          </div>

          <div className="sk-box fill-update" style={{ marginTop: 10 }}>
            <div className="row t-sm b" style={{ gap: 4 }}>
              <SkIcon name="fire" size={14} color="var(--update)"/> D-16 마감 임박!
            </div>
            <div className="t-xs muted2" style={{ marginTop: 2 }}>6월 7일 18:00까지</div>
          </div>

          <div className="t-md b" style={{ marginTop: 12 }}>한 줄 요약</div>
          <div className="note">배터리 셀·소재·공정 R&D 통합 모집. 석사 이상.</div>

          <div className="row" style={{ marginTop: 10, gap: 6, flexWrap:'wrap' }}>
            <span className="chip sm">대전</span>
            <span className="chip sm">석사+</span>
            <span className="chip sm">화학</span>
            <span className="chip sm">소재</span>
            <span className="chip sm">+5</span>
          </div>

          <div className="div"/>

          <div className="row" style={{ gap: 6, marginTop: 4 }}>
            <button className="btn sm" style={{ flex: 1 }}>전체보기</button>
            <button className="btn primary sm" style={{ flex: 1 }}>지원 →</button>
          </div>
        </div>
      </div>
    </Phone>
  );
}

window.DetailA = DetailA;
window.DetailB = DetailB;
window.DetailC = DetailC;
