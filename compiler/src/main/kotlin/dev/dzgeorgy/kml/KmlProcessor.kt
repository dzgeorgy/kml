package dev.dzgeorgy.kml

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo

class KmlProcessor(
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {

    private var invoked = false

    private val mapAnnotationProcessor = KmlMapProcessor()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }
        invoked = true
        val mappers = mapAnnotationProcessor.process(resolver)
        FileSpec.builder("dev.dzgeorgy.kml", "Mappers")
            .addFunctions(mappers)
            .build()
            .writeTo(codeGenerator, Dependencies(true))
        return emptyList()
    }

}
