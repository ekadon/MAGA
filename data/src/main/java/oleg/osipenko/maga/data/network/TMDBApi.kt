package oleg.osipenko.maga.data.network

import io.reactivex.Single
import oleg.osipenko.maga.data.network.dto.ApiConfigurationResponse
import oleg.osipenko.maga.data.network.dto.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * methods
 */
const val CONFIG = "/configuration"
const val NOW_PLAYING = "/movie/now_playing"
const val UPCOMING = "/movie/upcoming"

/**
 * query parameters
 */
const val API_KEY = "api_key"
const val LANG = "language"
const val PAGE = "page"
const val REGION = "region"

/**
 * Declares the TMDB API methods
 */
interface TMDBApi {

    /**
     * Retrieves API configuration values
     * https://developers.themoviedb.org/3/configuration/get-api-configuration
     */
    @GET(CONFIG)
    fun getConfig(@Query(API_KEY) apiKey: String): Single<ApiConfigurationResponse>

    /**
     * Returns the list of movies now playing
     * https://developers.themoviedb.org/3/movies/get-now-playing
     */
    @GET(NOW_PLAYING)
    fun getNowPlaying(
            @Query(API_KEY) apiKey: String,
            @Query(LANG) language: String,
            @Query(PAGE) page: Int,
            @Query(REGION) region: String): Single<MoviesResponse>

    /**
     * Returns the list of upcoming videos
     * https://developers.themoviedb.org/3/movies/get-upcoming
     */
    @GET(UPCOMING)
    fun getUpcoming(
            @Query(API_KEY) apiKey: String,
            @Query(LANG) language: String,
            @Query(PAGE) page: Int,
            @Query(REGION) region: String): Single<MoviesResponse>
}