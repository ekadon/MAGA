package oleg.osipenko.maga.data.network

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import oleg.osipenko.maga.data.BuildConfig
import oleg.osipenko.maga.data.network.dto.ApiConfigurationResponse
import oleg.osipenko.maga.data.network.dto.GenresResponse
import oleg.osipenko.maga.data.network.dto.MoviesResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

/**
 * Declares the TMDB API interface.
 */
@Suppress("ComplexInterface")
interface TMDBApi {

  companion object {
    private const val API_KEY = "api_key"
    private const val LANG = "language"
    private const val REGION = "region"
    private const val PAGE = "page"
    private const val BASE_URL = "https://api.themoviedb.org/"
    private const val KYLOBYTE = 1024
    private const val MEGABYTE = 1024
    private const val CACHE_SIZE = 10L

    /**
     * Factory method.
     */
    fun create(context: Context): TMDBApi = Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(CoroutineCallAdapterFactory())
      .client(getClient(context))
      .build()
      .create(TMDBApi::class.java)

    private fun getClient(context: Context): OkHttpClient =
      OkHttpClient.Builder()
      .addInterceptor(getLogger())
      .addInterceptor(getParamInterceptor())
      .cache(getCache(context))
      .build()

    private fun getLogger(): Interceptor {
      val logger = HttpLoggingInterceptor()
      logger.level = HttpLoggingInterceptor.Level.BODY
      return logger
    }

    private fun getParamInterceptor(): Interceptor = Interceptor { chain ->
      val original = chain.request()
      val originalHttpUrl = original.url()

      val locale = Locale.getDefault()

      val url = originalHttpUrl.newBuilder()
        .addQueryParameter(API_KEY, BuildConfig.API_KEY)
        .addQueryParameter(LANG, locale.language)
        .addQueryParameter(REGION, locale.country).build()

      val requestBuilder = original.newBuilder()
      val request = with(requestBuilder) {
        addHeader("Content-Encoding", "gzip")
        url(url)
      }.build()

      chain.proceed(request)
    }

    private fun getCache(context: Context): Cache {
      val cacheSize = CACHE_SIZE * MEGABYTE * KYLOBYTE
      return Cache(context.cacheDir, cacheSize)
    }
  }

  /**
   * Retrieves API configuration values.
   * https://developers.themoviedb.org/3/configuration/get-api-configuration
   */
  @GET("/3/configuration")
  fun getConfig(): Deferred<ApiConfigurationResponse>

  /**
   * Retrieves API configuration values.
   * https://developers.themoviedb.org/3/configuration/get-api-configuration
   */
  @GET("/3/configuration")
  fun getConfigCall(): Call<ApiConfigurationResponse>

  /**
   * Returns the list of movies now playing
   * https://developers.themoviedb.org/3/movies/get-now-playing
   */
  @GET("/3/movie/now_playing")
  fun getNowPlaying(@Query(PAGE) page: Int): Deferred<MoviesResponse>

  /**
   * Returns the list of upcoming videos
   * https://developers.themoviedb.org/3/movies/get-upcoming
   */
  @GET("/3/movie/upcoming")
  fun getUpcoming(@Query(PAGE) page: Int): Deferred<MoviesResponse>

  /**
   * Retrieves the list of genres
   * https://developers.themoviedb.org/3/genres/get-movie-list
   */
  @GET("/3/genre/movie/list")
  fun getGenres(): Deferred<GenresResponse>
}
