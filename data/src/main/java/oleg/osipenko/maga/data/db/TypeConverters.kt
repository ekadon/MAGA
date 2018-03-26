package oleg.osipenko.maga.data.db

import android.arch.persistence.room.TypeConverter

class TypeConverters {

    companion object {
        const val DELIMITER = ","
    }

    @TypeConverter
    fun intListToString(ints: List<Int>): String {
        return appendToString(ints, DELIMITER)
    }

    @TypeConverter
    fun intListFromString(input: String): List<Int> {
        return input.split(DELIMITER).map { it.toInt() }.toList()
    }

    @TypeConverter
    fun stringListToString(strings: List<String>): String {
        return appendToString(strings, DELIMITER)
    }

    @TypeConverter
    fun stringListFromString(input: String): List<String> {
        return input.split(DELIMITER).toList()
    }

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
