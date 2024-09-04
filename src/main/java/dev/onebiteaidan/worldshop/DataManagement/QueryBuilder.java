package dev.onebiteaidan.worldshop.DataManagement;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {
    private StringBuilder query;
    private Database database;
    private List<Object> parameters = new ArrayList<>();

    public QueryBuilder(Database database) {
        this.database = database;
        this.query = new StringBuilder();
    }

    public QueryBuilder select(String columns) {
        query.append("SELECT ").append(columns).append(" ");
        return this;
    }

    public QueryBuilder from(String table) {
        query.append("FROM ").append(table).append(" ");
        return this;
    }

    public QueryBuilder where(String condition) {
        query.append("WHERE ").append(condition).append(" ");
        return this;
    }

    public QueryBuilder insertInto(String table, String columns) {
        query.append("INSERT INTO ").append(table).append(" (").append(columns).append(") ");
        return this;
    }

    public QueryBuilder values(String placeholders) {
        query.append("VALUES (").append(placeholders).append(") ");
        return this;
    }

    public QueryBuilder update(String table) {
        query.append("UPDATE ").append(table).append(" ");
        return this;
    }

    public QueryBuilder set(String columnsAndPlaceholders) {
        query.append("SET ").append(columnsAndPlaceholders).append(" ");
        return this;
    }

    public QueryBuilder deleteFrom(String table) {
        query.append("DELETE FROM ").append(table).append(" ");
        return this;
    }

    // Method to add parameters safely
    public QueryBuilder addParameter(Object value) {
        parameters.add(value);
        return this;
    }

    private PreparedStatement prepareStatement() throws SQLException {
        PreparedStatement preparedStatement = database.getConnection().prepareStatement(query.toString());

        // Set all parameters safely
        for (int i = 0; i < parameters.size(); i++) {
            preparedStatement.setObject(i + 1, parameters.get(i));
        }
        return preparedStatement;
    }

    public ResultSet executeQuery() throws SQLException {
        PreparedStatement preparedStatement = prepareStatement();
        return preparedStatement.executeQuery();
    }

    public int executeUpdate() throws SQLException {
        PreparedStatement preparedStatement = prepareStatement();
        return preparedStatement.executeUpdate();
    }
}
