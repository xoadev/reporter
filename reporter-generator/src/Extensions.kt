package dev.xoa.reporter

import javax.annotation.processing.Messager

internal fun Messager.errorMessage(message : () -> String){
    this.printMessage(javax.tools.Diagnostic.Kind.ERROR, message())
}

internal fun Messager.noteMessage(message : () -> String){
    this.printMessage(javax.tools.Diagnostic.Kind.NOTE, message())
}

internal fun Messager.warningMessage(message : () -> String){
    this.printMessage(javax.tools.Diagnostic.Kind.WARNING, message())
}