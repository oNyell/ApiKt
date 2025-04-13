package com.onyell.enums

enum class LoggerEnum(private val value: String) {
    INFO("INFO"),
    ERROR("ERROR"),
    DEBUG("DEBUG"),
    WARNING("WARNING"),
    TRACE("TRACE");

    fun getValue(): String {
        return value
    }
}