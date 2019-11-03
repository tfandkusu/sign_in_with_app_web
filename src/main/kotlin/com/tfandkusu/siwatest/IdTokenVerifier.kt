package com.tfandkusu.siwatest

import com.auth0.jwk.UrlJwkProvider
import java.net.URL
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.security.interfaces.RSAPublicKey


object IdTokenVerifier {
    /**
     * JWTを検証してユーザIDを受け取る
     */
    fun verify(token: String): User {
        // JWTをデコードする
        val jwt = JWT.decode(token)
        // KeyIDから公開鍵を取得する
        val provider = UrlJwkProvider(URL("https://appleid.apple.com/auth/keys"))
        val jwk = provider[jwt.keyId]
        val publicKey = jwk.publicKey
        // JWTのPayloadからユーザIDとメールアドレスを取得する
        val email = jwt.claims["email"]?.asString() ?: ""
        val id = jwt.subject
        try {
            // 公開鍵で検証する
            val algorithm = Algorithm.RSA256(publicKey as RSAPublicKey, null)
            val verifier = JWT.require(algorithm)
                    .withIssuer("https://appleid.apple.com")
                    .withAudience("your.client.id") // Written by CircleCI
                    .build()
            verifier.verify(token)
            return User(id, email, true)
        } catch (e: JWTVerificationException) {
            // 検証に失敗(不正なログイン)
            return User(id, email, false)
        }
    }
}