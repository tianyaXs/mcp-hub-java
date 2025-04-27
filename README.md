# Spring AI MCP SSE Example: mcp-hub (Client) & mcp-weather-server (Server)

## Introduction

This project demonstrates communication using Spring AI's Multi-Connect Protocol (MCP) over Server-Sent Events (SSE).

It includes two main modules:

1.  **`mcp-hub`**: A Spring Boot application acting as the **MCP Client**. It uses the Spring AI `ChatClient` to interact with a Large Language Model (LLM) and invokes remote tools (e.g., weather lookup) defined on the `mcp-weather-server` via MCP/SSE.
2.  **`mcp-weather-server`**: A standalone **MCP Server** application (can be implemented in Java/Spring Boot, Python, or other languages) that exposes one or more tools (a simulated weather lookup tool in this example) via MCP/SSE.

This example showcases how to decouple long-running tools, tools requiring independent deployment, or stateful tools from the main AI application.

**Key Technologies:**

* Spring Boot
* Spring AI (Core, MCP Client, MCP Server, LLM Integration e.g., DashScope)
* Maven
* SSE (Server-Sent Events)

## Prerequisites

* **JDK**: Java 17 or later.
* **Maven**: 3.9.x or later.
* **Large Language Model (LLM) API Key**: An API Key from an LLM provider is required, for example, Alibaba Cloud DashScope (Tongyi Qianwen).
* **(`mcp-weather-server`)**: If the server needs to call external services (like OpenWeatherMap), an API Key for that service might be needed.

## Setup

### 1. Maven Configuration (Alibaba Cloud Mirror)

To potentially speed up dependency downloads, consider configuring the Alibaba Cloud mirror in your Maven `settings.xml` file (usually located at `~/.m2/settings.xml`). If the file doesn't exist, create it.

```xml
<settings xmlns="[http://maven.apache.org/SETTINGS/1.0.0](http://maven.apache.org/SETTINGS/1.0.0)"
          xmlns:xsi="[http://www.w3.org/2001/XMLSchema-instance](http://www.w3.org/2001/XMLSchema-instance)"
          xsi:schemaLocation="[http://maven.apache.org/SETTINGS/1.0.0](http://maven.apache.org/SETTINGS/1.0.0)
                              [https://maven.apache.org/xsd/settings-1.0.0.xsd](https://maven.apache.org/xsd/settings-1.0.0.xsd)">

  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>*</mirrorOf> <name>Alibaba Cloud Public Repository</name>
      <url>[https://maven.aliyun.com/repository/public](https://maven.aliyun.com/repository/public)</url>
    </mirror>
  </mirrors>

</settings>
```

# Project Dependencies (POM)
Ensure the project's pom.xml file includes the necessary Spring AI dependencies.

mcp-hub (Client) Main Dependencies:

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-mcp</artifactId>
        <version>1.0.0-M6</version> </dependency>

    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-mcp-client-spring-boot-starter</artifactId>
        <version>1.0.0-M6</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

    <dependency>
        <groupId>com.alibaba.cloud.ai</groupId> <artifactId>spring-ai-dashscope-spring-boot-starter</artifactId>
        </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```
mcp-weather-server (Server - if Java/Spring Boot) Main Dependencies:

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-mcp-server-webflux-spring-boot-starter</artifactId>
        <version>1.0.0-M6</version> </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

Note: The example uses the 1.0.0-M6 milestone version. For production environments, using stable GA versions is recommended. Ensure all Spring AI related modules are version-compatible (using the Spring AI BOM for version management is highly recommended).

Configuration (application.yml or application.properties)
mcp-hub (Client) Configuration
Configure in src/main/resources/application.yml:

``` YAML
spring:
  ai:
    # --- LLM Configuration (Example: DashScope) ---
    dashscope:
      api-key: YOUR_DASHSCOPE_API_KEY # Replace with your Alibaba Cloud DashScope API Key
      # Other DashScope related configurations...
    # --- Or configure other LLM providers, e.g., OpenAI ---
    # openai:
    #  api-key: YOUR_OPENAI_API_KEY

    # --- MCP Client Configuration ---
    mcp:
      client:
        sse:
          connections:
            # Define a connection name, e.g., "weatherService" (customizable)
            weatherService:
              # URL pointing to the SSE endpoint exposed by mcp-weather-server
              url: http://localhost:18081/sse # Ensure address and port are correct
              # Optional: other connection configurations...
spring.ai.dashscope.api-key: (or other LLM api-key) Used by ChatClient to communicate with the LLM. Get keys from your LLM provider (e.g., Alibaba Cloud Model Studio (DashScope)). Strongly Recommended: Do not hardcode API Keys in configuration files. Use environment variables or secrets management tools.
spring.ai.mcp.client.sse.connections.weatherService.url: Configures the server SSE endpoint address the MCP client should connect to. weatherService is a custom connection name. The URL should point to the running mcp-weather-server's address, port, and exposed SSE path (e.g., /sse).
mcp-weather-server (Server) Configuration
Configure in the server's corresponding configuration file (e.g., application.yml for Spring Boot, or constants in a Python script):
```

``` YAML

# Example for a Spring Boot based server
server:
  port: 18081 # Server port configuration (if not default 8080)

spring:
  ai:
    mcp:
      server:
        name: weather-server    # MCP server name
        version: 0.0.1            # Server version
    # Potentially configure API Keys for external services used by tools here
```    
Adjust server.port if needed.
Configure any API keys required by the server's tools (e.g., OpenWeatherMap API key).
Running the Example
# Start the MCP Server (mcp-weather-server)

If mcp-weather-server is a Spring Boot application:
Bash

cd mcp-weather-server
>mvn spring-boot:run -Dserver.port=18081 # Specify port if needed


Bash

cd mcp-hub
mvn spring-boot:run


You should see output similar to this in the client's console:
> QUESTION: What's the weather like in Beijing?

> ASSISTANT: The weather in Beijing is sunny.
> 
Check the logs of both the client and server to see the MCP communication flow (e.g., ListToolsRequest, CallToolRequest).
Code Implementation Notes

# Client (mcp-hub):
McpHubApplication.java: May contain a CommandLineRunner @Bean demonstrating how to build the ChatClient, inject the MCP ToolCallbackProvider, and initiate a call.
Requires a ChatModel @Bean (e.g., DashScopeChatModel, OpenAiChatModel) configured with the appropriate API Key.
The ToolCallbackProvider automatically discovers and prepares tools provided by the configured MCP server(s).
# Server (mcp-weather-server):
Java/Spring Implementation: Tools are typically defined as @Beans of type java.util.function.Function annotated with @Description. The spring-ai-mcp-server-webflux-spring-boot-starter automatically registers these.
Python Implementation (Example): Uses @mcp.tool() decorator to register tool functions. Core logic handles connections in handle_sse and runs mcp_server.run().
Further Information

For more information and examples regarding Alibaba Cloud MCP, refer to: Alibaba Cloud Model Studio - MCP Quick Start (Link might vary or be specific to Chinese docs)
Spring AI Reference Documentation: https://docs.spring.io/spring-ai/reference/