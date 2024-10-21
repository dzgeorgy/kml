package dev.dzgeorgy.kml

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.PROPERTY)
annotation class Alias(
    vararg val values: String
)
