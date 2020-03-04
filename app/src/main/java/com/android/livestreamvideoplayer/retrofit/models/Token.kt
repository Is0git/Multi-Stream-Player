package com.android.livestreamvideoplayer.retrofit.models


import com.squareup.moshi.Json


data class Token(

	@Json(name="sig")
	val sig: String? = null,

	@Json(name="expires_at")
	val expiresAt: String? = null,

	@Json(name="mobile_restricted")
	val mobileRestricted: Boolean? = null,

	@Json(name="token")
	var token: String? = null
)