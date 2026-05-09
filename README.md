# livinglink

Kotlin MCP server.

## Requirements

- JDK
- Docker
- Docker Compose

## MongoDB

Start MongoDB and Mongo Express:

```bash
docker compose up -d
```

## Build

```bash
./gradlew installDist
```

## local.properties

Add your local project path:

```properties
projectDir=/absolute/path/to/livinglink
```

## Claude Desktop config

### macOS

Edit:

```text
~/Library/Application Support/Claude/claude_desktop_config.json
```

Example:

```json
{
    "mcpServers": {
        "livinglink": {
            "command": "/absolute/path/to/livinglink/run-claude-mcp.sh"
        }
    }
}
```

## After changes

Rebuild the distribution:

```bash
./gradlew installDist
```

## Lint

Check formatting:

```bash
./gradlew ktlintCheck
```

Auto-format:

```bash
./gradlew ktlintFormat
```