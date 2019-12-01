package kr.hyperlink.app.httpmodule

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by rxgp1@hyper-link.com on 2019-12-01
 * Company : Hyper link
 * Author : Alex
 */

@RunWith(AndroidJUnit4::class)
class TestHttpAsync {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("kr.hyperlink.app.httpmodule", appContext.packageName)

    }
}