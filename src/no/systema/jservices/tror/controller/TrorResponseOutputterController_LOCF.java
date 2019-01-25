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

import no.systema.jservices.common.dao.LocfDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.LocfDaoService;
import no.systema.jservices.common.dto.HeadfDto;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.json.JsonResponseWriter;
import no.systema.jservices.common.util.ApplicationPropertiesUtil;
import no.systema.jservices.common.util.CSVOutputter;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_LOCF {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_LOCF.class.getName());
	
	/**
	 * File: 	LOCF - Flyfraktbrev export - Tradevision user-id table
	 * 
	 * @Example SELECT list http://localhost:8080/syjservicestror/syjsLOCF.do?user=OSCAR
	 * 
	 * Only allowed: perfect match of user
	 * 
	 * 
	 */
	@RequestMapping(value="syjsLOCF.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsDOKEFIM(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<LocfDao> jsonWriter = new JsonResponseWriter2<LocfDao>();
		StringBuffer sb = new StringBuffer();
		List<LocfDao> locfDaoList = new ArrayList<LocfDao>();
		String user = request.getParameter("user");
		
		try {
			logger.info("Inside syjsLOCF.do");		
			//String user = request.getParameter("user");
			//String csv = request.getParameter("csv");
			//String limit = request.getParameter("limit");
			
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				LocfDao dao = new LocfDao();
				dao.setLoid(user);
				//get list
				locfDaoList = locfDaoService.findAll(dao.getKeys());

				sb.append(jsonWriter.setJsonResult_Common_GetList(userName, locfDaoList));
				

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

	
	

	@Qualifier ("bridfDaoService")
	private BridfDaoService bridfDaoService;
	@Autowired
	@Required
	public void setBridfDaoService (BridfDaoService value){ this.bridfDaoService = value; }
	public BridfDaoService getBridfDaoService(){ return this.bridfDaoService; }	

	
	@Qualifier ("locfDaoService")
	private LocfDaoService locfDaoService;
	@Autowired
	@Required
	public void setLocfDaoService(LocfDaoService value){ this.locfDaoService = value; }
	public LocfDaoService getLocfDaoService(){ return this.locfDaoService; }		
	
	
}
