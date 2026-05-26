// app.jsx — 디자인 캔버스 (전체 하이파이)
// 백업: Wireframes Full Backup.html (v1 + v2 보존)

// 손글씨 코멘트 노트 (artboard 위에 띄움)
function Stamp({ children, color = 'var(--update)' }) {
  return (
    <div style={{
      display: 'inline-block',
      fontFamily: "'Nanum Pen Script', cursive",
      fontSize: 16,
      padding: '2px 6px',
      border: '1.6px solid '+color,
      color,
      borderRadius: 4,
      transform: 'rotate(-2deg)',
      background: 'rgba(255,255,255,0.85)'
    }}>{children}</div>
  );
}

const HF_W = 400;
const HF_H = 780;

function App() {
  const TWEAKS = /*EDITMODE-BEGIN*/{
    "showStamps": true,
    "mascot": "cat"
  }/*EDITMODE-END*/;
  const [t, setTweak] = useTweaks(TWEAKS);
  window.__mascotSpecies = t.mascot;

  const Cap = ({ tone = 'default', children }) => (
    t.showStamps ? (
      <div style={{ position:'absolute', left: 8, bottom: -28, zIndex: 2 }}>
        <Stamp color={
          tone === 'pick' ? 'var(--good)' :
          tone === 'warn' ? 'var(--closing)' : 'var(--update)'
        }>{children}</Stamp>
      </div>
    ) : null
  );

  return (
    <>
      <DesignCanvas>
        {/* ── 인터랙티브 프로토타입 ─────────── */}
        <DCSection id="prototype" title="📲 인터랙티브 프로토타입" subtitle="실제로 탭하며 흐름 체험 — 카드 우측 상단 ⤢로 풀스크린 추천">
          <DCArtboard id="proto" label="🎮 클릭 가능한 데모" width={520} height={760}>
            <Prototype/>
          </DCArtboard>
        </DCSection>

        {/* ── 하이파이 ① 온보딩 ─────────── */}
        <DCSection id="hifi-onb" title="🎨 하이파이 · 온보딩" subtitle="첫 진입 4단계 — 직군 → 기업/산업 → 회사 스와이프 → 위젯/알림">
          <DCArtboard id="hifi-onb1" label="① 직군 선택 (21개)" width={HF_W} height={HF_H}>
            <HiFi_Onb1/>
            <Cap tone="pick">사람인 21개 카테고리</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-onb2" label="② 기업 규모/산업" width={HF_W} height={HF_H}>
            <HiFi_Onb2/>
            <Cap>매칭 정확도 ↑</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-onbswipe" label="③ 회사 스와이프 (Reels)" width={HF_W} height={HF_H}>
            <HiFi_OnbSwipe/>
            <Cap tone="pick">찾아보기와 동일한 패턴</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-onb4" label="④ 위젯 + 알림 유도" width={HF_W} height={HF_H}>
            <HiFi_Onb4Widget/>
            <Cap tone="pick">자동 9시·21시</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 하이파이 ② 메인 흐름 ─────────── */}
        <DCSection id="hifi-main" title="🎨 하이파이 · 메인 흐름" subtitle="피드 · 상세 · 검색 · 필터">
          <DCArtboard id="hifi-main-feed" label="① 메인 피드" width={HF_W} height={HF_H}>
            <HiFi_Main/>
            <Cap tone="pick">NEW/UPDATE/CLOSING 토글</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-empty" label="② 메인 (빈 상태)" width={HF_W} height={HF_H}>
            <HiFi_MainEmpty/>
            <Cap>"오늘은 조용한 날"</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-detail" label="③ 공고 상세" width={HF_W} height={HF_H}>
            <HiFi_Detail/>
            <Cap tone="pick">탭 + AI 요약</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-filter" label="④ 필터 (풀스크린)" width={HF_W} height={HF_H}>
            <HiFi_Filter/>
            <Cap>21개 직군 + 6 그룹</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-search" label="⑤ 검색 (최근/인기/직군)" width={HF_W} height={HF_H}>
            <HiFi_Search/>
            <Cap>직군별 둘러보기</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-srchres" label="⑥ 검색 결과" width={HF_W} height={HF_H}>
            <HiFi_SearchResults/>
            <Cap>기업 + 공고 분리</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 하이파이 ③ 찾아보기 + 관심기업 ─────────── */}
        <DCSection id="hifi-explore" title="🎨 하이파이 · 찾아보기 / 관심기업" subtitle="릴스 스와이프 + 회사 상세">
          <DCArtboard id="hifi-discover" label="① 찾아보기 (릴스)" width={HF_W} height={HF_H}>
            <HiFi_Discover/>
            <Cap tone="pick">❤️ 관심기업 · 🔖 공고 저장</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-fav" label="② 관심 기업 그리드" width={HF_W} height={HF_H}>
            <HiFi_Favorites/>
            <Cap tone="pick">빨간점 = 오늘 새공고</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-company" label="③ 회사 상세 (공고 있음)" width={HF_W} height={HF_H}>
            <HiFi_CompanyDetail/>
            <Cap tone="pick">삼성전자</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-company-empty" label="④ 회사 상세 (공고 없음)" width={HF_W} height={HF_H}>
            <HiFi_CompanyDetailEmpty/>
            <Cap tone="warn">"현재 채용 없음"</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 하이파이 ④ 마이페이지 + 서브 ─────────── */}
        <DCSection id="hifi-me" title="🎨 하이파이 · 마이페이지" subtitle="스트릭 + 메뉴 + 서브페이지 5종">
          <DCArtboard id="hifi-mypage" label="① 마이페이지" width={HF_W} height={HF_H}>
            <HiFi_MyPage/>
            <Cap tone="pick">본 공고 + 저장한 공고</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-saved" label="② 저장한 공고" width={HF_W} height={HF_H}>
            <HiFi_SavedPostings/>
            <Cap>저장 카운터 → 진입</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-notifSet" label="③ 알림 설정" width={HF_W} height={HF_H}>
            <HiFi_NotifSettings/>
            <Cap>9시·21시 고정</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-widgetSet" label="④ 위젯 설정 (실시간 미리보기)" width={HF_W} height={HF_H}>
            <HiFi_WidgetSettings/>
            <Cap tone="pick">Small/Medium/Large</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-jobInt" label="⑤ 관심 직군" width={HF_W} height={HF_H}>
            <HiFi_JobInterests/>
            <Cap>21개 카테고리</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-feedback" label="⑥ 피드백 보내기" width={HF_W} height={HF_H}>
            <HiFi_Feedback/>
            <Cap>유형 + 자유 입력</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 하이파이 ⑤ 푸시 + 위젯 ─────────── */}
        <DCSection id="hifi-os" title="🎨 하이파이 · OS 노출 (잠금화면 + 위젯)" subtitle="듀오링고처럼 매일 노출되는 진입점">
          <DCArtboard id="hifi-lock" label="① 잠금화면 + 푸시" width={HF_W} height={HF_H}>
            <HiFi_LockScreen/>
            <Cap tone="pick">코랄 그라데이션</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-widget" label="② 바탕화면 위젯 (Large)" width={HF_W} height={HF_H}>
            <HiFi_Widget/>
            <Cap tone="pick">홈스크린 + 도크</Cap>
          </DCArtboard>
        </DCSection>

        {/* ── 추가 기능 (신규) ─────────── */}
        <DCSection id="hifi-new" title="🆕 추가 기능 (4종)" subtitle="알림 히스토리 · 마감 캘린더 · 공유 시트 · 비슷한 공고">
          <DCArtboard id="hifi-notifHistory" label="① 알림 히스토리" width={HF_W} height={HF_H}>
            <HiFi_NotifHistory/>
            <Cap tone="pick">놓친 푸시 모아보기</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-calendar" label="② 마감 캘린더" width={HF_W} height={HF_H}>
            <HiFi_Calendar/>
            <Cap tone="pick">월 그리드 + 마감 시각화</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-share" label="③ 공유 시트" width={HF_W} height={HF_H}>
            <HiFi_ShareSheet/>
            <Cap tone="pick">카톡/라인/링크</Cap>
          </DCArtboard>
          <DCArtboard id="hifi-similar" label="④ 비슷한 공고 추천" width={HF_W} height={HF_H}>
            <HiFi_Similar/>
            <Cap tone="pick">AI 매칭률 + 사유</Cap>
          </DCArtboard>
        </DCSection>
      </DesignCanvas>

      <TweaksPanel title="옵션">
        <TweakSection label="마스코트">
          <TweakRadio
            label="종(species)"
            value={t.mascot}
            options={[{ value: 'cat', label: '🐱 고양이' }, { value: 'dog', label: '🐶 강아지' }]}
            onChange={v => setTweak('mascot', v)}
          />
        </TweakSection>
        <TweakSection label="표시">
          <TweakToggle label="손글씨 코멘트" value={t.showStamps} onChange={v => setTweak('showStamps', v)}/>
        </TweakSection>
        <TweakSection label="안내">
          <div style={{ fontFamily: "'Nanum Pen Script', cursive", fontSize: 15, color: '#4a4a4a', lineHeight: 1.3 }}>
            • 캔버스 빈 공간 드래그 → 패닝<br/>
            • 휠/핀치 → 줌인/줌아웃<br/>
            • ⤢ 버튼 → 풀스크린<br/>
            • 백업: Wireframes Full Backup.html
          </div>
        </TweakSection>
      </TweaksPanel>
    </>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App/>);
