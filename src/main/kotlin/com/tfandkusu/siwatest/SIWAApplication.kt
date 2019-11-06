package com.tfandkusu.siwatest

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.html.respondHtml
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.title
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject


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

    val appModule = module {
        single { IdTokenVerifierImpl() as IdTokenVerifier }
        single { RedirectToPresenter(get()) }
    }
    install(Koin) {
        modules(appModule)
    }

    val redirectToPresenter: RedirectToPresenter by inject()

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
            // Get posted form data
            val params = call.receiveParameters()
            val idToken = params["id_token"] ?: ""
            val state = params["state"] ?: ""
            val error = params["error"] ?: ""
            // Create view model
            val model = redirectToPresenter.render(idToken, state, error)
            // Render HTML
            call.respond(FreeMarkerContent("redirect_to.html", model, "e"))
        }
        // Dummy redirect page for view test
        get("/redirect_to") {
            // Make view model
            val model = mutableMapOf<String, String>()
            // From jwt.io
            model["id_token"] = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
            model["state"] = "forCSRF"
            model["error"] = ""
            model["verify"] = "OK"
            model["userId"] = "12345"
            model["email"] = "mail@example.com"
            call.respond(FreeMarkerContent("redirect_to.html", model, "e"))
        }
        get("/redirect_to_canceled") {
            val model = mutableMapOf<String, String>()
            model["state"] = "forCSRF"
            model["error"] = "user_cancelled_authorize"
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