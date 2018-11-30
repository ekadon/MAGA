package oleg.osipenko.maga.data.repository

import oleg.osipenko.maga.data.entities.MovieRecord
import org.threeten.bp.LocalDate
import org.threeten.bp.Period

/**
 * Filters movies inside Upcoming feed. Leaves only unreleased movies.
 */
fun upcomingDateFilter(
  currentDate: LocalDate, movieRecord: MovieRecord
): Boolean {
  val movieDate = LocalDate.parse(movieRecord.releaseDate)
  val dateDelta = Period.between(
    currentDate, movieDate
  )
  return !dateDelta.isNegative
}
