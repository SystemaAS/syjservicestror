package no.systema.jservices.tror.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import no.systema.jservices.common.dao.HeadfDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.HeadfDaoService;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.util.CSVOutputter;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_HEADF {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_HEADF.class.getName());

	/**
	 * File: 	HEADF
	 * 
	 * @Example SELECT http://gw.systema.no:8080/syjservicestror/syjsHEADF.do?user=OSCAR&csv=true&limit=50
	 * 
	 */
	@RequestMapping(value="syjsHEADF.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String doHeadf(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<HeadfDao> jsonWriter = new JsonResponseWriter2<HeadfDao>();
		CSVOutputter<HeadfDao> csvOutputter = new CSVOutputter<HeadfDao>();
		StringBuffer sb = new StringBuffer();
		List<HeadfDao> headfDaoList = null;
		
		try {
			String user = request.getParameter("user");
			String csv = request.getParameter("csv");
			String limit = request.getParameter("limit");
			
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if ((userName != null && !"".equals(userName))) {
				if(StringUtils.hasValue(limit)){
					//discrete number of rows
					headfDaoList = headfDaoService.findAll(null, limit);
				}else{
					headfDaoList = headfDaoService.findAll(null);
				}
				if (headfDaoList != null) {
					if (StringUtils.hasValue(csv)) {
						sb.append(csvOutputter.writeAsString(headfDaoList));
					} else {
						sb.append(jsonWriter.setJsonResult_Common_GetList(userName, headfDaoList));
					}
				} else {
					errMsg = "ERROR on SELECT: Can not find HeadfDao list";
					status = "error";
					logger.info( status + errMsg);
					sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
				}

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

	@Qualifier ("headfDaoService")
	private HeadfDaoService headfDaoService;
	@Autowired
	@Required
	public void setHeadfDaoService(HeadfDaoService value){ this.headfDaoService = value; }
	public HeadfDaoService getHeadfDaoService(){ return this.headfDaoService; }		
	
	
}
