package com.example.maoliang.Repository;

import com.example.maoliang.Entity.Good;
import com.example.maoliang.Entity.Usr;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

//连接到MLUser的其他数据库操作方法
@Repository
public class UsrRepository {
    private final JdbcTemplate jdbcTemplate;

    public UsrRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean searchName(String username) {
        String sql = "SELECT * FROM MLuser WHERE username=?";
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, username);
        return !resultList.isEmpty();
    }


    public Usr search(String username) throws SQLException {
        String sql = "SELECT * FROM MLuser WHERE username=?";
        try {
            //System.out.println("hhh");
            return jdbcTemplate.queryForObject(sql, new Object[]{username}, (rs, rowNum) -> {
                Usr usr = new Usr();

                usr.setUserid(rs.getInt("userid"));
                usr.setUsername(rs.getString("username"));
                usr.setPwd(rs.getString("pwd"));
                usr.setPower(rs.getInt("power"));
                usr.setQuestion(rs.getString("question"));
                usr.setAnswer(rs.getString("answer"));
                if (usr.getPower() == 0) { // 买家
                    String infoSql = "SELECT * FROM MLinfo WHERE userid=?";
                    jdbcTemplate.query(infoSql, new Object[]{usr.getUserid()}, infoRs -> {
                        usr.setPhone(infoRs.getString("phone"));
                        usr.setAddress(infoRs.getString("address"));
                        usr.setDefault_address(infoRs.getString("default_address"));
                    });
                }
         //       System.out.println(usr.getUserid());
                return usr;
            });
        } catch (EmptyResultDataAccessException e) {
            // 如果查询结果为空，返回 null
            return null;
        }
    }


    public void modifyPwd(String username, String pwd) throws SQLException {
        String sql = "UPDATE MLuser SET pwd = ? WHERE username = ?";
        jdbcTemplate.update(sql, pwd, username);
    }

    public int find(String name) throws SQLException {
        String sql = "SELECT * FROM MLuser WHERE username = ?";
        try {
            jdbcTemplate.queryForObject(sql, new Object[]{name}, (rs, rowNum) -> {
                // 如果找到结果，返回1
                return 1;
            });
        } catch (EmptyResultDataAccessException e) {
            // 如果没有找到结果，返回0
            return 0;
        }
        return 0;
    }

    public void register(Usr usr) throws SQLException {
        String sql = "INSERT INTO MLuser (username, pwd, power, question, answer) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, usr.getUsername(), usr.getPwd(), usr.getPower(), usr.getQuestion(), usr.getAnswer());

        if (usr.getPower() == 0) { // 买家
            Integer userId = jdbcTemplate.queryForObject("SELECT MAX(userid) FROM MLuser", Integer.class);
            if (userId != null) {
                sql = "INSERT INTO MLinfo (userid, phone, address,default_address) VALUES (?, ?, ?, ?)";
                jdbcTemplate.update(sql, userId, usr.getPhone(), usr.getAddress(),usr.getDefault_address());
            }
        }
    }
}
