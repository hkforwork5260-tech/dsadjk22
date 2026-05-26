// screens-hifi.jsx — 하이파이 화면들 (듀오링고풍)

// ── 1. 온보딩 STEP 1 — 직군 ──────────────────────────
const HIFI_JOB_CATEGORIES = [
  '기획·전략', '마케팅·홍보·조사', '회계·세무·재무',
  '인사·노무·HRD', '총무·법무·사무', 'IT개발·데이터',
  '디자인', '영업·판매·무역', '고객상담·TM',
  '구매·자재·물류', '상품기획·MD', '운전·운송·배송',
  '서비스', '생산', '건설·건축',
  '의료', '연구·R&D', '교육',
  '미디어·문화·스포츠', '금융·보험', '공공·복지'
];

function HiFi_Onb1() {
  const nav = useNav();
  const [selected, setSelected] = React.useState({ 2: true, 5: true, 6: true });
  const count = Object.values(selected).filter(Boolean).length;
  const toggle = (i) => setSelected(s => ({ ...s, [i]: !s[i] }));

  return (
    <HiFiPhone showAppBar={false}>
      <div className="h-pad" style={{ flex: 1, display: 'flex', flexDirection: 'column', paddingBottom: 14 }}>
        <div className="h-row" style={{ gap: 6 }}>
          <span className="h-dot on"/>
          <span className="h-dot"/>
          <span className="h-dot"/>
          <span className="h-dot"/>
          <span style={{ flex: 1 }}/>
          <button className="h-btn ghost sm" onClick={() => nav.go('h_main')}>건너뛰기</button>
        </div>

        <div className="h-row" style={{ gap: 12, alignItems:'center', marginTop: 12 }}>
          <Mascot size={56} expression="wave"/>
          <div>
            <div className="h-display" style={{ fontSize: 24 }}>어떤 일을 찾고 있어?</div>
            <div className="h-body-2" style={{ marginTop: 2 }}>복수 선택 OK</div>
          </div>
        </div>

        <div style={{ flex: 1, overflowY: 'auto', marginTop: 14, paddingRight: 2 }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
            {HIFI_JOB_CATEGORIES.map((j, i) =>
              <button
                key={j}
                onClick={() => toggle(i)}
                className={'h-btn ' + (selected[i] ? 'primary' : '') + ' sm'}
                style={{
                  padding: '12px 8px',
                  fontSize: 13,
                  fontWeight: 700,
                  textAlign: 'center',
                  lineHeight: 1.2,
                  letterSpacing: -0.2,
                  textTransform: 'none'
                }}>
                {j}
              </button>
            )}
          </div>
        </div>

        <button
          className={'h-btn block ' + (count > 0 ? 'primary' : '')}
          style={{ marginTop: 14 }}
          onClick={() => nav.go('h_main')}
          disabled={count === 0}>
          다음 ({count}개 선택됨) →
        </button>
      </div>
    </HiFiPhone>
  );
}

// ── 2. 메인 피드 ──────────────────────────
function HiFi_Main() {
  const nav = useNav();
  const [section, setSection] = React.useState('new');
  const sections = {
    new: { count: 17, color: 'var(--h-new)', label: 'NEW' },
    update: { count: 4, color: 'var(--h-update)', label: 'UPDATE' },
    closing: { count: 3, color: 'var(--h-closing)', label: 'CLOSING' },
  };
  const newJobs = [
    ['삼성','삼성전자','2026 상반기 신입공채','D-24','~6/15'],
    ['N','네이버','신입 백엔드 개발자','D-19','~6/10'],
    ['LG','LG에너지솔루션','연구개발(R&D) 신입','D-16','~6/7'],
    ['카카오','카카오','신입 안드로이드 개발자','D-14','~6/5'],
    ['포스','포스코','2026 상반기 신입공채','D-12','~6/3'],
    ['HM','현대모비스','기계 R&D 신입','D-10','~6/1'],
  ];

  return (
    <HiFiPhone
      title="채용알리미"
      action={
        <div className="h-row" style={{ gap: 6 }}>
          <HiFiIconBtn name="bell" onClick={() => nav.go('h_notifHistory')}/>
          <HiFiIconBtn name="filter" onClick={() => nav.go('h_filter')}/>
        </div>
      }
    >
      <div style={{ padding: '0 20px 8px' }}>
        <div className="h-row" style={{ alignItems:'flex-end' }}>
          <div className="h-grow">
            <div className="h-body-2">5월 22일 목요일</div>
            <div className="h-title" style={{ marginTop: 2 }}>
              오늘 새 공고 <span style={{ color: 'var(--h-brand)' }}>17건</span>
            </div>
          </div>
          <div style={{ position:'relative' }}>
            <Mascot size={60} expression="happy"/>
            <div style={{
              position: 'absolute', top: -2, right: -4,
              minWidth: 24, height: 24, padding: '0 6px',
              borderRadius: 999, background: 'var(--h-brand)', color: '#fff',
              display:'flex', alignItems:'center', justifyContent:'center',
              fontSize: 12, fontWeight: 800,
              border: '2px solid #fff'
            }}>17</div>
          </div>
        </div>
      </div>

      {/* 섹션 토글 */}
      <div style={{ padding: '6px 20px 10px' }}>
        <div style={{ display: 'flex', gap: 8 }}>
          {Object.entries(sections).map(([id, s]) =>
            <button
              key={id}
              onClick={() => setSection(id)}
              className={'h-chip outline' + (section === id ? ' on' : '')}
              style={{
                flex: 1,
                justifyContent: 'center',
                background: section === id ? s.color : 'var(--h-bg)',
                borderColor: section === id ? s.color : 'var(--h-border)',
                color: section === id ? '#fff' : 'var(--h-text-2)',
              }}>
              {s.label} {s.count}
            </button>
          )}
        </div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 16px', display:'flex', flexDirection:'column', gap: 10 }}>
        {newJobs.map(([logo, comp, role, dday, date], i) =>
          <HiFiJobCard key={i}
            kind={section}
            logo={logo}
            company={comp}
            role={role}
            dday={dday}
            dateText={date}
            onClick={() => nav.go('h_detail')}
          />
        )}
        <div style={{ height: 4 }}/>
      </div>

      <HiFiTabBar active="home"/>
    </HiFiPhone>
  );
}

// ── 3. 공고 상세 ──────────────────────────
function HiFi_Detail() {
  const nav = useNav();
  const [tab, setTab] = React.useState('summary');
  const [saved, setSaved] = React.useState(false);

  return (
    <HiFiPhone
      showAppBar={true}
      title=""
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
      action={
        <div className="h-row" style={{ gap: 8 }}>
          <HiFiIconBtn name="bookmark" onClick={() => setSaved(s => !s)}/>
          <HiFiIconBtn name="share" onClick={() => nav.go('h_share')}/>
        </div>
      }
    >
      <div style={{ padding: '0 20px 8px' }}>
        <div className="h-row" style={{ gap: 14, alignItems:'flex-start' }}>
          <div className="h-logo lg">N</div>
          <div className="h-grow">
            <span className="h-label new">NEW</span>
            <div className="h-title" style={{ marginTop: 6 }}>신입 백엔드 개발자</div>
            <div className="h-body-2" style={{ marginTop: 2 }}>네이버 · 판교</div>
          </div>
        </div>

        <div className="h-row" style={{ gap: 6, marginTop: 12, flexWrap:'wrap' }}>
          <span className="h-chip outline" style={{ background:'var(--h-new-soft)', color:'var(--h-new-shadow)', borderColor:'transparent' }}>D-19</span>
          <span className="h-chip sm outline">신입</span>
          <span className="h-chip sm outline">학사+</span>
          <span className="h-chip sm outline">Java/Kotlin</span>
        </div>
      </div>

      {/* 탭 */}
      <div style={{ padding: '12px 20px 0', borderBottom: '1px solid var(--h-border)', display:'flex', gap: 20 }}>
        {[
          ['summary', '요약'],
          ['original', '원문'],
          ['company', '회사'],
          ['similar', '비슷한'],
        ].map(([id, t]) =>
          <div key={id}
            onClick={() => {
              if (id === 'similar') nav.go('h_similar');
              else setTab(id);
            }}
            style={{
              padding: '10px 0',
              fontSize: 15,
              fontWeight: 700,
              color: tab === id ? 'var(--h-brand)' : 'var(--h-text-3)',
              borderBottom: tab === id ? '3px solid var(--h-brand)' : '3px solid transparent',
              marginBottom: -1,
              cursor: 'pointer',
            }}>{t}</div>
        )}
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '16px 20px' }}>
        <div className="h-card brand">
          <div className="h-row" style={{ gap: 8, alignItems:'flex-start' }}>
            <span style={{ fontSize: 22 }}>✨</span>
            <div className="h-grow">
              <div className="h-caption" style={{ color: 'var(--h-brand-dark)' }}>꽁이의 한줄 요약</div>
              <div className="h-h2" style={{ marginTop: 4, fontWeight: 700 }}>
                검색·커머스 백엔드. Java/Kotlin 대규모 트래픽 처리. 학사 이상, 신입 가능.
              </div>
            </div>
          </div>
        </div>

        <div className="h-h2" style={{ marginTop: 22 }}>📋 핵심 정보</div>
        <div className="h-col" style={{ gap: 10, marginTop: 10 }}>
          {[
            ['마감', '6월 10일 (수) 23:59'],
            ['자격', '학사 이상, 신입~3년'],
            ['근무지', '분당구 정자동'],
            ['전형', '서류 → 코딩 → 면접'],
          ].map(([k, v]) =>
            <div key={k} className="h-row" style={{ alignItems:'flex-start' }}>
              <div className="h-body-2" style={{ width: 60, flexShrink: 0 }}>{k}</div>
              <div className="h-body" style={{ fontWeight: 700, flex: 1 }}>{v}</div>
            </div>
          )}
        </div>

        <div className="h-h2" style={{ marginTop: 22 }}>🎯 우대사항</div>
        <ul className="h-body" style={{ margin: '10px 0', paddingLeft: 20, lineHeight: 1.7 }}>
          <li>대규모 분산 시스템 경험</li>
          <li>Spring Boot / Kotlin 능숙자</li>
          <li>오픈소스 기여 경험</li>
        </ul>

        <div className="h-card" style={{ background:'var(--h-bg-2)', borderColor:'transparent', marginTop: 12 }}>
          <div className="h-row" style={{ gap: 8 }}>
            <SkIcon name="link" size={18} color="var(--h-text-2)"/>
            <div className="h-body-2" style={{ fontWeight: 700, color: 'var(--h-text-2)' }}>
              recruit.navercorp.com/...
            </div>
          </div>
        </div>
      </div>

      <div style={{ padding: '12px 20px 12px', borderTop: '1px solid var(--h-border)' }}>
        <button className="h-btn primary block lg" onClick={() => alert('실제 앱: 네이버 채용 사이트로 이동')}>
          지원하러 가기
        </button>
      </div>
    </HiFiPhone>
  );
}

// ── 1b. 온보딩 STEP 2 — 회사 스와이프 (Reels-style) ─────────
function HiFi_OnbSwipe() {
  const nav = useNav();
  const [companyFav, setCompanyFav] = React.useState({});
  const [postingSaved, setPostingSaved] = React.useState({});
  const scrollRef = React.useRef(null);

  const total = HIFI_DECK.length;
  const favCount = Object.values(companyFav).filter(Boolean).length;

  return (
    <HiFiPhone
      showAppBar={true}
      title="관심 회사 고르기"
      leading={<HiFiIconBtn name="chev-l" size={22} onClick={() => nav.back()}/>}
      action={<button className="h-btn ghost sm" onClick={() => nav.go('h_main')}>건너뛰기</button>}
    >
      {/* 진행 + 카운터 */}
      <div style={{ padding: '0 20px 12px' }}>
        <div className="h-row" style={{ gap: 6 }}>
          <span className="h-dot" style={{ background:'var(--h-brand)', width: 24, borderRadius: 4 }}/>
          <span className="h-dot" style={{ background:'var(--h-brand)', width: 24, borderRadius: 4 }}/>
          <span className="h-dot on"/>
          <span className="h-dot"/>
        </div>
        <div className="h-row" style={{ marginTop: 8, alignItems: 'center' }}>
          <div className="h-body-2 h-grow">스크롤하면서 ❤️ 누르면 관심기업으로!</div>
          <span className="h-chip sm" style={{ background:'var(--h-brand-soft)', color:'var(--h-brand-dark)', fontWeight: 800 }}>
            {favCount}개 추가됨
          </span>
        </div>
      </div>

      <div
        ref={scrollRef}
        className="hifi-reel-feed"
        style={{
          flex: 1,
          overflowY: 'scroll',
          scrollSnapType: 'y mandatory',
          scrollBehavior: 'smooth',
        }}>
        {HIFI_DECK.map((data, i) =>
          <HiFi_DiscoverCard
            key={i}
            data={data}
            companyFav={!!companyFav[data.name]}
            postingSaved={!!postingSaved[data.posting.role]}
            onToggleCompany={() => setCompanyFav(s => ({ ...s, [data.name]: !s[data.name] }))}
            onTogglePosting={() => setPostingSaved(s => ({ ...s, [data.posting.role]: !s[data.posting.role] }))}
            idx={i}
            total={total}
          />
        )}
        {/* 완료 카드 */}
        <div className="hifi-reel-card" style={{
          background:'var(--h-brand-soft)',
          display:'flex', flexDirection:'column', alignItems:'center', justifyContent:'center',
          padding: 28, textAlign:'center'
        }}>
          <HiFiMascot size={120} expression="happy"/>
          <div className="h-title" style={{ marginTop: 14, color: 'var(--h-brand-dark)' }}>
            {favCount > 0 ? `${favCount}개 관심기업 추가!` : '관심 회사를 골라봐!'}
          </div>
          <div className="h-body-2" style={{ marginTop: 6, lineHeight: 1.4 }}>
            매일 새 공고를 알려드릴게요
          </div>
          <button className="h-btn primary block" style={{ marginTop: 22 }} onClick={() => nav.go('h_main')}>
            완료
          </button>
        </div>
      </div>
    </HiFiPhone>
  );
}
const HIFI_DECK = [
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

function HiFi_DiscoverCard({ data, companyFav, postingSaved, onToggleCompany, onTogglePosting, idx, total }) {
  const k = data.posting.kind;
  return (
    <div className="hifi-reel-card">
      {/* 상단: 회사 정보 */}
      <div style={{ padding: '20px 20px 16px' }}>
        <div className="h-row" style={{ gap: 14, alignItems:'flex-start' }}>
          <div className="h-logo lg" style={{ background:'var(--h-brand-soft)', color:'var(--h-brand-dark)' }}>{data.logo}</div>
          <div className="h-grow">
            <div className="h-body-2" style={{ fontSize: 12 }}>{data.size} · {data.region}</div>
            <div className="h-title" style={{ marginTop: 2 }}>{data.name}</div>
            <div className="h-body-2" style={{ fontSize: 13 }}>{data.sector}</div>
          </div>
        </div>

        <div className="h-row" style={{ gap: 6, marginTop: 12, flexWrap:'wrap' }}>
          {data.tags.map(t => <span key={t} className="h-chip sm">#{t}</span>)}
          <span className="h-chip sm" style={{ background:'var(--h-brand-soft)', color:'var(--h-brand-dark)' }}>
            최근 공고 {data.recent}건
          </span>
        </div>
      </div>

      <div style={{ height: 1, background:'var(--h-border)', margin: '0 20px' }}/>

      {/* 하단: 공고 내용 (1건) */}
      <div style={{ padding: '20px 20px 24px', flex: 1, display:'flex', flexDirection:'column' }}>
        <div className="h-row" style={{ gap: 8 }}>
          <span className={`h-label ${k}`}>{k.toUpperCase()}</span>
          <span style={{
            fontSize: 13, fontWeight: 800,
            color: `var(--h-${k})`,
            fontFeatureSettings: '"tnum"'
          }}>{data.posting.dday}</span>
          <span style={{ flex: 1 }}/>
          <span className="h-body-2" style={{ fontSize: 12 }}>~{data.posting.date}</span>
        </div>

        <div className="h-display" style={{ fontSize: 26, marginTop: 12, lineHeight: 1.15 }}>
          {data.posting.role}
        </div>

        <div className="h-row" style={{ gap: 6, marginTop: 14, flexWrap:'wrap' }}>
          <span className="h-chip outline sm">📍 {data.posting.loc}</span>
          <span className="h-chip outline sm">🎓 {data.posting.edu}</span>
          <span className="h-chip outline sm">💼 {data.posting.exp}</span>
        </div>

        <div className="h-card brand" style={{ padding: 14, marginTop: 16 }}>
          <div className="h-row" style={{ gap: 8, alignItems:'flex-start' }}>
            <span style={{ fontSize: 18 }}>✨</span>
            <div className="h-body" style={{ fontWeight: 700, lineHeight: 1.4 }}>
              {data.posting.summary}
            </div>
          </div>
        </div>

        <div style={{ flex: 1 }}/>

        <div className="h-row" style={{ gap: 8, marginTop: 16 }}>
          <button className="h-btn block" style={{ paddingTop: 12, paddingBottom: 12 }}>
            상세 보기
          </button>
        </div>

        <div style={{
          textAlign:'center', marginTop: 14,
          color:'var(--h-text-3)', fontSize: 12, fontWeight: 700,
          opacity: idx === total - 1 ? 0 : 0.7
        }}>
          ↓ 다음 공고로 스크롤
        </div>
      </div>

      {/* 오른쪽 플로팅 액션 — heart=관심기업, bookmark=공고 저장 */}
      <div style={{
        position:'absolute', right: 14, bottom: 110,
        display:'flex', flexDirection:'column', gap: 16, alignItems:'center'
      }}>
        {/* ❤️ 관심기업 (회사 단위) */}
        <div style={{ display:'flex', flexDirection:'column', alignItems:'center', gap: 2 }}>
          <button
            onClick={onToggleCompany}
            style={{
              width: 48, height: 48, borderRadius: 999,
              background: companyFav ? 'var(--h-brand)' : '#fff',
              border: companyFav ? '2px solid var(--h-brand)' : '2px solid var(--h-border)',
              cursor:'pointer',
              boxShadow: companyFav ? '0 4px 12px rgba(255,107,53,0.35)' : '0 2px 6px rgba(0,0,0,0.08)',
              display:'flex', alignItems:'center', justifyContent:'center',
              transition: 'transform 0.15s, background 0.15s'
            }}>
            <SkIcon name="heart" size={22} color={companyFav ? '#fff' : 'var(--h-text-2)'} strokeWidth={2.2}/>
          </button>
          <span style={{ fontSize: 10, fontWeight: 800, color: companyFav ? 'var(--h-brand)' : 'var(--h-text-3)' }}>
            관심기업
          </span>
        </div>

        {/* 🔖 공고 저장 (개별 공고) */}
        <div style={{ display:'flex', flexDirection:'column', alignItems:'center', gap: 2 }}>
          <button
            onClick={onTogglePosting}
            style={{
              width: 48, height: 48, borderRadius: 999,
              background: postingSaved ? 'var(--h-update)' : '#fff',
              border: postingSaved ? '2px solid var(--h-update)' : '2px solid var(--h-border)',
              cursor:'pointer',
              boxShadow: postingSaved ? '0 4px 12px rgba(255,200,0,0.35)' : '0 2px 6px rgba(0,0,0,0.08)',
              display:'flex', alignItems:'center', justifyContent:'center',
              transition: 'transform 0.15s, background 0.15s'
            }}>
            <SkIcon name="bookmark" size={22} color={postingSaved ? '#fff' : 'var(--h-text-2)'} strokeWidth={2.2}/>
          </button>
          <span style={{ fontSize: 10, fontWeight: 800, color: postingSaved ? 'var(--h-update-shadow)' : 'var(--h-text-3)' }}>
            공고 저장
          </span>
        </div>
      </div>

      {/* 진행 인디케이터 */}
      <div style={{
        position:'absolute', left: 14, top: 14,
        display:'flex', gap: 3
      }}>
        {Array.from({ length: total }).map((_, i) =>
          <span key={i} style={{
            width: i === idx ? 16 : 6,
            height: 3,
            borderRadius: 999,
            background: i <= idx ? 'var(--h-brand)' : 'var(--h-border)',
            transition: 'width 0.3s'
          }}/>
        )}
      </div>
    </div>
  );
}

function HiFi_Discover() {
  const [companyFav, setCompanyFav] = React.useState({});   // 회사 단위 즐겨찾기 (heart)
  const [postingSaved, setPostingSaved] = React.useState({}); // 공고 단위 저장 (bookmark)
  const [activeIdx, setActiveIdx] = React.useState(0);
  const scrollRef = React.useRef(null);

  React.useEffect(() => {
    const el = scrollRef.current;
    if (!el) return;
    const onScroll = () => {
      const h = el.clientHeight;
      const i = Math.round(el.scrollTop / h);
      if (i !== activeIdx) setActiveIdx(i);
    };
    el.addEventListener('scroll', onScroll);
    return () => el.removeEventListener('scroll', onScroll);
  }, [activeIdx]);

  return (
    <HiFiPhone title="찾아보기" action={<HiFiIconBtn name="filter"/>}>
      <div
        ref={scrollRef}
        className="hifi-reel-feed"
        style={{
          flex: 1,
          overflowY: 'scroll',
          scrollSnapType: 'y mandatory',
          scrollBehavior: 'smooth',
        }}>
        {HIFI_DECK.map((data, i) =>
          <HiFi_DiscoverCard
            key={i}
            data={data}
            companyFav={!!companyFav[data.name]}
            postingSaved={!!postingSaved[data.posting.role]}
            onToggleCompany={() => setCompanyFav(s => ({ ...s, [data.name]: !s[data.name] }))}
            onTogglePosting={() => setPostingSaved(s => ({ ...s, [data.posting.role]: !s[data.posting.role] }))}
            idx={i}
            total={HIFI_DECK.length}
          />
        )}
        {/* 마지막에 완료 카드 */}
        <div className="hifi-reel-card" style={{ background:'var(--h-brand-soft)', display:'flex', flexDirection:'column', alignItems:'center', justifyContent:'center', padding: 28, textAlign:'center' }}>
          <HiFiMascot size={120} expression="happy"/>
          <div className="h-title" style={{ marginTop: 14, color: 'var(--h-brand-dark)' }}>오늘은 여기까지!</div>
          <div className="h-body-2" style={{ marginTop: 6, lineHeight: 1.4 }}>
            내일 또 새로운 공고가<br/>기다릴게요
          </div>
          <div style={{ display:'flex', gap: 12, marginTop: 24 }}>
            <div style={{ textAlign:'center' }}>
              <div className="h-mono-num" style={{ fontSize: 28, color:'var(--h-brand)' }}>{Object.values(companyFav).filter(Boolean).length}</div>
              <div className="h-body-2" style={{ fontSize: 11 }}>관심기업</div>
            </div>
            <div style={{ textAlign:'center' }}>
              <div className="h-mono-num" style={{ fontSize: 28, color:'var(--h-update-shadow)' }}>{Object.values(postingSaved).filter(Boolean).length}</div>
              <div className="h-body-2" style={{ fontSize: 11 }}>공고 저장</div>
            </div>
          </div>
        </div>
      </div>

      <HiFiTabBar active="discover"/>
    </HiFiPhone>
  );
}

// ── 5. 마이페이지 ──────────────────────────
function HiFi_MyPage() {
  const nav = useNav();
  return (
    <HiFiPhone title="내 정보" action={
      <div className="h-row" style={{ gap: 6 }}>
        <HiFiIconBtn name="bell" onClick={() => nav.go('h_notifHistory')}/>
        <HiFiIconBtn name="settings"/>
      </div>
    }>
      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 16px' }}>
        {/* 스트릭 카드 */}
        <div className="h-card brand" style={{ padding: 18 }}>
          <div className="h-row" style={{ gap: 14 }}>
            <Mascot size={72} expression="happy"/>
            <div className="h-grow">
              <div className="h-title" style={{ color: 'var(--h-brand-dark)' }}>잘하고 있어요! 🔥</div>
              <div className="h-body-2" style={{ marginTop: 2 }}>꽁이가 12일째 챙겨주는 중</div>
            </div>
          </div>
          <div style={{
            marginTop: 16, paddingTop: 14, borderTop: '1px dashed var(--h-brand)',
            display: 'flex', justifyContent: 'space-around'
          }}>
            <div style={{ textAlign:'center', flex: 1 }}>
              <div className="h-mono-num" style={{ fontSize: 26, color: 'var(--h-text)' }}>87</div>
              <div className="h-body-2" style={{ fontSize: 11 }}>본 공고</div>
            </div>
            <div style={{ textAlign:'center', flex: 1, borderLeft:'1px dashed var(--h-brand)', cursor:'pointer' }}
                 onClick={() => nav.go('h_saved')}>
              <div className="h-mono-num" style={{ fontSize: 26, color: 'var(--h-update-shadow)' }}>14</div>
              <div className="h-body-2" style={{ fontSize: 11 }}>저장한 공고 ›</div>
            </div>
          </div>
        </div>

        {/* 메뉴 — 관심 기업은 하단 탭에 있으므로 제외 */}
        <div style={{ marginTop: 22 }}>
          {[
            ['🔔','알림 설정','매일 9시 / 21시','h_notifSettings'],
            ['📜','알림 히스토리','받은 알림 다시 보기','h_notifHistory'],
            ['📅','마감 캘린더','저장한 공고 마감일','h_calendar'],
            ['📱','바탕화면 위젯','켜짐 (Medium)','h_widgetSettings'],
            ['🎯','관심 직군','개발 · 디자인 · 데이터','h_jobInterests'],
            ['💬','피드백 보내기','','h_feedback'],
            ['ℹ️','앱 정보','v0.1.0 · 베타',null],
          ].map(([e, t, sub, route], i, arr) =>
            <div key={i} className="h-row" style={{
              padding: '14px 4px',
              borderBottom: i < arr.length - 1 ? '1px solid var(--h-border)' : 'none',
              gap: 14, cursor:'pointer'
            }} onClick={() => route && nav.go(route)}>
              <span style={{ fontSize: 22 }}>{e}</span>
              <div className="h-grow">
                <div className="h-body" style={{ fontWeight: 700 }}>{t}</div>
                {sub && <div className="h-body-2" style={{ fontSize: 12 }}>{sub}</div>}
              </div>
              <SkIcon name="chev" size={16} color="var(--h-text-3)"/>
            </div>
          )}
        </div>
      </div>
      <HiFiTabBar active="me"/>
    </HiFiPhone>
  );
}

// ── 6. 잠금화면 + 푸시 알림 ──────────────────────────
function HiFi_LockScreen() {
  return (
    <HiFiPhone showAppBar={false} lockScreen>
      <div style={{ flex: 1, display:'flex', flexDirection:'column', padding: '20px 22px' }}>
        <div style={{ marginTop: 30, textAlign:'center' }}>
          <div className="h-time">9:00</div>
          <div className="h-date">5월 22일 목요일</div>
        </div>

        <div style={{ marginTop: 48 }}>
          {/* 메인 푸시 */}
          <div className="h-push">
            <div className="ico-box">
              <Mascot size={28} expression="wave"/>
            </div>
            <div className="h-grow">
              <div className="h-row" style={{ justifyContent:'space-between' }}>
                <div style={{ fontSize: 13, fontWeight: 800, color: 'var(--h-text)' }}>채용알리미</div>
                <div className="h-body-2" style={{ fontSize: 11 }}>지금</div>
              </div>
              <div className="h-h2" style={{ marginTop: 4 }}>☀️ 좋은 아침! 오늘 새 공고 17건</div>
              <div className="h-body-2" style={{ marginTop: 2, fontSize: 13 }}>
                삼성전자 · 네이버 · LG에너지솔루션 외 14곳
              </div>
            </div>
          </div>

          {/* 다른 푸시 */}
          <div className="h-push" style={{ marginTop: 10, opacity: 0.95 }}>
            <div className="ico-box" style={{ background:'#5b8def' }}>
              <span style={{ fontSize: 18 }}>💬</span>
            </div>
            <div className="h-grow">
              <div className="h-row" style={{ justifyContent:'space-between' }}>
                <div style={{ fontSize: 13, fontWeight: 800, color: 'var(--h-text)' }}>메시지</div>
                <div className="h-body-2" style={{ fontSize: 11 }}>8:42</div>
              </div>
              <div className="h-body" style={{ fontWeight: 700, marginTop: 2 }}>엄마</div>
              <div className="h-body-2" style={{ fontSize: 13 }}>오늘 점심 뭐 먹어?</div>
            </div>
          </div>
        </div>

        <div style={{ flex: 1 }}/>
        <div style={{ textAlign:'center', color:'rgba(255,255,255,0.85)', fontSize: 13, fontWeight: 600 }}>
          ↑ 위로 밀어 잠금 해제
        </div>
      </div>
    </HiFiPhone>
  );
}

// ── 7. 바탕화면 위젯 (Large) ──────────────────────────
function HiFi_Widget() {
  return (
    <div className="hifi" style={{ position:'relative' }}>
      <div className="hifi-phone" style={{ background: '#1a1a1a' }}>
        <div className="hifi-status" style={{ color:'#fff' }}>
          <span>9:41</span>
          <span style={{ flex: 1 }}/>
          <span className="right">
            <span style={{ fontSize: 12 }}>📶</span>
            <span style={{
              display: 'inline-block', width: 22, height: 11, borderRadius: 2,
              border: `1.4px solid #fff`, position: 'relative'
            }}>
              <span style={{ position:'absolute', left: 1, top: 1, bottom: 1, width: '80%', background: '#fff', borderRadius: 1 }}/>
            </span>
          </span>
        </div>

        {/* 시계 */}
        <div style={{ padding: '24px 0 0', textAlign:'center', color:'#fff' }}>
          <div style={{ fontSize: 58, fontWeight: 700, lineHeight: 1, fontFamily:'Pretendard Variable, Pretendard, sans-serif' }}>9:41</div>
          <div style={{ fontSize: 14, opacity: 0.7, marginTop: 6 }}>5월 22일 목요일</div>
        </div>

        {/* 큰 위젯 */}
        <div style={{ padding: '32px 16px 0' }}>
          <div className="h-widget">
            <div className="h-row" style={{ gap: 12 }}>
              <Mascot size={48} expression="happy"/>
              <div className="h-grow">
                <div className="h-body-2" style={{ fontSize: 12 }}>5/22 (목)</div>
                <div className="h-h2" style={{ marginTop: 2 }}>오늘 새 공고</div>
                <div className="h-row" style={{ gap: 8, marginTop: 2, alignItems:'baseline' }}>
                  <span className="h-mono-num" style={{ fontSize: 36, color:'var(--h-brand)', lineHeight: 1 }}>17</span>
                  <span className="h-body-2" style={{ fontSize: 13 }}>+ 마감 3</span>
                </div>
              </div>
              <div style={{
                minWidth: 28, height: 28, padding: '0 6px', borderRadius: 999,
                background: 'var(--h-brand)', color: '#fff',
                display:'flex', alignItems:'center', justifyContent:'center',
                fontSize: 13, fontWeight: 800
              }}>17</div>
            </div>
            <div style={{ height: 1, background: 'var(--h-border)', margin: '14px 0' }}/>
            <div className="h-caption" style={{ marginBottom: 6 }}>오늘 핵심</div>
            <div className="h-col" style={{ gap: 6 }}>
              {[
                ['삼성전자', '신입공채', 'D-24'],
                ['네이버', '백엔드', 'D-19'],
                ['LG에너지', 'R&D', 'D-16'],
              ].map(([c, r, d]) =>
                <div key={c} className="h-row" style={{ fontSize: 14 }}>
                  <span style={{ fontWeight: 700, width: 84 }}>{c}</span>
                  <span className="h-grow" style={{ color: 'var(--h-text-2)' }}>{r}</span>
                  <span style={{ fontWeight: 800, color:'var(--h-brand)' }}>{d}</span>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* 앱 도크 */}
        <div style={{ flex: 1 }}/>
        <div style={{ padding: '0 16px 24px', display:'grid', gridTemplateColumns:'repeat(4, 1fr)', gap: 12 }}>
          {[
            ['brand', <Mascot size={32} expression="default"/>, '채용알리미', '17'],
            ['blue', '💬', '메시지', null],
            ['green', '🗺️', '지도', null],
            ['orange', '📷', '카메라', null],
          ].map(([color, icon, label, badge], i) =>
            <div key={i} style={{ display:'flex', flexDirection:'column', alignItems:'center', gap: 4 }}>
              <div style={{
                width: 56, height: 56, borderRadius: 16,
                background: color === 'brand' ? 'var(--h-brand)' :
                           color === 'blue' ? '#5b8def' :
                           color === 'green' ? '#3ba861' : '#f97316',
                display:'flex', alignItems:'center', justifyContent:'center',
                fontSize: 26, color:'#fff',
                position: 'relative',
                boxShadow: '0 4px 12px rgba(0,0,0,0.3)'
              }}>
                {icon}
                {badge && (
                  <div style={{
                    position:'absolute', top:-4, right:-4,
                    minWidth: 22, height: 22, padding:'0 5px', borderRadius:999,
                    background:'#ff4b4b', color:'#fff',
                    display:'flex', alignItems:'center', justifyContent:'center',
                    fontSize: 11, fontWeight: 800,
                    border:'2px solid #1a1a1a'
                  }}>{badge}</div>
                )}
              </div>
              <span style={{ fontSize: 11, color:'#fff', fontWeight: 600 }}>{label}</span>
            </div>
          )}
        </div>

        <div className="hifi-nav">
          <div className="hifi-nav-pill" style={{ background:'#fff', opacity: 0.7 }}/>
        </div>
      </div>
    </div>
  );
}

// 토스트 애니메이션
if (typeof document !== 'undefined' && !document.getElementById('hifi-styles-inline')) {
  const s = document.createElement('style');
  s.id = 'hifi-styles-inline';
  s.textContent = `
    @keyframes hifiToast {
      0% { opacity: 0; transform: translateY(-12px) scale(0.9); }
      15% { opacity: 1; transform: translateY(0) scale(1); }
      85% { opacity: 1; transform: translateY(0) scale(1); }
      100% { opacity: 0; transform: translateY(-6px) scale(0.95); }
    }
    .hifi .icon-btn {
      width: 38px; height: 38px;
      border-radius: 12px;
      background: var(--h-bg-2);
      display: flex; align-items: center; justify-content: center;
      cursor: pointer;
    }
  `;
  document.head.appendChild(s);
}

Object.assign(window, {
  HiFi_Onb1, HiFi_OnbSwipe, HiFi_Main, HiFi_Detail, HiFi_Discover, HiFi_MyPage,
  HiFi_LockScreen, HiFi_Widget
});
