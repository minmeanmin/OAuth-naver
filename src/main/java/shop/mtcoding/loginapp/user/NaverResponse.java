package shop.mtcoding.loginapp.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public class NaverResponse {

    @Data // getter, setter
    public static class TokenDTO {
        @JsonProperty("access_token") // 이렇게 해주면 access_token으로 파싱된 값이 accessToken에 들어간다.(jackson 라이브러리)
        private String accessToken;
        @JsonProperty("refresh_token")
        private String refreshToken;
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("expires_in")
        private Integer expiresIn;
    }

    @Data
    public static class NaverUserDTO {
        private String message;
        private Response response;

        @Data
        class Response {
            private String id;
            private String name; // 회원 이름
            private String email; // 이메일 주소
        }
    }

//    {
//        "access_token": "AAAAQosjWDJieBiQZc3to9YQp6HDLvrmyKC+6+iZ3gq7qrkqf50ljZC+Lgoqrg",
//            "refresh_token": "c8ceMEJisO4Se7uGisHoX0f5JEii7JnipglQipkOn5Zp3tyP7dHQoP0zNKHUq2gY",
//            "token_type": "bearer",
//            "expires_in": "3600"
//    }

//    {
//        "resultcode": "00",
//                "message": "success",
//                "response": {
//            "id": "injwCcPKGqTqOyQ9SXOSDiZ6faa7m8u1IflBCA_iWnk",
//                    "email": "wed1604@naver.com",
//                    "name": "서지민"
//        }
//    }
}