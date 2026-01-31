package com.nuvio.tv.domain.model

data class TmdbSettings(
    val enabled: Boolean = true,
    // Group: Artwork (logo, backdrop)
    val useArtwork: Boolean = false,
    // Group: Basic Info (description, genres, rating)
    val useBasicInfo: Boolean = false,
    // Group: Details (runtime, release info, country, language)
    val useDetails: Boolean = false,
    // Group: Credits (cast with photos, director, writer)
    val useCredits: Boolean = false,
    // Group: Episodes (episode titles, overviews, thumbnails)
    val useEpisodes: Boolean = false
)
