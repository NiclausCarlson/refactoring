package ru.akirakozov.sd.refactoring.database;

public enum SQLCommand {
    SELECT("SELECT"),
    INSERT("INSERT INTO"),
    VALUES("VALUES"),
    FROM("FROM"),
    ORDER_BY("ORDER BY"),
    DESC("DESC");
    String command;
    SQLCommand(String str) {
        this.command = str;
    }
}
