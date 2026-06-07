package com.jobalert.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * MVC 인터셉터 등록. [AdminAuthInterceptor]를 관리자 경로에만 매핑한다.
 */
@Configuration
class WebConfig(
    private val adminAuthInterceptor: AdminAuthInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(adminAuthInterceptor)
            .addPathPatterns("/api/v1/admin/**")
    }
}
