import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import no.systema.jservices.common.dao.Ffr00fDao;
import no.systema.jservices.common.dao.services.Ffr00fDaoService;

import java.util.*;

public class TestJFfr00fService {
	
	Ffr00fDaoService service = null;
	StringBuffer errorStackTrace = new StringBuffer();
	ApplicationContext context = null;
	String prefix = "177"; //77 - 117
	String awb = "81140743"; //97957440 - 81140743
	
	
	
	@Before
	public void setUp() throws Exception {
		context = new ClassPathXmlApplicationContext("classpath:syjservicestror-data-service-mod.xml");
		service = (Ffr00fDaoService) context.getBean("ffr00fDaoService");
		
	}

	@Test
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

}
