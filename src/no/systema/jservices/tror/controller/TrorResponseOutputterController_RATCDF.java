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

import no.systema.jservices.common.dao.RatcdfDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.RatcdfDaoService;
import no.systema.jservices.common.dto.HeadfDto;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.json.JsonResponseWriter;
import no.systema.jservices.common.util.ApplicationPropertiesUtil;
import no.systema.jservices.common.util.CSVOutputter;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_RATCDF {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_RATCDF.class.getName());
	
	/**
	 * File: 	RATCDF - Flyfraktbrev export - Rate Class Codes
	 * 
	 * @Example SELECT list http://localhost:8080/syjservicestror/syjsRATCDF.do?user=OSCAR...
	 * 
	 * 
	 * 
	 */
	@RequestMapping(value="syjsRATCDF.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsDOKEFIM(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<RatcdfDao> jsonWriter = new JsonResponseWriter2<RatcdfDao>();
		StringBuffer sb = new StringBuffer();
		List<RatcdfDao> daoList = new ArrayList<RatcdfDao>();
		String user = request.getParameter("user");
		
		try {
			logger.info("Inside syjsRATCDF.do");		
			//String user = request.getParameter("user");
			//String csv = request.getParameter("csv");
			//String limit = request.getParameter("limit");
			
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				RatcdfDao dao = new RatcdfDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				//alternatives
				if(StringUtils.hasValue(dao.getRaracd())){
					daoList = ratcdfDaoService.findAll(dao.getKeys());
				}else{
					StringBuffer orderBy = new StringBuffer("order by raracd");
					daoList = ratcdfDaoService.findAll(null, orderBy );
				}
				sb.append(jsonWriter.setJsonResult_Common_GetList(userName, daoList));
				
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

	
	@Qualifier ("ratcdfDaoService")
	private RatcdfDaoService ratcdfDaoService;
	@Autowired
	@Required
	public void setRatcdfDaoService(RatcdfDaoService value){ this.ratcdfDaoService = value; }
	public RatcdfDaoService getRatcdfDaoService(){ return this.ratcdfDaoService; }		
	
	
}
