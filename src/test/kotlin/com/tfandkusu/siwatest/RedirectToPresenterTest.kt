package com.tfandkusu.siwatest

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class RedirectToPresenterTest {
    @Test
    fun success() {
        val idTokenVerifier = mockk<IdTokenVerifier>()
        every {
            idTokenVerifier.verify("jwt.payload.signature")
        } returns User("12345", "mail@example.com", true)
        val presenter = RedirectToPresenter(idTokenVerifier)
        val model = presenter.render("jwt.payload.signature", "forCSRF", "")
        model shouldBe mapOf("id_token" to "jwt.payload.signature", "userId" to "12345",
                "state" to "forCSRF", "error" to "", "verify" to "OK", "email" to "mail@example.com")
    }

    @Test
    fun verificationFailed() {
        val idTokenVerifier = mockk<IdTokenVerifier>()
        every {
            idTokenVerifier.verify("jwt.payload.signature")
        } returns User("12345", "mail@example.com", false)
        val presenter = RedirectToPresenter(idTokenVerifier)
        val model = presenter.render("jwt.payload.signature", "forCSRF", "")
        model shouldBe mapOf("id_token" to "jwt.payload.signature", "userId" to "12345",
                "state" to "forCSRF", "error" to "", "verify" to "Failed", "email" to "mail@example.com")
    }

    @Test
    fun errorInApple() {
        val idTokenVerifier = mockk<IdTokenVerifier>()
        val presenter = RedirectToPresenter(idTokenVerifier)
        val model = presenter.render("", "forCSRF", "user_cancelled_authorize")
        model shouldBe mapOf("state" to "forCSRF", "error" to "user_cancelled_authorize")
    }
}