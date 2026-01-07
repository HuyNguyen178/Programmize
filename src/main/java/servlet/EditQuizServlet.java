package servlet;

import dao.QuizDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Answer;
import model.Question;
import model.Quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/edit-quiz")
public class EditQuizServlet extends HttpServlet {
    private QuizDAO quizDAO = new QuizDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Quiz quiz = quizDAO.getQuizById(id); // Bạn cần viết hàm lấy quiz kèm questions/answers
        request.setAttribute("quiz", quiz);
        request.getRequestDispatcher("WEB-INF/views/edit-quiz.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int quizId = Integer.parseInt(request.getParameter("quizId"));
            int chapterId = Integer.parseInt(request.getParameter("chapterId"));
            String title = request.getParameter("title");
            String description = request.getParameter("description");

            Quiz quiz = new Quiz();
            quiz.setId(quizId);
            quiz.setTitle(title);
            quiz.setDescription(description);

            List<Question> questions = new ArrayList<>();
            List<List<Answer>> allAnswers = new ArrayList<>();

            // Thu thập dữ liệu động giống logic add
            int qIdx = 1;
            while (request.getParameter("q_text_" + qIdx) != null) {
                Question q = new Question();
                q.setContent(request.getParameter("q_text_" + qIdx));
                questions.add(q);

                List<Answer> answers = new ArrayList<>();
                int correctAnsIdx = Integer.parseInt(request.getParameter("correct_ans_" + qIdx));

                int aIdx = 0;
                while (request.getParameter("ans_text_" + qIdx + "_" + aIdx) != null) {
                    Answer a = new Answer();
                    a.setContent(request.getParameter("ans_text_" + qIdx + "_" + aIdx));
                    a.setCorrect(aIdx == correctAnsIdx);
                    answers.add(a);
                    aIdx++;
                }
                allAnswers.add(answers);
                qIdx++;
            }

            quizDAO.updateFullQuiz(quiz, questions, allAnswers);
            response.sendRedirect("chapter-details?id=" + chapterId);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500);
        }
    }
}