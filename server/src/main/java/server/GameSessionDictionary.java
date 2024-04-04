package server;

import org.eclipse.jetty.websocket.api.*;

import java.util.*;

public class GameSessionDictionary {
    // Define the dictionary
    private Map<Integer, List<Session>> gameSessions;

    // Constructor to initialize the dictionary
    public GameSessionDictionary() {
        gameSessions = new HashMap<>();
    }

    // Method to add a session to a game ID
    public void addSessionToGame(int gameId, Session session) {
        // If the gameId already exists, add the session to its list
        // Otherwise, create a new list and add the session
        gameSessions.computeIfAbsent(gameId, k -> new ArrayList<>()).add(session);
    }

    // Method to get sessions for a specific game ID
    public List<Session> getSessionsForGame(int gameId) {
        return gameSessions.getOrDefault(gameId, new ArrayList<>());
    }

    // Method to remove a session from a game ID
    public void removeSessionFromGame(int gameId, Session session) {
        List<Session> sessions = gameSessions.get(gameId);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    // Other methods as needed...
}