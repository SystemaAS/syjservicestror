import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import no.systema.jservices.common.dao.DokefDao;
import no.systema.jservices.common.dao.services.DokefDaoService;

public class TestJDokefService {
	
	DokefDaoService service = null;
	StringBuffer errorStackTrace = new StringBuffer();
	ApplicationContext context = null;
	String dfopd = null;
	String dflop = null;
	String dfavd = null;
	
	@Before
	public void setUp() throws Exception {
		context = new ClassPathXmlApplicationContext("classpath:syjservicestror-data-service-mod.xml");
		service = (DokefDaoService) context.getBean("dokefDaoService");
		dfopd = "155603";
		//dfopd = "199999";
		dfavd = "1";
		dflop = "1";
	}

	@Test
	public void findRecord() {
		int avd = Integer.parseInt(dfavd);
		int opd = Integer.parseInt(dfopd);
		int fbnr = Integer.parseInt(dflop);
		DokefDao qDao = new DokefDao();
		qDao.setDfavd(avd);
		qDao.setDfopd(opd);
		qDao.setDflop(fbnr);
		DokefDao dao = service.find(qDao);
		
		assertTrue(dao !=null);
		
	}

}
