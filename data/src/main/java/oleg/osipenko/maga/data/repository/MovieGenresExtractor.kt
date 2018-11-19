package oleg.osipenko.maga.data.repository

import oleg.osipenko.maga.data.entities.MovieGenreRecord
import oleg.osipenko.maga.data.entities.MovieRecord

object MovieGenresExtractor :
  Function1<List<MovieRecord>?, List<MovieGenreRecord>> {
  override fun invoke(movies: List<MovieRecord>?): List<MovieGenreRecord> {
    return movies?.flatMap { movieRecord ->
      if (hasGenres(movieRecord)) {
        movieGenresForMovie(movieRecord)
      } else {
        geNonExistantValues(movieRecord)
      }
    }?.toList() ?: emptyList()
  }

  private fun geNonExistantValues(movie: MovieRecord): List<MovieGenreRecord> {
    return listOf(
      MovieGenreRecord(
        movieId = movie.id ?: Int.MIN_VALUE, genreId = Int.MIN_VALUE
      )
    )
  }

  private fun hasGenres(movieRecord: MovieRecord): Boolean {
    return movieRecord.genreIds?.isNotEmpty() != false
  }

  private fun movieGenresForMovie(movie: MovieRecord): List<MovieGenreRecord> {
    return movie.genreIds?.map { genreId ->
      MovieGenreRecord(
        movieId = movie.id ?: Int.MIN_VALUE, genreId = genreId
      )
    }?.toList() ?: emptyList()
  }
}
