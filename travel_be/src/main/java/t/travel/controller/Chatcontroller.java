package t.travel.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import t.travel.service.OpenAiService;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:52268", "http://10.0.2.2:8080"})
public class Chatcontroller {

    private final OpenAiService openAiService;

    @PostMapping("/new")
    public ResponseEntity<?> getChatResponse(@RequestBody String prompt) {
        try {
            String response =  openAiService.getResponse(prompt);
            System.out.println("gpt의 대답 : " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("질문받기 실패");
        }
    }

    @GetMapping("/write")
    public ResponseEntity<?> writeChat(@RequestBody String prompt) {
        try {
            openAiService.notStirng(prompt);
            return ResponseEntity.ok("입력창이 안비어있고 50글자 내외임");
        } catch (IllegalArgumentException e) {
            // 입력창 비어있으면 에러 발생
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    @PostMapping("/recommend")
    public ResponseEntity<?> getChatResponse(@RequestBody Map<String, String> request) {
        try {
            String userInput = request.get("input");
            if (userInput == null || userInput.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("입력값이 비어있습니다.");
            }

            String response = openAiService.getResponse(userInput);
            System.out.println("GPT의 대답: " + response);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("추천 생성 중 오류가 발생했습니다.");
        }
    }

}
