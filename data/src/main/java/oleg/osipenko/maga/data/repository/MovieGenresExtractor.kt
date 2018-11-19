package oleg.osipenko.maga.data.repository

import oleg.osipenko.maga.data.entities.MovieGenreRecord
import oleg.osipenko.maga.data.entities.MovieRecord

object MovieGenresExtractor :
  Function1<List<MovieRecord>?, List<MovieGenreRecord>> {
  override fun invoke(movies: List<MovieRecord>?): List<MovieGenreRecord> =
    movies?.flatMap { movieRecord ->
      if (hasGenres(movieRecord)) {
        movieGenresForMovie(movieRecord)
      } else {
        geNonExistentValues(movieRecord)
      }
    }?.toList() ?: emptyList()

  private fun geNonExistentValues(movie: MovieRecord): List<MovieGenreRecord> =
    listOf(
      MovieGenreRecord(
        movieId = movie.id ?: Int.MIN_VALUE, genreId = Int.MIN_VALUE
      )
    )

  private fun hasGenres(movieRecord: MovieRecord): Boolean =
    movieRecord.genreIds?.isNotEmpty() != false

  private fun movieGenresForMovie(movie: MovieRecord): List<MovieGenreRecord> =
    movie.genreIds?.map { genreId ->
      MovieGenreRecord(
        movieId = movie.id ?: Int.MIN_VALUE, genreId = genreId
      )
    }?.toList() ?: emptyList()
}
