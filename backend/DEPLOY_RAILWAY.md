# Railway 배포 가이드 (채용알리미 백엔드)

> 목적: 로컬에서만 돌던 백엔드를 **항상 켜져 있는 클라우드 서버**로 올려서,
> 매일 자동으로 공고를 수집하고(cron) 앱이 실데이터를 받게 만든다.
>
> 이 문서는 **사용자가 직접 클릭/입력**해야 하는 단계 위주다. 코드·설정(Dockerfile, prod 프로파일,
> Redis 제거)은 이미 준비돼 있다.

---

## 0. 큰 그림 (왜 이렇게 하나)

```
GitHub(dsadjk22) ──push──▶ Railway가 자동 감지
                              ├─ 백엔드 서비스 (Dockerfile로 빌드 → 항상 켜짐)
                              └─ PostgreSQL 서비스 (관리형 DB)
                                     ▲
                  백엔드가 DATABASE_URL로 연결
```

- **항상 켜짐**이 핵심. 우리 앱은 매일 18시(KST) 수집 cron, 9시·21시 푸시 cron이 돌아야 한다.
  Railway는 서버를 재우지 않아서 cron이 안정적으로 뜬다. (Render 무료티어는 재워서 부적합 → 그래서 Railway 선택)
- DB는 우리가 직접 깔지 않고 Railway **관리형 Postgres** 버튼 하나로 만든다.
- 비밀값(DB 비번 등)은 코드가 아니라 Railway **환경변수**에 넣는다.

---

## 1. 사전 준비

1. 이 변경사항을 GitHub에 push 한다(아직 안 했다면). Railway가 이 코드를 가져간다.
   ```
   git add backend/Dockerfile backend/.dockerignore backend/build.gradle.kts \
           backend/docker-compose.yml backend/src/main/resources/application*.yml \
           backend/DEPLOY_RAILWAY.md
   git commit -m "배포: Railway용 Dockerfile + Redis 제거 + prod 정비"
   git push
   ```
2. Railway 계정: https://railway.com → **Login with GitHub** (GitHub 계정으로 가입하면 repo 연결이 쉬움).
   - 요금: 가입 시 무료 크레딧 제공 → 이후 Hobby 플랜(월 $5에 사용량 $5 포함, 소규모면 대개 그 안). 정확한 최신 요금은 가입 화면에서 확인.

---

## 2. 프로젝트 + DB 만들기

1. Railway 대시보드 → **New Project**.
2. **Deploy from GitHub repo** 선택 → `hkforwork5260-tech/dsadjk22` 선택.
   - 권한 요청이 뜨면 이 repo에 접근 허용.
3. 백엔드 서비스가 생기면, 같은 프로젝트 캔버스에서 **New → Database → Add PostgreSQL** 클릭.
   - `Postgres`라는 서비스가 하나 더 생긴다. (DB 비번 등은 자동 생성됨)

---

## 3. 백엔드 서비스 설정

백엔드 서비스 카드를 클릭 → **Settings** 탭.

### 3-1. Root Directory (★ 꼭 해야 함)
- 우리 repo는 루트가 `jobalert/`이고 백엔드는 `backend/` 하위에 있다.
- **Settings → Build → Root Directory** 에 `backend` 입력.
  - 이렇게 해야 Railway가 `backend/Dockerfile`을 찾아서 빌드한다.
- Builder는 Dockerfile이 있으면 자동으로 **Dockerfile**로 잡힌다. (아니면 수동 선택)

### 3-2. 환경변수 (Variables 탭)
아래를 그대로 추가한다. `${{Postgres.XXX}}`는 Railway의 **다른 서비스 변수 참조** 문법이다
(Postgres 서비스가 들고 있는 값을 가져온다). Postgres 서비스 이름이 `Postgres`가 아니면 그 이름으로 바꿔라.

| 변수 이름 | 값 | 설명 |
|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}` | ⚠️ Railway 기본 `DATABASE_URL`은 `postgresql://`라 Spring이 못 읽음. **반드시 `jdbc:` 붙은 이 형태로 직접 입력** |
| `DATABASE_USERNAME` | `${{Postgres.PGUSER}}` | DB 사용자 |
| `DATABASE_PASSWORD` | `${{Postgres.PGPASSWORD}}` | DB 비번 |
| `SPRING_PROFILES_ACTIVE` | `prod` | prod 프로파일 활성 (Dockerfile에도 기본값 있지만 명시 권장) |
| `COLLECTOR_ENABLED` | `true` | 매일 자동수집 cron 켜기 (prod 기본 true지만 명시) |
| `PUSH_ENABLED` | `false` | 푸시는 FCM 준비 전까지 끔(아래 7장 참고). 켜도 키 없으면 무해하지만 깔끔하게 false |

**데이터 더 받으려면(선택, 권장)** — 사용자가 가진 data.go.kr / data.seoul.go.kr 키:

| 변수 이름 | 값 | 설명 |
|---|---|---|
| `PUBINST_ENABLED` | `true` | 기재부 공공기관(공기업 500건+) |
| `JOBALERT_PUBINST_KEY` | (본인 data.go.kr 키) | |
| `SEOUL_ENABLED` | `true` | 서울시 일자리포털(441건+, 급여 제공) |
| `JOBALERT_SEOUL_KEY` | (본인 data.seoul.go.kr 키) | |

> Greenhouse/Lever는 키가 필요 없어서 위 키가 없어도 ~399건은 자동 수집된다.
> 키를 넣으면 공기업·서울시까지 합쳐 1,300건+ 가 된다.

### 3-3. 공개 도메인 만들기
- **Settings → Networking → Generate Domain** 클릭.
- `xxxx.up.railway.app` 주소가 나온다. 앱이 이 주소로 접속한다.
- 포트: 우리 앱은 `${PORT:8080}`을 쓰고 Railway가 `PORT`를 자동 주입하므로 별도 설정 불필요.

---

## 4. 배포 & 확인

1. 위 설정을 저장하면 Railway가 자동으로 다시 빌드·배포한다. (Deployments 탭에서 로그 확인)
   - 빌드 로그에 `BUILD SUCCESSFUL`, 실행 로그에 `Started JobAlertApplicationKt` 가 보이면 성공.
   - Flyway가 DB에 테이블 10개+를 자동 생성한다(`Successfully applied ... migrations`).
2. 헬스체크:
   ```
   curl https://<도메인>/actuator/health
   → {"status":"UP"}
   ```
3. **첫 수집 수동 트리거** (v0.1은 인증 없음):
   ```
   curl -X POST https://<도메인>/api/v1/admin/collect
   ```
   - 로그에 수집 건수가 찍힌다. 끝나면:
   ```
   curl "https://<도메인>/api/v1/jobs/today"
   ```
   로 공고가 나오는지 확인.
4. 이후부터는 매일 **18:00 KST**에 자동 수집된다(`COLLECTOR_ENABLED=true`).

---

## 5. cron 시간표 (이미 코드에 설정됨, UTC 기준)

| 작업 | 코드 cron(UTC) | 실제(KST) |
|---|---|---|
| 자동 수집 | `0 0 9 * * *` | 매일 18:00 |
| 아침 푸시 | `0 0 0 * * *` | 매일 09:00 |
| 저녁 푸시 | `0 0 12 * * *` | 매일 21:00 |

Railway 서버는 UTC로 도므로 위 변환이 그대로 맞다. 변경 불필요.

---

## 6. 안드로이드 앱 연결 (배포 후)

앱은 지금 에뮬레이터용 `10.0.2.2:8080`을 본다. 클라우드 주소로 바꿔야 실기기에서도 동작한다.
- 파일: `android-app/.../data/api/ApiClient.kt` 의 `BASE_URL`
- `https://<도메인>/` 으로 변경 후 앱 재빌드(사용자 PC에서).
- (이 단계는 안드로이드 작업이라 이 배포와 별개. 서버 먼저 띄운 뒤 진행.)

---

## 7. FCM 푸시 (후속 — 첫 배포엔 불필요)

푸시를 켜려면 FCM 서비스 계정 키(`secrets/fcm-service-account.json`)가 서버에 있어야 한다.
이 파일은 보안상 **이미지·git에 안 들어간다**(.dockerignore, .gitignore). 그래서 Railway에선:

- **방법(권장)**: 키 JSON 전체를 환경변수(예 `FCM_CREDENTIALS_JSON`)에 넣고, 시작 시 파일로 떨구도록
  `FcmSender`를 살짝 고친다(현재는 파일 경로만 읽음). → 다음 세션에서 작업.
- 준비되면 `FCM_ENABLED=true`, `PUSH_ENABLED=true`로 전환.

지금은 `PUSH_ENABLED=false`로 두고 수집·조회만 운영해도 앱의 핵심(매일 새 공고)은 동작한다.

---

## 8. 자주 막히는 곳

| 증상 | 원인/해결 |
|---|---|
| 부팅 직후 죽고 로그에 `Failed to configure a DataSource` | `DATABASE_URL`이 `jdbc:`로 시작 안 함. 3-2 표대로 다시 입력 |
| `UnknownHostException ... PGHOST` | 참조변수 서비스명이 `Postgres`가 아님. 실제 Postgres 서비스 이름으로 수정 |
| 빌드가 Dockerfile 안 쓰고 이상하게 됨 | Root Directory가 `backend`로 안 잡힘(3-1) |
| 공고가 0건 | 첫 수집을 안 돌림 → 4-3의 `POST /admin/collect` 실행 |
| 헬스는 UP인데 도메인 접속 404 | 도메인 생성(3-3) 안 했거나 빌드 진행 중 |
