package com.boilerplate.boilerplate.utils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class TestReflectionUtil {

    public static void setId(Object target, Long id) {
        try {
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (Exception e) {
            throw new RuntimeException("ID 설정 실패", e);
        }
    }

    public static void setCreatedAt(Object target, LocalDateTime createdAt) {
        try {
            Field field = target.getClass().getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(target, createdAt);
        } catch (Exception e) {
            throw new RuntimeException("createdAt 설정 실패", e);
        }
    }
}
