package com.felix.livinglink.core.delivery.mcp.server

import com.felix.livinglink.core.system.Env
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
