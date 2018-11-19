package oleg.osipenko.maga.data.repository

import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.maga.data.entities.MovieRecord

object MovieMapper: Function1<MovieRecord, Movie> {
  override fun invoke(movieRecord: MovieRecord): Movie {
    return Movie(
      movieRecord.posterPath ?: "", movieRecord.adult ?: false,
      movieRecord.overview ?: "", movieRecord.releaseDate ?: "",
      movieRecord.genres?.split(",") ?: emptyList(),
      movieRecord.id ?: Int.MIN_VALUE, movieRecord.originalTitle ?: "",
      movieRecord.originalLanguage ?: "", movieRecord.title ?: "",
      movieRecord.backdropPath ?: "", movieRecord.popularity ?: Float.MIN_VALUE,
      movieRecord.voteCount ?: Int.MIN_VALUE, movieRecord.video ?: "",
      movieRecord.voteAverage ?: Float.MIN_VALUE
    )
  }
}
