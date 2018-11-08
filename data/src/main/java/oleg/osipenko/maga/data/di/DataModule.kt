package oleg.osipenko.maga.data.di

import oleg.osipenko.domain.repository.MoviesRepository
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.network.TMDBApi
import oleg.osipenko.maga.data.repository.MoviesDataRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import java.util.concurrent.Executor
import java.util.concurrent.Executors

val dataModule = module {
  single { TMDBApi.create(androidContext()) }

  single { MoviesDb.create(androidContext()) }

  single<Executor> { Executors.newFixedThreadPool(5) }

  single<MoviesRepository> {
    MoviesDataRepository(get(), get(), get())
  }
}