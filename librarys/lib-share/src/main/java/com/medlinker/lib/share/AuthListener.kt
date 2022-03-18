package com.medlinker.lib.share


interface AuthListener {
   fun onStart(platformName: String?)

   fun onComplete(platformName: String?, code: Int, info: MutableMap<String, String>?)

   fun onError(platformName: String?, code: Int, throwable: Throwable?)

   fun onCancel(platformName: String?, code: Int)
}