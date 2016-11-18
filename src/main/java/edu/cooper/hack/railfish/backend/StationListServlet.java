package edu.cooper.hack.railfish.backend;

import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class StationListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print("{\"stations\":[");
        resp.getWriter().println(
                StringUtils.join(RouteMap.stations.values().stream().map(
                        st -> String.format("{\"id\":\"%s\", \"name\":\"%s\"}", st.name, st.desc)).collect(Collectors.toSet()), ","));
        resp.getWriter().print("]}");
    }
}
