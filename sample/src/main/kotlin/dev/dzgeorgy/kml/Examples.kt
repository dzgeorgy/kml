package dev.dzgeorgy.kml

import dev.dzgeorgy.kml.ext.TrackEntity

fun main() {
    println("Test")

    val track = Track("Blank Space", 3.51f, "Taylor Swift")
    val trackEntity = TrackEntity("Blank Space", 3.51f, "Taylor Swift")
    val trackModel = TrackModel("Blank Space", 3.51f, "Taylor Swift")
    println(track.toTrackEntity())
    println(track.toTrackModel())
//    println(trackEntity.toTrackModel())
}

@Map(TrackEntity::class)
@Map(TrackModel::class)
data class Track(
    val name: String,
    var length: Float,
    val author: String
)

class TrackModel(
    val name: CharSequence,
    val length: Float,
    val author: CharSequence
) {
    override fun toString(): String {
        return "TrackModel(name=$name, length=$length, author=$author)"
    }
}
