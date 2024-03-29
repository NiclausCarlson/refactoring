package main.java.ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.ResponseFiller.ResponseFiller;
import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.database.SQLCommand;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author akirakozov
 */
public class AddProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        long price = Long.parseLong(request.getParameter("price"));
        try {
            try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
                Statement stmt = c.createStatement();
                Database db = new Database(stmt);
                db.queryBuilder().command(SQLCommand.INSERT)
                        .text("PRODUCT")
                        .text("(NAME, PRICE)")
                        .command(SQLCommand.VALUES)
                        .text("(\"" + name + "\"," + price + ")")
                        .executeUpdate();
                stmt.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ResponseFiller filler = new ResponseFiller(response);
        filler.setContentType().setOkStatus().setText("OK");
    }
}
