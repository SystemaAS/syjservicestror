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

import no.systema.jservices.common.dao.KodtfrDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.KodtfrDaoService;
import no.systema.jservices.common.json.JsonResponseWriter2;

@Controller
public class TrorResponseOutputterController_KODTFR {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_KODTFR.class.getName());

	/**
	 * File: 	KODTFR
	 * 
	 * @Example SELECT http://gw.systema.no:8080/syjservicestror/syjsKODTFR.do?user=OSCAR
	 * 
	 */
	@RequestMapping(value="syjsKODTFR.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsKODTFR(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<KodtfrDao> jsonWriter = new JsonResponseWriter2<KodtfrDao>();
		StringBuffer sb = new StringBuffer();
		List<KodtfrDao> kodtfrDaoList = null;
		
		try {
			logger.info("Inside syjsKODTFR.do");
			String user = request.getParameter("user");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if ((userName != null && !"".equals(userName))) {
				kodtfrDaoList = kodtfrDaoService.findAll(null);
				if (kodtfrDaoList != null) {
						sb.append(jsonWriter.setJsonResult_Common_GetList(userName, kodtfrDaoList));
				} else {
					errMsg = "ERROR on SELECT: Can not find KodtfrDao list";
					status = "error";
					logger.info( status + errMsg);
					sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
				}

			} else {
				errMsg = "ERROR on SELECT";
				status = "error";
				dbErrorStackTrace.append("request input parameters are invalid: <user>");
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

	@Qualifier ("kodtvaDaoService")
	private KodtfrDaoService kodtfrDaoService;
	@Autowired
	@Required
	public void setKodtfrDaoService (KodtfrDaoService value){ this.kodtfrDaoService = value; }
	public KodtfrDaoService getKodtfrDaoService(){ return this.kodtfrDaoService; }		
	
	
}
