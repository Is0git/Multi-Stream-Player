package com.iso.player.api.twitch.models


import com.squareup.moshi.Json


data class Token(

	@field: Json(name="sig")
	val sig: String? = null,

	@field: Json(name="expires_at")
	val expiresAt: String? = null,

	@field: Json(name="mobile_restricted")
	val mobileRestricted: Boolean? = null,

	@field: Json(name="token")
	var token: String? = null
)