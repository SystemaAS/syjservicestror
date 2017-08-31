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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import no.systema.jservices.common.dao.TranDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.TranDaoService;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_TRAN {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_TRAN.class.getName());

	/**
	 * File: TRAN
	 * 
	 * @Example SELECT specific:
	 *          http://gw.systema.no:8080/syjservicestror/syjsTRAN.do?user=OSCAR&vmtran=96&vmtrku=24&vmtrle=1
	 * @Example SELECT list:
	 *          http://gw.systema.no:8080/syjservicestror/syjsTRAN.do?user=OSCAR
	 * 
	 */
	@RequestMapping(value = "syjsTRAN.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsSTED2(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<TranDao> jsonWriter = new JsonResponseWriter2<TranDao>();
		StringBuffer sb = new StringBuffer();
		List<TranDao> tranDaoList = new ArrayList<TranDao>();

		try {
			logger.info("Inside syjsTRAN");
			String user = request.getParameter("user");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				TranDao resultDao = new TranDao();
				TranDao dao = new TranDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);

				if (dao.getVmtran() > 0) {
					if (dao.getVmtrku() > 0) {
						if (dao.getVmtrle() > 0) {
							resultDao = tranDaoService.find(dao);
							tranDaoList.add(resultDao);
						} else {
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("vmtran", dao.getVmtran());
							params.put("vmtrku", dao.getVmtrku());
							tranDaoList = tranDaoService.findAll(params);
						}
					} else {
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("vmtran", dao.getVmtran());
						tranDaoList = tranDaoService.findAll(params);
					}
				} else {
					tranDaoList = tranDaoService.findAll(null);
				}
				if (tranDaoList != null) {
					sb.append(jsonWriter.setJsonResult_Common_GetList(userName, tranDaoList));
				} else {
					errMsg = "ERROR on SELECT: Can not find TranDao list";
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

	@Qualifier("tranDaoService")
	private TranDaoService tranDaoService;

	@Autowired
	@Required
	public void setTranDaoService(TranDaoService value) {
		this.tranDaoService = value;
	}

	public TranDaoService getTranDaoService() {
		return this.tranDaoService;
	}

}
