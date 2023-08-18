package sky.tavrov.suaclient.util

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Test

class LogDebugMockkTest {

    @Test
    fun `logDebug should call Log d with given tag and msg and execute onFinal`() {
        // Arrange
        val tag = "TEST_TAG"
        val msg = "Test message"
        var onFinalCalled = false

        mockkStatic(Log::class)

        every { Log.d(tag, msg) } returns 0
        logDebug(tag, msg) {
            onFinalCalled = true
        }

        verify { Log.d(tag, msg) }
        assert(onFinalCalled)
    }
}