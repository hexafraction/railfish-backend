package edu.cooper.hack.railfish.backend;

import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RouteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        String src = req.getParameter("src");
        String dst = req.getParameter("dst");
        if(src==null||dst==null||src.trim().isEmpty()||dst.trim().isEmpty()){
            resp.getWriter().println("{\"result\":\"error\", \"error\":\"src or dst is null or empty\"}");
        }
        String json = RouteMap.getJSON(src, dst);
        resp.getWriter().println(json);
    }


}
