package com.scheduler.isdayoff

import com.scheduler.isdayoff.IsDayOffDateType
import com.scheduler.isdayoff.enums.DayType
import com.scheduler.isdayoff.enums.DirectionType
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Основной класс библиотеки
 * Позволяет получить тип дня по датам
 */
class IsDayOff(builder: IsDayOffBuilder) {
    private var userAgent = "isdayoff-java-lib/${javaClass.getPackage().implementationVersion ?: "DEVELOP"}"
    private val properties = IsDayOffProps(builder)
    private val cache = IsDayOffCache(builder)
    private val httpClient: HttpClient = builder.httpClient

    suspend fun initCacheForYear() {
        val calendar = Calendar.getInstance()
        if (cache.isCached && !cache.checkCacheFile(calendar[Calendar.YEAR])) {
            val response = buildUrlAndSendRequest(calendar[Calendar.YEAR], null, null)
            if (response != null) {
                cache.createCacheFile(response, calendar[Calendar.YEAR])
            } else {
                println("IsDayOff: init cache build failed. Reason: response was null")
            }
        }
    }

    /**
     * Тип сегодняшнего дня
     * @return Тип текущего дня
     * @see com.groupstp.isdayoff.enums.DayType
     */
    suspend fun todayType(): DayType {
        val today = Calendar.getInstance()
        val response = getResponseByDate(today[Calendar.YEAR], today[Calendar.MONTH], today[Calendar.DAY_OF_MONTH])
        return DayType.fromId(response)
    }

    /**
     * Тип завтрашнего дня
     * @return Тип завтрашнего дня
     * @see com.groupstp.isdayoff.enums.DayType
     */
    suspend fun tomorrowType(): DayType {
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_YEAR, 1)
        val response =
            getResponseByDate(tomorrow[Calendar.YEAR], tomorrow[Calendar.MONTH], tomorrow[Calendar.DAY_OF_MONTH])
        return DayType.fromId(response)
    }

    /**
     * Тип конкретного дня
     * @param date день, который нужно проверить
     * @return Тип этого дня
     * @see com.groupstp.isdayoff.enums.DayType
     */
    suspend fun dayType(date: Date): DayType {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val response =
            getResponseByDate(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        return DayType.fromId(response)
    }

    /**
     * Тип всех дней конкретного месяца
     * @param date месяц, который нужно проверить
     * @return Массив IsDayOffDateType с датой и типом для каждого дня месяца
     * @see com.groupstp.isdayoff.IsDayOffDateType
     */
    suspend fun daysTypeByMonth(date: Date): List<IsDayOffDateType> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar[Calendar.DAY_OF_MONTH] = 1
        val response = getResponseByDate(calendar[Calendar.YEAR], calendar[Calendar.MONTH], null)
        return parseArrayResponseToList(response, calendar)
    }

    /**
     * Тип всех дней конкретного года
     * @param date год, для которого нужно провести проверку
     * @return Массив IsDayOffDateType с датой и типом для каждого дня года
     * @see com.groupstp.isdayoff.IsDayOffDateType
     */
    suspend fun daysTypeByYear(date: Date): List<IsDayOffDateType> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar[Calendar.DAY_OF_MONTH] = 1
        calendar[Calendar.MONTH] = 0
        val response = getResponseByDate(calendar[Calendar.YEAR], null, null)
        return parseArrayResponseToList(response, calendar)
    }

    /**
     * Проверка года на високосность
     * @param date год
     * @return true, если год високосный и false - если нет
     */
    suspend fun checkIsLeap(date: Date): Boolean? {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val queryParams = mapOf("year" to calendar[Calendar.YEAR].toString())
//        val response = request(baseUrl + "isleap?year=" + calendar[Calendar.YEAR])
        val response = request(url = "isleap", queryParams)
        if (response == null || response == "0") {
            return false
        }
        return if (response == "1") {
            true
        } else null
    }

    /**
     * Проверка отрезка дат
     * @param startDate  Начало отрезка
     * @param endDate Конец
     * @return Массив IsDayOffDateType с датой и типом для каждого дня отрезка
     * @see com.groupstp.isdayoff.IsDayOffDateType
     */
    suspend fun daysTypeByRange(startDate: Date, endDate: Date): List<IsDayOffDateType> {
        if (startDate.after(endDate)) {
            throw IllegalArgumentException("Start date must be before end date")
        }
        val diffInMillies = Math.abs(endDate.time - startDate.time)
        val diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
        if (diffInDays > 365) {
            throw IllegalArgumentException("Diff in days must be <= 365")
        }
        val calendarStartDate = Calendar.getInstance()
        calendarStartDate.time = startDate
        val calendarEndDate = Calendar.getInstance()
        calendarEndDate.time = endDate
        if (cache.isCached && calendarStartDate[Calendar.YEAR] == calendarEndDate[Calendar.YEAR] &&
            cache.checkCacheFile(calendarStartDate[Calendar.YEAR])
        ) {
            val cachedDays = cache.getCachedDays(calendarStartDate, calendarEndDate)
            if (cachedDays != "") {
                return parseArrayResponseToList(cachedDays, calendarStartDate)
            }
        }
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
        val startDateStr = simpleDateFormat.format(startDate)
        val endDateStr = simpleDateFormat.format(endDate)
        val response = getResponseByRange(startDateStr, endDateStr)
        return parseArrayResponseToList(response, calendarStartDate)
    }

    /**
     * Получить первый день по типу
     * @param date День, относительно которого начинать отсчет
     * @param dayType Тип дня, который нужно получить
     * @param directionType Направление(Искать в прошлом или будущем)
     * @return Первый день, подходящий под условие
     */
    suspend fun getFirstDayByType(date: Date, dayType: DayType, directionType: DirectionType): Date {
        val direction: Int = when (directionType) {
            DirectionType.PAST -> -1
            DirectionType.FUTURE -> 1
        }
        val calendar = Calendar.getInstance()
        calendar.time = date
        while (dayType(calendar.time) != dayType) {
            calendar.add(Calendar.DAY_OF_YEAR, direction)
        }
        return calendar.time
    }

    /**
     * Количество дней подряд по типу
     * @param date День, относительно которого начинать отсчет
     * @param dayType Тип дня
     * @param directionType Направление(Искать в прошлом или будущем)
     * @return Кол-во дней, включая день отсчета
     */
    suspend fun getCountDaysByType(date: Date, dayType: DayType, directionType: DirectionType): Int {
        val direction: Int = when (directionType) {
            DirectionType.PAST -> -1
            DirectionType.FUTURE -> 1
        }
        val calendar = Calendar.getInstance()
        calendar.time = date
        var countDaysByType = 0
        while (dayType(calendar.time) == dayType) {
            calendar.add(Calendar.DAY_OF_YEAR, direction)
            countDaysByType++
        }
        return countDaysByType
    }

    private fun parseArrayResponseToList(response: String?, startDate: Calendar): List<IsDayOffDateType> {
        val result: MutableList<IsDayOffDateType> = ArrayList()
        val days = response!!.toCharArray()
        for (day in days) {
            val dayType = DayType.fromId(day.toString())
            result.add(IsDayOffDateType(startDate.time, dayType))
            startDate.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }

    private suspend fun getResponseByRange(startDate: String?, endDate: String?): String? {
        if (startDate == null || endDate == null) {
            //Вызывать исключение
            return "199"
        }

        val rangeParams = mapOf(
            "date1" to startDate,
            "date2" to endDate,
        )

        return request(url = "getdata", rangeParams + getRequestParameters())
    }

    private fun buildCalendar(year: Int, month: Int, day: Int): Calendar {
        val calendar = Calendar.getInstance()
        calendar[year, month - 1] = day
        return calendar
    }

    private suspend fun getResponseByDate(year: Int, month: Int?, day: Int?): String? {
        if (cache.isCached) {
            if (!cache.checkCacheFile(year)) {
                val response = buildUrlAndSendRequest(year, null, null)
                val dayType = DayType.fromId(response)
                if (response == null || DayType.ERROR_DATE == dayType || DayType.NOT_FOUND == dayType || DayType.SERVER_ERROR == dayType) {
                    return response
                }
                cache.createCacheFile(response, year)
            }
            return cache.getCachedDay(year, month, day)
        }
        return buildUrlAndSendRequest(year, month!! + 1, day)
    }

    private suspend fun buildUrlAndSendRequest(year: Int, month: Int?, day: Int?): String? {
        val queryParams = mapOf(
            "year" to year,
            "month" to month,
            "day" to day,
        )
            .filter { it.value != null }
            .mapValues { it.value.toString() }

        return request(url = "getdata", queryParams + getRequestParameters())
    }

    private fun getRequestParameters(): Map<String, String?> {
        return mapOf(
            "cc" to properties.locale.id,
            "pre" to properties.preHolidaysDay,
            "covid" to properties.covidWorkingDays,
            "sd" to properties.sixDaysWorkWeek,
        ).mapValues { it.value.toString() }
    }

//    private fun appendProperties(url: StringBuilder) {
//        url
//            .append("cc=").append(properties.locale.id).append("&")
//            .append("pre=").append(properties.preHolidaysDay).append("&")
//            .append("covid=").append(properties.covidWorkingDays).append("&")
//            .append("sd=").append(properties.sixDaysWorkWeek)
//    }

    private suspend fun request(url: String, queryParams: Map<String, String?>): String? {
        val response = httpClient.get(url) {
            queryParams.forEach { parameter(it.key, it.value) }
            header("User-Agent", userAgent)
        }

        return if (response.status == HttpStatusCode.OK) {
            response.bodyAsText()
        } else {
            null
        }

//        val client = HttpClient.newHttpClient()
//        val request = HttpRequest.newBuilder()
//            .uri(URI.create(url))
//            .header("User-Agent", userAgent) //                .header("Host", "isdayoff.ru")
//            .build()
//        return try {
//            val response: HttpResponse<*> = client.send(request, HttpResponse.BodyHandlers.ofString())
//            var result: String? = null
//            if (response.statusCode() == 200) result = response.body().toString()
//            result
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>): Unit = runBlocking {
            val build = IsDayOff(IsDayOffBuilder())
            val instance = Calendar.getInstance()
            instance[2021, Calendar.JANUARY] = 1
            val firstDayByType = build.getCountDaysByType(instance.time, DayType.NOT_WORKING_DAY, DirectionType.PAST)
            println("Non-working days: $firstDayByType")
        }
    }
}