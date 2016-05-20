package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.TransformIterator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.FileVersion;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
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
 * Retrieves and iterates over OSF Registration data
 * @author khanson
 */
public class OsfRegApiTransformIterator extends TransformIterator {

    private int position = -1;
	private List<Registration> registrations = null;
	private Registration currRegistration = null;
	
    public OsfRegApiTransformIterator(String filters) throws Exception{
    	super(filters);
		if (!params.containsKey("filter[public]")){
			params.put("filter[public]", "true");
		}
		// this loads next record to be retrieved, each next() retrieves currReg and loads next one.
		loadNext(); 
    }
    
	@Override
	public Object next() {
		Registration registration = null;
		if (hasNext()){
			registration = currRegistration;
			currId = registration.getId();
			loadNext();
		} else {
			throw new RuntimeException("No more Registration records available in this batch");
		}
		return registration;
	}


	@Override
	public boolean hasNext() {
		return (currRegistration!=null);			
	}
	

	/**
	 * Collect OSF data from API using parameters defined
	 */
	private void loadBatch() {
		position = -1;
    	try {
    		registrations = tempGetRegList(params);
    	} catch(Exception e){
    		log.error("Could not load list of records to iterate over, exiting.");
    		throw new RuntimeException(e);
    	}	
	}

	private void loadNext(){
		currRegistration = null;
		if (registrations == null) {
			loadBatch();
		}
		if (registrations.size()>0 && !isLastRow()){
			Registration reg = null;
			do {
				//load next
				position = position+1;
				reg = registrations.get(position);
				if (hasExclusionCriteria(reg)||!isTopAccessibleLevel(reg)){
					reg = null;
				}
			} while ((reg==null)&&!isLastRow());
			currRegistration = reg;			
		}
	}
	
	private boolean isLastRow(){
		return (position==(registrations.size()-1));
	}
    
	private boolean hasExclusionCriteria(Registration reg){
		if (reg.isPending_embargo_approval() || reg.isPending_registration_approval()
				|| reg.isPending_withdrawal() ||  reg.isWithdrawn()){
			return true; // don't include these.
		}
		else {return false;}
	}
	
	private boolean isTopAccessibleLevel(Registration reg){
		//check if we are at top level
		String root = reg.getRoot();
		String rootId = OsfUtils.extractLastSubFolder(root);
		
		if (!rootId.equals(reg.getId())){
			try {
				URL url = new URL(root); 
				HttpURLConnection connection = (HttpURLConnection)url.openConnection(); 
				connection.setRequestMethod("GET"); connection.connect(); 
				int code = connection.getResponseCode();
				if (code==401){// process this
					//need to go up parent paths.	
					//TODO: code not easily available for this yet
					return true;
				} else {
					return false;
				}
			} catch (Exception e){
				throw new RuntimeException("Could not validate Registration accessibility");
			}
		}
		return true;
	}	
	
	

	/**
	 * This method is temporary - just until the osf-client code is done.
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private List<Registration> tempGetRegList(Map<String,String> params) throws Exception{	
		URI baseUrl = new URI("http://192.168.99.100:8000/v2/");
		
    	// Create object mapper
        ObjectMapper objectMapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient();

        ResourceConverter converter = new ResourceConverter(objectMapper, Registration.class, File.class, 
        													FileVersion.class, Contributor.class, User.class, Node.class);
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

        OsfService osfSvc = retrofit.create(OsfService.class);

        Call<List<Registration>> listCall = osfSvc.registrationList(params);
        Response<List<Registration>> res = listCall.execute();
       
        List<Registration> registrations = null;
        if (res.isSuccess()) {
         	registrations = res.body();
        }
        return registrations;
	}
	
	
}
