package shop.mtcoding.loginapp.user;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void 회원가입(String username, String password, String email){
        User user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
        userRepository.save(user);
    }

    public User 로그인(String email, String password){
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new RuntimeException("아이디가 없습니다.");
        }else{
            if(user.getPassword().equals(password)){
                return user;
            }else{
                throw new RuntimeException("비밀번호가 틀렸습니다.");
            }
        }
    }

    public User 네이버로그인(String code) {
        // 1. code로 네이버에서 토큰 받기 (위임 완료) - OAuth2.0
        // 1.1 RestTemplate 설정
        RestTemplate rt = new RestTemplate();

        // 1.2 http header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 1.3 http body 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "_xz4hDgPVCdesQpGBjuH");
        body.add("client_secret", "koaPIW9qIG");
        body.add("grant_type", "authorization_code");
        body.add("state", "1234"); // 테스트니까 임의로 1234 설정
        body.add("code", code);

        // 1.4 body+header 객체 만들기
        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        // 1.5 api 요청하기 (토큰 받기)
        ResponseEntity<NaverResponse.TokenDTO> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token?",
                HttpMethod.POST,
                request,
                NaverResponse.TokenDTO.class);

        // 1.6 값 확인
        System.out.println(response.getBody().toString());

        // 2. 토큰으로 사용자 정보 받기(강제 회원가입을 하기 위해 받는다.) - PK와 email을 준다.
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers2.add("Authorization", "Bearer "+response.getBody().getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request2 =
                new HttpEntity<>(headers2);

        ResponseEntity<NaverResponse.NaverUserDTO> response2 = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                request2,
                NaverResponse.NaverUserDTO.class);

        System.out.println("response2 : "+response2.getBody().toString());

        // 3. 해당 정보로 DB 조회 (있을 수도 있고 없을 수도 있다.)
        String email = "naver_"+response2.getBody().getResponse().getEmail();
        User userPS = userRepository.findByEmail(email);

        // 4. 있으면 조회된 유저 정보를 리턴한다.
        if(userPS != null){
            System.out.println("어? 유저가 있네? 강제로그인 진행");
            return userPS;
        }else {
            System.out.println("어? 유저가 없네? 강제회원가입 and 강제로그인 진행");
            // 5. 없으면? - 강제 회원가입
            // 유저네임 : (provider_pk)
            // 비밀번호 : UUID
            // 이메일 : email 받은 값
            // 프로바이더 : kakao
            User user = User.builder()
                    .username(response2.getBody().getResponse().getName())
                    .password(UUID.randomUUID().toString())
                    .email(email)
                    .provider("naver")
                    .build();
            User returnUser = userRepository.save(user);
            return returnUser;
        }
    }
}
