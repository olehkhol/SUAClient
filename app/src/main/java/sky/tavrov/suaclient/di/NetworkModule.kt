package sky.tavrov.suaclient.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import sky.tavrov.suaclient.data.remote.KtorApi
import sky.tavrov.suaclient.util.Constants.BASE_URL
import java.net.CookieManager
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@OptIn(ExperimentalSerializationApi::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCookieManager(): CookieManager = CookieManager()

    @Provides
    @Singleton
    fun provideHttpClient(cookieManager: CookieManager): OkHttpClient =
        OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideKtorApi(retrofit: Retrofit) = retrofit.create(KtorApi::class.java)
}