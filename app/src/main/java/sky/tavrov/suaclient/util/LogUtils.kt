package sky.tavrov.suaclient.util

import android.util.Log

fun logDebug(
    tag: String,
    msg: String,
    onFinal: () -> Unit = {}
) {
    Log.d(tag, msg)
    onFinal()
}