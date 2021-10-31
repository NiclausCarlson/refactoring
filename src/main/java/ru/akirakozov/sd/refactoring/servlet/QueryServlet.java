package main.java.ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.ResponseFiller.ResponseFiller;
import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.database.SQLCommand;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.Set;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    private final Set<String> SUPPORTED_COMMANDS = Set.of("max", "min", "sum", "count");

    private Database generateDatabaseQuery(final String command, final Statement stmt) {
        Database db = new Database(stmt);
        switch (command) {
            case "max":
                db.queryBuilder()
                        .command(SQLCommand.SELECT)
                        .text("*")
                        .command(SQLCommand.FROM)
                        .text("PRODUCT")
                        .command(SQLCommand.ORDER_BY)
                        .text("PRICE")
                        .command(SQLCommand.DESC)
                        .text("LIMIT 1");
                break;
            case "min":
                db.queryBuilder()
                        .command(SQLCommand.SELECT)
                        .text("*")
                        .command(SQLCommand.FROM)
                        .text("PRODUCT")
                        .command(SQLCommand.ORDER_BY)
                        .text("PRICE")
                        .text("LIMIT 1");
                break;
            case "sum":
                db.queryBuilder()
                        .command(SQLCommand.SELECT)
                        .text("SUM(price)")
                        .command(SQLCommand.FROM)
                        .text("PRODUCT");
                break;
            case "count":
                db.queryBuilder()
                        .command(SQLCommand.SELECT)
                        .text("COUNT(*)")
                        .command(SQLCommand.FROM)
                        .text("PRODUCT");
                break;
        }
        return db;
    }

    private void writeResponse(final String command, final ResultSet rs, ResponseFiller filler) throws IOException, SQLException {
        filler.openHeadBody();
        if (command.equals("max") || command.equals("min")) {
            filler.setH1("Product with " + command + " price: ");
            while (rs.next()) {
                String name = rs.getString("name");
                int price = rs.getInt("price");
                filler.setCloseBr(name + "\t" + price);
            }
        } else if (command.equals("count") || command.equals("sum")) {
            if (command.equals("count")) {
                filler.setText("Number of products: ");
            } else {
                filler.setText("Summary price: ");
            }
            if (rs.next()) {
                filler.setText(Integer.toString(rs.getInt(1)));
            }
        }
        filler.closeHeadBody();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseFiller filler = new ResponseFiller(response);
        String command = request.getParameter("command");
        if (SUPPORTED_COMMANDS.contains(command)) {
            try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
                Statement stmt = c.createStatement();
                ResultSet rs = generateDatabaseQuery(command, stmt).executeQuery();
                writeResponse(command, rs, filler);
                rs.close();
                stmt.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            filler.setText("Unknown command: " + command);
        }
        filler.setContentType().setOkStatus();
    }
}
