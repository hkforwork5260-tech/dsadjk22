-- 기기 관심 회사규모(개인화 다이제스트용). 직군은 device_categories 테이블, 규모는 적어서 컬럼으로.
-- 콤마 구분 코드(large_corp,public,…). 비어 있으면 규모 무관(전체).
ALTER TABLE devices ADD COLUMN IF NOT EXISTS interest_sizes TEXT;
