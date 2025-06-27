FROM eclipse-temurin:17-jre-alpine

# Create non-root user and set up application directory
RUN addgroup -S hoverflymcp && \
    adduser -S -G hoverflymcp -h /opt/hoverfly-mcp hoverflymcp && \
    mkdir -p /opt/hoverfly-mcp && \
    chown -R hoverflymcp:hoverflymcp /opt/hoverfly-mcp

# Copy the JAR
COPY --chown=hoverflymcp:hoverflymcp target/hoverfly-mcp-server-0.0.1-SNAPSHOT.jar /opt/hoverfly-mcp/hoverfly-mcp.jar

USER hoverflymcp
WORKDIR /opt/hoverfly-mcp

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "hoverfly-mcp.jar"]

