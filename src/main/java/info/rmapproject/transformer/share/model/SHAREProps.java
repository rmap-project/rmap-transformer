package info.rmapproject.transformer.share.model;

public enum SHAREProps {
	TITLE("title"),
	DESCRIPTION("description"),
	CONTRIBUTORS("contributors"),
	PUBLISHER("publisher"),
	SAMEAS("sameAs"),
	URIS("uris"),
	CANONICALURI("canonicalUri"),
	PROVIDERURIS("providerUris"),
	OBJECTURIS("objectUris"),
	DESCRIPTORURIS("descriptorUris"),
	LANGUAGES("languages"),
	NAME("name"),
	EMAIL("email"),
	AFFILIATION("affiliation"),
	GIVENNAME("givenName"),
	FAMILYNAME("familyName"),
	ADDITIONALNAME("additionalName"),
	SPONSORSHIPS("sponsorships"),
	SPONSOR("sponsor"),
	AWARD("award"),
	SPONSORNAME("sponsorName"),
	SPONSORID("sponsorIdentifier"),
	AWARDNAME("awardName"),
	AWARDID("awardIdentifier"),
	VERSION("version"),
	VERSIONID("versionId"),
	VERSIONDATETIME("versionDateTime"),
	VERSIONOF("versionOf"),
	OTHERPROPERTIES("otherProperties"),
	PROPNAME("name"),
	PROPERTIES("properties"),
	DOI("doi"),
	FORMAT("format"),
	IDENTIFIER("identifier"),
	ISBN("isbn"),
	ISBN_UC("ISBN"),
	PRINTISBN("printIsbn"),
	ELECTRONICISBN("electronicIsbn"),
	ISSN("issn"),
	ISSN_UC("ISSN"),
	EISSN("eissn"),
	LINK("link"),
	LINKS("links"),
	PARENT_URL("parent_url"),
	RELATION("relation"),
	TYPE("type");

	private String str;
	
	private SHAREProps(String str){
		this.str = str;
	}	
	
	public String getValue(){
		return this.str;
	}
	
}
