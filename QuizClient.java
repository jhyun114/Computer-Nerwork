import java.io.*;
import java.net.*;
import java.util.Properties;

public class QuizClient {
    private static String serverAddress = "localhost"; // 서버 주소
    private static int port = 123; // 포트 번호

    public static void main(String[] args) {
        // 서버 정보 load
        loadServerConfig(); // 파일에서 서버 정보 load

        // socket 연결, 서버와 통신을 시작
        try (Socket socket = new Socket(serverAddress, port); // 지정된 서버 주소와 포트로 socket 생성
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // 서버로 데이터 전송
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 서버로부터 메시지 수신
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) { // 사용자 입력 받기

            System.out.println("Connected to the quiz server."); // 서버와 연결 성공

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("QUESTION: ")) {
                    System.out.println(serverMessage.substring(10));
                    System.out.print("Your answer: ");
                    String answer = userInput.readLine(); // 답변 입력받기
                    out.println(answer); // 서버로 답변 전송

                } else if (serverMessage.startsWith("FEEDBACK: ")) {
                    System.out.println(serverMessage.substring(10));

                } else if (serverMessage.startsWith("SCORE: ")) {
                    System.out.println(serverMessage.substring(7)); // 최종 점수 출력
                    break;
                }
            }

            System.out.println("Quiz finished. Thank you for playing!");

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }

    // 서버 설정을 파일에서 읽어오는 메소드
    private static void loadServerConfig() {
        File configFile = new File("server_info.dat"); // 설정 파일을 가리키는 File 객체 생성
        if (configFile.exists()) { // 설정 파일이 존재하는지 확인
            try (FileInputStream fis = new FileInputStream(configFile)) {
                Properties props = new Properties();
                props.load(fis); // 설정 파일 로드

                serverAddress = props.getProperty("server_address", "localhost");
                port = Integer.parseInt(props.getProperty("port", "1234"));

            } catch (IOException | NumberFormatException e) {
                System.out.println("Error reading server config file. Using default values.");
            }
        } else {
            System.out.println("Config file not found. Using default server address and port.");
        }
    }
}
