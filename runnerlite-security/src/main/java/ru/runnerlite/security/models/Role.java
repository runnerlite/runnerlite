package ru.runnerlite.security.models;

import org.springframework.security.core.GrantedAuthority;

//TODO Добавить в базу данных соответсвующию таблицу с ролями
public class Role implements GrantedAuthority {

    private Long id;
    private String name;

    @Override
    public String getAuthority() {
        return "ROLE_ADMIN"; // заглушка
    }
}
