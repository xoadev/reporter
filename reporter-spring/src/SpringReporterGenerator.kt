package dev.xoa.reporter

import com.google.auto.service.AutoService
import java.io.PrintWriter
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(SpringReporterGenerator.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class SpringReporterGenerator : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Reporter::class.java).forEach { element ->
            if (element.kind != ElementKind.INTERFACE) {
                processingEnv.messager.errorMessage { "Can only be applied to interfaces,  element: $element " }
            }

            generateImpl(element as TypeElement, processingEnv.elementUtils.getPackageOf(element).toString())
        }

        return false
    }

    private fun generateImpl(element: TypeElement, packageOfType: String) {

        val implClassName = "$packageOfType.${element.simpleName}Impl"
        val implFile = processingEnv.filer.createSourceFile(implClassName)
        val commands = element.enclosedElements.toCommand()

        val useMeter = commands.any { it.first is ReporterCommand.Meter }

        PrintWriter(implFile.openWriter()).use { out ->
            out.append("package $packageOfType;\n\n")
            out.append("import org.slf4j.LoggerFactory;\n")
            out.append("import org.slf4j.Logger;\n")
            out.append("import org.springframework.stereotype.Component;\n\n")

            if (useMeter) {
                out.append("import io.micrometer.core.instrument.MeterRegistry;\n\n")
                out.append("import io.micrometer.core.instrument.Tag;\n\n")
            }

            out.append("@Component\n")
            out.append("public class ${element.simpleName}Impl implements ${element.simpleName} {\n\n")
            out.append("\tprivate static final Logger log = LoggerFactory.getLogger(${element.simpleName}Impl.class);\n\n")

            if (useMeter) {
                out.append("\tprivate final MeterRegistry meterRegistry;\n\n")

                out.append("\tpublic ${element.simpleName}Impl(MeterRegistry meterRegistry) {\n")
                out.append("\t\tthis.meterRegistry = meterRegistry;\n")
                out.append("\t}\n")
            }

            commands.forEach { commandAndMethod ->
                out.generateMethod(commandAndMethod.first, commandAndMethod.second)
            }

            out.append("}\n")
        }
    }

    private fun List<Element>.toCommand() : List<Pair<ReporterCommand, ExecutableElement>> {
        return ElementFilter.methodsIn(this)
            .map { method ->
                Pair(ReporterCommand.parse(
                    method.simpleName.toString(),
                    method.parameters.map { it.simpleName.toString() }
                ), method)
            }
    }

    private fun PrintWriter.generateMethod(command: ReporterCommand, method: ExecutableElement) {
        append("\t@Override\n")
        append("\tpublic void ${method.simpleName}(")
        append(method.parameters.joinToString(separator = ", ") {
            "${it.asType()} ${it.simpleName}"
        })
        append(") {\n")

        when(command) {
            is ReporterCommand.Log -> {
                val arguments = with(command.message.arguments) {
                    if (isNotEmpty()) ", ${joinToString()}" else ""
                }

                val message = command.message.joinAsString(separator = " ", replaceUnderlineBy = "{}")

                append("\t\tlog.${command.mode.name.lowercase()}(\"$message\"$arguments);\n")
            }
            is ReporterCommand.Meter -> {
                val code = command.code.joinAsString(separator = ".", replaceUnderlineBy = "%s")

                if (command.code.arguments.isEmpty()) {
                    append("\t\tString code = \"$code\";\n")
                } else {
                    append("\t\tString code = String.format($code, ${command.code.arguments.joinToString()});\n")
                }

                append("\t\tIterable<Tag> tags = java.util.Arrays.asList(${command.tags.entries.joinToString { 
                    "Tag.of(\"${it.key}\", ${it.value.toCode()})"
                }});\n")

                when (command.mode) {
                    MeterMode.Counter ->
                        append("\t\tthis.meterRegistry.counter(code, tags).increment(${command.amount.toCode()});\n")
                    MeterMode.Gauge ->
                        append("\t\tthis.meterRegistry.gauge(code, tags, ${command.amount.toCode()});\n")
                }
            }
            else -> TODO()
        }

        append("\t}\n\n")
    }

    private fun Token.toCode() = when (this) {
        is Token.Ref -> value
        is Token.Text -> "\"$value\""
        is Token.Number -> value.toString()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Reporter::class.java.canonicalName)
    }
}