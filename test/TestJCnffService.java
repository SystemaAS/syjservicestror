import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import no.systema.jservices.common.dao.Ffr00fDao;
import no.systema.jservices.common.dao.services.CnffDaoService;

import java.util.*;

public class TestJCnffService {
	
	CnffDaoService service = null;
	StringBuffer errorStackTrace = new StringBuffer();
	ApplicationContext context = null;
	String prefix = "177"; //77 - 117
	String awb = "81140743"; //97957440 - 81140743
	
	
	
	@Before
	public void setUp() throws Exception {
		context = new ClassPathXmlApplicationContext("classpath:syjservicestror-data-service-mod.xml");
		service = (CnffDaoService) context.getBean("cnffDaoService");
		
	}

	@Test
	public void findRecord() {
		int result = service.getCnrecnAfterIncrement();
		assertTrue(result>0);
		System.out.println(result);
	}

}
