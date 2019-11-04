package com.tfandkusu.siwatest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RedirectToPresenter(private val idTokenVerifier: IdTokenVerifier) {
    suspend fun render(idToken: String, state: String, error: String): Map<String, String> {
        val model = mutableMapOf<String, String>()
        model["state"] = state
        model["error"] = error
        if (error.isEmpty()) {
            // Success for redirect
            val user = withContext(Dispatchers.IO) {
                idTokenVerifier.verify(idToken)
            }
            if (user.verify) {
                model["verify"] = "OK"
            } else {
                model["verify"] = "Failed"
            }
            model["id_token"] = idToken
            model["userId"] = user.id
            model["email"] = user.email
        }
        return model
    }
}