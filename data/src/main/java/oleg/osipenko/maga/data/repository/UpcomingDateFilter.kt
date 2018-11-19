package oleg.osipenko.maga.data.repository

import oleg.osipenko.maga.data.entities.MovieRecord
import org.threeten.bp.LocalDate
import org.threeten.bp.Period

fun upcomingDateFilter(currentDate: LocalDate, movieRecord: MovieRecord):
Boolean {
  val movieDate = LocalDate.parse(movieRecord.releaseDate)
  val dateDelta = Period.between(
    currentDate, movieDate
  )
  return !dateDelta.isNegative
}
