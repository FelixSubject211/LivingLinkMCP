package com.felix.livinglink.server.user.config

import com.felix.livinglink.server.core.config.McpRequestUser
import com.felix.livinglink.server.user.domain.User

fun McpRequestUser.toDomain(): User =
    User(
        id = id,
        username = username,
    )
