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

import no.systema.jservices.common.dao.KodttstDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.KodttstDaoService;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_KODTTST {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_KODTTST.class.getName());

	/**
	 * File: KODTTST
	 * 
	 * @Example SELECT specific:
	 *          http://gw.systema.no:8080/syjservicestror/syjsKODTTST.do?user=OSCAR&ktskod=19
	 * @Example SELECT list:
	 *          http://gw.systema.no:8080/syjservicestror/syjsKODTTST.do?user=OSCAR
	 * 
	 */
	@RequestMapping(value = "syjsKODTTST.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsKODTTST(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<KodttstDao> jsonWriter = new JsonResponseWriter2<KodttstDao>();
		StringBuffer sb = new StringBuffer();
		List<KodttstDao> kodttstDaoList = new ArrayList<KodttstDao>();

		try {
			logger.info("Inside syjsKODTTST");
			String user = request.getParameter("user");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				KodttstDao resultDao = new KodttstDao();
				KodttstDao dao = new KodttstDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);

				if (dao.getKtskod() > 0) {
					resultDao = kodttstDaoService.find(dao);
					kodttstDaoList.add(resultDao);
				} else {
					kodttstDaoList = kodttstDaoService.findAll(null);
				}
				if (kodttstDaoList != null) {
					sb.append(jsonWriter.setJsonResult_Common_GetList(userName, kodttstDaoList));
				} else {
					errMsg = "ERROR on SELECT: Can not find KodttstDao list";
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
	public void setBridfDaoService(BridfDaoService value) {
		this.bridfDaoService = value;
	}

	public BridfDaoService getBridfDaoService() {
		return this.bridfDaoService;
	}

	@Qualifier("kodttstDaoService")
	private KodttstDaoService kodttstDaoService;

	@Autowired
	@Required
	public void setKodttstDaoService(KodttstDaoService value) {
		this.kodttstDaoService = value;
	}

	public KodttstDaoService getKodttstDaoService() {
		return this.kodttstDaoService;
	}

}
