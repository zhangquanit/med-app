package com.medlinker.lib.share


interface ShareListener {
   fun onStart(platformName: String?)

   fun onComplete(platformName: String?)

   fun onError(platformName: String?, throwable: Throwable?)

   fun onCancel(platformName: String?)
}