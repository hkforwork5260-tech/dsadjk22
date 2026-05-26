package com.jobalert.backend.client.saramin

interface SaraminClient {
    fun fetchJobs(params: SaraminFetchParams): List<SaraminJobDto>
}
