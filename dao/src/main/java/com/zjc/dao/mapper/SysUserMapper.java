package com.zjc.dao.mapper;

import com.zjc.dao.model.SysUser;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    @ResultMap("BaseResultMap")
    @Select("select * from USER where username = #{username}")
    SysUser findByUsername(String username);

    @ResultMap("BaseResultMap")
    @Select("select * from USER")
    List<SysUser> findAll();

    @ResultMap("WithMenuResultMap")
    @Select("SELECT\n" +
            "\tU.*,\n" +
            "\tSM.MENU_ID m_MENU_ID,\n" +
            "\tSM.CODE m_code,\n" +
            "\tSM.NAME m_name,\n" +
            "\tSM.url m_url,\n" +
            "\tSM.ORDER_NO m_order_no,\n" +
            "\tSM.ico m_ico,\n" +
            "\tSM.MENU_ID_PARENT m_MENU_ID_PARENT\n" +
            "FROM\n" +
            "\tSYS_USER U\n" +
            "\tLEFT JOIN SYS_USER_ROLE R ON U.id = R.USER_ID\n" +
            "\tLEFT JOIN SYS_ROLE SR ON R.ROLE_ID = SR.ROLE_ID\n" +
            "\tLEFT JOIN SYS_ROLE_MENU M ON SR.ROLE_ID = M.ROLE_ID\n" +
            "\tLEFT JOIN SYS_MENU SM ON M.MENU_ID = SM.MENU_ID\n" +
            "WHERE U.username = #{username}")
    SysUser findUserWithMenu(String username);

}