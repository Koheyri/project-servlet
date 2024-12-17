package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends javax.servlet.http.HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //get the current session
        HttpSession currentSession = req.getSession();

        //get the game field object from the session
        Field field = extractField(currentSession);

        //get the index of the cell that was clicked
        int index = getSelectedIndex(req);
        Sign currentSing = field.getField().get(index);

        //check that the cell that was clicked is empty
        if (Sign.EMPTY != currentSing){
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        //put a cross in the cell that the user clocked on
        field.getField().put(index,Sign.CROSS);
        if (checkWin(resp, currentSession, field)) {
            return;
        }

        //get an empty field cell
        int emptyFieldIndex = field.getEmptyFieldIndex();

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if (checkWin(resp, currentSession, field)) {
                return;
            }
        }

        else {
            // Add a flag to the session if a draw has occurred
            currentSession.setAttribute("draw", true);

            // count the list of icons
            List<Sign> data = field.getFieldData();

            // update this list in the session
            currentSession.setAttribute("data", data);

            resp.sendRedirect("/index.jsp");
            return;
        }

        //Count the list of icons
        List<Sign> data = field.getFieldData();

        //Update the field object and the list of icons in the session
        currentSession.setAttribute("data", data);
        currentSession.setAttribute("index", index);

        resp.sendRedirect("/index.jsp");
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()){
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }

    private int getSelectedIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;

    }

    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            //add a flag that shows that someone has won
            currentSession.setAttribute("winner", winner);

            // counting the list of icons
            List<Sign> data = field.getFieldData();

            // update this list in the session
            currentSession.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
