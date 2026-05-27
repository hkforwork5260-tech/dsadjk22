# API Contract — 채용알리미 v0.1

> **백엔드와 안드로이드가 이 문서를 진실의 출처(source of truth)로 삼는다.** 변경 시 PR 단위로 합의.

## 기본 규칙

- **Base URL**: `https://api.jobalert.app/api/v1` (운영) / `http://10.0.2.2:8080/api/v1` (안드로이드 에뮬레이터 → host)
- **Content-Type**: `application/json; charset=utf-8`
- **시간 표기**: ISO 8601 UTC 문자열 (예: `"2026-05-26T18:00:00Z"`). 클라이언트에서 KST 변환.
- **인증 (v0.1)**: 익명 디바이스 기반. `X-Device-Id: <uuid>` 헤더 (앱 설치 시 1회 생성). 별도 로그인 없음.
- **에러 응답** (4xx, 5xx):
  ```json
  { "error": { "code": "JOB_NOT_FOUND", "message": "공고를 찾을 수 없습니다.", "details": null } }
  ```
- **페이지네이션**: cursor 기반. 요청에 `cursor`, 응답에 `next_cursor` (`null`이면 끝).

## 공통 타입

### `Job` (요약형)
공고 카드에 노출되는 최소 정보.

```json
{
  "id": "samsung-2026-h1",
  "company": {
    "id": 1,
    "name": "삼성전자",
    "logo": "삼성",
    "logo_url": "https://logo.clearbit.com/samsung.com",
    "industry": "전기·전자"
  },
  "title": "2026 상반기 신입공채",
  "kind": "NEW",
  "dday": "D-24",
  "deadline": "2026-06-15T14:59:59Z",
  "location": "수원",
  "experience": "신입",
  "education": "학사+",
  "tags": ["반도체", "DS", "신입공채"],
  "is_favorited": false
}
```

- `kind`: `"NEW" | "UPDATE" | "CLOSING" | "EXPIRED"`
- `dday`: 사람이 보기 좋은 표기 (`D-1`, `D-Day`, `D+3` 등). 로직은 백엔드에서 계산
- `deadline`: 마감 시각 (UTC). 클라이언트 정렬 시 사용

### `JobDetail` (상세형)
공고 상세 화면에서 사용. `Job` + 추가 필드.

```json
{
  "id": "samsung-2026-h1",
  "company": { ... },
  "title": "2026 상반기 신입공채",
  "kind": "NEW",
  "dday": "D-24",
  "deadline": "2026-06-15T14:59:59Z",
  "posting_date": "2026-05-20T09:00:00Z",
  "location": "수원",
  "experience": "신입",
  "education": "학사+",
  "salary": "회사내규",
  "job_categories": ["IT개발·데이터"],
  "tags": ["반도체", "DS", "신입공채"],
  "description": "삼성전자에서 2026년 상반기 신입공채를 시작합니다. ...",
  "summary": "삼성 DS부문 신입공채. 학사 이상, 전공 무관, 6월 15일까지.",
  "preferred": [
    "대규모 분산 시스템 경험",
    "Spring Boot / Kotlin 능숙자"
  ],
  "process": ["서류전형", "코딩테스트", "1차 면접", "최종 면접"],
  "original_url": "https://www.samsungcareers.com/...",
  "source": "saramin",
  "is_favorited": false
}
```

### `Company` (요약형 / 상세형 공용)
```json
{
  "id": 1,
  "name": "삼성전자",
  "name_normalized": "samsungelectronics",
  "logo_url": "https://logo.clearbit.com/samsung.com",
  "industry": "전기·전자",
  "group": "삼성",
  "size": "large_corp",
  "homepage_url": "https://www.samsung.com/sec/",
  "careers_url": "https://www.samsungcareers.com/",
  "active_job_count": 12,
  "is_favorited": false
}
```

### `JobCategory`
```json
{ "code": "it_dev_data", "label": "IT개발·데이터" }
```

22개 카테고리 코드 (사람인 카테고리 21개 + "기타" 1개). 안드로이드는 `data/model/Job.kt` `JobCategories`, 백엔드는 `JobCategory` enum에 정의 (둘 다 22개 동기화).

---

## 엔드포인트

### 1. 오늘의 공고

```
GET /api/v1/jobs/today
```

**Query params**:
- `kind` (optional): `NEW | UPDATE | CLOSING` — 필터 (없으면 셋 다 묶어서 반환)
- `categories` (optional, repeatable): `it_dev_data,design` — 사용자의 관심 직군
- `limit` (default 30): 페이지 사이즈

**Response**:
```json
{
  "date": "2026-05-26",
  "counts": { "new": 6, "update": 2, "closing": 1 },
  "jobs": [ { ...Job }, ... ],
  "next_cursor": null
}
```

**Notes**:
- 헤더 `X-Device-Id` 있으면 해당 사용자의 관심 직군 자동 필터링
- 없으면 전체

---

### 2. 공고 상세

```
GET /api/v1/jobs/{id}
```

**Response**: `JobDetail`

**Errors**:
- 404 `JOB_NOT_FOUND`

---

### 3. 유사 공고

```
GET /api/v1/jobs/{id}/similar
```

**Response**:
```json
{ "jobs": [ { ...Job }, ... ] }
```

직군·산업·회사규모 기준 유사도 매칭. 최대 10개.

---

### 4. 검색

```
GET /api/v1/jobs/search
```

**Query params**:
- `q` (required): 키워드 (회사명·직무·태그)
- `kind` (optional)
- `categories` (optional)
- `location` (optional): `서울 | 경기 | ...`
- `experience` (optional): `신입 | 경력 | 무관`
- `cursor` (optional)
- `limit` (default 20)

**Response**:
```json
{
  "query": "삼성",
  "total_estimate": 38,
  "jobs": [ ... ],
  "next_cursor": "eyJvZmZzZXQiOjIwfQ=="
}
```

---

### 5. 회사 상세

```
GET /api/v1/companies/{id}
```

**Response**: `Company` 객체 + 추가 필드:
```json
{
  ...Company,
  "description": "삼성전자는 글로벌 ...",
  "stats": {
    "total_postings_30d": 32,
    "avg_postings_per_week": 7
  }
}
```

---

### 6. 회사의 공고 목록

```
GET /api/v1/companies/{id}/jobs
```

**Query params**: `kind`, `cursor`, `limit`

**Response**: `{ "company": {...Company}, "jobs": [...], "next_cursor": null }`

---

### 7. 직군 목록 (온보딩)

```
GET /api/v1/onboarding/categories
```

**Response**:
```json
{
  "categories": [
    { "code": "plan_strategy", "label": "기획·전략" },
    { "code": "marketing_pr", "label": "마케팅·홍보·조사" },
    ...
  ]
}
```

**정식 코드 키 21개 (canonical — 백엔드 source of truth, Flyway V1)**:

| code | label |
|---|---|
| `plan_strategy` | 기획·전략 |
| `marketing_pr` | 마케팅·홍보·조사 |
| `accounting_finance` | 회계·세무·재무 |
| `hr_hrd` | 인사·노무·HRD |
| `admin_legal` | 총무·법무·사무 |
| `it_dev_data` | IT개발·데이터 |
| `design` | 디자인 |
| `sales_trade` | 영업·판매·무역 |
| `customer_tm` | 고객상담·TM |
| `purchase_logistics` | 구매·자재·물류 |
| `md_planning` | 상품기획·MD |
| `driving_delivery` | 운전·운송·배송 |
| `service` | 서비스 |
| `production` | 생산 |
| `construction` | 건설·건축 |
| `medical` | 의료 |
| `research` | 연구·R&D |
| `education` | 교육 |
| `media_culture_sport` | 미디어·문화·스포츠 |
| `finance_insurance` | 금융·보험 |
| `public_welfare` | 공공·복지 |

안드로이드는 첫 진입에 1회 호출 후 캐시. "기타" 카테고리는 v0.2 이후 사람인 분류 외 공고용으로 별도 검토.

---

### 8. 인기 회사 (온보딩 ②)

```
GET /api/v1/onboarding/popular-companies
```

**Query params**:
- `categories` (optional): 사용자가 선택한 직군 기반 추천

**Response**: `{ "companies": [ ... ] }`

24개 정도 반환. 안드로이드는 스와이프로 좋아요/싫어요 입력 (Tinder 스타일).

---

### 9. 디바이스 등록 (FCM 토큰)

```
POST /api/v1/devices/register
```

**Body**:
```json
{
  "fcm_token": "fcm-abc123...",
  "platform": "android",
  "device_id": "<uuid same as X-Device-Id>",
  "app_version": "0.1.0",
  "os_version": "14",
  "preferences": {
    "categories": ["it_dev_data", "design"],
    "favorite_companies": [1, 42, 88],
    "push_morning": true,
    "push_evening": true
  }
}
```

**Response**:
```json
{ "device_id": "<uuid>", "registered_at": "2026-05-26T10:00:00Z" }
```

---

### 10. 디바이스 설정 변경

```
PATCH /api/v1/devices/{device_id}/preferences
```

**Body**: 부분 업데이트 가능
```json
{ "categories": ["it_dev_data"], "push_morning": false }
```

**Response**: 업데이트된 전체 preferences 객체.

---

### 11. 관심기업 추가 / 제거

```
POST   /api/v1/users/me/favorites/{company_id}
DELETE /api/v1/users/me/favorites/{company_id}
```

**Header**: `X-Device-Id: <uuid>` (필수)

**Response**: `{ "favorited": true | false, "company_id": 42 }`

---

### 12. 관심기업 목록

```
GET /api/v1/users/me/favorites
```

**Response**: `{ "companies": [ ...Company ] }`

---

### 13. 알림 히스토리

```
GET /api/v1/notifications/history
```

**Header**: `X-Device-Id` 필수

**Query**: `cursor`, `limit` (default 30)

**Response**:
```json
{
  "notifications": [
    {
      "id": "ntf-001",
      "sent_at": "2026-05-26T00:00:00Z",
      "kind": "morning_digest",
      "title": "오늘 새 공고 6건 ☀️",
      "body": "삼성·네이버·카카오·...",
      "job_ids": ["samsung-2026-h1", "naver-backend", ...],
      "read": false
    },
    ...
  ],
  "next_cursor": null
}
```

---

### 14. 알림 읽음 처리

```
POST /api/v1/notifications/{id}/read
```

**Header**: `X-Device-Id`

**Response**: `{ "id": "ntf-001", "read": true }`

---

### 15. 마감 임박 (캘린더용)

```
GET /api/v1/jobs/upcoming
```

**Query**:
- `days` (default 14): 향후 며칠 안 마감
- `categories`, `companies` 필터

**Response**:
```json
{
  "days": 14,
  "by_date": {
    "2026-05-28": [ { ...Job } ],
    "2026-05-29": [ { ...Job } ],
    "2026-06-01": [ { ...Job }, { ...Job } ]
  }
}
```

---

## 푸시 알림 페이로드 (FCM)

백엔드 → 안드로이드 디바이스로 전송 시 사용.

```json
{
  "notification": {
    "title": "오늘 새 공고 6건 ☀️",
    "body": "삼성전자, 네이버, 카카오 외 3건"
  },
  "data": {
    "kind": "morning_digest",
    "notification_id": "ntf-001",
    "deep_link": "jobalert://today"
  }
}
```

`data.deep_link` 예시:
- `jobalert://today` — 메인 피드
- `jobalert://jobs/{id}` — 특정 공고 상세
- `jobalert://notifications` — 알림 히스토리

안드로이드 측: NavGraph deep link 매핑 등록.

---

## Mock 응답 예시

Phase 1·2에서 백엔드 mock 모드 / 안드로이드 mock Repository 둘 다 동일한 JSON을 반환해야 함. 샘플 파일 위치:
- `backend/src/main/resources/mock/jobs-today.json`
- `android-app/app/src/main/assets/mock/jobs-today.json` (또는 hard-coded `SampleJobs.kt`)

샘플 JSON 8건은 `android-app/app/src/main/kotlin/com/jobalert/app/data/sample/SampleJobs.kt`의 데이터를 그대로 JSON 직렬화하면 됨.

---

## 향후 v0.2+ 추가 예정 (참고)

- `POST /api/v1/feedback` — 사용자 피드백
- `GET /api/v1/widget/glance` — 홈 위젯용 경량 응답
- `POST /api/v1/jobs/{id}/apply` — 외부 지원 페이지 이동 추적
- `GET /api/v1/insights` — 채용 트렌드 인사이트

---

## 변경 로그

- 2026-05-26: 초안 작성 (Phase 0)
