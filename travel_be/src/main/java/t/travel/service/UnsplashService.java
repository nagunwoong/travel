package t.travel.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UnsplashService {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String UNSPLASH_API_URL = "https://api.unsplash.com/search/photos";

}
