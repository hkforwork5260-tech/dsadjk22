package com.jobalert.backend.service

/**
 * 회사명 정규화.
 *
 * 같은 회사인데 사람인 응답과 우리 시드 데이터에서 표기가 달라서 매칭에 실패하는 걸 막는 게 목적이다.
 * 예) "주식회사 카카오" / "(주)카카오" / "㈜카카오" / "Kakao Corp." → 모두 "카카오"로 환원.
 *
 * 규칙 (결정적, 같은 입력은 항상 같은 출력):
 * 1. 양끝 공백 제거
 * 2. 회사 접두/접미 제거 (주식회사·㈜·(주)·유한회사·(유)·재단법인·사단법인·법인·Corp·Inc·Ltd 등)
 * 3. 모든 공백·중점(·)·점(.)·하이픈·언더스코어 제거
 * 4. 영문 소문자화
 * 5. NFKC 정규화 (전각/반각 통일)
 */
object CompanyNameNormalizer {

    // 접두/접미 마커. 앞·뒤 양쪽에서 제거한다.
    private val MARKERS = listOf(
        "주식회사", "유한회사", "재단법인", "사단법인", "의료법인", "학교법인", "법인",
        "(주)", "(유)", "(재)", "(사)", "(의)", "㈜", "㈐",
        "corp.", "corp", "inc.", "inc", "ltd.", "ltd", "co.,ltd.", "co.,ltd", "co.ltd",
        "company", "limited",
    )

    // 한 번에 지워버릴 문자들 (공백·중점·점·하이픈·언더스코어 등).
    // 한글 자모·완성형·영숫자만 남긴다.
    private val STRIPPED_CHARS = Regex("""[\s·.\-_,/'"&()\[\]\\:;!?@#$%^*+=<>|~`]""")

    fun normalize(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        var s = java.text.Normalizer.normalize(raw.trim(), java.text.Normalizer.Form.NFKC).lowercase()

        // 마커 제거를 안정 상태까지 반복 (예: "(주) 주식회사 카카오" → "카카오")
        var changed = true
        while (changed) {
            changed = false
            for (m in MARKERS) {
                val before = s
                s = s.removePrefix(m).removeSuffix(m).trim()
                if (s != before) changed = true
            }
        }

        return s.replace(STRIPPED_CHARS, "")
    }
}
