package oleg.osipenko.maga.data.di

import oleg.osipenko.domain.repository.ConfigRepository
import oleg.osipenko.domain.repository.MoviesRepository
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.network.TMDBApi
import oleg.osipenko.maga.data.repository.ConfigDataRepository
import oleg.osipenko.maga.data.repository.MoviesDataRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val dataModule = module {
  single { TMDBApi.create(androidContext()) }

  single { MoviesDb.create(androidContext()) }

  single<MoviesRepository> { MoviesDataRepository(get(), get()) }

  single<ConfigRepository> { ConfigDataRepository(get(), get(), get()) }
}
