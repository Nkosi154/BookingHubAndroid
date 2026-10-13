package com.bookinghub.app.data.repository

import com.bookinghub.app.data.model.Category
import com.bookinghub.app.data.model.ProviderDetail
import com.bookinghub.app.data.model.ProviderSummary
import com.bookinghub.app.data.model.Review
import com.bookinghub.app.data.model.ReviewRequest
import com.bookinghub.app.data.remote.ApiService
import com.bookinghub.app.data.remote.safeApiCall

class CatalogRepository(private val api: ApiService) {

    suspend fun getCategories(): Result<List<Category>> =
        safeApiCall { api.getCategories() }

    suspend fun getProviders(
        category: String? = null,
        query: String? = null,
        availability: String? = null,
        minRating: String? = null,
        sort: String? = null
    ): Result<List<ProviderSummary>> =
        safeApiCall { api.getProviders(category, query, availability, minRating, sort) }

    suspend fun getProviderDetail(providerId: Int): Result<ProviderDetail> =
        safeApiCall { api.getProviderDetail(providerId) }

    suspend fun postReview(providerId: Int, rating: Int, comment: String): Result<Review> =
        safeApiCall { api.postReview(providerId, ReviewRequest(rating, comment)) }
}
