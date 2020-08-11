package com.shouman.apps.reseller.admin.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.shouman.apps.reseller.admin.data.model.Branch
import com.shouman.apps.reseller.admin.data.model.Company
import com.shouman.apps.reseller.admin.data.model.User
import com.shouman.apps.reseller.admin.preferences.UserPreferences
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


enum class ResponseCode {
    SUCCESS,
    FIREBASE_CODE_NOT_VALID,
    NEW_USER_NOT_VALID,
    NEW_USER_INFO_NOT_VALID
}

data class ServerResponse<T>(val responseCode: ResponseCode, val body: T?)

class BasicAuthInterceptor(val user: String, val password: String) : Interceptor {

    val credential = Credentials.basic(user, password)
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
private val BASE_URL = "http://192.168.1.8:8080/hawk/"


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
    fun addNewUserToDatabaseAsync(@Body user: User?): Deferred<ServerResponse<User?>>

    @PUT("users/companies/{firebaseUID}")
    fun updateCompanyInfoAsync(@Path("firebaseUID") firebaseUID: String, @Body company: Company?): Deferred<ServerResponse<User?>>

    @GET("users/{firebaseUID}")
    fun getUserByFirebaseUID(@Path("firebaseUID") firebaseUID: String): Deferred<ServerResponse<User?>>

}

interface BranchesApiServices {


    @POST("companies/{companyID}")
    fun addNewBranchToServerAsync(@Path("companyId") companyId:Long,
                                  @Body branch: Branch): Deferred<ServerResponse<Branch?>>

    @PUT("users/companies/{firebaseUID}")
    fun updateCompanyInfoAsync(@Path("firebaseUID") firebaseUID: String, @Body company: Company?): Deferred<ServerResponse<User?>>

    @GET("users/{firebaseUID}")
    fun getUserByFirebaseUID(@Path("firebaseUID") firebaseUID: String): Deferred<ServerResponse<User?>>

}


object UsersApi {
    val usersService: UsersApiServices by lazy {
        retrofit.create(UsersApiServices::class.java)
    }

    val branchesServices: BranchesApiServices by lazy{
        retrofit.create(BranchesApiServices::class.java)
    }
}