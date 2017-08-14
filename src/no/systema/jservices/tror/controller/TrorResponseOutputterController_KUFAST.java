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

import no.systema.jservices.common.dao.KufastDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.KufastDaoService;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.util.StringUtils;
import no.systema.jservices.common.values.FasteKoder;

@Controller
public class TrorResponseOutputterController_KUFAST {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_KUFAST.class.getName());

	
	/**
	 * File: 	KUFAST
	 * 
	 * @Example SELECT specific: http://gw.systema.no:8080/syjservicestror/syjsKUFAST.do?user=OSCAR&kftyp=PRODTYPE&kfkod=L
	 * @Example SELECT list: http://gw.systema.no:8080/syjservicestror/syjsKUFAST.do?user=OSCAR&kftyp=PRODTYPE
	 * 
	 */
	@RequestMapping(value="syjsKUFAST.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsKUFAST(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<KufastDao> jsonWriter = new JsonResponseWriter2<KufastDao>();
		StringBuffer sb = new StringBuffer();
		List<KufastDao> kufastDaoList = new ArrayList<KufastDao>();
		
		try {
			logger.info("Inside syjsKUFAST");
			String user = request.getParameter("user");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				KufastDao resultDao = new KufastDao();
				KufastDao dao = new KufastDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);

				if (StringUtils.hasValue(dao.getKftyp())) {
					if (StringUtils.hasValue(dao.getKfkod())) {
						resultDao = kufastDaoService.find(FasteKoder.valueOf(dao.getKftyp()), dao.getKfkod());
						kufastDaoList.add(resultDao);
					} else {
						kufastDaoList  = kufastDaoService.getList(FasteKoder.valueOf(dao.getKftyp()));
					}
				} else {
					kufastDaoList = kufastDaoService.findAll(null);
				}
				if (kufastDaoList != null) {
					sb.append(jsonWriter.setJsonResult_Common_GetList(userName, kufastDaoList));
				} else {
					errMsg = "ERROR on SELECT: Can not find KufastDao list";
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


	@Qualifier ("bridfDaoService")
	private BridfDaoService bridfDaoService;
	@Autowired
	@Required
	public void setBridfDaoService (BridfDaoService value){ this.bridfDaoService = value; }
	public BridfDaoService getBridfDaoService(){ return this.bridfDaoService; }	

	@Qualifier ("kodtvaDaoService")
	private KufastDaoService kufastDaoService;
	@Autowired
	@Required
	public void setKufastDaoService (KufastDaoService value){ this.kufastDaoService = value; }
	public KufastDaoService getKufastDaoService(){ return this.kufastDaoService; }		
	
	
}
