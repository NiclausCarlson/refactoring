import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainTest {
    private Server server;
    private static final String URI = "localhost:8081/";
    private static final String HTTP = "http://";

    private final Map<String, Integer> products = new LinkedHashMap<>();

    @Before
    public void initConnection() throws Exception {
        products.put("q", 12);
        products.put("w", 13);
        products.put("e", 120);
        products.put("r", 52);
        products.put("qwerty", 120);

        try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
            String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " PRICE          INT     NOT NULL)";
            Statement stmt = c.createStatement();

            stmt.executeUpdate(sql);
            stmt.close();
        }

        server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new main.java.ru.akirakozov.sd.refactoring.servlet.AddProductServlet()), "/add-product");
        context.addServlet(new ServletHolder(new main.java.ru.akirakozov.sd.refactoring.servlet.GetProductsServlet()), "/get-products");
        context.addServlet(new ServletHolder(new main.java.ru.akirakozov.sd.refactoring.servlet.QueryServlet()), "/query");

        server.start();
    }

    @After
    public void disconect() throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
            String sql = "DROP TABLE IF EXISTS PRODUCT";
            Statement stmt = c.createStatement();

            stmt.executeUpdate(sql);
            stmt.close();
        }
        server.stop();
    }

    private String generateRequestUrl(final String handle, final String args) {
        return HTTP + URI + handle + args;
    }

    private String doRequest(final String request) throws IOException {
        URL url = new URL(request);
        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder sb = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        in.close();
        return sb.toString();
    }

    private String addProduct(final String name, int price) throws IOException {
        products.putIfAbsent(name, price);
        final String handle = "add-product";
        String args = "?" + "name" + "=" + name + "&" +
                "price" + "=" + price;
        String request = generateRequestUrl(handle, args);
        return doRequest(request);
    }

    private String getProducts() throws IOException {
        final String handle = "get-products";
        String request = generateRequestUrl(handle, "");
        return doRequest(request);
    }

    private String getQuery(final String command) throws IOException {
        final String handle = "query";
        String args = "?" + "command" + "=" + command;
        String request = generateRequestUrl(handle, args);
        return doRequest(request);
    }

    private String getHtmlBody(final String something) {
        return "<html><body>" + something + "</body></html>";
    }

    private String getHtmlHead(final String something) {
        return "<h1>" + something + "</h1>";
    }

    private String getHtmlProductList() {
        StringBuilder sb = new StringBuilder();
        for (var lr : products.entrySet()) {
            sb.append(lr.getKey())
                    .append("\t")
                    .append(lr.getValue())
                    .append("</br>");
        }
        return sb.toString();
    }

    @Test
    public void testAddProduct() throws Exception {
        for (var lr : products.entrySet()) {
            Assert.assertEquals("OK", addProduct(lr.getKey(), lr.getValue()));
        }
    }

    @Test
    public void testGetProducts() throws Exception {
        for (var lr : products.entrySet()) {
            Assert.assertEquals("OK", addProduct(lr.getKey(), lr.getValue()));
        }

        String expectedResponse = getHtmlBody(getHtmlProductList());
        Assert.assertEquals(expectedResponse, getProducts());

        Assert.assertEquals("OK", addProduct("weew", 111));
        expectedResponse = getHtmlBody(getHtmlProductList());
        Assert.assertEquals(expectedResponse, getProducts());

        Assert.assertEquals("OK", addProduct("ewwwdd", 222));
        expectedResponse = getHtmlBody(getHtmlProductList());
        Assert.assertEquals(expectedResponse, getProducts());
    }

    @Test
    public void testGetQuery() throws Exception {
        int expectedSum = 0;
        int expectedMin = Integer.MAX_VALUE;
        String minProduct = "";
        for (var lr : products.entrySet()) {
            Assert.assertEquals("OK", addProduct(lr.getKey(), lr.getValue()));
            expectedSum += lr.getValue();
            if (expectedMin > lr.getValue()) {
                expectedMin = lr.getValue();
                minProduct = lr.getKey();
            }
        }
        String expectedSumResponse = getHtmlBody("Summary price: " + expectedSum);
        Assert.assertEquals(expectedSumResponse, getQuery("sum"));
        String expectedMinResponse = getHtmlBody(getHtmlHead("Product with min price: ") +
                minProduct +
                "\t" +
                expectedMin +
                "</br>");
        Assert.assertEquals(expectedMinResponse, getQuery("min"));

        Assert.assertEquals("OK", addProduct("wewwwew", -111));
        expectedSum -= 111;
        expectedMin = -111;
        minProduct = "wewwwew";
        expectedSumResponse = getHtmlBody("Summary price: " + expectedSum);
        Assert.assertEquals(expectedSumResponse, getQuery("sum"));
        expectedMinResponse = getHtmlBody(getHtmlHead("Product with min price: ") +
                minProduct +
                "\t" +
                expectedMin +
                "</br>");
        Assert.assertEquals(expectedMinResponse, getQuery("min"));
    }
}