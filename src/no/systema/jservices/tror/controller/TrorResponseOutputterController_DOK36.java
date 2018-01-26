package no.systema.jservices.tror.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import no.systema.jservices.common.dao.Dok36Dao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.Dok36DaoService;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_DOK36 {
	
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_DOK36.class.getName());
	
	/**
	 * File: 	DOK36
	 * @Example SELECT list http://localhost:8080/syjservicestror/syjsDOK36.do?user=OSCAR&d36avd=1&d36opd=52919&d36fnr=1
	 * 
	 */
	@RequestMapping(value="syjsDOK36.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsDOKUFE(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<Dok36Dao> jsonWriter = new JsonResponseWriter2<Dok36Dao>();
		StringBuffer sb = new StringBuffer();
		List<Dok36Dao> dok29DaoList = new ArrayList<Dok36Dao>();
		String user = request.getParameter("user");
		
		try {
			logger.info("Inside syjsDOK36.do");		
			
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				Dok36Dao dao = new Dok36Dao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				
				if (dao.getD36avd() > 0 && dao.getD36opd() >0 && dao.getD36fnr() > 0) {
					//get list
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("d36avd", dao.getD36avd());
					params.put("d36opd", dao.getD36opd()); 
					params.put("d36fnr", dao.getD36fnr()); 
					dok29DaoList = dok36DaoService.findAll(params);
				}
				sb.append(jsonWriter.setJsonResult_Common_GetList(userName, dok29DaoList));
				

			} else {
				errMsg = "ERROR on SELECT";
				status = "error";
				dbErrorStackTrace.append(" request input parameters are invalid: <user>");
				sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
			}
		} catch (Exception e) {
			logger.info("Error :", e);
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);
			return "ERROR [JsonResponseOutputterController]" + writer.toString();
		}

		session.invalidate();
		return sb.toString();

	}

	
	/**
	 * Update Database DML operations File: DOK29
	 * The model (db) does not support the UPDATE operation. Only DELETE and INSERT
	 * @Example INSERT
	 * 			http://gw.systema.no:8080/syjservicestror/syjsDOK36_U.do?user=OSCAR&d36avd=1&d36opd=100&d36fnr=1....and all the rest...&mode=A/D
	 *
	 */
	@RequestMapping(value = "syjsDOK36_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsDOKUFE_U(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<Dok36Dao> jsonWriter = new JsonResponseWriter2<Dok36Dao>();
		StringBuffer sb = new StringBuffer();
		String userName = null;
		String errMsg = null;
		String status = null;
		StringBuffer dbErrorStackTrace = null;

		try {
			logger.info("Inside syjsDOK36_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			dbErrorStackTrace = new StringBuffer();
			Dok36Dao dao = new Dok36Dao();
			Dok36Dao resultDao = new Dok36Dao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			//NOTE: No rulerLord, data i validated in client

			if (userName != null && !"".equals(userName)) {
				logger.info("mode:" + mode);
				if ("D".equals(mode)) {
					dok36DaoService.delete(dao);
				} else if ("A".equals(mode)) {
					resultDao = dok36DaoService.createWithoutDulicateCheck(dao);
				} 
				if (resultDao == null) {
					errMsg = "ERROR on UPDATE ";
					status = "error ";
					dbErrorStackTrace.append("Could not add/update dao=" + ReflectionToStringBuilder.toString(dao));
					sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
				} else {
					// OK UPDATE
					sb.append(jsonWriter.setJsonResult_Common_GetComposite(userName, resultDao));	
				}

			} else {
				// write JSON error output
				errMsg = "ERROR on UPDATE";
				status = "error";
				dbErrorStackTrace.append("request input parameters are invalid: <user>");
				sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
			}

		} catch (Exception e) {
			errMsg = "ERROR on UPDATE ";
			status = "error ";
			logger.info("Error:",e);
			dbErrorStackTrace.append(e.getMessage());
			sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status,dbErrorStackTrace));

		}
		session.invalidate();
		return sb.toString();

	}
	
	

	@Qualifier ("bridfDaoService")
	private BridfDaoService bridfDaoService;
	@Autowired
	@Required
	public void setBridfDaoService (BridfDaoService value){ this.bridfDaoService = value; }
	public BridfDaoService getBridfDaoService(){ return this.bridfDaoService; }	

	@Qualifier ("dok36DaoService")
	private Dok36DaoService dok36DaoService;
	@Autowired
	@Required
	public void setDok36DaoService(Dok36DaoService value){ this.dok36DaoService = value; }
	public Dok36DaoService getDok36DaoService(){ return this.dok36DaoService; }		
	
	
}
