package com.icm2330.ServicesNotifications.model

import java.util.Date

class Message {
    var uuid: String? = null
    var text: String? = null
    var userId: String? = null
    var timestamp: Date? = null

    constructor(uuid: String?, text: String?, userId: String?, timestamp: Date?) {
        this.uuid = uuid
        this.text = text
        this.userId = userId
        this.timestamp = timestamp
    }

    constructor()
}