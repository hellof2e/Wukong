package com.hellobike.magiccube.v2.reports.session

import java.util.*

class SessionResult {

    private var sessionId: String = UUID.randomUUID().toString()

    fun isSameSessionId(other: SessionResult?): Boolean {
        if (other == null) return false
        return sessionId == other.sessionId
    }

    fun reloadSessionId() {
        sessionId = UUID.randomUUID().toString()
    }
}