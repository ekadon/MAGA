package oleg.osipenko.maga.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import oleg.osipenko.maga.data.network.dto.ApiConfigurationResponse
import oleg.osipenko.maga.data.network.dto.MoviesResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Declares the TMDB API methods
 */
interface TMDBApi {

    companion object {
        /**
         * query parameters
         */
        private const val API_KEY = "api_key"
        private const val LANG = "language"
        private const val PAGE = "page"
        private const val REGION = "region"
        private const val BASE_URL = "https://api.themoviedb.org/"
        const val KEY = "599635c208eca8fcc4bb1a1330965ae7"

        fun create(): TMDBApi {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                    .create(TMDBApi::class.java)
        }
    }

    /**
     * Retrieves API configuration values
     * https://developers.themoviedb.org/3/configuration/get-api-configuration
     */
    @GET("/3/configuration")
    fun getConfig(@Query(API_KEY) apiKey: String): Call<ApiConfigurationResponse>

    /**
     * Returns the list of movies now playing
     * https://developers.themoviedb.org/3/movies/get-now-playing
     */
    @GET("/3/movie/now_playing")
    fun getNowPlaying(
            @Query(API_KEY) apiKey: String,
            @Query(LANG) language: String,
            @Query(REGION) region: String,
            @Query(PAGE) page: Int): Call<MoviesResponse>

    /**
     * Returns the list of upcoming videos
     * https://developers.themoviedb.org/3/movies/get-upcoming
     */
    @GET("/3/movie/upcoming")
    fun getUpcoming(
            @Query(API_KEY) apiKey: String,
            @Query(LANG) language: String,
            @Query(PAGE) page: Int,
            @Query(REGION) region: String): Call<MoviesResponse>
}