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
package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.cos.share.client.service.ShareApiIterator;
import info.rmapproject.transformer.TransformUtils;
import info.rmapproject.transformer.model.RecordDTO;
import info.rmapproject.transformer.model.RecordType;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Iterates over SHARE API data - can next() over records.
 * Retrieve JSON records from API using path and params provided.
 * @author khanson
 *
 */
public class ShareApiTransformIterator implements Iterator<RecordDTO>{
        		
    /** The SHARE API iterator. */
    private ShareApiIterator shareApiIterator = null;

    /**
     * Initiate iterator using filters provided.
     *
     * @param filters the filters
     */
	public ShareApiTransformIterator(String filters){
		HashMap<String,String> params=null;
		try{
			params = TransformUtils.readParamsIntoMap(filters, "UTF-8");
    		shareApiIterator = new ShareApiIterator(params);
		} catch(URISyntaxException e){
			throw new IllegalArgumentException("URL invalid, parameters could not be parsed");
		} catch (Exception e){
			throw new RuntimeException("could not initiate SHARE Api Iterator", e);    		
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public RecordDTO next() {
		RecordDTO shareDTO = null;
		try {
			Record sharerec = shareApiIterator.next();
			String id = sharerec.getShareProperties().getDocID();
			String source = sharerec.getShareProperties().getSource();
			if (source!=null && source.length()>0){
				id = source + "_" + id;
			}
			shareDTO = new RecordDTO(sharerec, id, RecordType.SHARE);
		} catch (Exception ex) {
			throw new RuntimeException("Could not generate SHARE record", ex);
		}
		return shareDTO;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return shareApiIterator.hasNext();
	}
			 
}
