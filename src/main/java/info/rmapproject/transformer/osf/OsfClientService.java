/*******************************************************************************
 * Copyright 2017 Johns Hopkins University
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
package info.rmapproject.transformer.osf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dataconservancy.cos.osf.client.retrofit.RetrofitOsfServiceFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import info.rmapproject.cos.osf.client.model.Contributor;
import info.rmapproject.cos.osf.client.model.Identifier;
import info.rmapproject.cos.osf.client.model.Institution;
import info.rmapproject.cos.osf.client.model.LightNode;
import info.rmapproject.cos.osf.client.model.LightRegistration;
import info.rmapproject.cos.osf.client.model.LightUser;
import info.rmapproject.cos.osf.client.model.Node;
import info.rmapproject.cos.osf.client.model.Registration;
import info.rmapproject.cos.osf.client.model.User;
import info.rmapproject.cos.osf.client.retrofit.OsfService;
import retrofit.Call;
import retrofit.Response;

/**
 * This class interacts with the OSF client so that model objects are returned 
 * instead of retrofit calls.
 *
 * @author khanson
 */
public class OsfClientService {
	
	/** The osf service. */
	private OsfService osfService = null;
	
	/**
	 * Instantiates a new osf client service.
	 */
	public OsfClientService(){
		try {
	    	// Create object mapper
	        ObjectMapper objectMapper = new ObjectMapper();
	        OkHttpClient client = new OkHttpClient();

	        ResourceConverter converter = new ResourceConverter(objectMapper, LightNode.class, LightRegistration.class, Registration.class, Identifier.class,
	        													Contributor.class, User.class, LightUser.class, Institution.class, Node.class); 
	        converter.setGlobalResolver(relUrl -> {
	            System.err.println("Resolving " + relUrl);
	            com.squareup.okhttp.Call req = client.newCall(new Request.Builder().url(relUrl).build());
	            try {
	                byte[] bytes = req.execute().body().bytes();
	                return bytes;
	            } catch (IOException e) {
	                throw new RuntimeException(e.getMessage(), e);
	            }
	        });

	        RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory("classpath*:/osf-config.json");
	        osfService = factory.getOsfService(OsfService.class);
	        	
		} catch (Exception e){
			throw new RuntimeException("Could not start OSF Service", e);
		}
	}
	
	/**
	 * Get single OSF registration by passing in single ID e.g. "cgur9"
	 *
	 * @param id the id of the registration
	 * @return the registration
	 */
	public Registration getRegistration(String id){	
	
        Call<Registration> listCall = osfService.getRegistrationById(id);
        Response<Registration> res;
		try {
			res = listCall.execute();
		} catch (Exception e) {
			throw new RuntimeException("cannot retrieve registration with ID " + id, e);
		}
       
        Registration registration = null;
        if (res.isSuccess()) {
        	registration = res.body();
        } else {
			throw new RuntimeException("Cannot retrieve registration with ID " + id + "; Response code:" + res.code() + " ; Url: " + res.raw().request().urlString());
        }
        return registration;
	}
	
	/**
	 * Get list of registration Ids filtered by parameters provided.
	 *
	 * @param params the params for the API
	 * @return a list of Registration IDs list
	 */
	public List<LightRegistration> getRegistrationIds(Map<String,String> params){	

        Call<List<LightRegistration>> listCall = osfService.getRegistrationIds(params);
        Response<List<LightRegistration>> res;
		try {
			res = listCall.execute();
		} catch (Exception e) {
			throw new RuntimeException("cannot retrieve Registration ID list", e);
		}

		List<LightRegistration> registrations = null;
        if (res.isSuccess()) {
        	registrations = res.body();
        } else {
			throw new RuntimeException("Cannot retrieve registration with IDs; Response code:" + res.code() + " ; Url: " + res.raw().request().urlString());
        }
        
        return registrations;
	}
	

	/**
	 * Gets a single Node using an ID.
	 *
	 * @param id the Node id
	 * @return the Node
	 */
	public Node getNode(String id){	
		
        Call<Node> listCall = osfService.getNodeById(id);
        Response<Node> res;
		try {
			res = listCall.execute();
		} catch (Exception e) {
			throw new RuntimeException("cannot retrieve node with ID " + id, e);
		}
       
        Node node = null;
        if (res.isSuccess()) {
        	node = res.body();
        } else {
    		throw new RuntimeException("Cannot retrieve registration with ID " + id + "; Response code:" + res.code() + " ; Url: " + res.raw().request().urlString());
        }
        return node;
	}
	
	/**
	 * Gets the Node ID list.
	 *
	 * @param params the params to filter the node list by
	 * @return the Node ID list
	 */
	public List<LightNode> getNodeIds(Map<String,String> params){	

        Call<List<LightNode>> listCall = osfService.getNodeIds(params);
        Response<List<LightNode>> res;
		try {
			res = listCall.execute();
		} catch (Exception e) {
			throw new RuntimeException("cannot retrieve LightNode list",e);
		}

        List<LightNode> nodes = null;
        if (res.isSuccess()) {
        	nodes = res.body();
        } else {
			throw new RuntimeException("Cannot retrieve registration with IDs; Response code:" + res.code() + " ; Url: " + res.raw().request().urlString());
        }

        return nodes;
	}

	

	/**
	 * Gets a OSF user based on the ID.
	 *
	 * @param id the User ID
	 * @return the OSF User
	 */
	public User getUser(String id){	
		
        Call<User> listCall = osfService.getUserById(id);
        Response<User> res;
		try {
			res = listCall.execute();
		} catch (Exception e) {
			throw new RuntimeException("cannot retrieve node with ID " + id, e);
		}
       
		User user = null;
        if (res.isSuccess()) {
        	user = res.body();
        } else {
			throw new RuntimeException("Cannot retrieve User ID:" + id + "; Response code:" + res.code() + " ; Url: " + res.raw().request().urlString());
        }

        return user;
	}
	

	/**
	 * Gets a User ID list filter by params provided
	 *
	 * @param params the params to filter ID list
	 * @return the user ID list
	 */
	public List<LightUser> getUserIds(Map<String,String> params){	

        Call<List<LightUser>> listCall = osfService.getUserIds(params);
        Response<List<LightUser>> res;
		try {
			res = listCall.execute();
		} catch (Exception e) {
			throw new RuntimeException("cannot retrieve User ID list",e);
		}
       
        List<LightUser> users = null;
        if (res.isSuccess()) {
        	users = res.body();
        } else {
			throw new RuntimeException("Cannot retrieve Users; Response code:" + res.code() + " ; Url: " + res.raw().request().urlString());
        }

        return users;
	}
	
}
