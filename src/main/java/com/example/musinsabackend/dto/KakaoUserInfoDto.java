package com.example.musinsabackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;  // 카카오 고유 ID
    private String connected_at;
    private KakaoAccount kakao_account;

    @Getter
    @Setter
    public static class KakaoAccount {
        private boolean profile_nickname_needs_agreement;
        private boolean profile_image_needs_agreement;
        private Profile profile;
        private boolean has_email;
        private boolean email_needs_agreement;
        private boolean is_email_valid;
        private boolean is_email_verified;

        @Getter
        @Setter
        public static class Profile {
            private String nickname;
            private String thumbnail_image_url;
            private String profile_image_url;
            private boolean is_default_image;
        }
    }
}
