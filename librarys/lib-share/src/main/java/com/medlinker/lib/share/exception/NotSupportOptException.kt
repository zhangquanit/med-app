package com.medlinker.lib.share.exception

import java.lang.Exception

class NotSupportOptException(message: String): Exception("not suport ${message}") {
}