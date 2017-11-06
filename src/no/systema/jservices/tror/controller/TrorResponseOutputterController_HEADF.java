package no.systema.jservices.tror.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

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

import no.systema.jservices.model.dao.entities.HeadfStatusDao;
import no.systema.jservices.model.dao.services.HeadfStatusDaoServices;

import no.systema.jservices.common.dao.HeadfDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.HeadfDaoService;
import no.systema.jservices.common.dto.HeadfDto;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.json.JsonResponseWriter;
import no.systema.jservices.common.util.ApplicationPropertiesUtil;
import no.systema.jservices.common.util.CSVOutputter;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_HEADF {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_HEADF.class.getName());
	private static String DAYS_TO_VIEW_DEFAULT = ApplicationPropertiesUtil.getProperty("no.systema.headf.daystoview.default");
	
	/**
	 * File: 	HEADF
	 * 
	 * @Example SELECT list http://gw.systema.no:8080/syjservicestror/syjsHEADF.do?user=OSCAR&limit=50&csv=true
	 * 
	 * @Example SELECT
	 *          specific: http://gw.systema.no:8080/syjservicestror/syjsHEADF.do?user=OSCAR&heavd=1&heopd=100
	 * 
	 */
	@RequestMapping(value="syjsHEADF.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsHEADF(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<HeadfDao> jsonWriter = new JsonResponseWriter2<HeadfDao>();
		CSVOutputter<HeadfDao> csvOutputter = new CSVOutputter<HeadfDao>();
		StringBuffer sb = new StringBuffer();
		List<HeadfDao> headfDaoList = new ArrayList<HeadfDao>();
		String heavd = request.getParameter("heavd");
		String heopd = request.getParameter("heopd");
		
		try {
			logger.info("Inside syjsHEADF.do");		
			String user = request.getParameter("user");
			String csv = request.getParameter("csv");
			String limit = request.getParameter("limit");
			
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				if (StringUtils.hasValue(heavd) && StringUtils.hasValue(heopd)) {
					HeadfDao dao = fetchRecord(heavd, heopd);
					headfDaoList.add(dao);
				} else {
					if(StringUtils.hasValue(limit)){
						//discrete number of rows
						headfDaoList = headfDaoService.findAll(null, limit);
					}else{
						headfDaoList = headfDaoService.findAll(null);
					}
				}
				if (headfDaoList != null) {
					if (StringUtils.hasValue(csv)) {
						sb.append(csvOutputter.writeAsString(headfDaoList));
					} else {
						sb.append(jsonWriter.setJsonResult_Common_GetList(userName, headfDaoList));
					}
				} else {
					errMsg = "ERROR on SELECT: Can not find HeadfDao list";
					status = "error";
					logger.info( status + errMsg);
					sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
				}

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

	private HeadfDao fetchRecord(String heavd, String heopd) {
		int avd = Integer.parseInt(heavd);
		int opd = Integer.parseInt(heopd);
		HeadfDao dao = headfDaoService.find(avd, opd);
		return dao;
	}

	/**
	 * File: 	HEADF
	 * 
	 * @Example SELECT http://gw.systema.no:8080/syjservicestror/syjsHEADF_LITE.do?user=OSCAR&dftdg=5&heavd=2&heopd=100&hedtop=20170807&henas=Kalle&henak=knattarna
	 * 
	 */
	@RequestMapping(value="syjsHEADF_LITE.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsHEADF_LITE(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<HeadfDto> jsonWriter = new JsonResponseWriter2<HeadfDto>();
		StringBuffer sb = new StringBuffer();
		List<HeadfDto> headfDtoList = null;
		
		try {
			logger.info("Inside syjsHEADF_LITE.do");		
			String user = request.getParameter("user");
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();
			if (StringUtils.hasValue(userName)) {
				HeadfDto qDto = null;
	            qDto = getDto(request);
	            headfDtoList = headfDaoService.get(qDto);
	            if (headfDtoList != null) {
					sb.append(jsonWriter.setJsonResult_Common_GetList(userName, headfDtoList));
				} else {
					errMsg = "ERROR on SELECT: Can not find HeadfDto list";
					status = "error";
					logger.info( status + errMsg);
					sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
				}
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
	 * Update Database DML operations File: HEADF
	 * 
	 * @Example UPDATE:
	 * 			http://gw.systema.no:8080/syjservicestror/syjsHEADF_U.do?user=OSCAR&heavd=2&heopd=100&hedtop=20170807&henas=Kalle&henak=knattarna....and all the rest...&mode=U/A/D
	 *
	 */
	@RequestMapping(value = "syjsHEADF_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsHEADF_U(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<HeadfDao> jsonWriter = new JsonResponseWriter2<HeadfDao>();
		StringBuffer sb = new StringBuffer();
		String userName = null;
		String errMsg = null;
		String status = null;
		StringBuffer dbErrorStackTrace = null;

		try {
			logger.info("Inside syjsHEADF_U.do");
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			// Check ALWAYS user in BRIDF
			userName = bridfDaoService.getUserName(user); 
			errMsg = "";
			status = "ok";
			dbErrorStackTrace = new StringBuffer();
			HeadfDao dao = new HeadfDao();
			HeadfDao resultDao = new HeadfDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);
			
			//NOTE: No rulerLord, data i validated in client

			if (userName != null && !"".equals(userName)) {
				if ("D".equals(mode)) {
					headfDaoService.delete(dao);
				} else if ("A".equals(mode)) {
					resultDao = headfDaoService.create(dao);
				} else if ("U".equals(mode)) {
					resultDao = headfDaoService.update(dao);
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
	/**
	 * Update of columnd HEST (STATUS) in HEADF. LIGHT update with old technique
	 * @param session
	 * @param request
	 * @return
	 * 
	 * http://gw.systema.no:8080/syjservicestror/syjsHEADF_STATUS_U.do?user=OSCAR&mode=U&heavd=1&heopd=184&hest=S
	 */
	@RequestMapping(value="syjsHEADF_STATUS_U.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsR_U( HttpSession session, HttpServletRequest request) {
		JsonResponseWriter jsonWriter = new JsonResponseWriter();
		StringBuffer sb = new StringBuffer();
		
		try{
			logger.info("Inside syjsHEADF_STATUS_U.do");
			//TEST-->logger.info("Servlet root:" + AppConstants.VERSION_SYJSERVICES);
			String user = request.getParameter("user");
			String mode = request.getParameter("mode");
			//Check ALWAYS user in BRIDF
            String userName = this.bridfDaoService.getUserName(user);
            //DEBUG --> logger.info("USERNAME:" + userName + "XX");
            String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();
			
			//bind attributes is any
			HeadfStatusDao dao = new HeadfStatusDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
            binder.bind(request);
            //rules
            //XXXX rulerLord = new XXXX();
			//Start processing now
			if(userName!=null && !"".equals(userName)){
				int dmlRetval = 0;
				if("D".equals(mode)){
					logger.info("DELETE ... NOT IMPLEMENTED");
					
				}else{
					//do ADD
					if("A".equals(mode)){
						logger.info("INSERT ... NOT IMPLEMENTED");
						
					}else if("U".equals(mode)){
						logger.info("Before UPDATE ...");
						dmlRetval = this.headfStatusDaoServices.update(dao, dbErrorStackTrace);
					}
				}
				//----------------------------------
				//check returns from dml operations
				//----------------------------------
				if(dmlRetval<0){
					//write JSON error output
					errMsg = "ERROR on UPDATE: invalid?  Try to check: <DaoServices>.insert/update/delete";
					status = "error";
					sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
				}else{
					//OK UPDATE
					sb.append(jsonWriter.setJsonSimpleValidResult(userName, status));
				}
				
			}else{
				//write JSON error output
				errMsg = "ERROR on UPDATE";
				status = "error";
				dbErrorStackTrace.append("request input parameters are invalid: <user>, <other mandatory fields>");
				sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
			}
			
		}catch(Exception e){
			//write std.output error output
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
	 * @param request
	 * @return
	 */
	private HeadfDto getDto(HttpServletRequest request) {
		String WILD_CARD = "%";
		HeadfDto qDto = new HeadfDto();
		ServletRequestDataBinder binder = new ServletRequestDataBinder(qDto);
        binder.bind(request);

        if (qDto.getHenas() != null)  {
        	qDto.setHenas(WILD_CARD+qDto.getHenas()+WILD_CARD);
        }
        if (qDto.getHenak() != null) {
        	qDto.setHenak(WILD_CARD+qDto.getHenak()+WILD_CARD); 
        }
        if (qDto.getHesg() != null) {
        	qDto.setHesg(WILD_CARD+qDto.getHesg()+WILD_CARD); 
        }
        if (qDto.getHegn() != null) {
        	qDto.setHegn(WILD_CARD+qDto.getHegn()+WILD_CARD); 
        }
        if (qDto.getDftdg() == 0) {
        	qDto.setDftdg(Integer.valueOf(DAYS_TO_VIEW_DEFAULT)); 
        }
        
        return qDto;
        
	}

	@Qualifier ("bridfDaoService")
	private BridfDaoService bridfDaoService;
	@Autowired
	@Required
	public void setBridfDaoService (BridfDaoService value){ this.bridfDaoService = value; }
	public BridfDaoService getBridfDaoService(){ return this.bridfDaoService; }	

	@Qualifier ("headfDaoService")
	private HeadfDaoService headfDaoService;
	@Autowired
	@Required
	public void setHeadfDaoService(HeadfDaoService value){ this.headfDaoService = value; }
	public HeadfDaoService getHeadfDaoService(){ return this.headfDaoService; }		
	
	
	@Qualifier ("headfStatusDaoServices")
	private HeadfStatusDaoServices headfStatusDaoServices;
	@Autowired
	@Required
	public void setHeadfStatusDaoServices(HeadfStatusDaoServices value){ this.headfStatusDaoServices = value; }
	public HeadfStatusDaoServices getHeadfStatusDaoServices(){ return this.headfStatusDaoServices; }		
	
	
}
