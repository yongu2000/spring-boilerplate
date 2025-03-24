package com.boilerplate.boilerplate.domain.auth.oauth2;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class OAuth2UserInfo {

    private String email;
    private String password;
    private String name;
    private String provider;

    public static OAuth2UserInfo of(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new RuntimeException();
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
            .provider("google")
            .email((String) attributes.get("email"))
            .password((String) attributes.get("sub"))
            .name((String) attributes.get("name"))
            .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
            .provider("kakao")
            .email("kakao@kakao.com")
            .password(attributes.get("id").toString())
            .name((String) ((Map) attributes.get("properties")).get("nickname"))
            .build();
    }

    private static OAuth2UserInfo ofNaver(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
            .provider("naver")
            .email((String) ((Map) attributes.get("response")).get("email"))
            .password((String) ((Map) attributes.get("response")).get("id"))
            .name((String) ((Map) attributes.get("response")).get("name"))
            .build();
    }

}
