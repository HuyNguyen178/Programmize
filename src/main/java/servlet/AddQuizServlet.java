package servlet;

import dao.QuizDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/add-quiz")
public class AddQuizServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/add-quiz.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            // Khởi tạo Quiz model
            Quiz quiz = new Quiz();
            quiz.setTitle(request.getParameter("title"));
            quiz.setDescription(request.getParameter("description"));
            Chapter chapter = new Chapter();
            chapter.setChapterId(Integer.parseInt(request.getParameter("chapterId")));
            quiz.setChapter(chapter);

            List<Question> questions = new ArrayList<>();
            List<List<Answer>> allAnswers = new ArrayList<>();

            int qIdx = 1;
            while (request.getParameter("q_text_" + qIdx) != null) {
                // Tạo Question model
                Question q = new Question();
                q.setContent(request.getParameter("q_text_" + qIdx));
                questions.add(q);

                int correctAnsIdx = Integer.parseInt(request.getParameter("correct_ans_" + qIdx));
                List<Answer> answersOfQ = new ArrayList<>();

                int aIdx = 0;
                while (request.getParameter("ans_text_" + qIdx + "_" + aIdx) != null) {
                    // Tạo Answer model
                    Answer a = new Answer();
                    a.setContent(request.getParameter("ans_text_" + qIdx + "_" + aIdx));
                    a.setCorrect(aIdx == correctAnsIdx);
                    answersOfQ.add(a);
                    aIdx++;
                }
                allAnswers.add(answersOfQ);
                qIdx++;
            }

            QuizDAO dao = new QuizDAO();
            if (dao.addFullQuiz(quiz, questions, allAnswers)) {
                request.getSession().setAttribute("successMessage", "Quiz created successfully!");
                response.sendRedirect("chapter-details?id=" + chapter.getChapterId());
            } else {
                throw new Exception("Insert failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getHeader("referer"));
        }
    }
}