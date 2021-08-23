package dev.xoa.reporter

@Reporter
interface ApiReporter {

    fun infoHello_Requested(name: String?)

    fun incrementHelloRequestedWithName_(name: String?)
}