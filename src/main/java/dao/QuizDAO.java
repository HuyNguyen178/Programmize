package dao;

import model.*;
import utils.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {
    public void addQuiz(Quiz quiz) throws SQLException {
        String insertQuizSql = "INSERT INTO quiz (chapter_id, title, description) VALUES (?, ?, ?)";
        String insertQuestionSql = "INSERT INTO question (quiz_id, content) VALUES (?, ?)";
        String insertAnswerSql = "INSERT INTO answer (question_id, content, is_correct) VALUES (?, ?, ?)";

        Connection conn = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            int quizId;

            try (PreparedStatement ps = conn.prepareStatement(insertQuizSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, quiz.getChapter().getChapterId());
                ps.setString(2, quiz.getTitle());
                ps.setString(3, quiz.getDescription());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("Creating quiz failed.");
                quizId = rs.getInt(1);
            }

            for (Question q : quiz.getQuestions()) {

                int questionId;
                try (PreparedStatement psQ = conn.prepareStatement(insertQuestionSql, Statement.RETURN_GENERATED_KEYS)) {
                    psQ.setInt(1, quizId);
                    psQ.setString(2, q.getContent());
                    psQ.executeUpdate();

                    ResultSet rsQ = psQ.getGeneratedKeys();
                    if (!rsQ.next()) throw new SQLException("Creating question failed.");
                    questionId = rsQ.getInt(1);
                }

                try (PreparedStatement psA = conn.prepareStatement(insertAnswerSql)) {
                    for (Answer a : q.getAnswers()) {
                        psA.setInt(1, questionId);
                        psA.setString(2, a.getContent());
                        psA.setBoolean(3, a.isCorrect());
                        psA.addBatch();
                    }
                    psA.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }


    public boolean addFullQuiz(Quiz quiz, List<Question> questions, List<List<Answer>> allAnswers) throws SQLException {
        String insertQuizSql = "INSERT INTO quiz (chapter_id, title, description) VALUES (?, ?, ?)";
        String insertQuestionSql = "INSERT INTO question (quiz_id, content) VALUES (?, ?)";
        String insertAnswerSql = "INSERT INTO answer (question_id, content, is_correct) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            int quizId;
            // 1. Lưu Quiz
            try (PreparedStatement ps = conn.prepareStatement(insertQuizSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, quiz.getChapter().getChapterId());
                ps.setString(2, quiz.getTitle());
                ps.setString(3, quiz.getDescription());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("Creating quiz failed.");
                quizId = rs.getInt(1);
            }

            // 2. Lưu Questions và Answers
            for (int i = 0; i < questions.size(); i++) {
                int questionId;
                Question q = questions.get(i);
                try (PreparedStatement psQ = conn.prepareStatement(insertQuestionSql, Statement.RETURN_GENERATED_KEYS)) {
                    psQ.setInt(1, quizId);
                    psQ.setString(2, q.getContent());
                    psQ.executeUpdate();

                    ResultSet rsQ = psQ.getGeneratedKeys();
                    if (!rsQ.next()) throw new SQLException("Creating question failed.");
                    questionId = rsQ.getInt(1);
                }

                // 3. Lưu danh sách đáp án của câu hỏi hiện tại
                List<Answer> answers = allAnswers.get(i);
                try (PreparedStatement psA = conn.prepareStatement(insertAnswerSql)) {
                    for (Answer a : answers) {
                        psA.setInt(1, questionId);
                        psA.setString(2, a.getContent());
                        psA.setBoolean(3, a.isCorrect());
                        psA.addBatch();
                    }
                    psA.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    public List<Quiz> getQuizzesByChapterId(int chapterId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT quiz_id, title, description FROM quiz WHERE chapter_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = new Quiz();
                    quiz.setId(rs.getInt("quiz_id"));
                    quiz.setTitle(rs.getString("title"));
                    quiz.setDescription(rs.getString("description"));

                    Chapter c = new Chapter();
                    c.setChapterId(chapterId);
                    quiz.setChapter(c);

                    list.add(quiz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalQuizzesByChapterId(int chapterId) {
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT COUNT(*) AS total_quizzes FROM quiz WHERE chapter_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, chapterId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateFullQuiz(Quiz quiz, List<Question> questions, List<List<Answer>> allAnswers) throws SQLException {
        String updateQuizSql = "UPDATE quiz SET title = ?, description = ? WHERE quiz_id = ?";
        String deleteAnswersSql = "DELETE FROM answer WHERE question_id IN (SELECT question_id FROM question WHERE quiz_id = ?)";
        String deleteQuestionsSql = "DELETE FROM question WHERE quiz_id = ?";
        String insertQuestionSql = "INSERT INTO question (quiz_id, content) VALUES (?, ?)";
        String insertAnswerSql = "INSERT INTO answer (question_id, content, is_correct) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Cập nhật thông tin Quiz
            try (PreparedStatement ps = conn.prepareStatement(updateQuizSql)) {
                ps.setString(1, quiz.getTitle());
                ps.setString(2, quiz.getDescription());
                ps.setInt(3, quiz.getId());
                ps.executeUpdate();
            }

            // 2. Xóa Answers và Questions cũ
            try (PreparedStatement psA = conn.prepareStatement(deleteAnswersSql)) {
                psA.setInt(1, quiz.getId());
                psA.executeUpdate();
            }
            try (PreparedStatement psQ = conn.prepareStatement(deleteQuestionsSql)) {
                psQ.setInt(1, quiz.getId());
                psQ.executeUpdate();
            }

            // 3. Chèn lại Questions và Answers mới (Tận dụng logic giống hàm addFullQuiz của bạn)
            for (int i = 0; i < questions.size(); i++) {
                int questionId;
                try (PreparedStatement psQ = conn.prepareStatement(insertQuestionSql, Statement.RETURN_GENERATED_KEYS)) {
                    psQ.setInt(1, quiz.getId());
                    psQ.setString(2, questions.get(i).getContent());
                    psQ.executeUpdate();
                    ResultSet rs = psQ.getGeneratedKeys();
                    if (!rs.next()) throw new SQLException("Update failed at question " + i);
                    questionId = rs.getInt(1);
                }

                List<Answer> answers = allAnswers.get(i);
                try (PreparedStatement psAns = conn.prepareStatement(insertAnswerSql)) {
                    for (Answer a : answers) {
                        psAns.setInt(1, questionId);
                        psAns.setString(2, a.getContent());
                        psAns.setBoolean(3, a.isCorrect());
                        psAns.addBatch();
                    }
                    psAns.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    public Quiz getQuizById(int quizId) {
        Quiz quiz = null;
        String sqlQuiz = "SELECT * FROM quiz WHERE quiz_id = ?";
        String sqlQuestions = "SELECT * FROM question WHERE quiz_id = ?";
        String sqlAnswers = "SELECT * FROM answer WHERE question_id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            // 1. Lấy thông tin cơ bản của Quiz
            try (PreparedStatement ps = conn.prepareStatement(sqlQuiz)) {
                ps.setInt(1, quizId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        quiz = new Quiz();
                        quiz.setId(rs.getInt("quiz_id"));
                        quiz.setTitle(rs.getString("title"));
                        quiz.setDescription(rs.getString("description"));

                        Chapter c = new Chapter();
                        c.setChapterId(rs.getInt("chapter_id"));
                        quiz.setChapter(c);
                    }
                }
            }

            if (quiz != null) {
                List<Question> questions = new ArrayList<>();
                try (PreparedStatement psQ = conn.prepareStatement(sqlQuestions)) {
                    psQ.setInt(1, quizId);
                    try (ResultSet rsQ = psQ.executeQuery()) {
                        while (rsQ.next()) {
                            Question q = new Question();
                            q.setId(rsQ.getInt("question_id"));
                            q.setContent(rsQ.getString("content"));
                            q.setExplanation(rsQ.getString("explanation"));

                            // 3. Với mỗi Question, lấy danh sách Answers
                            List<Answer> answers = new ArrayList<>();
                            try (PreparedStatement psA = conn.prepareStatement(sqlAnswers)) {
                                psA.setInt(1, q.getId());
                                try (ResultSet rsA = psA.executeQuery()) {
                                    while (rsA.next()) {
                                        Answer a = new Answer();
                                        a.setId(rsA.getInt("answer_id"));
                                        a.setContent(rsA.getString("content"));
                                        a.setCorrect(rsA.getBoolean("is_correct"));
                                        answers.add(a);
                                    }
                                }
                            }
                            q.setAnswers(answers);
                            questions.add(q);
                        }
                    }
                }
                quiz.setQuestions(questions);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quiz;
    }
}
