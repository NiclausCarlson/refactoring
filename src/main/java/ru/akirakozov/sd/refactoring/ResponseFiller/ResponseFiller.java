package ru.akirakozov.sd.refactoring.ResponseFiller;

import org.eclipse.jetty.util.IO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseFiller {
    private final HttpServletResponse response;

    public ResponseFiller(HttpServletResponse response) {
        this.response = response;
    }

    public ResponseFiller setText(final String txt) throws IOException {
        response.getWriter().println(txt);
        return this;
    }

    public ResponseFiller openHeadBody() throws IOException {
        return setText("<html><body>");
    }

    public ResponseFiller closeHeadBody() throws IOException {
        return setText("</body></html>");
    }

    public ResponseFiller setContentType() throws IOException {
        response.setContentType("text/html");
        return this;
    }
    public ResponseFiller setCloseBr(final String txt) throws IOException {
        return setText(txt + "</br>");
    }

    public ResponseFiller setH1(final String txt) throws IOException {
        return setText("<h1>" + txt + "</h1>");
    }

    public ResponseFiller setOkStatus() {
        response.setStatus(HttpServletResponse.SC_OK);
        return this;
    }
}
