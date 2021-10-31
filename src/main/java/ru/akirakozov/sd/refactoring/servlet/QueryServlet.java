package main.java.ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.database.SQLCommand;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        if ("max".equals(command)) {
            try {
                try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
                    Statement stmt = c.createStatement();
                    Database db = new Database(stmt);
                    ResultSet rs = db.queryBuilder()
                            .addSQLCommand(SQLCommand.SELECT)
                            .addCommandArgument("*")
                            .addSQLCommand(SQLCommand.FROM)
                            .addCommandArgument("PRODUCT")
                            .addSQLCommand(SQLCommand.ORDER_BY)
                            .addCommandArgument("PRICE")
                            .addSQLCommand(SQLCommand.DESC)
                            .addCommandArgument("LIMIT 1")
                            .execute();
                    response.getWriter().println("<html><body>");
                    response.getWriter().println("<h1>Product with max price: </h1>");

                    while (rs.next()) {
                        String name = rs.getString("name");
                        int price = rs.getInt("price");
                        response.getWriter().println(name + "\t" + price + "</br>");
                    }
                    response.getWriter().println("</body></html>");

                    rs.close();
                    stmt.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("min".equals(command)) {
            try {
                try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
                    Statement stmt = c.createStatement();
                    Database db = new Database(stmt);
                    ResultSet rs = db.queryBuilder()
                            .addSQLCommand(SQLCommand.SELECT)
                            .addCommandArgument("*")
                            .addSQLCommand(SQLCommand.FROM)
                            .addCommandArgument("PRODUCT")
                            .addSQLCommand(SQLCommand.ORDER_BY)
                            .addCommandArgument("PRICE")
                            .addCommandArgument("LIMIT 1")
                            .execute();
                    response.getWriter().println("<html><body>");
                    response.getWriter().println("<h1>Product with min price: </h1>");

                    while (rs.next()) {
                        String name = rs.getString("name");
                        int price = rs.getInt("price");
                        response.getWriter().println(name + "\t" + price + "</br>");
                    }
                    response.getWriter().println("</body></html>");

                    rs.close();
                    stmt.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("sum".equals(command)) {
            try {
                try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
                    Statement stmt = c.createStatement();
                    Database db = new Database(stmt);
                    ResultSet rs = db.queryBuilder()
                            .addSQLCommand(SQLCommand.SELECT)
                            .addCommandArgument("SUM(price)")
                            .addSQLCommand(SQLCommand.FROM)
                            .addCommandArgument("PRODUCT")
                            .execute();
                    response.getWriter().println("<html><body>");
                    response.getWriter().println("Summary price: ");

                    if (rs.next()) {
                        response.getWriter().println(rs.getInt(1));
                    }
                    response.getWriter().println("</body></html>");

                    rs.close();
                    stmt.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("count".equals(command)) {
            try {
                try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
                    Statement stmt = c.createStatement();
                    Database db = new Database(stmt);
                    ResultSet rs = db.queryBuilder()
                            .addSQLCommand(SQLCommand.SELECT)
                            .addCommandArgument("COUNT(*)")
                            .addSQLCommand(SQLCommand.FROM)
                            .addCommandArgument("PRODUCT")
                            .execute();
                    response.getWriter().println("<html><body>");
                    response.getWriter().println("Number of products: ");

                    if (rs.next()) {
                        response.getWriter().println(rs.getInt(1));
                    }
                    response.getWriter().println("</body></html>");

                    rs.close();
                    stmt.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            response.getWriter().println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
