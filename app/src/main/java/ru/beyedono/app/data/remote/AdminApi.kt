// file: app/src/main/java/ru/beyedono/app/data/remote/AdminApi.kt
package ru.beyedono.app.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.beyedono.app.data.remote.dto.ProductDto

data class ProductCreateRequest(
    val category_id: String,
    val name: String,
    val description: String? = null,
    val price: Double,
    val image_url: String? = null,
    val is_available: Boolean = true,
    val is_spicy: Boolean = false,
    val is_vegetarian: Boolean = false
)

interface AdminApi {
    @POST("admin/products")
    suspend fun createProduct(@Body request: ProductCreateRequest): ProductDto

    @PUT("admin/products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body request: ProductCreateRequest): ProductDto

    @DELETE("admin/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String)
}
