package com.scheduler.isdayoff

import com.scheduler.isdayoff.enums.LocalesType
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Класс для работы с кэшем
 */
class IsDayOffCache(builder: IsDayOffBuilder) {
    val isCached: Boolean = builder.cache
    private val locale: LocalesType = builder.locale
    var cacheDir: String = builder.cacheDir
        private set
    val cacheStorageDays: Int = builder.cacheStorageDays
    private val simpleDateFormat = SimpleDateFormat("yyyyMMdd")

    /**
     * Создать файл с кэшем
     * @param data Строка рабочих/нерабочих дней
     * @param year Год, для которого создается кэш
     */
    fun createCacheFile(data: String, year: Int) {
        val cacheFile = getCacheFile(year)
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
        }
        try {
            FileOutputStream(cacheFile).use { fileOutputStream ->
                fileOutputStream.write(
                    """${simpleDateFormat.format(Date())}
$data""".toByteArray()
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getCacheFile(year: Int): File {
        val fileName = "IsDayOffCache " + year + "-" + locale.name
        if (!cacheDir.endsWith("/") && !cacheDir.isEmpty()) {
            cacheDir += "/"
        }
        return File("$cacheDir$fileName.txt")
    }

    /**
     * Проверить кэш файл на наличие и актуальность
     * @param year год для проверки
     * @return true - кэш актуален, false - кэш не создан или требуется его обновить
     */
    fun checkCacheFile(year: Int): Boolean {
        val cacheFile = getCacheFile(year)
        if (cacheFile.exists()) {
            try {
                BufferedReader(FileReader(cacheFile)).use { reader ->
                    val fileDateLine = reader.readLine()
                    var fileCreateDate = simpleDateFormat.parse(fileDateLine)
                    val calendar = Calendar.getInstance()
                    calendar.time = fileCreateDate
                    calendar.add(Calendar.DAY_OF_YEAR, cacheStorageDays)
                    fileCreateDate = calendar.time
                    return fileCreateDate.after(Date())
                }
            } catch (e: Exception) {
                return false
            }
        } else {
            return false
        }
    }

    /**
     * Получение закэшированного дня/месяца/года
     * @param year год
     * @param month месяц. Может быть null - тогда выгружаются данные за год
     * @param day день. Может быть null - тогда выгружаются данные за месяц
     * @return строку с id типа указанного дня/месяца/года
     */
    fun getCachedDay(year: Int?, month: Int?, day: Int?): String {
        val calendar = Calendar.getInstance()
        var tempMonth = month
        var tempDay = day
        if (tempMonth == null) {
            tempMonth = 0
        }
        if (tempDay == null) {
            tempDay = 1
        }
        calendar[year!!, tempMonth] = tempDay
        val cacheFile = getCacheFile(calendar[Calendar.YEAR])
        if (cacheFile.exists()) {
            try {
                BufferedReader(FileReader(cacheFile)).use { reader ->
                    reader.readLine()
                    return if (day != null && month != null) {
                        val data = reader.readLine().toCharArray()
                        data[calendar[Calendar.DAY_OF_YEAR] - 1].toString()
                    } else {
                        if (month != null) {
                            val firstDay = calendar[Calendar.DAY_OF_YEAR] - 1
                            val lastDay = firstDay + calendar.getMaximum(Calendar.DAY_OF_MONTH)
                            reader.readLine().substring(firstDay, lastDay)
                        } else {
                            val firstDay = calendar[Calendar.DAY_OF_YEAR] - 1
                            val lastDay = firstDay + calendar.getMaximum(Calendar.DAY_OF_YEAR) - 1
                            reader.readLine().substring(firstDay, lastDay)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }

    /**
     * Получение отрезка дней из кэша
     * @param startDate Начало отрезка
     * @param endDate Конец
     * @return строку с id типа отрезка дней
     */
    fun getCachedDays(startDate: Calendar, endDate: Calendar): String {
        val cacheFile = getCacheFile(startDate[Calendar.YEAR])
        if (cacheFile.exists()) {
            try {
                BufferedReader(FileReader(cacheFile)).use { reader ->
                    reader.readLine()
                    val data = reader.readLine()
                    var days = endDate[Calendar.DAY_OF_YEAR] - startDate[Calendar.DAY_OF_YEAR]
                    var day = startDate[Calendar.DAY_OF_YEAR]
                    days += day
                    return data.substring(--day, --days)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }
}