package info.rmapproject.transformer.share.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO representing a SHARE agent - a person or organization that is a publisher, contributor or affiliation.
 * Note that this isn't split into subtypes person/org because it isn't always clear which a thing is.
 * Looking at the SHARE data, many Org names have been split to populate familyName etc. 
 * The only way to be close to sure of the agent type is if it's either side of an affiliation relationship 
 * (person affiliated with org), or if it's a publisher (org).  Those are the criteria by which a type is assigned.  
 * Otherwise type is null.
 * @author khanson
 *
 */
public class SHAREAgent {
	
	//organization or person properties
	private List<URI> sameAs;
	private String name;
	private String email;
	private SHAREAgentType type;
	
	//person only properties - if any of these are present it is probably a person unless there is a data error!
	private String familyName;
	private String givenName;
	private String additionalName;
	private List<SHAREAgent> affiliations;
	
	public SHAREAgent(){}
	
	public List<URI> getSameAs() {
		return sameAs;
	}
	public void setSameAs(List<URI> sameAs) {
		this.sameAs = sameAs;
	}
	
	public void addSameAs(String sameAs) throws Exception {
		if (sameAs!=null){
			if (this.sameAs==null) {
				this.sameAs=new ArrayList<URI>();
			}
			try {
				this.sameAs.add(new URI(sameAs));
			} catch (URISyntaxException e) {
				throw new Exception("Could not convert Agent sameAs to URI");
			}			
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getAdditionalName() {
		return additionalName;
	}

	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;
	}

	public List<SHAREAgent> getAffiliations() {
		return affiliations;
	}

	public void setAffiliations(List<SHAREAgent> affiliations) {
		this.affiliations = affiliations;
	}
	
	public void addAffilitation(SHAREAgent affiliation){
		if (affiliation!=null){
			if (this.affiliations==null) {
				this.affiliations=new ArrayList<SHAREAgent>();
			}
			this.affiliations.add(affiliation);			
		}
	}	

	public SHAREAgentType getType() {
		return type;
	}

	public void setType(SHAREAgentType type) {
		this.type = type;
	}	
	
}
