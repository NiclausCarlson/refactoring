package ru.akirakozov.sd.refactoring.database;

public enum SQLCommand {
    SELECT("SELECT"),
    INSERT("INSERT INTO"),
    VALUES("VALUES"),
    FROM("FROM"),
    ORDER_BY("ORDER BY"),
    IF("IF"),
    NOT("NOT"),
    EXISTS("EXISTS"),
    INTEGER("INTEGER"),
    PRIMARY_KEY("PRIMARY KEY"),
    NULL("NULL"),
    NOT_NULL("NOT NULL"),
    TEXT("TEXT"),
    INT("INT"),
    CREATE_TABLE("CREATE TABLE"),
    DESC("DESC");
    String command;
    SQLCommand(String str) {
        this.command = str;
    }
}
