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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import no.systema.jservices.common.dao.Ffr03fDao;

import no.systema.jservices.common.dao.modelmapper.converter.DaoConverter;

import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.CnffDaoService;
import no.systema.jservices.common.dao.services.Ffr03fDaoService;
import no.systema.jservices.common.dto.HeadfDto;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.json.JsonResponseWriter;
import no.systema.jservices.common.util.ApplicationPropertiesUtil;
import no.systema.jservices.common.util.CSVOutputter;
import no.systema.jservices.common.util.StringUtils;

@Controller
public class TrorResponseOutputterController_FFR03F {
	private static final Logger logger = Logger.getLogger(TrorResponseOutputterController_FFR03F.class.getName());
	private ModelMapper modelMapper = new ModelMapper();
	private DaoConverter daoConverter = new DaoConverter();
	
	/**
	 * File: 	FFR03F - Flyfraktbrev export - Tradevision child table
	 * 
	 * @Example SELECT list http://localhost:8080/syjservicestror/syjsFFR03F.do?user=OSCAR...
	 * 
	 * 
	 * 
	 */
	
	@RequestMapping(value="syjsFFR03F.do", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String syjsFFR00F(HttpSession session, HttpServletRequest request) {
		JsonResponseWriter2<Ffr03fDao> jsonWriter = new JsonResponseWriter2<Ffr03fDao>();
		StringBuffer sb = new StringBuffer();
		List<Ffr03fDao> ffr03fDaoList = new ArrayList<Ffr03fDao>();
		String user = request.getParameter("user");
		String all = request.getParameter("all");
		
		try {
			logger.info("Inside syjsFFR03F.do");		
			// Check ALWAYS user in BRIDF
			String userName = bridfDaoService.getUserName(user); 
			String errMsg = "";
			String status = "ok";
			StringBuffer dbErrorStackTrace = new StringBuffer();

			if (StringUtils.hasValue(userName)) {
				Ffr03fDao dao = new Ffr03fDao();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
				binder.bind(request);
				//alternatives
				if(StringUtils.hasValue(all)){
					ffr03fDaoList = ffr03fDaoService.findAll(null);
				}else if(dao.getF03rec()>=0){
					ffr03fDaoList = ffr03fDaoService.findAll(dao.getKeys());
				}
				sb.append(jsonWriter.setJsonResult_Common_GetList(userName, ffr03fDaoList));
				
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
	

	
	
	@Qualifier ("bridfDaoService")
	private BridfDaoService bridfDaoService;
	@Autowired
	@Required
	public void setBridfDaoService (BridfDaoService value){ this.bridfDaoService = value; }
	public BridfDaoService getBridfDaoService(){ return this.bridfDaoService; }	

	
	@Qualifier ("ffr03fDaoService")
	private Ffr03fDaoService ffr03fDaoService;
	@Autowired
	@Required
	public void setFfr03fDaoService(Ffr03fDaoService value){ this.ffr03fDaoService = value; }
	public Ffr03fDaoService getFfr03fDaoService(){ return this.ffr03fDaoService; }		
	
	
	
	
}
