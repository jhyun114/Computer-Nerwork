import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizServer {
    private static final int PORT = 123;
    private static final List<Question> questions = new ArrayList<>();

    // Question 클래스
    static class Question {
        String question;
        String answer;

        public Question(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    public static void main(String[] args) {
        questions.add(new Question("What is the capital of Korea?", "Seoul"));
        questions.add(new Question("What is 6 * 3?", "18"));
        questions.add(new Question("What is the biggest country in the world?", "Russia"));

        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // 소켓 생성
            System.out.println("Quiz Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                pool.execute(new ClientHandler(clientSocket)); // 새로운 클라이언트 처리를 위한 스레드 생성
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // client 요청 처리
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket; // 클라이언트와 통신하기 위한 소켓

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket; // 생성자에서 소켓 초기화
        }

        @Override
        public void run() {
            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                int score = 0;

                for (Question q : questions) {
                    out.println("QUESTION: " + q.question);

                    String clientAnswer = in.readLine();
                    if (clientAnswer != null && clientAnswer.equalsIgnoreCase(q.answer)) { // 정답 여부 확인
                        score++;
                        out.println("FEEDBACK: Correct!");
                    } else {
                        out.println("FEEDBACK: Incorrect. The correct answer is " + q.answer + ".");
                    }
                }

                // 최종 점수 전송
                out.println("SCORE: Your final score is " + score + "/" + questions.size() + ".");
                System.out.println("Quiz completed for client.");

            } catch (IOException e) {
                System.out.println("Client error: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }
}
