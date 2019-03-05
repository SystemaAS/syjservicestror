import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import no.systema.jservices.common.dao.Ffr00fDao;
import no.systema.jservices.common.dao.Ffr03fDao;

import no.systema.jservices.common.dao.facade.Ffr00fDaoFacade;
import no.systema.jservices.common.dto.Ffr00fDto;
import no.systema.jservices.common.dao.services.Ffr00fDaoService;
import no.systema.jservices.common.dao.services.CnffDaoService;
import org.modelmapper.ModelMapper;
import no.systema.jservices.common.dao.modelmapper.converter.DaoConverter;

import java.util.*;

public class TestJFfr00fService {
	
	Ffr00fDaoService service = null;
	CnffDaoService cnffDaoService = null;
	StringBuffer errorStackTrace = new StringBuffer();
	ApplicationContext context = null;
	String prefix = "177"; //77 - 117
	String awb = "81140743"; //97957440 - 81140743
	//
	//private ModelMapper modelMapper = new ModelMapper();
	//private DaoConverter daoConverter = new DaoConverter();
	
	
	@Before
	public void setUp() throws Exception {
		context = new ClassPathXmlApplicationContext("classpath:syjservicestror-data-service-mod.xml");
		service = (Ffr00fDaoService) context.getBean("ffr00fDaoService");
		cnffDaoService = (CnffDaoService) context.getBean("cnffDaoService");
	
	}

	/*@Test
	public void findRecord() {
		int _211 = Integer.valueOf(prefix);
		int _213 = Integer.valueOf(awb);
		Ffr00fDao dao = new Ffr00fDao();
		dao.setF0211(_211);
		dao.setF0213(_213);
		//List result = service.findAll(dao.getKeysAwb());
		List result = service.findAll(null);
		//assertTrue(result!=null);
		assertTrue(result.size()>0);
		System.out.println(result.toString());
	}
	
	
	@Test
	public void create() {
	
		int _211 = Integer.valueOf(prefix);
		int _213 = Integer.valueOf(awb);
		Ffr00fDto dto = new Ffr00fDto();
		dto.setF0211(String.valueOf(_211));
		dto.setF0213(String.valueOf(_213));
		dto.setF00rec( (String.valueOf(cnffDaoService.getCnrecnAfterIncrement())) );
		//List result = service.findAll(dto.getKeysAwb());
		Ffr00fDao resultDao = service.create(dto, modelMapper, daoConverter);
		//assertTrue(result!=null);
		assertTrue(resultDao!=null);
		System.out.println(resultDao.toString());
		
		Ffr00fDto dto = new Ffr00fDto();
		dto.setF0211(prefix);
		dto.setF0213(awb);
		dto.setF00rec( (String.valueOf(cnffDaoService.getCnrecnAfterIncrement())) );
		//Facade for ModelMapper
		Ffr00fDaoFacade facade = new Ffr00fDaoFacade(dto);
		facade.setFfr00fDao((Ffr00fDao)facade.getDao(Ffr00fDao.class));
		facade.setFfr03fDao((Ffr03fDao)facade.getDao(Ffr03fDao.class));
		//
		Ffr00fDao resultDao = service.create(dto, facade);
		assertTrue(resultDao!=null);
		System.out.println(resultDao.toString());
	}
	
	@Test
	public void update() {
		String f00rec = "162220";
		//
		int _f00 = Integer.valueOf(f00rec);
		Ffr00fDao dto = new Ffr00fDao();
		dto.setF00rec(_f00);
		//List result = service.findAll(dto.getKeysAwb());
		Ffr00fDao target = service.find(dto);
		target.setF0221("OSL");
		//List result = service.findAll(dto.getKeysAwb());
		Ffr00fDao resultDao = service.update(target, modelMapper, daoConverter);
		//assertTrue(result!=null);
		assertTrue(resultDao!=null);
		System.out.println(resultDao.toString());
	}*/
	
	/*
	@Test
	public void delete() {
		String f00rec = "TODO";
		int _f00 = Integer.valueOf(f00rec);
		Ffr00fDto dto = new Ffr00fDto();
		dto.setF00rec(f00rec);
		//1-delete
		service.delete(dto);
		
		//2-check if not-exists
		Ffr00fDao dao = new Ffr00fDao();
		dao.setF00rec(_f00);
		dao = service.find(dao);
		assertTrue(dao==null);
		System.out.println("OK delete");
	}
	*/
	

}
