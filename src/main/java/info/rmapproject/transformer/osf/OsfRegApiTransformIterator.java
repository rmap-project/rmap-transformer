package info.rmapproject.transformer.osf;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.FileVersion;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.NodeBase;
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
public class OsfRegApiTransformIterator extends OsfBaseApiTransformIterator {
	
    public OsfRegApiTransformIterator(String filters){
    	super(filters);
    }
    	
	/**
	 * Load batch of OSF data from API using parameters defined
	 */
    @Override
	protected void loadBatch() {
		position = -1;
    	try {
    		records = tempGetRegList(params);
    	} catch(Exception e){
    		log.error("Could not load list of records to iterate over, exiting.");
    		throw new RuntimeException(e);
    	}	
	}

    /**
     * Checks for any criteria that would exclude this record
     * for registrations, records not yet approved, under embargo, 
     * or in the process of withdrawal are excluded as well as those that
     * have an accessible parent Registration
     * @param nodeBase
     * @return
     */
    @Override
	protected boolean hasExclusionCriteria(NodeBase nodeBase){
    	Registration reg = (Registration) nodeBase;
		if (hasAccessibleParent(nodeBase)
				|| reg.isPending_embargo_approval() 
				|| reg.isPending_registration_approval()
				|| reg.isPending_withdrawal() 
				|| reg.isWithdrawn()){
			return true; // don't include these.
		}
		else {return false;}
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
