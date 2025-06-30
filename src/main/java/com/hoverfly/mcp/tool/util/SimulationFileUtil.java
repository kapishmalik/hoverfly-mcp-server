package com.hoverfly.mcp.tool.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.specto.hoverfly.junit.core.model.Simulation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/** Utility class for simulation file operations. */
public class SimulationFileUtil {

  public static final String TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss";
  public static final String SIMULATION_FILE_PREFIX = "hoverfly_simulation_";
  public static final String SIMULATION_FILE_EXTENSION = ".json";

  /**
   * Checks if a file is a simulation file based on its name.
   *
   * @param filename The filename to check
   * @return true if it's a simulation file, false otherwise
   */
  public static boolean isSimulationFile(String filename) {
    return filename.startsWith(SIMULATION_FILE_PREFIX)
        && filename.endsWith(SIMULATION_FILE_EXTENSION);
  }

  /**
   * Writes a Simulation object to a file as JSON.
   *
   * @param simulation The Simulation object
   * @param filePath The file path to write to
   * @param objectMapper The Jackson ObjectMapper
   * @throws IOException if writing fails
   */
  public static void writeSimulationToFile(
      Simulation simulation, Path filePath, ObjectMapper objectMapper) throws IOException {
    String simulationJson = objectMapper.writeValueAsString(simulation);
    Files.write(filePath, simulationJson.getBytes());
  }

  /**
   * Reads a Simulation object from a file containing JSON.
   *
   * @param filePath The file path to read from
   * @param objectMapper The Jackson ObjectMapper
   * @return The Simulation object
   * @throws IOException if reading or parsing fails
   */
  public static Simulation readSimulationFromFile(Path filePath, ObjectMapper objectMapper)
      throws IOException {
    String simulationJson = Files.readString(filePath);
    return objectMapper.readValue(simulationJson, Simulation.class);
  }

  /**
   * Finds the most recent simulation file in the given directory.
   *
   * @param directory The directory to search in
   * @return Path to the most recent simulation file, or null if none found
   */
  public static Path findLatestSimulationFile(Path directory) {
    try (var stream = Files.list(directory)) {
      return stream
          .filter(path -> isSimulationFile(path.getFileName().toString()))
          .max(Comparator.comparing(Path::getFileName))
          .orElse(null);
    } catch (IOException e) {
      return null;
    }
  }
}
