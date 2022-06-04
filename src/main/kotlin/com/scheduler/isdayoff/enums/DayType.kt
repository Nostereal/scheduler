package com.scheduler.isdayoff.enums

/**
 * Тип дня
 */
enum class DayType(val id: String) {
    /**
     * Рабочий день
     */
    WORKING_DAY("0"),

    /**
     * Выходной
     */
    NOT_WORKING_DAY("1"),

    /**
     * Сокращенный день
     * Появляется, если установить PreHolidaysDay
     * @see IsDayOffBuilder.addPreHolidaysDay
     */
    SHORT_DAY("2"),

    /**
     * Рабочий день в пандемию
     * Появляется, если установить CovidWorkingDays
     * @see IsDayOffBuilder.addCovidWorkingDays
     */
    WORKING_DAY_COVID("4"),

    /**
     * Ошибка в дате
     */
    ERROR_DATE("100"),

    /**
     * Данные не найдены
     */
    NOT_FOUND("101"),

    /**
     * Ошибка сервиса
     */
    SERVER_ERROR("199");

    val isWorkingDay: Boolean?
        get() {
            if (this == WORKING_DAY || this == WORKING_DAY_COVID || this == SHORT_DAY) {
                return true
            } else if (this == NOT_WORKING_DAY) {
                return false
            }
            return null
        }

    companion object {
        fun fromId(id: String?): DayType {
            for (value in values()) {
                if (value.id == id) {
                    return value
                }
            }
            return SERVER_ERROR
        }
    }
}