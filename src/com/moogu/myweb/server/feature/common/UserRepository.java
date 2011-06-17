package com.moogu.myweb.server.feature.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;

import com.moogu.myweb.shared.common.SUser;

@Component
public class UserRepository {

    @Autowired
    private SimpleJdbcTemplate jdbcTemplate;

    public Map<String, String> getAllUsersInfo() {
        final Map<String, String> result = new HashMap<String, String>();
        final List<Map<String, Object>> tmp = this.jdbcTemplate.queryForList("select CODE, NVL(NAME, CODE) as FULL_NAME from TB_USER");
        for (final Map<String, Object> map : tmp) {
            result.put((String) map.get("CODE"), (String) map.get("FULL_NAME"));
        }
        return result;
    }

    public SUser findByCode(String userCode) {
        final List<SUser> list = this.jdbcTemplate.query(
                        "select * from TB_USER where code = ?",
                        this.createRowMapper(),
                        userCode);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public void renameUser(String code, String newName) {
        this.jdbcTemplate.update("update TB_USER set name = ? where code = ?", newName, code);
    }

    public SUser createUser(String code, String name) {
        final Integer id = this.jdbcTemplate.queryForInt("select SQ_USER.nextval from dual");
        this.jdbcTemplate.update("insert into TB_USER (id, code, name) values (?,?,?)", id, code, name);
        return new SUser(id, code, name);
    }

    public List<SUser> getUsersOrderedByName() {
        final RowMapper<SUser> rowMapper = this.createRowMapper();
        return this.jdbcTemplate.query(
                        "select * from TB_USER where code <> 'DEV' and code <> 'ALL' order by name asc",
                        rowMapper);
    }

    public SUser findById(Integer id) {
        final RowMapper<SUser> rowMapper = this.createRowMapper();
        final List<SUser> users = this.jdbcTemplate.query("select * from TB_USER where id = ?", rowMapper, id);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    private RowMapper<SUser> createRowMapper() {
        final RowMapper<SUser> result = new RowMapper<SUser>() {

            public SUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                final SUser user = new SUser(rs.getInt("id"), rs.getString("code"), rs.getString("name"));
                return user;
            }
        };
        return result;
    }

}