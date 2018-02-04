package oleg.osipenko.maga.data.repository

import io.reactivex.Observable
import io.reactivex.Single
import oleg.osipenko.maga.data.db.Database
import oleg.osipenko.maga.data.db.dbo.Configuration
import oleg.osipenko.maga.data.db.dbo.Movie
import oleg.osipenko.maga.data.network.TMDBApi

/**
 * Facade of the data layer
 */
interface Repository {
    fun getConfiguration(): Single<DataState<Configuration>>
    fun getNowPlaying(): Observable<DataState<List<Movie>>>
    fun getUpcoming(): Observable<DataState<List<Movie>>>
}

/**
 * Implementation of the [Repository]
 */
class DataRepository(
        private val api: TMDBApi,
        private val db: Database) : Repository {
    override fun getConfiguration(): Single<DataState<Configuration>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNowPlaying(): Observable<DataState<List<Movie>>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUpcoming(): Observable<DataState<List<Movie>>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}