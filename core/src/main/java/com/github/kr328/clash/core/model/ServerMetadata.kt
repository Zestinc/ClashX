package com.github.kr328.clash.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ServerMetadata(
    val title: String,
    val subtitle: String,
    val url: String
)