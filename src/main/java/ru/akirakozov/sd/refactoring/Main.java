package main.java.ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import main.java.ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import main.java.ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import main.java.ru.akirakozov.sd.refactoring.servlet.QueryServlet;
import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.database.SQLCommand;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author akirakozov
 */
public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
            Statement stmt = c.createStatement();
            Database db = new Database(stmt);
            db.queryBuilder().command(SQLCommand.CREATE_TABLE)
                    .command(SQLCommand.IF).command(SQLCommand.NOT).command(SQLCommand.EXISTS)
                    .text("PRODUCT")
                    .openBracket()
                    .command(SQLCommand.INTEGER).command(SQLCommand.PRIMARY_KEY).text("AUTOINCREMENT")
                    .comma()
                    .text("NAME").command(SQLCommand.TEXT).command(SQLCommand.NOT_NULL)
                    .comma()
                    .text("PRICE").command(SQLCommand.INT).command(SQLCommand.NOT_NULL)
                    .closeBracket()
                    .executeUpdate();
            stmt.close();
        }

        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet()), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet()), "/get-products");
        context.addServlet(new ServletHolder(new QueryServlet()), "/query");

        server.start();
        server.join();
    }
}
