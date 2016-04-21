package info.rmapproject.transformer.share.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author khanson
 *
 */
public class SHARERecord {

	URI canonicalUri;
	List<URI> providerUris;
	List<URI> descriptorUris;
	List<URI> objectUris;

	String title;
	String description;
	List <SHAREAgent> contributors;
	SHAREAgent publisher;
	List<URI> languages;
	List<SHARESponsorship> sponsorships;
	
	String versionId;
	Date versionDateTime;
	URI versionOf;
	
	/* * * * * * * * * * * * * * * * * * * * 
	* These are excluded because they won't be part of DiSCO, but documenting their existence for 
	* the record!
	* 
	* List<SHAREShareProperty> shareProperties
	* List<SHAREOtherProperty> otherProperties
	* List<SHARETag> tags
	* Date providerUpdatedDateTime
	* Date freeToReadStartDate
	* Date freeToReadEndDate
	* List<SHARELicense> licenses
	* List<String> subjects
	*/
	
	public SHARERecord(){}
	
	public URI getCanonicalUri() {
		return canonicalUri;
	}
	
	public void setCanonicalUri(URI canonicalUri) throws RuntimeException {
		if (canonicalUri==null){
			throw new RuntimeException("Value for canonicalUri cannot be null. Record cannot be created");
		}
		this.canonicalUri = canonicalUri;
	}
	
	public List<URI> getProviderUris() {
		return providerUris;
	}
	
	public void setProviderUris(List<URI> providerUris) {
		this.providerUris = providerUris;
	}
	public List<URI> getDescriptorUris() {
		return descriptorUris;
	}
	public void setDescriptorUris(List<URI> descriptorUris) {
		this.descriptorUris = descriptorUris;
	}
	public List<URI> getObjectUris() {
		return objectUris;
	}
	public void setObjectUris(List<URI> objectUris) {
		this.objectUris = objectUris;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<SHAREAgent> getContributors() {
		return contributors;
	}
	public void setContributors(List<SHAREAgent> contributors) {
		this.contributors = contributors;
	}

	public void addContributors(SHAREAgent contributor) throws Exception{
		if (contributor==null){
			throw new Exception("A null contributor was passed in");
		}
		if (this.contributors==null){
			this.contributors = new ArrayList<SHAREAgent>();
		}
		this.contributors.add(contributor);
	}
	
	public SHAREAgent getPublisher() {
		return publisher;
	}
	public void setPublisher(SHAREAgent publisher) {
		this.publisher = publisher;
	}
	public List<URI> getLanguages() {
		return languages;
	}
	public void setLanguages(List<URI> languages) {
		this.languages = languages;
	}
	public List<SHARESponsorship> getSponsorships() {
		return sponsorships;
	}
	public void setSponsorships(List<SHARESponsorship> sponsorships) {
		this.sponsorships = sponsorships;
	}
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	public Date getVersionDateTime() {
		return versionDateTime;
	}
	public void setVersionDateTime(Date versionDateTime) {
		this.versionDateTime = versionDateTime;
	}
	public URI getVersionOf() {
		return versionOf;
	}
	public void setVersionOf(URI versionOf) {
		this.versionOf = versionOf;
	}
		
	
	
}
