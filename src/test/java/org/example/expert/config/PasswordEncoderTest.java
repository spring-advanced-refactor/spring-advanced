package org.example.expert.config;

import org.example.expert.config.auth.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {


    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new PasswordEncoder();
    }

    @Test
    @DisplayName("동일한 원본 비밀번호로 matches 메서드 호출 시 true를 반환")
    void matches_returnTrue_whenPasswordsMatch() {
        // given
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // when
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        // then
        assertTrue(matches);
    }

    @Test
    @DisplayName("같은 비밀번호를 여러 번 인코딩해도 matches 메서드는 true를 반환")
    void matches_returnTrue_whenSamePasswordEncodedMultipleTimes() {
        // given
        String rawPassword = "testPassword";
        String encodedPassword1 = passwordEncoder.encode(rawPassword);
        String encodedPassword2 = passwordEncoder.encode(rawPassword);

        // when
        boolean matches1 = passwordEncoder.matches(rawPassword, encodedPassword1);
        boolean matches2 = passwordEncoder.matches(rawPassword, encodedPassword2);

        // then
        assertAll(
                () -> assertTrue(matches1),
                () -> assertTrue(matches2),
                () -> assertNotEquals(encodedPassword1, encodedPassword2)
        );
    }


    @Test
    @DisplayName("다른 비밀번호로 matches 메서드 호출 시 false를 반환")
    void matches_returnFalse_whenPasswordsDoNotMatch() {
        // given
        String rawPassword = "testPassword";
        String wrongPassword = "rikubogosipo";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // when
        boolean matches = passwordEncoder.matches(wrongPassword, encodedPassword);

        // then
        assertFalse(matches);
    }
}
