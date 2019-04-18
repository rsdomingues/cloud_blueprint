package com.fooapp.echo.usecase

import spock.lang.Specification


class ExecuteEchoSpec extends Specification {

    private ExecuteEcho echo

    def setup() {
        echo = new ExecuteEcho()
    }

    def "test echo with foo creates a coolId"() {
        given: "i have a message with foo"
        def sentece = "sentence with foo in it"

        when: "when i echo it"
        def response = echo.execute(sentece)

        then: "It generate a coolId and echo the message"
        response.echoSentence == sentece
        response.echoSentence == response.sentence
        response.cool
    }

    def "test echo without foo do not create coolId"() {
        given: "i have a message with foo"
        def sentece = "sentence with no cool factor in it"

        when: "when i echo it"
        def response = echo.execute(sentece)

        then: "It does not generate a coolId and echo the message"
        response.echoSentence == sentece
        response.echoSentence == response.sentence
        !response.cool
    }

    def "test echo with bad words"() {
        given: "i have a message with bar"
        def sentece = "sentence with bar in it"

        when: "when i echo it"
        def response = echo.execute(sentece)

        then: "It get a bad word error"
        IllegalArgumentException ex = thrown()
        ex.message == "bar is a bad word"
        response == null

    }

    def "test echo with null sentence"() {
        given: "i have a message with bar"
        def sentece = null

        when: "when i echo it"
        def response = echo.execute(sentece)

        then: "It get a bad word error"
        IllegalArgumentException ex = thrown()
        ex.message == "this is a bad word"
        response == null
    }
}
