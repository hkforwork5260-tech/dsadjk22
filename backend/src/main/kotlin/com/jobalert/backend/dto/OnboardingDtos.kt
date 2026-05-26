package com.jobalert.backend.dto

data class JobCategoryDto(
    val code: String,
    val label: String,
)

data class CategoriesResponse(
    val categories: List<JobCategoryDto>,
)
