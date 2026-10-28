
package com.flores.denunciasapp.network

import com.flores.denunciasapp.model.Denuncia
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface DenunciaApi {

    @POST("denuncias/")
    fun registrar(
        @Body denuncia: Denuncia
    ): Call<Denuncia>

    @GET("denuncias/")
    fun listar(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<List<Denuncia>>

    @GET("denuncias/{id}")
    fun obtenerPorId(
        @Path("id") id: Int
    ): Call<Denuncia>

    @GET("denuncias/ciudadano/{dni}")
    fun buscarPorDni(
        @Path("dni") dni: String
    ): Call<List<Denuncia>>

    @PUT("denuncias/{id}")
    fun atender(
        @Path("id") id: Int
    ): Call<Denuncia>
}

object ApiClient {

    private const val BASE_URL = "http://127.0.0.1:8080/"

    val service: DenunciaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DenunciaApi::class.java)
    }
}
