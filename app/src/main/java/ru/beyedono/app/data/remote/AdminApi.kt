// file: app/src/main/java/ru/beyedono/app/data/remote/AdminApi.kt
package ru.beyedono.app.data.remote

import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import ru.beyedono.app.data.remote.dto.ProductDto

@Serializable // <--- ДОБАВЛЕНО (из-за этого была ошибка)
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

@Serializable
data class CategoryCreateRequest(val name: String)

@Serializable
data class UploadResponse(val url: String)

interface AdminApi {
    @POST("admin/products")
    suspend fun createProduct(@Body request: ProductCreateRequest): ProductDto

    @PUT("admin/products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body request: ProductCreateRequest): ProductDto

    @DELETE("admin/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String)

    @Multipart
    @POST("admin/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): UploadResponse

    @POST("admin/categories")
    suspend fun createCategory(@Body request: CategoryCreateRequest)

    @PUT("admin/categories/{old_name}")
    suspend fun renameCategory(@Path("old_name") oldName: String, @Body request: CategoryCreateRequest)

    @DELETE("admin/categories/{cat_id}")
    suspend fun deleteCategory(@Path("cat_id") catId: String)
}
