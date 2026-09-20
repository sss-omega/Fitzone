package kz.evn.otbor

import org.junit.Test
import org.junit.Assert.*
import java.security.MessageDigest

class ExampleUnitTest {

    @Test
    fun testSha256() {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest("абай".toByteArray(Charsets.UTF_8))
        val hex = digest.joinToString("") { "%02x".format(it) }
        assertEquals(64, hex.length)
    }

    @Test
    fun testServerUrl() {
        assertNotNull(RetrofitKlient.baseUrl)
        assertTrue(RetrofitKlient.baseUrl.startsWith("http"))
    }
}