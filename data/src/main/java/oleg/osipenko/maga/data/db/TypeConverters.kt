package oleg.osipenko.maga.data.db

import android.arch.persistence.room.TypeConverter

class TypeConverters {

    @TypeConverter fun toString(ints: List<Int>): String {
        val sb = StringBuilder()
        for ((index, i) in ints.withIndex()) {
            sb.append(i)
            if (index < ints.size - 1) sb.append(",")
        }
        return sb.toString()
    }

    @TypeConverter fun fromString(input: String): List<Int> {
        return input.asIterable()
                .filter { it != ',' }
                .map { it.toInt() }
                .toList()
    }
}
