package com.scheduler.isdayoff.enums

/**
 * Страна, по которой проводить проверку
 */
enum class LocalesType(val id: String) {
    RUSSIA("ru"),
    UKRAINE("ua"),
    KAZAKHSTAN("kz"),
    BELARUS("by"),
    USA("us"),
    UZBEKISTAN("uz"),
    TURKEY("tr");

    companion object {
        fun fromId(id: String): LocalesType? {
            for (value in values()) {
                if (value.id == id) {
                    return value
                }
            }
            return null
        }
    }
}