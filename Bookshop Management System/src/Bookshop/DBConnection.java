package Bookshop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bookshop";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    private static Connection connection = null;
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    // Private constructor to prevent instantiation
    private DBConnection() {}

    /**
     * Get the connection to the database using the singleton pattern.
     * If the connection is already established, it will return the existing connection.
     * This method now checks for an invalid connection and attempts to reset if necessary.
     * 
     * @return The established database connection
     */
    public static Connection getConnection() {
        if (connection == null || isConnectionInvalid()) {
            synchronized (DBConnection.class) {  // Thread-safe initialization
                if (connection == null || isConnectionInvalid()) {
                    try {
                        // Load the MySQL JDBC driver
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                        LOGGER.log(Level.INFO, "Connected to the database successfully!");
                    } catch (ClassNotFoundException e) {
                        LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found!", e);
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Connection failed! Check your database credentials.", e);
                        // Optionally notify the user to correct the information and retry
                    }
                }
            }
        }
        return connection;
    }

    /**
     * Check if the current connection is invalid (closed or failed).
     * @return true if the connection is invalid, false otherwise
     */
    private static boolean isConnectionInvalid() {
        try {
            return connection == null || connection.isClosed() || !connection.isValid(2);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking connection validity.", e);
            return true;
        }
    }

    /**
     * Close the database connection safely.
     * This should be called when the application ends or when the connection is no longer needed.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                LOGGER.log(Level.INFO, "Database connection closed successfully.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing the connection.", e);
            }
        }
    }

    /**
     * A helper method for graceful shutdown that can be called by the application or during shutdown
     * to ensure all resources are freed.
     */
    public static void shutdown() {
        closeConnection();
    }
}
