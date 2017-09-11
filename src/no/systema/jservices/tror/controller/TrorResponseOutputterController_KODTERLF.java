package no.systema.jservices.tror.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import no.systema.jservices.common.dao.KodterlfDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.KodterlfDaoService;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_KODTERLF {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_KODTERLF.class.getName());

	/**
	 * File: KODTERLF
	 * 
	 * @Example SELECT specific:
	 *          http://gw.systema.no:8080/syjservicestror/syjsKODTERLF.do?user=OSCAR
	 * 
	 */
	@RequestMapping(value = "syjsKODTERLF.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsKODTTST(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<KodterlfDao> jsonWriter = new JsonResponseWriter2<KodterlfDao>();
		StringBuffer sb = new StringBuffer();
		List<KodterlfDao> kodterlfDaoList = new ArrayList<KodterlfDao>();

		try {
			logger.info("Inside syjsKODTERLF");
			String user = request.getParameter("user");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				KodterlfDao resultDao = new KodterlfDao();
				KodterlfDao dao = new KodterlfDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);

				kodterlfDaoList = kodterlfDaoService.findAll(null);

				if (kodterlfDaoList != null) {
					sb.append(jsonWriter.setJsonResult_Common_GetList(userName, kodterlfDaoList));
				} else {
					errMsg = "ERROR on SELECT: Can not find KodterlfDao list";
					status = "error";
					logger.info(status + errMsg);
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

	@Qualifier("bridfDaoService")
	private BridfDaoService bridfDaoService;
	@Autowired
	@Required
	public void setBridfDaoService(BridfDaoService value) { this.bridfDaoService = value; }
	public BridfDaoService getBridfDaoService() { return this.bridfDaoService; }

	
	@Qualifier("kodterlfDaoService") 
	private KodterlfDaoService kodterlfDaoService;
	@Autowired
	@Required
	public void setKodterlfDaoService(KodterlfDaoService value) { this.kodterlfDaoService = value;}
	public KodterlfDaoService getKodterlfDaoService() { return this.kodterlfDaoService; }

}
