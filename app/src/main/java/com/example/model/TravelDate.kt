package com.example.model

import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

/**
 * API-24 safe calendar date representation backed by GregorianCalendar.
 * Avoids java.time API level 26 restrictions while guaranteeing deterministic date math.
 */
data class TravelDate(
    val year: Int,
    val month: Int, // 1..12
    val dayOfMonth: Int // 1..31
) : Comparable<TravelDate> {

    val isoString: String
        get() = String.format(Locale.US, "%04d-%02d-%02d", year, month, dayOfMonth)

    val yearMonthKey: Int
        get() = year * 100 + month

    /**
     * Monday = 1 .. Sunday = 7
     */
    val dayOfWeekMondayFirst: Int
        get() {
            val cal = toCalendar()
            return when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> 1
                Calendar.TUESDAY -> 2
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY -> 4
                Calendar.FRIDAY -> 5
                Calendar.SATURDAY -> 6
                else -> 7
            }
        }

    val dayOfWeekShortEs: String
        get() = when (dayOfWeekMondayFirst) {
            1 -> "Lun"
            2 -> "Mar"
            3 -> "Mié"
            4 -> "Jue"
            5 -> "Vie"
            6 -> "Sáb"
            else -> "Dom"
        }

    val monthShortEs: String
        get() = when (month) {
            1 -> "ene"
            2 -> "feb"
            3 -> "mar"
            4 -> "abr"
            5 -> "may"
            6 -> "jun"
            7 -> "jul"
            8 -> "ago"
            9 -> "sep"
            10 -> "oct"
            11 -> "nov"
            else -> "dic"
        }

    val monthFullEs: String
        get() = when (month) {
            1 -> "Enero"
            2 -> "Febrero"
            3 -> "Marzo"
            4 -> "Abril"
            5 -> "Mayo"
            6 -> "Junio"
            7 -> "Julio"
            8 -> "Agosto"
            9 -> "Septiembre"
            10 -> "Octubre"
            11 -> "Noviembre"
            else -> "Diciembre"
        }

    val shortLabelEs: String
        get() = "$dayOfMonth $monthShortEs"

    val fullDayLabelEs: String
        get() = "$dayOfWeekShortEs $dayOfMonth $monthShortEs $year"

    val monthYearLabelEs: String
        get() = "$monthFullEs $year"

    fun plusDays(days: Int): TravelDate {
        val cal = toCalendar()
        cal.add(Calendar.DAY_OF_MONTH, days)
        return fromCalendar(cal)
    }

    fun plusMonths(months: Int): TravelDate {
        val cal = GregorianCalendar(year, month - 1, 1)
        cal.add(Calendar.MONTH, months)
        return fromCalendar(cal)
    }

    fun firstOfMonth(): TravelDate = TravelDate(year, month, 1)

    fun daysInMonth(): Int {
        val cal = GregorianCalendar(year, month - 1, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun toEpochDay(): Long {
        val cal = GregorianCalendar(year, month - 1, dayOfMonth)
        cal.set(Calendar.HOUR_OF_DAY, 12)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis / MILLIS_PER_DAY
    }

    private fun toCalendar(): Calendar = GregorianCalendar(year, month - 1, dayOfMonth)

    override fun compareTo(other: TravelDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return dayOfMonth.compareTo(other.dayOfMonth)
    }

    companion object {
        private const val MILLIS_PER_DAY = 86_400_000L

        fun today(): TravelDate = fromCalendar(Calendar.getInstance())

        fun fromCalendar(cal: Calendar): TravelDate {
            return TravelDate(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH) + 1,
                dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun daysBetween(start: TravelDate, end: TravelDate): Int {
            return (end.toEpochDay() - start.toEpochDay()).toInt()
        }

        fun parseIso(iso: String): TravelDate? {
            val parts = iso.split("-")
            if (parts.size != 3) return null
            val y = parts[0].toIntOrNull() ?: return null
            val m = parts[1].toIntOrNull() ?: return null
            val d = parts[2].toIntOrNull() ?: return null
            return TravelDate(y, m, d)
        }
    }
}
