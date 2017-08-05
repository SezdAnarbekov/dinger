package data.network.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationUserJob private constructor(
        @field:Json(name = "company")
        val company: RecommendationUserJobCompany,
        @field:Json(name = "title")
        val title: RecommendationUserJobTitle)
