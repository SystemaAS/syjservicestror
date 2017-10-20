package no.systema.jservices.model.dao.services;
import java.io.Writer;
import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import no.systema.jservices.model.dao.mapper.HeadfStatusMapper;
import no.systema.jservices.model.dao.entities.HeadfStatusDao;
import no.systema.main.util.DbErrorMessageManager;


public class HeadfStatusDaoServicesImpl implements HeadfStatusDaoServices {
	private static Logger logger = Logger.getLogger(HeadfStatusDaoServicesImpl.class.getName());
	private DbErrorMessageManager dbErrorMessageMgr = new DbErrorMessageManager();
	
	
	/**
	 * 
	 */
	public List getList(StringBuffer errorStackTrace){
		//Not implemented
		return null;
	}
	
	/**
	 * 
	 */
	public List findById(String id, StringBuffer errorStackTrace){
		//not implemented
		return null;
	}
	
	public int insert(Object dao, StringBuffer errorStackTrace){
		//not implemented
		return 0;
	}
	/**
	 * update status
	 */
	public int update(Object daoObj, StringBuffer errorStackTrace){
		
		int retval = 0;
		
		try{
			HeadfStatusDao dao = (HeadfStatusDao)daoObj;
			StringBuffer sql = new StringBuffer();
			//DEBUG --> logger.info("mydebug");
			sql.append(" UPDATE headf SET hest = ? ");
			sql.append(" WHERE heavd = ? ");
			sql.append(" AND heopd = ? ");
			
			//params
			retval = this.jdbcTemplate.update( sql.toString(), new Object[] { dao.getHest(), 
					//WHERE
					dao.getHeavd(), 
					dao.getHeopd() } );
			
		}catch(Exception e){
			Writer writer = this.dbErrorMessageMgr.getPrintWriter(e);
			logger.info(writer.toString());
			//Chop the message to comply to JSON-validation
			errorStackTrace.append(this.dbErrorMessageMgr.getJsonValidDbException(writer));
			retval = -1;
		}
		return retval;
	}
	
	public int delete(Object dao, StringBuffer errorStackTrace){
		//not implemented
		return 0;
	}
	
	/**                                                                                                  
	 * Wires jdbcTemplate                                                                                
	 *                                                                                                   
	 */                                                                                                  
	private JdbcTemplate jdbcTemplate = null;                                                            
	public void setJdbcTemplate( JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}          
	public JdbcTemplate getJdbcTemplate() {return this.jdbcTemplate;}

}
