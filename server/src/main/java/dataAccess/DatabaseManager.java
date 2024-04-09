package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DatabaseManager {
    private static final String databaseName;
    private static final String user;
    private static final String password;
    private static final String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to load db.properties");
                Properties props = new Properties();
                props.load(propStream);
                databaseName = props.getProperty("db.name");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                fillStatementParameters(ps, params);

                ps.executeUpdate();
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static List<Object> executeQuery(String statement, String outputType, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                fillStatementParameters(ps, params);

                List<Object> resultList = new ArrayList<>();
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        switch (outputType) {
                            case "AuthData" -> {
                                String username = rs.getString("username");
                                String authToken = rs.getString("authToken");

                                resultList.add(new AuthData(username, authToken));
                            }
                            case "UserData" -> {
                                String username = rs.getString("username");
                                String password = rs.getString("password");
                                String email = rs.getString("email");

                                resultList.add(new UserData(username, password, email));
                            }
                            case null, default -> resultList.add(rs.getString(outputType));
                        }

                    }
                }
                return resultList;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static List<GameData> executeQueryGameData(String statement, String outputType, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                fillStatementParameters(ps, params);

                List<GameData> resultList = new ArrayList<>();
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int gameId = rs.getInt("gameId");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);

                        resultList.add(new GameData(gameId, whiteUsername, blackUsername, gameName, game));
                    }
                    return resultList;
                } catch (Exception ex) {
                    return resultList;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static String executeQueryPlayerName(String statement, String outputType, Object... params) throws DataAccessException {
        String playerName = null;
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                fillStatementParameters(ps, params);
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        playerName = rs.getString(outputType);
                    }
                } catch (Exception ex) {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return playerName;
    }

    private static void fillStatementParameters(PreparedStatement ps, Object[] params) throws SQLException {
        for (var i = 0; i < params.length; i++) {
            var param = params[i];
            switch (param) {
                case String p -> ps.setString(i + 1, p);
                case Integer p -> ps.setInt(i + 1, p);
                case null -> ps.setNull(i + 1, NULL);
                default -> {
                }
            }
        }
    }

    public static void createTables() throws DataAccessException {
        String[] sqlStatements = {"CREATE TABLE IF NOT EXISTS AuthTokens (" +
                "username VARCHAR(255) not null, " +
                "authToken VARCHAR(255) PRIMARY KEY not null" +
                ")",
                "CREATE TABLE IF NOT EXISTS Users (" +
                        "username VARCHAR(255) not null, " +
                        "password VARCHAR(255) PRIMARY KEY not null, " +
                        "email VARCHAR(255) not null" +
                        ")",
                "CREATE TABLE IF NOT EXISTS Games (" +
                        "gameId INT AUTO_INCREMENT PRIMARY KEY not null, " +
                        "whiteUsername VARCHAR(255), " +
                        "blackUsername VARCHAR(255), " +
                        "gameName VARCHAR(255) not null, " +
                        "game JSON not null" +
                        ")"
        };
        for (String sql : sqlStatements) {
            executeUpdate(sql);
        }
    }
}
