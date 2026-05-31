package com.felix.livinglink.server.session.delivery.mcp.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserMcpDto(
    val id: String,
    val username: String,
)
