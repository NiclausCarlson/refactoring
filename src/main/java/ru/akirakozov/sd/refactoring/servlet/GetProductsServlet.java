package main.java.ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.ResponseFiller.ResponseFiller;
import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.database.SQLCommand;

import javax.servlet.RequestDispatcher;
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
public class GetProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseFiller filler = new ResponseFiller(response);
        try {
            try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
                Statement stmt = c.createStatement();
                Database db = new Database(stmt);
                ResultSet rs = db.queryBuilder().command(SQLCommand.SELECT)
                        .text("*")
                        .command(SQLCommand.FROM)
                        .text("PRODUCT")
                        .executeQuery();

                filler.openHeadBody();
                while (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    filler.setCloseBr(name + "\t" + price);
                }
                filler.closeHeadBody();
                rs.close();
                stmt.close();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        filler.setContentType().setOkStatus();
    }
}
