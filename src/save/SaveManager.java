package save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.Game;
import java.io.*;
import java.nio.file.*;

/**
 * Simple save/load helper using Gson. Loads/saves the plain DTO `GameState`.
 * Note: applying the loaded DTO back into a live `Game` instance requires
 * mapping logic (not implemented here) because the domain model contains
 * behavior and private state. This helper focuses on JSON persistence.
 */
public class SaveManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Serializes the given game to the given file path (JSON). */
    public static void save(Path file, Game game) throws IOException {
        GameState state = GameState.fromGame(game);
        Path tmp = file.resolveSibling(file.getFileName().toString() + ".tmp");
        try (BufferedWriter w = Files.newBufferedWriter(tmp)) {
            GSON.toJson(state, w);
        }
        Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    /** Reads a GameState DTO from the given file. */
    public static GameState load(Path file) throws IOException {
        try (BufferedReader r = Files.newBufferedReader(file)) {
            return GSON.fromJson(r, GameState.class);
        }
    }
}
