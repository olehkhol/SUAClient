package sky.tavrov.suaclient.util

import android.util.Log
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LogDebugMockitoTest {

    @Test
    fun `logDebug should call Log d with given tag and msg and execute onFinal`() {

        val tag = "TEST_TAG"
        val msg = "Test message"
        var onFinalCalled = false

        mockStatic(Log::class.java).use { mocked ->

            `when`(Log.d(tag, msg)).thenReturn(0)
            logDebug(tag, msg) { onFinalCalled = true }

            mocked.verify { Log.d(tag, msg) }
            assert(onFinalCalled)
        }
    }
}