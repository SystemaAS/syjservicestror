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

import no.systema.jservices.common.dao.DokufeDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.DokufeDaoService;
import no.systema.jservices.common.dto.HeadfDto;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.json.JsonResponseWriter;
import no.systema.jservices.common.util.ApplicationPropertiesUtil;
import no.systema.jservices.common.util.CSVOutputter;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_DOKUFE {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_DOKUFE.class.getName());
	
	/**
	 * File: 	DOKUFE
	 * 
	 * @Example SELECT list http://localhost:8080/syjservicestror/syjsDOKUFE.do?user=OSCAR&fe_dfavd=1&fe_dfopd=52919&fe_dffbnr=1
	 * 
	 * Could be a list with several records since there are different parties (CN, CZ, DP, etc in column= FE_N3035)
	 * 
	 * 
	 */
	@RequestMapping(value="syjsDOKUFE.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsDOKUFE(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<DokufeDao> jsonWriter = new JsonResponseWriter2<DokufeDao>();
		StringBuffer sb = new StringBuffer();
		List<DokufeDao> dokufeDaoList = new ArrayList<DokufeDao>();
		String user = request.getParameter("user");
		String p_avd = request.getParameter("fe_dfavd");
		String p_opd = request.getParameter("fe_dfopd");
		String p_fbnr = request.getParameter("fe_dffbnr");
		String p_n3035 = request.getParameter("fe_n3035");
		
		try {
			logger.info("Inside syjsDOKUFE.do");		
			//String user = request.getParameter("user");
			//String csv = request.getParameter("csv");
			//String limit = request.getParameter("limit");
			
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				if (StringUtils.hasValue(p_avd) && StringUtils.hasValue(p_opd)) {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("fe_dfavd", Integer.parseInt(p_avd));
					params.put("fe_dfopd", Integer.parseInt(p_opd));
					//fraktbrev nr
					if(StringUtils.hasValue(p_fbnr)){
						params.put("fe_dffbnr", Integer.parseInt(p_fbnr));
					}
					//party type = CN, CZ, DP ...
					if(StringUtils.hasValue(p_fbnr)){
						params.put("fe_n3035", p_n3035);
					}
					dokufeDaoList = dokufeDaoService.findAll(params);
				}
				sb.append(jsonWriter.setJsonResult_Common_GetList(userName, dokufeDaoList));
				

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

	
	/**
	 * Update Database DML operations File: DOKUFE
	 * 
	 * @Example UPDATE:
	 * 			http://gw.systema.no:8080/syjservicestror/syjsDOKUFE_U.do?user=OSCAR&fe_dfavd=1&fe_dfopd=100&fe_dffbnr=1&fe_n3035=CN....and all the rest...&mode=U/A/D
	 *
	 */
	@RequestMapping(value = "syjsDOKUFE_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsDOKUFE_U(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<DokufeDao> jsonWriter = new JsonResponseWriter2<DokufeDao>();
		StringBuffer sb = new StringBuffer();
		String userName = null;
		String errMsg = null;
		String status = null;
		StringBuffer dbErrorStackTrace = null;

		try {
			logger.info("Inside syjsDOKUFE_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			dbErrorStackTrace = new StringBuffer();
			DokufeDao dao = new DokufeDao();
			DokufeDao resultDao = new DokufeDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			//NOTE: No rulerLord, data i validated in client

			if (userName != null && !"".equals(userName)) {
				if ("D".equals(mode)) {
					dokufeDaoService.delete(dao);
				} else if ("A".equals(mode)) {
					resultDao = dokufeDaoService.create(dao);
				} else if ("U".equals(mode)) {
					resultDao = dokufeDaoService.update(dao);
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

	@Qualifier ("dokufeDaoService")
	private DokufeDaoService dokufeDaoService;
	@Autowired
	@Required
	public void setDokufeDaoService(DokufeDaoService value){ this.dokufeDaoService = value; }
	public DokufeDaoService getDokufeDaoService(){ return this.dokufeDaoService; }		
	
	
}
