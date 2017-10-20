package no.systema.jservices.model.dao.entities;
import java.io.Serializable;

/**
 * 
 * @author oscardelatorre
 * @date Okt 20, 2017
 * 
 */
public class HeadfStatusDao implements Serializable {
	
	private String heavd = null;                                
	public void setHeavd (String value){ this.heavd = value;   }   
	public String getHeavd (){ return this.heavd;   }              

	private String heopd = null;                                
	public void setHeopd (String value){ this.heopd = value;   }   
	public String getHeopd (){ return this.heopd;   }              

	private String hest = null;                                
	public void setHest (String value){ this.hest = value;   }   
	public String getHest (){ return this.hest;   }              
        

}
