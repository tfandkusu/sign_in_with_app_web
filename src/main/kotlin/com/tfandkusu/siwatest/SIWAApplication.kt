package com.tfandkusu.siwatest

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.html.respondHtml
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.*
import kotlinx.html.*


// Entry Point of the application as defined in resources/application.conf.
// @see https://ktor.io/servers/configuration.html#hocon-file
fun Application.main() {
    // This adds Date and Server headers to each response, and allows custom additional headers
    install(DefaultHeaders)
    // This uses use the logger to log every call (request/response)
    install(CallLogging)
    install(FreeMarker) {
        // this: FreeMarkerEngine
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    // Registers routes
    routing {
        // Here we use a DSL for building HTML on the route "/"
        // @see https://github.com/Kotlin/kotlinx.html
        get("/") {
            call.respondHtml {
                head {
                    title { +"Sign in with Apple Test Page." }
                }
                body {
                    a(href = "/login") {
                        +"Login page"
                    }
                }
            }
        }
        // Login page
        get("/login") {
            val model = mapOf<String, String>()
            call.respond(FreeMarkerContent("login.html", model, "e"))
        }
        // Redirect page
        post("/redirect_to") {
            // フォームデータの取得
            val params = call.receiveParameters()
            val idToken = params["id_token"] ?: ""
            // JWTを検証する
            val user = IdTokenVerifier.verify(idToken)
            // HTMLをレンダリングする
            val model = mutableMapOf<String, String>()
            model["code"] = params["code"] ?: ""
            model["id_token"] = idToken
            model["state"] = params["state"] ?: ""
            model["error"] = params["error"] ?: ""
            if (user.verify) {
                model["verify"] = "OK"
            } else {
                model["verify"] = "Failed"
            }
            model["userId"] = user.id
            model["email"] = user.email
            call.respond(FreeMarkerContent("redirect_to.html", model, "e"))
        }
        // Static files
        static("") {
            resource("jquery.min.js")
            resource("bootstrap.min.css")
            resource("bootstrap.min.js")
        }
    }
}