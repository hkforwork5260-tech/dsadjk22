-- 공공기관(JOB-ALIO) 원문 링크 직링크 수정.
-- recruitview.do는 파라미터가 recrutPblntSn이 아니라 idx여야 해당 공고로 바로 간다
-- (recrutPblntSn은 무시돼 통합 목록으로 빠졌음). 기존에 적재된 행을 일괄 보정.
UPDATE jobs
SET original_url = REPLACE(original_url, 'recruitview.do?recrutPblntSn=', 'recruitview.do?idx=')
WHERE original_url LIKE '%recruitview.do?recrutPblntSn=%';
