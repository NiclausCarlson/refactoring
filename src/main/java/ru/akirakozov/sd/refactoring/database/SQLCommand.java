package ru.akirakozov.sd.refactoring.database;

public enum SQLCommand {
    SELECT("SELECT"),
    FROM("FROM"),
    ORDER_BY("ORDER BY"),
    DESC("DESC");
    String command;
    SQLCommand(String str) {
        this.command = str;
    }
}
