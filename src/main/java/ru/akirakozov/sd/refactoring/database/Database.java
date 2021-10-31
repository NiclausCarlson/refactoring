package ru.akirakozov.sd.refactoring.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {

    public static class QueryBuilder {
        List<String> query = new ArrayList<>();
        Database db;

        QueryBuilder(Database db) {
            this.db = db;
        }

        public QueryBuilder addSQLCommand(SQLCommand command) {
            query.add(command.command);
            return this;
        }

        public QueryBuilder addCommandArgument(String arg) {
            query.add(arg);
            return this;
        }

        private String build() {
            return String.join(" ", query);
        }

        public ResultSet execute() throws SQLException {
            return db.execute();
        }
    }

    private final QueryBuilder builder;
    private final Statement stmt;

    public Database(final Statement stmt) {
        this.builder = new QueryBuilder(this);
        this.stmt = stmt;
    }

    public QueryBuilder queryBuilder() {
        return builder;
    }

    public ResultSet execute() throws SQLException {
        return stmt.executeQuery(builder.build());
    }
}
