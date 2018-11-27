package com.ua.sutty.jdbc.repository;


import com.ua.sutty.jdbc.domain.Role;

public interface RoleDao {

    void create(Role role);

    void update(Role role);

    void remove(Role role);

    Role findByName(String name);

}
