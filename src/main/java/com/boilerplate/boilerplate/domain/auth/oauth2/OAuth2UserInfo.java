package com.boilerplate.boilerplate.domain.auth.oauth2;

import com.boilerplate.boilerplate.domain.user.entity.Role;
import com.boilerplate.boilerplate.domain.user.entity.User;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class OAuth2UserInfo {

    private String username;
    private String password;
    private String email;
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
            .username("google_" + attributes.get("sub"))
            .password((String) attributes.get("sub"))
            .email((String) attributes.get("email"))
            .name((String) attributes.get("name"))
            .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
            .provider("kakao")
            .username("kakao_" + attributes.get("id").toString())
            .password(attributes.get("id").toString())
            .name((String) ((Map) attributes.get("properties")).get("nickname"))
            .email("kakao@kakao.com")
            .build();
    }

    private static OAuth2UserInfo ofNaver(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
            .provider("naver")
            .username("naver_" + ((Map) attributes.get("response")).get("id"))
            .password((String) ((Map) attributes.get("response")).get("id"))
            .email((String) ((Map) attributes.get("response")).get("email"))
            .name((String) ((Map) attributes.get("response")).get("name"))
            .build();
    }

    public User toEntity() {
        return User.builder()
            .username(username)
            .password(password)
            .name(name)
            .email(email)
            .role(Role.USER)
            .provider(provider)
            .build();
    }

}
