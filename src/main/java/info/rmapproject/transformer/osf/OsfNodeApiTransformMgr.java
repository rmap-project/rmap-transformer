package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.DiscoFile;
import info.rmapproject.transformer.DiscoModel;
import info.rmapproject.transformer.TransformMgr;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.FileVersion;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.openrdf.model.Model;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

public class OsfNodeApiTransformMgr extends TransformMgr {
	
    private String filters;
           
    public OsfNodeApiTransformMgr(String outputpath, String filters, String discoDescription){
    	super(outputpath, discoDescription);
    	this.filters = filters;
    }
    
	/**
	 * Collect OSF data from API and transform to DiSCOs
	 * @param inputUrl - API url 
	 * @param numRecords - number of records to retrieve. Will paginate if necessary.
	 */
	public Integer transform(Integer numRecords) throws Exception {
		if (numRecords==null){
			throw new IllegalArgumentException("numRecords cannot be null");
		}

		//Reset counter
		Integer counter = 0;
		
		// split out params
		HashMap<String,String> params=null;
		try{
			params = readParamsIntoMap(this.filters, "UTF-8");
			if (!params.containsKey("filter[public]")){
				params.put("filter[public]", "true");
			}
		} catch(URISyntaxException e){
			throw new IllegalArgumentException("URL invalid, parameters could not be parsed");
		}

		List<Node> nodes = tempGetNodeList(params);
        
        for (Node node : nodes) {
        	if (counter==numRecords) {
        		break;
        	}
	        String nodeId = null;
    		try {
    			if (node!=null){
    				nodeId = node.getId();
		          	
    				DiscoModel discoModel = new OsfNodeDiscoModel(node);
					Model model = discoModel.getModel();
					
					String filename = getNewFilename(nodeId);
					DiscoFile disco = new DiscoFile(model, this.outputPath, filename);
					disco.writeFile();
		        	
					counter = counter + 1;
					log.info("DiSCO created: " + nodeId + " -> " + filename);
    			}
    		} catch (Exception e) {
    			String logMsg = "Could not complete export for node " + counter + "\n Continuing to next record. Msg: " + e.getMessage();
    			if (node!=null){
    				logMsg = "Could not complete export for nodeId: " + nodeId
        					+ "\n Continuing to next record. Msg: " + e.getMessage();
    			} 
    			log.error(logMsg,e);
    		}
		} 

		return counter;		
	}

	/**
	 * This method is temporary - just until the osf-client code is done.
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private List<Node> tempGetNodeList(Map<String,String> params) throws Exception{	
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

        Call<List<Node>> listCall = osfSvc.nodeList(params);
        Response<List<Node>> res = listCall.execute();
       
        List<Node> nodes = null;
        if (res.isSuccess()) {
        	nodes = res.body();
        }
        return nodes;
	}
	
	
}
