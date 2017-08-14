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

import no.systema.jservices.common.dao.KodtsfDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.KodtsfDaoService;
import no.systema.jservices.common.json.JsonResponseWriter2;

@Controller
public class TrorResponseOutputterController_KODTSF {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_KODTSF.class.getName());

	/**
	 * File: 	KODTSF
	 * 
	 * @Example SELECT http://gw.systema.no:8080/syjservicestror/syjsKODTSF.do?user=OSCAR
	 * 
	 */
	@RequestMapping(value="syjsKODTSF.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsKODTSF(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<KodtsfDao> jsonWriter = new JsonResponseWriter2<KodtsfDao>();
		StringBuffer sb = new StringBuffer();
		List<KodtsfDao> kodtsfDaoList = null;
		
		try {
			logger.info("Inside syjsKODTSF.do");
			String user = request.getParameter("user");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if ((userName != null && !"".equals(userName))) {
				kodtsfDaoList = kodtsfDaoService.findAll(null);
				if (kodtsfDaoList != null) {
						sb.append(jsonWriter.setJsonResult_Common_GetList(userName, kodtsfDaoList));
				} else {
					errMsg = "ERROR on SELECT: Can not find KodtsfDao list";
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

	@Qualifier ("kodtsfDaoService")
	private KodtsfDaoService kodtsfDaoService;
	@Autowired
	@Required
	public void setKodtsfDaoService (KodtsfDaoService value){ this.kodtsfDaoService = value; }
	public KodtsfDaoService getKodtsfDaoService(){ return this.kodtsfDaoService; }		
	
	
}
