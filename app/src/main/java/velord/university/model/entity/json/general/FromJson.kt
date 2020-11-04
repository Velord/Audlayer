package com.statuscasellc.statuscase.model.json.general

interface FromJson <T, F> {

    fun fromJson(value: T): F
}