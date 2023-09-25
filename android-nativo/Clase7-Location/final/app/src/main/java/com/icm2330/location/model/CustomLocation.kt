package com.icm2330.location.model

import org.json.JSONException
import org.json.JSONObject
import java.util.Date


class CustomLocation {
    var latitud = 0.0
    var longitud = 0.0
    var fecha: Date? = null

    fun toJSON(): JSONObject {
        val obj = JSONObject()
        try {
            obj.put("latitud", latitud)
            obj.put("longitud", longitud)
            obj.put("date", fecha)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return obj
    }

}