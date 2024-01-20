package database;

import com.example.shipsgamegui.GameResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameDatabase {

    public static List<GameResult> getGameResults() {
        List<GameResult> results = new ArrayList<>();

        try (Connection connection = DatabaseConnector.connect()) {
            String sql = "SELECT host, player, winner, data FROM game_results";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String host = resultSet.getString("host");
                    String player2 = resultSet.getString("player");
                    String winner = resultSet.getString("winner");
                    String data = resultSet.getString("data");

                    GameResult result = new GameResult(host, player2, winner, data);
                    results.add(result);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Obsłuż błąd (np. wyświetl komunikat dla użytkownika)
        }

        return results;
    }
    public static void saveGameResult(String host, String player, String winner) {
        try (Connection connection = DatabaseConnector.connect()) {
            String sql = "INSERT INTO game_results (host, player, winner) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, host);
                preparedStatement.setString(2, player);
                preparedStatement.setString(3, winner);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
