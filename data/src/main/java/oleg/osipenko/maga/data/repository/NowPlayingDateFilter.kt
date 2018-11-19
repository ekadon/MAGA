package oleg.osipenko.maga.data.repository

import oleg.osipenko.maga.data.entities.MovieRecord
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

fun nowPlayingDateFilter(
    currentDate: LocalDate, movieRecord: MovieRecord
): Boolean {
  val movieDate = LocalDate.parse(movieRecord.releaseDate)
  val daysSinceRelease = ChronoUnit.DAYS.between(
    currentDate, movieDate
  )
  return daysSinceRelease in (MoviesDataRepository.MONTH + 1)..0
}
