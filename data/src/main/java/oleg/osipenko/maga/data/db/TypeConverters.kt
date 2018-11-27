package oleg.osipenko.maga.data.db

import android.arch.persistence.room.TypeConverter

/**
 * Utility methods for converting between various objects and formats
 * supported by SQL database.
 */
class TypeConverters {

  companion object {
    private const val DELIMITER = ","
  }

  /**
   * Concatenates list of integers into the string, separated
   * integers with [DELIMITER]. To be used in the database.
   */
  @TypeConverter
  fun intListToString(ints: List<Int>): String = appendToString(ints, DELIMITER)

  /**
   * Converts string, of integers separated with [DELIMITER], into the list of
   * integers. To be used in the database.
   */
  @TypeConverter
  fun intListFromString(input: String): List<Int> =
    input.split(DELIMITER).map { it.toInt() }.toList()

  /**
   * Concatenates list of strings into the string, with original strings
   * separated with [DELIMITER]. To be used in the database.
   */
  @TypeConverter
  fun stringListToString(strings: List<String>): String =
    appendToString(strings, DELIMITER)

  /**
   * Converts the list of strings, separated with [DELIMITER], into the list
   * of strings. To be used in the database.
   */
  @TypeConverter
  fun stringListFromString(input: String): List<String> =
    input.split(DELIMITER).toList()

  private fun appendToString(values: List<Any>, delimiter: String): String {
    val sb = StringBuilder()
    with(sb) {
      for ((index, i) in values.withIndex()) {
        append(i)
        if (index < values.size - 1) append(delimiter)
      }
    }
    return sb.toString()
  }
}
