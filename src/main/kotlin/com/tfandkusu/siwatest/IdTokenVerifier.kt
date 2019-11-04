package com.tfandkusu.siwatest

import com.auth0.jwk.UrlJwkProvider
import java.net.URL
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.interfaces.RSAPublicKey


interface IdTokenVerifier {
    /**
     * Verify JWT and get user information
     */
    suspend fun verify(token: String): User
}


class IdTokenVerifierImpl : IdTokenVerifier {
    override suspend fun verify(token: String): User {
        // Decode JWT
        val jwt = JWT.decode(token)
        // Get public key from KeyID
        val jwk = withContext(Dispatchers.IO) {
            val provider = UrlJwkProvider(URL("https://appleid.apple.com/auth/keys"))
            provider[jwt.keyId]
        }
        val publicKey = jwk.publicKey
        // Get user id and password from payload of JWT
        val email = jwt.claims["email"]?.asString() ?: ""
        val id = jwt.subject
        try {
            // Verify with public key
            val algorithm = Algorithm.RSA256(publicKey as RSAPublicKey, null)
            val verifier = JWT.require(algorithm)
                    .withIssuer("https://appleid.apple.com")
                    .withAudience("your.client.id") // Written by CircleCI
                    .build()
            verifier.verify(token)
            return User(id, email, true)
        } catch (e: JWTVerificationException) {
            // Verification failed (unauthorized)
            return User(id, email, false)
        }
    }
}