package data.tinder.recommendation

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(indices = arrayOf(Index("id")))
internal class RecommendationUserSpotifyThemeTrackAlbumEntity(
        var name: String,
        @PrimaryKey
        var id: String)
