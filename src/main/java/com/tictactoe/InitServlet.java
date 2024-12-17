package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // creating a new session
        HttpSession currentSession = req.getSession(true);

        //creating a playing field
        Field field = new Field();
        Map<Integer,Sign> fieldData = field.getField();

        //get a list of field values
        List<Sign> data = field.getFieldData();

        //adding field parameters to the session
        currentSession.setAttribute("field", field);
        //and field values sorted by index
        currentSession.setAttribute("data", data);

        //redirecting a request to the index.jsp page vie the server
        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
