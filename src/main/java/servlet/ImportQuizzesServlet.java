package servlet;

import dao.ChapterDAO;
import dao.QuizDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Answer;
import model.Chapter;
import model.Question;
import model.Quiz;
import org.apache.poi.ss.usermodel.*;
import utils.ExcelFileUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/import-quizzes")
public class ImportQuizzesServlet extends HttpServlet {
    private QuizDAO quizDAO;
    private ChapterDAO chapterDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        quizDAO = new QuizDAO();
        chapterDAO = new ChapterDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int chapterId = Integer.parseInt(request.getParameter("id"));
        Chapter chapter = chapterDAO.getChapterById(chapterId);
        Part filePart = request.getPart("quizFile");
        List<String> errors = new ArrayList<>();
        int totalQuizzes = 0;
        int successCount = 0;

        if (filePart == null || filePart.getSize() == 0) {
            errors.add("No file chosen!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("chapter-details?id=" + chapterId);
            return;
        }

        String fileName = filePart.getSubmittedFileName().toLowerCase();
        if (!fileName.endsWith("xlsx")) {
            errors.add("Invalid file type. Please upload an Excel file!");
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect("chapter-details?id=" + chapterId);
            return;
        }

        try (Workbook workbook = WorkbookFactory.create(filePart.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                errors.add("Excel file has no header row!");
                request.getSession().setAttribute("errors", errors);
                response.sendRedirect("course-list");
                return;
            }

            Map<String, Integer> indexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                indexMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }

            Map<String, Quiz> quizMap = new HashMap<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String title = ExcelFileUtil.getCell(row, indexMap.get("title"));
                if (title == null || title.isEmpty()) {
                    errors.add("Title is blank at row " + (i + 1));
                    continue;
                }
                String description = ExcelFileUtil.getCell(row, indexMap.get("description"));
                String questionContent = ExcelFileUtil.getCell(row, indexMap.get("question"));
                if (questionContent == null || questionContent.isEmpty()) {
                    errors.add("Question is blank at row " + (i + 1));
                    continue;
                }
                String answerContent = ExcelFileUtil.getCell(row, indexMap.get("answer"));
                if (answerContent == null || answerContent.isEmpty()) {
                    errors.add("Answer is blank at row " + (i + 1));
                    continue;
                }
                String isCorrectStr = ExcelFileUtil.getCell(row, indexMap.get("is_correct"));
                if (!"true".equalsIgnoreCase(isCorrectStr) && !"false".equalsIgnoreCase(isCorrectStr)) {
                    errors.add("is_correct must be true or false at row " + (i + 1));
                    continue;
                }
                Boolean isCorrect = Boolean.parseBoolean(isCorrectStr);

                Quiz quiz = quizMap.get(title);
                if (quiz == null) {
                    quiz = new Quiz();
                    quiz.setTitle(title);
                    quiz.setDescription(description);
                    quiz.setChapter(chapter);
                    quiz.setQuestions(new ArrayList<>());
                    quizMap.put(title, quiz);

                    totalQuizzes++;
                }

                Question question = quiz.getQuestions()
                        .stream()
                        .filter(q -> q.getContent().equals(questionContent))
                        .findFirst()
                        .orElse(null);

                if (question == null) {
                    question = new Question();
                    question.setContent(questionContent);
                    question.setAnswers(new ArrayList<>());
                    quiz.getQuestions().add(question);
                }

                Answer answer = new Answer();
                answer.setContent(answerContent);
                answer.setCorrect(isCorrect);
                question.getAnswers().add(answer);


            }

            for (Quiz quiz : quizMap.values()) {
                for (Question q : quiz.getQuestions()) {
                    boolean hasCorrect = q.getAnswers().stream().anyMatch(Answer::isCorrect);
                    if (!hasCorrect) {
                        errors.add("Question '" + q.getContent() + "' has no correct answer (Quiz: " + quiz.getTitle() + ")");
                    }
                }
                try {
                    quizDAO.addQuiz(quiz);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Failed to import quiz: " + quiz.getTitle());
                }
            }

            if (!errors.isEmpty()) {
                request.getSession().setAttribute("errors", errors);
            }

            request.getSession().setAttribute("successMessage","Import successfully " + successCount + " of " + totalQuizzes + " quiz(zes)");
            response.sendRedirect("chapter-details?id=" + chapterId);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("chapter-details?id=" + chapterId + "&error=ImportFailed");
        }
    }
}
