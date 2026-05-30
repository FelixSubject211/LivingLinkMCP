package com.felix.livinglink.user.config

import com.felix.livinglink.core.config.McpRequestUser
import com.felix.livinglink.user.domain.User

fun McpRequestUser.toDomain(): User =
    User(
        id = id,
        username = username,
    )
