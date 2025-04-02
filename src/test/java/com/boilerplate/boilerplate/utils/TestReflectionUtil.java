package com.boilerplate.boilerplate.utils;

import java.lang.reflect.Field;

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
}
