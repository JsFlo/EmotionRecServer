//package com.emotionrec.api
//
//import io.ktor.http.HttpMethod
//import io.ktor.server.testing.*
//import junit.framework.Assert.assertEquals
//import junit.framework.Assert.assertFalse
//import org.jetbrains.spek.api.Spek
//import org.jetbrains.spek.api.dsl.given
//import org.junit.runner.RunWith
//import org.junit.platform.runner.JUnitPlatform
//
//@RunWith(JUnitPlatform::class)
//object HelloApplicationSpec : Spek({
//    given("an application") {
//        val engine = TestApplicationEngine(createTestEnvironment())
//        engine.start(wait = false) // for now we can't eliminate it
//        engine.application.main() // our main module function
//
//        with(engine) {
//            on("ping") {
//                it("should return Hello World") {
//                    handleRequest(HttpMethod.Get, "/ping").let { call ->
//                        assertEquals("Hello, World!", call.response.content)
//                    }
//                }
//                it("should return 404 on POST") {
//                    handleRequest(HttpMethod.Post, "/", {
//                        body = "HTTP post body"
//                    }).let { call ->
//                        assertFalse(call.requestHandled)
//                    }
//                }
//            }
//        }
//    }
//})