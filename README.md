# Hoverfly MCP Server

A **Spring Boot**-based **Model Context Protocol (MCP)** server that exposes [Hoverfly](https://hoverfly.io/) as a programmable tool for AI assistants like **Cursor**, **Claude Desktop**, **GitHub Copilot**, or any other assistant supporting MCP. It enables dynamic mocking of third-party APIs to unblock development and testing when external services are unavailable.

---

## 🛠️ Installation

### Prerequisites

- Java 17
- Maven 3.6+

### Build & Run

```bash
git clone <repository-url>
cd hoverfly-mcp-server
mvn clean package
java -jar target/hoverfly-mcp-server-0.0.1-SNAPSHOT.jar
```

---

## ⚙️ Configuring as an MCP Server (with Docker)

To use this server with an AI assistant that supports Model Context Protocol (MCP), add the following to your `.mcp.json` or `mcp.config.json`:

```json
{
  "mcpServers": {
    "hoverfly-mcp": {
      "command": "docker",
      "args": [
        "run",
        "-i",
        "--rm",
        "-p 8500:8500",
        "-p 8888:8888",
        "docker.io/kapish88/hoverfly-mcp-server"
      ]
    }
  }
}
```

- `8500`: Hoverfly proxy port (mocked services)
- `8888`: Hoverfly admin port (mock control API)

> Make sure Docker is installed. The image will be pulled automatically if not available locally.

---

## 🔧 Exposed MCP Tools

| Tool Name                       | Description                                                      |
|----------------------------------|------------------------------------------------------------------|
| `hoverflyStatus()`               | Checks if Hoverfly is running                                    |
| `startHoverflyAsWebServer()`     | Starts Hoverfly in simulate mode as a web server                 |
| `stopHoverfly()`                 | Stops Hoverfly and clears mocks                                  |
| `getHoverflyVersion()`           | Returns Hoverfly version                                         |
| `listAllMockAPIs()`              | Lists all active mock APIs (request-response pairs)              |
| `createMockAPI(json)`            | Adds a mock API using a JSON RequestResponsePair definition      |
| `removeAllMockedAPIs()`          | Removes all existing mock APIs                                   |
| `getHoverflyAccessInfo()`        | Returns key Hoverfly endpoints and example usage                 |
| `lookupDocumentation(topic)`     | Returns Hoverfly documentation for a specific topic              |
| `matcherSuggestionsForPair(json)`| Suggests matcher options for a given request-response pair JSON  |
| `debugHoverflyWithLogs(limit)`   | Fetches recent Hoverfly logs for debugging (limit is optional)   |

These tools can be invoked programmatically by AI assistants through the MCP interface.

---

## 🤝 Contributing

1. Fork this repo  
2. Create a feature branch  
3. Submit a Pull Request  

---

## 📜 License

See [LICENSE](LICENSE) file for licensing terms.
