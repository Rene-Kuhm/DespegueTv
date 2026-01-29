package com.nuvio.tv.data.mapper

import com.nuvio.tv.data.remote.dto.MetaDto
import com.nuvio.tv.data.remote.dto.MetaLinkDto
import com.nuvio.tv.data.remote.dto.VideoDto
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.Meta
import com.nuvio.tv.domain.model.MetaLink
import com.nuvio.tv.domain.model.PosterShape
import com.nuvio.tv.domain.model.Video

fun MetaDto.toDomain(): Meta {
    return Meta(
        id = id,
        type = ContentType.fromString(type),
        name = name,
        poster = poster,
        posterShape = PosterShape.fromString(posterShape),
        background = background,
        logo = logo,
        description = description,
        releaseInfo = releaseInfo,
        imdbRating = imdbRating?.toFloatOrNull(),
        genres = genres ?: emptyList(),
        runtime = runtime,
        director = director ?: emptyList(),
        cast = cast ?: emptyList(),
        videos = videos?.map { it.toDomain() } ?: emptyList(),
        country = country,
        awards = awards,
        language = language,
        links = links?.mapNotNull { it.toDomain() } ?: emptyList()
    )
}

fun VideoDto.toDomain(): Video {
    return Video(
        id = id,
        title = name ?: title ?: "Episode ${episode ?: number ?: 0}",
        released = released,
        thumbnail = thumbnail,
        season = season,
        episode = episode ?: number,
        overview = overview ?: description
    )
}

fun MetaLinkDto.toDomain(): MetaLink? {
    return url?.let {
        MetaLink(
            name = name,
            category = category,
            url = it
        )
    }
}
