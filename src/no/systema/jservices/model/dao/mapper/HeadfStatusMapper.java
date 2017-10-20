package no.systema.jservices.model.dao.mapper;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import no.systema.jservices.model.dao.entities.HeadfStatusDao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @author oscardelatorre
 * @date  Okt 20, 2017
 * 
 */
public class HeadfStatusMapper implements RowMapper {
	private static Logger logger = Logger.getLogger(HeadfStatusMapper.class.getName());
	
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
    	HeadfStatusDao dao = new HeadfStatusDao();
    	//dao.setSlalfa(rs.getString("slalfa"));
    	//TODO
    	
        return dao;
    }

}


