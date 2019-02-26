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

import no.systema.jservices.common.dao.Ffr00fDao;
import no.systema.jservices.common.dto.Ffr00fDto;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.CnffDaoService;
import no.systema.jservices.common.dao.services.Ffr00fDaoService;
import no.systema.jservices.common.dto.HeadfDto;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.json.JsonResponseWriter;
import no.systema.jservices.common.util.ApplicationPropertiesUtil;
import no.systema.jservices.common.util.CSVOutputter;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_FFR00F {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_FFR00F.class.getName());
	
	/**
	 * File: 	FFR00F - Flyfraktbrev export - Tradevision main parent table
	 * 
	 * @Example SELECT list http://localhost:8080/syjservicestror/syjsFFR00F.do?user=OSCAR...
	 * 
	 * 
	 * 
	 */
	@RequestMapping(value="syjsFFR00F.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsDOKEFIM(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<Ffr00fDao> jsonWriter = new JsonResponseWriter2<Ffr00fDao>();
		StringBuffer sb = new StringBuffer();
		List<Ffr00fDao> ffr00fDaoList = new ArrayList<Ffr00fDao>();
		String user = request.getParameter("user");
		String all = request.getParameter("all");
		
		try {
			logger.info("Inside syjsFFR00F.do");		
			//String user = request.getParameter("user");
			//String csv = request.getParameter("csv");
			//String limit = request.getParameter("limit");
			
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				Ffr00fDao dao = new Ffr00fDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				//alternatives
				if(StringUtils.hasValue(all)){
					ffr00fDaoList = ffr00fDaoService.findAll(null);
				}else if(dao.getF0211()>=0 && dao.getF0213()>=0){
					ffr00fDaoList = ffr00fDaoService.findAll(dao.getKeysAwb());
				}else if(dao.getF00rec()>=0){
					ffr00fDaoList = ffr00fDaoService.findAll(dao.getKeys());
				}
				sb.append(jsonWriter.setJsonResult_Common_GetList(userName, ffr00fDaoList));
				
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
	 * 
	 * @Example SELECT list http://localhost:8080/syjservicestror/syjsFFR00F.do?user=OSCAR...
	 * 
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "syjsFFR00F_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsFFR00F_U(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<Ffr00fDao> jsonWriter = new JsonResponseWriter2<Ffr00fDao>();
		StringBuffer sb = new StringBuffer();
		String userName = null;
		String errMsg = null;
		String status = null;
		StringBuffer dbErrorStackTrace = null;

		try {
			logger.info("Inside syjsFFR00F_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			dbErrorStackTrace = new StringBuffer();
			Ffr00fDto dto = new Ffr00fDto();
			Ffr00fDao resultDao = new Ffr00fDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dto);
			binder.bind(request);
			
			//NOTE: No rulerLord, data i validated in client
			if (userName != null && !"".equals(userName)) {
				logger.info("mode:" + mode);
				
				if ("D".equals(mode)) {
					if(StringUtils.hasValue(dto.getF00rec())){
						this.ffr00fDaoService.delete(dto);
					}else{
						logger.info("ERROR on delete::: id(f00rec) == 0");
					}
					
				} else if ( "A".equals(mode)) {
					logger.info("Create new...");
					//prepare for create/update
					int keyId = this.cnffDaoService.getCnrecnAfterIncrement();
					dto.setF00rec(String.valueOf(keyId));
					resultDao = this.ffr00fDaoService.create(dto);
					
				} else if ( "U".equals(mode)) {
					logger.info("Update ...");
					//TODO resultDao = this.ffr00fDaoService.update(dao);
				}
				//deal with the results
				if (resultDao == null) {
					errMsg = "ERROR on UPDATE ";
					status = "error ";
					dbErrorStackTrace.append("Could not add/update dao=" + ReflectionToStringBuilder.toString(dto));
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

	
	@Qualifier ("ffr00fDaoService")
	private Ffr00fDaoService ffr00fDaoService;
	@Autowired
	@Required
	public void setFfr00fDaoService(Ffr00fDaoService value){ this.ffr00fDaoService = value; }
	public Ffr00fDaoService getFfr00fDaoService(){ return this.ffr00fDaoService; }		
	
	
	@Qualifier ("cnffDaoService")
	private CnffDaoService cnffDaoService;
	@Autowired
	@Required
	public void setCnffDaoService(CnffDaoService value){ this.cnffDaoService = value; }
	public CnffDaoService getCnffDaoService(){ return this.cnffDaoService; }		
	
	
}
