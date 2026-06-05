package com.jobalert.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

/**
 * 시간 의존성을 빈으로 빼서 테스트에서 고정 시각을 주입할 수 있게 한다.
 *
 * diff 라벨링(CLOSING 판정·closedAt 기록 등)이 "지금"에 의존하므로, 코드 안에서
 * `OffsetDateTime.now()`를 직접 부르지 않고 주입받은 [Clock]을 쓴다. 그래야 테스트가
 * "마감 2일 전" 같은 상황을 결정적으로 재현할 수 있다.
 */
@Configuration
class TimeConfig {
    @Bean
    fun clock(): Clock = Clock.systemUTC()
}
