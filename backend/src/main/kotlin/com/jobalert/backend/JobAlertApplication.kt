package com.jobalert.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableAsync
class JobAlertApplication

fun main(args: Array<String>) {
    runApplication<JobAlertApplication>(*args)
}
