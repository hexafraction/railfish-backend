package edu.cooper.hack.railfish.backend;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class DelayReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        try {
            String action = req.getParameter("action");
            switch (action) {
                case "add":
                    NodeSet.costOverrides.put(req.getParameter("segment"), Integer.parseInt(req.getParameter("time")));
                    break;
                case "remove":
                    NodeSet.costOverrides.remove(req.getParameter("segment"));
                    break;
                case "clear":
                    NodeSet.costOverrides.clear();
                    break;
                default:
                    System.out.println("{\"result\":\"error\", \"error\":\"invalid action\"}");
                    return;
            }
            resp.getWriter().println("{\"result\":\"ok\", \"error\":\"\"}");
        } catch (Exception e) {
            resp.getWriter().println(String.format("{\"result\":\"error\", \"error\":\"%s\"}",
                    StringEscapeUtils.escapeJavaScript(e.toString()+":"+e.getMessage()+"//"+
                            StringUtils.join(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toArray(), "//"))));
        }
    }
}
