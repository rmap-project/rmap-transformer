package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.TransformUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.Institution;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.NodeId;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.RegistrationId;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.model.UserId;
import org.dataconservancy.cos.osf.client.service.OsfService;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * This class interacts with the OSF client
 * @author khanson
 *
 */
public class OsfClientService {
	private OsfService osfService = null;
	private static final String OSF_BASEURL_PROPNAME = "rmaptransformer.osfApiBaseUrl";
	
	
	public OsfClientService(){
		try {
			String sBaseUrl = TransformUtils.getPropertyValue(OSF_BASEURL_PROPNAME);
			URI baseUrl = new URI(sBaseUrl);

	    	// Create object mapper
	        ObjectMapper objectMapper = new ObjectMapper();
	        OkHttpClient client = new OkHttpClient();

	        ResourceConverter converter = new ResourceConverter(objectMapper, NodeId.class, RegistrationId.class, Registration.class, File.class, 
	        													Contributor.class, User.class, UserId.class, Institution.class, Node.class);//taking out this for now... FileVersion.class, 
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
	        JSONAPIConverterFactory converterFactory = new JSONAPIConverterFactory(converter);

	        Retrofit retrofit = new Retrofit.Builder()
	                .baseUrl(baseUrl.toString())
	                .addConverterFactory(converterFactory)
	                .client(client)
	                .build();

	        osfService = retrofit.create(OsfService.class);
		} catch (URISyntaxException e){
			throw new RuntimeException("Could not define base URL for OSF service", e);			
		} catch (Exception e){
			throw new RuntimeException("Could not start OSF Service", e);
		}
	}
	

	/**
	 * Get list of nodes - this will walk down the branches of relationships
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Node> getNodeList(Map<String,String> params){	
		
        Call<List<Node>> listCall = osfService.nodeList(params);
        Response<List<Node>> res;
		try {
			res = listCall.execute();
		} catch (IOException e) {
			throw new RuntimeException("cannot retrieve node list");
		}
       
        List<Node> nodes = null;
        if (res.isSuccess()) {
        	nodes = res.body();
        }
        return nodes;
	}


	/**
	 * Get list of registrations from OSF API. This will walk down the relationship branches
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Registration> getRegList(Map<String,String> params){	

        Call<List<Registration>> listCall = osfService.registrationList(params);
        Response<List<Registration>> res;
		try {
			res = listCall.execute();
		} catch (IOException e) {
			throw new RuntimeException("cannot retrieve registration list");
		}
       
        List<Registration> registrations = null;
        if (res.isSuccess()) {
         	registrations = res.body();
        }
        return registrations;
	}

	/**
	 * Get single OSF registration by passing in single ID e.g. "cgur9"
	 * @param id
	 * @return
	 */
	public Registration getRegistration(String id){	

        Call<Registration> listCall = osfService.registration(id);
        Response<Registration> res;
		try {
			res = listCall.execute();
		} catch (IOException e) {
			throw new RuntimeException("cannot retrieve registration with ID " + id);
		}
       
        Registration registration = null;
        if (res.isSuccess()) {
        	registration = res.body();
        }
        return registration;
	}
	
	/**
	 * Get list of registration Ids filtered by parameters provided
	 * @param params
	 * @return
	 */
	public List<RegistrationId> getRegIdList(Map<String,String> params){	

        Call<List<RegistrationId>> listCall = osfService.registrationIdList(params);
        Response<List<RegistrationId>> res;
		try {
			res = listCall.execute();
		} catch (IOException e) {
			throw new RuntimeException("cannot retrieve Registration ID list");
		}
       
        List<RegistrationId> registrations = null;
        if (res.isSuccess()) {
         	registrations = res.body();
        }
        return registrations;
	}
	

	public Node getNode(String id){	
		
        Call<Node> listCall = osfService.node(id);
        Response<Node> res;
		try {
			res = listCall.execute();
		} catch (IOException e) {
			throw new RuntimeException("cannot retrieve node with ID " + id);
		}
       
        Node node = null;
        if (res.isSuccess()) {
        	node = res.body();
        }
        return node;
	}
	

	public List<NodeId> getNodeIdList(Map<String,String> params){	

        Call<List<NodeId>> listCall = osfService.nodeIdList(params);
        Response<List<NodeId>> res;
		try {
			res = listCall.execute();
		} catch (IOException e) {
			throw new RuntimeException("cannot retrieve nodeId list");
		}
       
        List<NodeId> nodes = null;
        if (res.isSuccess()) {
        	nodes = res.body();
        }
        return nodes;
	}

	

	public User getUser(String id){	
		
        Call<User> listCall = osfService.user(id);
        Response<User> res;
		try {
			res = listCall.execute();
		} catch (IOException e) {
			throw new RuntimeException("cannot retrieve node with ID " + id);
		}
       
		User user = null;
        if (res.isSuccess()) {
        	user = res.body();
        }
        return user;
	}
	

	public List<UserId> getUserIdList(Map<String,String> params){	

        Call<List<UserId>> listCall = osfService.userIdList(params);
        Response<List<UserId>> res;
		try {
			res = listCall.execute();
		} catch (IOException e) {
			throw new RuntimeException("cannot retrieve nodeId list");
		}
       
        List<UserId> users = null;
        if (res.isSuccess()) {
        	users = res.body();
        }
        return users;
	}
	
}
