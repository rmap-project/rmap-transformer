/*******************************************************************************
 * Copyright 2016 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This software was produced as part of the RMap Project (http://rmap-project.info),
 * The RMap Project was funded by the Alfred P. Sloan Foundation and is a 
 * collaboration between Data Conservancy, Portico, and IEEE.
 *******************************************************************************/
package info.rmapproject.transformer;

import info.rmapproject.transformer.model.RecordType;
import info.rmapproject.transformer.osf.OsfNodeDiscoBuilder;
import info.rmapproject.transformer.osf.OsfRegistrationDiscoBuilder;
import info.rmapproject.transformer.osf.OsfUserDiscoBuilder;
import info.rmapproject.transformer.share.ShareDiscoBuilder;

/**
 * A factory for creating DiscoBuilder objects.
 * @author khanson
 */
public class DiscoBuilderFactory {
	
	/**
	 * Creates a new DiscoBuilder object based on type requested.
	 *
	 * @param type the source data type
	 * @param discoDescription the DiSCO description
	 * @return the new DiSCO builder
	 */
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
	
	/**
	 * Creates a new DiscoBuilder object.
	 *
	 * @param type the source data type
	 * @return the DiSCO builder
	 */
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
