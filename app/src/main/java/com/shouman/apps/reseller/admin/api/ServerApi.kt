package com.shouman.apps.reseller.admin.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.shouman.apps.reseller.admin.data.model.DatabaseCustomer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


class BasicAuthInterceptor(user: String, password: String) : Interceptor {

    private val credential = Credentials.basic(user, password)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authRequest = request
            .newBuilder()
            .addHeader("Authorization", credential)
            .build()
        return chain.proceed(authRequest)
    }
}


val BASIC_NAME: String = "admin"
val BASIC_PASWORD: String = "password"
private val BASE_URL = "http://192.168.1.6:8080/hawk/"


/**
 * Build the Moshi object that Retrofit will be using, add Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi by lazy {
    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}


private val client by lazy {
    OkHttpClient.Builder()
        .addInterceptor(BasicAuthInterceptor(BASIC_NAME, BASIC_PASWORD))
        .build()
}


private val retrofit by lazy {

//    val interceptor = HttpLoggingInterceptor();
//    interceptor.level = HttpLoggingInterceptor.Level.BODY;
//    val client = OkHttpClient.Builder().addInterceptor(interceptor).build();

    Retrofit
        .Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()
}


interface UsersApiServices {

    @POST("users")
    fun addNewUserToDatabaseAsync(@Body user: ServerUser?): Deferred<ServerResponse<ServerUser?>>

    @PUT("users/companies/{firebaseUID}")
    fun updateCompanyInfoAsync(
        @Path("firebaseUID") firebaseUID: String,
        @Body company: ServerCompany?
    ): Deferred<ServerResponse<ServerUser?>>

    @GET("users/{firebaseUID}")
    fun getUserByFirebaseUID(@Path("firebaseUID") firebaseUID: String): Deferred<ServerResponse<ServerUser?>>

}

interface BranchesApiServices {


    @POST("companies/{companyId}/branches")
    fun addNewBranchToServerAsync(
        @Path("companyId") companyId: Long,
        @Body branch: ServerBranch
    ): Deferred<ServerResponse<ServerBranch?>>


    @GET("companies/{companyID}/branches")
    fun getAllBranchesAsync(@Path("companyID") companyID: Long): Deferred<Set<ServerBranch>>
}

interface CustomersApiServices {

    @POST("companies/{companyId}/branches/{branchId}/admin/customers/")
    fun addNewCustomerByAdminAsync(
        @Path("companyId") companyId: Long,
        @Path("branchId") branchId: Long,
        @Body customer: ServerCustomer
    ): Deferred<ServerResponse<DatabaseCustomer?>>

    @GET("companies/{companyId}/customers")
    fun getAllCustomersAsync(
        @Path("companyId") companyID: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Deferred<List<PageableCustomer>>

    @GET("companies/{companyId}/customers/{customerId}/profile")
    fun getCustomerProfileAsync(
        @Path("companyId") companyID: Long,
        @Path("customerId") customerId: Long
    ): Deferred<ServerResponse<CustomerProfile?>>
}

interface VisitsApiServices {

    @POST("companies/{companyId}/customers/{customerId}/visits")
    fun addNewVisitByAdminAsync(
        @Path("companyId") companyId: Long,
        @Path("customerId") customerId: Long,
        @Body visit: ServerVisit
    ): Deferred<ResponseCode>

    @GET("companies/{companyId}/customers/{customerId}/visits")
    fun getAllCustomerVisitsAsync(
        @Path("companyId") companyID: Long,
        @Path("customerId") customerId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Deferred<List<ServerVisit>>
}

interface ActivitiesApiServices {

    @GET("companies/{companyId}/activities/{date}")
    fun getAllCompanyActivitiesByDateAsync(
        @Path("companyId") companyID: Long,
        @Path("date") date: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Deferred<List<PageableActivity>>

    @GET("companies/{companyId}/activities/{date}")
    fun getCompanyDaySummaryAsync(
        @Path("companyId") companyID: Long,
        @Path("date") date: Long
    ): Deferred<ServerResponse<CompanyDateSummary?>>
}

object NetworkCall {
    val usersService: UsersApiServices by lazy {
        retrofit.create(UsersApiServices::class.java)
    }

    val branchesServices: BranchesApiServices by lazy {
        retrofit.create(BranchesApiServices::class.java)
    }

    val customersServices: CustomersApiServices by lazy {
        retrofit.create(CustomersApiServices::class.java)
    }

    val visitsServices: VisitsApiServices by lazy {
        retrofit.create(VisitsApiServices::class.java)
    }

    val activitiesServices by lazy {
        retrofit.create(ActivitiesApiServices::class.java)
    }
}