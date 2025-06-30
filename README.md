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
        "-v /path/to/your/data:/opt/hoverfly-mcp/simulation-data",
        "docker.io/kapish88/hoverfly-mcp-server"
      ]
    }
  }
}
```

- `8500`: Hoverfly proxy port (mocked services)
- `8888`: Hoverfly admin port (mock control API)
- `-v /path/to/your/data:/data`: Mount a volume for simulation persistence

> Make sure Docker is installed. The image will be pulled automatically if not available locally.

---

## 🔧 Exposed MCP Tools

| Tool Name                       | Description                                                      |
|----------------------------------|------------------------------------------------------------------|
| `get_hoverfly_status`              | Checks if Hoverfly is running                                    |
| `start_hoverfly_web_server`        | Starts Hoverfly in simulate mode as a web server with optional auto-load of simulation from mounted volume |
| `stop_hoverfly_server`             | Stops Hoverfly and clears mocks                                  |
| `fetch_hoverfly_version`           | Returns Hoverfly version                                         |
| `list_hoverfly_mocks`              | Lists all active mock APIs (request-response pairs)              |
| `add_hoverfly_mock`                | Adds a mock API using a JSON RequestResponsePair definition      |
| `clear_hoverfly_mocks`             | Removes all existing mock APIs                                   |
| `show_hoverfly_endpoints_info`     | Returns key Hoverfly endpoints and example usage                 |
| `get_hoverfly_documentation`       | Returns Hoverfly documentation for a specific topic              |
| `suggest_hoverfly_matchers`        | Suggests matcher options for a given request-response pair JSON  |
| `get_hoverfly_debug_logs`          | Fetches recent Hoverfly logs for debugging (limit is optional)   |
| `download_hoverfly_simulation`     | Downloads current simulation to mounted volume                   |

These tools can be invoked programmatically by AI assistants through the MCP interface.

---

## 💾 Simulation Persistence

The server supports automatic simulation persistence through mounted volumes:

### Auto-Load on Startup
When starting Hoverfly with `start_hoverfly_web_server`, the server automatically:
- Loads the most recent simulation file (if available)
- Continues with a clean state if no simulation is found


### Persistent Data and Volume Mounting

For persistent simulation data, you must mount a host directory to `/opt/hoverfly-mcp/simulation-data` inside the container. This is the only supported location for persistent data.


## 🤝 Contributing

1. Fork this repo  
2. Create a feature branch  
3. Submit a Pull Request  

---

## 📜 License

See [LICENSE](LICENSE) file for licensing terms.
