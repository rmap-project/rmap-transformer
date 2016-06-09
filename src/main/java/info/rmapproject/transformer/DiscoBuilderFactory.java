package info.rmapproject.transformer;

import info.rmapproject.transformer.model.RecordType;
import info.rmapproject.transformer.osf.OsfNodeDiscoBuilder;
import info.rmapproject.transformer.osf.OsfRegistrationDiscoBuilder;
import info.rmapproject.transformer.osf.OsfUserDiscoBuilder;
import info.rmapproject.transformer.share.ShareDiscoBuilder;

public class DiscoBuilderFactory {
	public static DiscoBuilder createDiscoBuilder(RecordType type, String discoDescription) {
			
			DiscoBuilder model = null;
						
			switch(type){
			case SHARE:
				model = new ShareDiscoBuilder(discoDescription);
				break;
			case OSF_REGISTRATION:
				model = new OsfRegistrationDiscoBuilder(discoDescription);
				break;
			case OSF_USER:
				model = new OsfUserDiscoBuilder(discoDescription);
				break;
			case OSF_NODE:
				model = new OsfNodeDiscoBuilder(discoDescription);
				break;
			default:
				model = new ShareDiscoBuilder(discoDescription);
				break;		
			}
			
			return model;
	}
	
	public static DiscoBuilder createDiscoBuilder(RecordType type) {
		
		DiscoBuilder model = null;
					
		switch(type){
		case SHARE:
			model = new ShareDiscoBuilder();
			break;
		case OSF_REGISTRATION:
			model = new OsfRegistrationDiscoBuilder();
			break;
		case OSF_USER:
			model = new OsfUserDiscoBuilder();
			break;
		case OSF_NODE:
			model = new OsfNodeDiscoBuilder();
			break;
		default:
			model = new ShareDiscoBuilder();
			break;		
		}
		
		return model;
	}
	
}
