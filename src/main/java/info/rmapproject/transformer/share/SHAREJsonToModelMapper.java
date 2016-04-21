package info.rmapproject.transformer.share;

import static info.rmapproject.transformer.share.model.SHAREProps.ADDITIONALNAME;
import static info.rmapproject.transformer.share.model.SHAREProps.AFFILIATION;
import static info.rmapproject.transformer.share.model.SHAREProps.CANONICALURI;
import static info.rmapproject.transformer.share.model.SHAREProps.CONTRIBUTORS;
import static info.rmapproject.transformer.share.model.SHAREProps.DESCRIPTION;
import static info.rmapproject.transformer.share.model.SHAREProps.DESCRIPTORURIS;
import static info.rmapproject.transformer.share.model.SHAREProps.EMAIL;
import static info.rmapproject.transformer.share.model.SHAREProps.FAMILYNAME;
import static info.rmapproject.transformer.share.model.SHAREProps.GIVENNAME;
import static info.rmapproject.transformer.share.model.SHAREProps.LANGUAGES;
import static info.rmapproject.transformer.share.model.SHAREProps.NAME;
import static info.rmapproject.transformer.share.model.SHAREProps.OBJECTURIS;
import static info.rmapproject.transformer.share.model.SHAREProps.PROVIDERURIS;
import static info.rmapproject.transformer.share.model.SHAREProps.PUBLISHER;
import static info.rmapproject.transformer.share.model.SHAREProps.SAMEAS;
import static info.rmapproject.transformer.share.model.SHAREProps.TITLE;
import static info.rmapproject.transformer.share.model.SHAREProps.URIS;
import info.rmapproject.transformer.share.model.SHAREAgent;
import info.rmapproject.transformer.share.model.SHAREAgentType;
import info.rmapproject.transformer.share.model.SHARERecord;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Maps raw json text to SHARERecord model 
 * @author khanson
 *
 */
public class SHAREJsonToModelMapper {

	private final static String LANG_URL_PREFIX = "http://www.lexvo.org/page/iso639-3/";

	public SHARERecord toModel(String record) throws Exception {
		
		SHARERecord sharerec = new SHARERecord();
		//transform JSON to java obj
		JSONObject root = new JSONObject(record);
		
		if (root.isNull(URIS.getValue())){
			throw new RuntimeException("JSON does not contain a URI. A URI is required.");
		}
		JSONObject uris = root.getJSONObject(URIS.getValue());
		List<URI> canonicalUris = extractUris(uris, CANONICALURI.getValue());
		sharerec.setCanonicalUri(canonicalUris.get(0)); //there is only 1
		
		if (!uris.isNull(PROVIDERURIS.getValue())){
			List<URI> providerUris = extractUris(uris, PROVIDERURIS.getValue());
			sharerec.setProviderUris(providerUris);
		}
		
		if (!uris.isNull(OBJECTURIS.getValue())){
			List<URI> objectUris = extractUris(uris, OBJECTURIS.getValue());
			sharerec.setObjectUris(objectUris);
		}
		
		if (!uris.isNull(DESCRIPTORURIS.getValue())){
			List<URI> descriptorUris = extractUris(uris, DESCRIPTORURIS.getValue());
			sharerec.setObjectUris(descriptorUris);
		}
		
		if (!root.isNull(TITLE.getValue())){
			sharerec.setTitle(root.getString(TITLE.getValue()));
		}
		
		if (!root.isNull(DESCRIPTION.getValue())){
			sharerec.setDescription(root.getString(DESCRIPTION.getValue()));
		}
		
		if (!root.isNull(CONTRIBUTORS.getValue())) {
			List<SHAREAgent> contributors = extractContributors(root.getJSONArray(CONTRIBUTORS.getValue()));
			sharerec.setContributors(contributors);
		}

		if (!root.isNull(PUBLISHER.getValue())) {
			SHAREAgent publisher = extractAgent(root.getJSONObject(PUBLISHER.getValue()), SHAREAgentType.ORGANIZATION);
			sharerec.setPublisher(publisher);
		}
		
		if (!root.isNull(LANGUAGES.getValue())){
			List<URI> languages = extractLanguages(root.getJSONArray(LANGUAGES.getValue()));
			sharerec.setLanguages(languages);
		}
			
		return sharerec;
	}

	private List<SHAREAgent> extractContributors(JSONArray contribset) throws Exception {
	
		List<SHAREAgent> contributors = new ArrayList<SHAREAgent>();
	
		for (Object contributor:contribset){
			JSONObject jsonAgent = (JSONObject) contributor;
			
			SHAREAgent agent = extractAgent(jsonAgent, null);
			
			if (agent!=null){
				contributors.add(agent);
			}				
		}
		
		return contributors;
	}
	
	private SHAREAgent extractAgent(JSONObject jsonAgent, SHAREAgentType type) throws Exception {
						
		//create SHAREAgent
		SHAREAgent agent = new SHAREAgent();

		if (!jsonAgent.isNull(NAME.getValue())){
			agent.setName(jsonAgent.getString(NAME.getValue()));
		}
		if (!jsonAgent.isNull(ADDITIONALNAME.getValue())){
			agent.setAdditionalName(jsonAgent.getString(ADDITIONALNAME.getValue()));
		}
		if (!jsonAgent.isNull(FAMILYNAME.getValue())){
			agent.setFamilyName(jsonAgent.getString(FAMILYNAME.getValue()));
		}
		if (!jsonAgent.isNull(GIVENNAME.getValue())){
			agent.setGivenName(jsonAgent.getString(GIVENNAME.getValue()));
		}
		if (!jsonAgent.isNull(EMAIL.getValue())){
			agent.setEmail(jsonAgent.getString(EMAIL.getValue()));
		}

		if (!jsonAgent.isNull(SAMEAS.getValue())){
			JSONArray sameAsList = jsonAgent.getJSONArray(SAMEAS.getValue());
			for (Object sameAs : sameAsList) {
				agent.addSameAs(sameAs.toString());
			}
		}
		
		if (!jsonAgent.isNull(AFFILIATION.getValue())){
			JSONArray affiliations = jsonAgent.getJSONArray(AFFILIATION.getValue());
			if (affiliations.length()>0){
				//only person will have affiliations
				agent.setType(SHAREAgentType.PERSON);
			}
			for (Object affiliation:affiliations){
				//only an org will be affiliation
				SHAREAgent affilAgent = extractAgent((JSONObject)affiliation, SHAREAgentType.ORGANIZATION);
				agent.addAffilitation(affilAgent);
			}
		}
						
		return agent;
	}
	
	private List<URI> extractUris(JSONObject uris, String uritype) throws URISyntaxException {
		List<URI> extractedUris = new ArrayList<URI>();
		
		if (!uris.isNull(uritype)) {
			Object object = uris.get(uritype);
			
			if (object instanceof JSONArray) {
				JSONArray uriSet = (JSONArray) object;
				for (Object uri : uriSet) {
					String suri = uri.toString();
					extractedUris.add(new URI(suri));
				}
			}	
			else {
				String uri = object.toString();
				extractedUris.add(new URI(uri));			
			}
		}
				
		return extractedUris;
	}	
	
	private List<URI> extractLanguages(JSONArray langJson) {
		List <URI> langlist = new ArrayList<URI>();
		String languri = null;

		try {
			for (Object lang : langJson){
				String sLang = lang.toString();
				if (sLang.length()==3){
					languri = LANG_URL_PREFIX + sLang;
					langlist.add(new URI(languri));
				}			
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException("Could not convert language string to URI: " + languri, e);
		}
		
		return langlist;		
	}
	

}
