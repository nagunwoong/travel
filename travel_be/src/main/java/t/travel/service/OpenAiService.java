package t.travel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class OpenAiService {

    @Value("${API_KEY}")
    private String apiKey;

    @Value("${API_URL}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getResponse(String userInput) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // JSON 요청 생성
        String jsonPayload = objectMapper.writeValueAsString(Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", "너는 항상 한국어로 응답해야 되며, 사용자가 기분을 입력하면 그 기분에 따라 국내 여행지를 추천해주는 AI야. 여행지를 추천하때는 1박2일로 가기 좋은 곳을 추천해야되 그리고 너무 뻔한곳 말고 한국에서 좀 특이한곳을 추천해봐. 먼저 사용자의 기분을 확인하고, 다음에 그 기분에 맞는 이유를 포함한 여행지를 추천해야 해. 이유를 말할때는 너무 짧게 말하지말고 좀 장엄하고 좀 길게 말해 사용자의 기분에 따른 위로의 말이나 공감의 말도 같이 포함하면서 마지막으로 추천한 여행지의 유명한 관광명소 5곳을 제안해야 해 관광명소를 추천하면 이곳을 왜 추천하는지 하나하나 설명도 자세하게 해 그리고 관광지를 말할때는 예를 들어 제주도를 추천하면 1.한라산 : 등반이 좋아요  이런식으로 깔끔하게 말해죠. 그리고 사용자의 성별은 남자인지 여자인지 모르니까 언니, 오빠 이런 단어는 절대 사용하지 말고 사용자가 적은 기분에 공감만 하도록해"),
                        Map.of("role", "user", "content", userInput)
                ),
                "max_tokens", 1500, // 응답이 길어질 수 있으므로 충분한 토큰 할당했음
                "stream", true
        ));

        RequestBody body = RequestBody.create(
                MediaType.get("application/json"),
                jsonPayload
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                String responseBody = response.body().string();
                throw new RuntimeException("API 호출 실패: " + responseBody);
            }

            String responseBody = response.body().string();


            // OpenAI 응답에서 content만 추출
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        }
    }

    // 입력값 검증 (비어있거나 너무 길 경우 처리)
    public String notStirng(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("입력값이 비어있음");
        }
        if (prompt.length() > 50) {
            prompt = prompt.substring(0, 50); // 입력값을 50자로 제한
        }
        return prompt;
    }
}