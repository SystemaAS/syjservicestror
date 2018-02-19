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

import no.systema.jservices.common.dao.DokefimDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.DokefimDaoService;
import no.systema.jservices.common.dto.HeadfDto;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.json.JsonResponseWriter;
import no.systema.jservices.common.util.ApplicationPropertiesUtil;
import no.systema.jservices.common.util.CSVOutputter;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_DOKEFIM {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_DOKEFIM.class.getName());
	
	/**
	 * File: 	DOKEFIM - Flyfraktbrev import (pre-preparation for DOKEF)
	 * 
	 * @Example SELECT list http://localhost:8080/syjservicestror/syjsDOKEFIM.do?user=OSCAR&imavd=1&imopd=52919&imlop=1
	 * 
	 * Could be a list with several records (several flyfraktbrev
	 * 
	 * 
	 */
	@RequestMapping(value="syjsDOKEFIM.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsDOKUFE(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<DokefimDao> jsonWriter = new JsonResponseWriter2<DokefimDao>();
		StringBuffer sb = new StringBuffer();
		List<DokefimDao> dokufeDaoList = new ArrayList<DokefimDao>();
		String user = request.getParameter("user");
		/*String p_avd = request.getParameter("fe_dfavd");
		String p_opd = request.getParameter("fe_dfopd");
		String p_fbnr = request.getParameter("fe_dffbnr");
		String p_n3035 = request.getParameter("fe_n3035");
		*/
		try {
			logger.info("Inside syjsDOKEFIM.do");		
			//String user = request.getParameter("user");
			//String csv = request.getParameter("csv");
			//String limit = request.getParameter("limit");
			
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				DokefimDao dao = new DokefimDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				
				
				if (dao.getImavd() > 0 && dao.getImopd() >0) {
					boolean isSingleRecord = false;
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("imavd", dao.getImavd());
					params.put("imopd", dao.getImopd());
					//fraktbrev nr
					if(dao.getImlop() > 0){
						params.put("imlop", dao.getImlop());
					}
					
					if(isSingleRecord){
						//use defualt keys
					}else{
						//this is in order to make the select wider than default keys... 
						if(dao.getImlop() > 0){
							dao.setKeys(dao.getImavd(), dao.getImopd(), dao.getImlop());
						}else{
							dao.setKeys(dao.getImavd(), dao.getImopd());
						}
					}
					//get list
					dokufeDaoList = dokefimDaoService.findAll(params);
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
	 * Update Database DML operations File: DOKEFIM
	 * 
	 * @Example UPDATE:
	 * 			http://gw.systema.no:8080/syjservicestror/syjsDOKEFIM_U.do?user=OSCAR&imavd=1&imopd=100&imlop=1....and all the rest...&mode=U/A/D
	 *
	 */
	@RequestMapping(value = "syjsDOKEFIM_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsDOKUFE_U(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<DokefimDao> jsonWriter = new JsonResponseWriter2<DokefimDao>();
		StringBuffer sb = new StringBuffer();
		String userName = null;
		String errMsg = null;
		String status = null;
		StringBuffer dbErrorStackTrace = null;

		try {
			logger.info("Inside syjsDOKEFIM_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			dbErrorStackTrace = new StringBuffer();
			DokefimDao dao = new DokefimDao();
			DokefimDao resultDao = new DokefimDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			//NOTE: No rulerLord, data i validated in client

			if (userName != null && !"".equals(userName)) {
				logger.info("mode:" + mode);
				if ("D".equals(mode)) {
					this.dokefimDaoService.delete(dao);
				} else if ("A".equals(mode)) {
					resultDao = this.dokefimDaoService.create(dao);
				} else if ("U".equals(mode)) {
					resultDao = this.dokefimDaoService.update(dao);
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

	
	@Qualifier ("dokefimDaoService")
	private DokefimDaoService dokefimDaoService;
	@Autowired
	@Required
	public void setDokefimDaoService(DokefimDaoService value){ this.dokefimDaoService = value; }
	public DokefimDaoService getDokefimDaoService(){ return this.dokefimDaoService; }		
	
	
}
