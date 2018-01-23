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

import no.systema.jservices.common.dao.DokufmDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.DokufmDaoService;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_DOKUFM {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_DOKUFM.class.getName());
	
	/**
	 * File: 	DOKUFM
	 * 
	 * @Example SELECT list http://localhost:8080/syjservicestror/syjsDOKUFM.do?user=OSCAR&fmavd=1&fmopd=52919&fmfbnr=1
	 * 
	 * 
	 * 
	 */
	@RequestMapping(value="syjsDOKUFM.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsDOKUFE(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<DokufmDao> jsonWriter = new JsonResponseWriter2<DokufmDao>();
		StringBuffer sb = new StringBuffer();
		List<DokufmDao> dokufeDaoList = new ArrayList<DokufmDao>();
		String user = request.getParameter("user");
		/*String p_avd = request.getParameter("fe_dfavd");
		String p_opd = request.getParameter("fe_dfopd");
		String p_fbnr = request.getParameter("fe_dffbnr");
		String p_n3035 = request.getParameter("fe_n3035");
		*/
		try {
			logger.info("Inside syjsDOKUFM.do");		
			//String user = request.getParameter("user");
			//String csv = request.getParameter("csv");
			//String limit = request.getParameter("limit");
			
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				DokufmDao dao = new DokufmDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				
				if (dao.getFmavd() > 0 && dao.getFmopd() >0) {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("fmavd", dao.getFmavd());
					params.put("fmopd", dao.getFmopd()); 
					if(dao.getFmfbnr() > 0){
						params.put("fmfbnr", dao.getFmfbnr());
					}else{
						dao.setKeys(dao.getFmavd(), dao.getFmopd());
					}
				
					//get list
					dokufeDaoList = dokufmDaoService.findAll(params);
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
	 * Update Database DML operations File: DOKUFM
	 * 
	 * @Example UPDATE:
	 * 			http://gw.systema.no:8080/syjservicestror/syjsDOKUFM_U.do?user=OSCAR&fmavd=1&fmopd=100&fmfbnr=1....and all the rest...&mode=U/A/D
	 *
	 */
	@RequestMapping(value = "syjsDOKUFM_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsDOKUFE_U(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<DokufmDao> jsonWriter = new JsonResponseWriter2<DokufmDao>();
		StringBuffer sb = new StringBuffer();
		String userName = null;
		String errMsg = null;
		String status = null;
		StringBuffer dbErrorStackTrace = null;

		try {
			logger.info("Inside syjsDOKUFM_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			dbErrorStackTrace = new StringBuffer();
			DokufmDao dao = new DokufmDao();
			DokufmDao resultDao = new DokufmDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			//NOTE: No rulerLord, data i validated in client

			if (userName != null && !"".equals(userName)) {
				logger.info("mode:" + mode);
				if ("D".equals(mode)) {
					dokufmDaoService.delete(dao);
				} else if ("A".equals(mode)) {
					resultDao = dokufmDaoService.create(dao);
				} else if ("U".equals(mode)) {
					resultDao = dokufmDaoService.update(dao);
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

	@Qualifier ("dokufmDaoService")
	private DokufmDaoService dokufmDaoService;
	@Autowired
	@Required
	public void setDokufmDaoService(DokufmDaoService value){ this.dokufmDaoService = value; }
	public DokufmDaoService getDokufmDaoService(){ return this.dokufmDaoService; }		
	
	
}
