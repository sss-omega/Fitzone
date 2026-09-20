package kz.evn.otbor

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServis {

    @POST("api/login")
    suspend fun vhod(@Body zapros: VhodZapros): Response<VhodOtvet>

    @POST("api/register")
    suspend fun registraciya(@Body zapros: RegistraciyaZapros): Response<RegistraciyaOtvet>

    @POST("api/forgot-password")
    suspend fun zabylParol(@Body zapros: ZabylParolZapros): Response<ZabylParolOtvet>

    @POST("api/reset-password")
    suspend fun sbrosParolya(@Body zapros: SbrosParolyaZapros): Response<SbrosParolyaOtvet>

    @GET("api/profile")
    suspend fun poluchitProfil(): Response<Polzovatel>

    @PUT("api/profile")
    suspend fun obnovitProfil(@Body zapros: ObnovitProfilZapros): Response<ProstoyOtvet>

    @GET("api/favorite-clubs")
    suspend fun poluchitIzbrannyeKluby(): Response<List<IzbrannyKlub>>

    @POST("api/favorite-clubs")
    suspend fun dobavitIzbrannyKlub(@Body zapros: DobavitKlubZapros): Response<ProstoyOtvet>

    @PUT("api/favorite-clubs/{id}")
    suspend fun sdelatOsnovnymKlubom(@Path("id") id: Int): Response<ProstoyOtvet>

    @DELETE("api/favorite-clubs/{id}")
    suspend fun udalitIzbrannyKlub(@Path("id") id: Int): Response<ProstoyOtvet>

    @GET("api/clubs")
    suspend fun poluchitVseKluby(): Response<List<Klub>>

    @GET("api/visit-history")
    suspend fun poluchitPosescheniya(): Response<List<Poseschenie>>

    @GET("api/security-questions")
    suspend fun poluchitVoprosy(): Response<List<String>>
}
