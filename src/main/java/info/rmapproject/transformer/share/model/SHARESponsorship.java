package info.rmapproject.transformer.share.model;

import java.net.URI;

public class SHARESponsorship {

	URI sponsorIdentifier;
	String sponsorName;
	URI awardIdentifier;
	String awardName;
	
	public URI getSponsorIdentifier() {
		return sponsorIdentifier;
	}
	public void setSponsorIdentifier(URI sponsorIdentifier) {
		this.sponsorIdentifier = sponsorIdentifier;
	}
	public String getSponsorName() {
		return sponsorName;
	}
	public void setSponsorName(String sponsorName) {
		this.sponsorName = sponsorName;
	}
	public URI getAwardIdentifier() {
		return awardIdentifier;
	}
	public void setAwardIdentifier(URI awardIdentifier) {
		this.awardIdentifier = awardIdentifier;
	}
	public String getAwardName() {
		return awardName;
	}
	public void setAwardName(String awardName) {
		this.awardName = awardName;
	}
	
	
	
}
