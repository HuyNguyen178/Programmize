package servlet;

import dao.QuizDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/import-quizzes")
public class ImportQuizzesServlet extends HttpServlet {
    private QuizDAO quizDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        quizDAO = new QuizDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
