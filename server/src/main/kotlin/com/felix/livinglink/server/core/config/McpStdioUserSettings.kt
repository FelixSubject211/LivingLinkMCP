package com.felix.livinglink.server.core.config

import org.koin.core.annotation.Single

@Single
class McpStdioUserSettings {
    val user: McpRequestUser by lazy {
        McpRequestUser(
            id = Env.required("LIVINGLINK_STDIO_USER_ID"),
            username = Env.required("LIVINGLINK_STDIO_USERNAME"),
        )
    }
}
