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

import no.systema.jservices.common.dao.DokufDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.DokufDaoService;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_DOKUF {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_DOKUF.class.getName());
	
	/**
	 * File: 	DOKUF
	 * 
	 * @Example SELECT list http://gw.systema.no:8080/syjservicestror/syjsDOKUF.do?user=OSCAR&dfavd=1&dfopd=999
	 * 
	 * @Example SELECT
	 *          specific:   http://gw.systema.no:8080/syjservicestror/syjsDOKUF.do?user=OSCAR&dfavd=1&dfopd=999&dffbnr=1
	 * 
	 */
	@RequestMapping(value = "syjsDOKUF.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsHEADF(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<DokufDao> jsonWriter = new JsonResponseWriter2<DokufDao>();
		StringBuffer sb = new StringBuffer();
		List<DokufDao> dokfDaoList = new ArrayList<DokufDao>();
		String dfavd = request.getParameter("dfavd");
		String dfopd = request.getParameter("dfopd");
		String dffbnr = request.getParameter("dffbnr");

		try {
			logger.info("Inside syjsDOKUF.do");
			String user = request.getParameter("user");
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user);
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName) && StringUtils.hasValue(dfavd) && StringUtils.hasValue(dfopd)) {
				if (StringUtils.hasValue(dffbnr)) {
					DokufDao dao = fetchRecord(dfavd, dfopd, dffbnr);
					dokfDaoList.add(dao);
				} else {
					dokfDaoList = fetchRecords(dfavd, dfopd);
				}
				if (dokfDaoList != null) {
					sb.append(jsonWriter.setJsonResult_Common_GetList(userName, dokfDaoList));
				} else {
					errMsg = "ERROR on SELECT: Can not find DokufDao list";
					status = "error";
					logger.info(status + errMsg);
					sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
				}

			} else {
				errMsg = "ERROR on SELECT";
				status = "error";
				dbErrorStackTrace.append(" request input parameters are invalid: <user> <dfavd> <dfopd>");
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

	private List<DokufDao> fetchRecords(String dfavd, String dfopd) {
		int avd = Integer.parseInt(dfavd);
		int opd = Integer.parseInt(dfopd);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dfavd", avd);
		params.put("dfopd", opd);

		List<DokufDao> daoList = dokufDaoService.findAll(params);
		return daoList;
	}

	private DokufDao fetchRecord(String dfavd, String dfopd, String dffbnr) {
		int avd = Integer.parseInt(dfavd);
		int opd = Integer.parseInt(dfopd);
		int fbnr = Integer.parseInt(dffbnr);
		DokufDao qDao = new DokufDao();
		qDao.setDfavd(avd);
		qDao.setDfopd(opd);
		qDao.setDffbnr(fbnr);

		DokufDao dao = dokufDaoService.find(qDao);
		return dao;
	}	
	
	/**
	 * Update Database DML operations File: DOKUF
	 * 
	 * @Example UPDATE:
	 * 			http://gw.systema.no:8080/syjservicestror/syjsDOKUF_U.do?user=OSCAR&dfavd=2&dfopd=100&dfsg=JOV....and all the rest...&mode=U/A/D
	 *
	 */
	@RequestMapping(value = "syjsDOKUF_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsDOKUF_U(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<DokufDao> jsonWriter = new JsonResponseWriter2<DokufDao>();
		StringBuffer sb = new StringBuffer();
		String userName = null;
		String errMsg = null;
		String status = null;
		StringBuffer dbErrorStackTrace = null;

		try {
			logger.info("Inside syjsDOKUF_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			dbErrorStackTrace = new StringBuffer();
			DokufDao dao = new DokufDao();
			DokufDao resultDao = new DokufDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			//TODO:  rulerLord ? or data validated in UI?

			if (userName != null && !"".equals(userName)) {
				if ("D".equals(mode)) {
					dokufDaoService.delete(dao);
				} else if ("A".equals(mode)) {
					resultDao = dokufDaoService.create(dao);
				} else if ("U".equals(mode)) {
					resultDao = dokufDaoService.update(dao);
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

	@Qualifier ("dokufDaoService")
	private DokufDaoService dokufDaoService;
	@Autowired
	@Required
	public void setDokufDaoServicee(DokufDaoService value){ this.dokufDaoService = value; }
	public DokufDaoService getDokufDaoService(){ return this.dokufDaoService; }		
	
	
}
