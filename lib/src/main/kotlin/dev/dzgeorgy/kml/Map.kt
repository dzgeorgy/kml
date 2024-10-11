package dev.dzgeorgy.kml

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class Map(
    val value: KClass<*>
)
