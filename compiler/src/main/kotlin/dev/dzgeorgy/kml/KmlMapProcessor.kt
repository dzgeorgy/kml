package dev.dzgeorgy.kml

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toTypeName

class KmlMapProcessor {

    private val parametersCache: MutableMap<KSClassDeclaration, Set<ParameterData>> = mutableMapOf()
    private val propertiesCache: MutableMap<KSClassDeclaration, Set<ParameterData>> = mutableMapOf()

    fun process(resolver: Resolver): List<FunSpec> {
        return resolver.getSources()
            .getProperties()
            .associateWithTargets()
            .map { pair ->
                val args = pair.target.constructorParams.associateWith { param ->
                    pair.source.properties.find { param.name == it.name && param.type.isAssignableFrom(it.type) }
                        ?: error("Unable to find matching property for $param")
                }
                MappingData(
                    pair.source.type,
                    pair.target.type,
                    args
                )
            }
            .map { data ->
                FunSpec.builder("to${data.target}")
                    .receiver(data.source.asStarProjectedType().toTypeName())
                    .returns(data.target.asStarProjectedType().toTypeName())
                    .addCode(data.args.map { "${it.key.name}=${it.value.name}" }
                        .joinToString(
                            separator = ", ",
                            prefix = "return ${data.target}(",
                            postfix = ")"
                        ))
                    .build()
            }
            .toList()
    }

    private fun Resolver.prepareData() = getSources()
        .getProperties()
        .associateWithTargets()

    private fun Resolver.getSources() = getSymbolsWithAnnotation(Map::class.qualifiedName!!)
        .map { it as KSClassDeclaration }

    private fun Sequence<KSClassDeclaration>.getProperties() = map { klass ->
        val props = propertiesCache.getOrPut(klass) {
            println("Caching properties for: $klass")
            klass.getAllProperties().map { ParameterData(it.simpleName.asString(), it.type.resolve()) }.toSet()
        }.apply { println("Loaded properties for: $klass") }
        SourceData(klass, props)
    }

    private fun Sequence<KSClassDeclaration>.getConstructorParameters() = map { klass ->
        val parameters = parametersCache.getOrPut(klass) {
            println("Caching parameters for: $klass")
            klass.primaryConstructor!!.parameters.map { ParameterData(it.name!!.asString(), it.type.resolve()) }.toSet()
        }.apply { println("Loaded parameters for: $klass") }
        TargetData(klass, parameters)
    }

    private fun Sequence<SourceData>.associateWithTargets() = flatMap { source ->
        source.getTarget()
            .getConstructorParameters()
            .map { MappingPair(source, it) }
    }

    private fun SourceData.getTarget() = type.annotations
        .filter { it.shortName.asString() == Map::class.simpleName }
        .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == Map::class.qualifiedName }
        .map { annotation -> (annotation.arguments.first().value as KSType).declaration as KSClassDeclaration }

}

data class MappingData(
    val source: KSClassDeclaration,
    val target: KSClassDeclaration,
    val args: kotlin.collections.Map<ParameterData, ParameterData>
)

data class MappingPair(
    val source: SourceData,
    val target: TargetData
)

data class ParameterData(
    val name: String,
    val type: KSType
) {
    override fun toString(): String {
        return "$name: $type"
    }
}

data class TargetData(
    val type: KSClassDeclaration,
    val constructorParams: Set<ParameterData>
)

data class SourceData(
    val type: KSClassDeclaration,
    val properties: Set<ParameterData>
)
