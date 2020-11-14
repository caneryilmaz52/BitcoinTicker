package com.caneryilmazapps.bitcointicker.data.models.response

import java.io.Serializable

data class ApiStatusResponse(
    val gecko_says: String
) : Serializable {
    override fun toString(): String {
        return "ApiStatusResponse(gecko_says='$gecko_says')"
    }
}