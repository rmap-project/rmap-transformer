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
 * Retrieves and iterates over OSF Node data
 * @author khanson
 */
public class OsfNodeApiTransformIterator extends TransformIterator {
	
	private int position = -1;
    private List<Node> nodes = null;
    private Node currNode = null;
               
    public OsfNodeApiTransformIterator(String filters) throws Exception{
    	super(filters);
		if (!params.containsKey("filter[public]")){
			params.put("filter[public]", "true");
		}
		// this loads next record to be retrieved, each next() retrieves currReg and loads next one.
		loadNext(); 
    }    

	@Override
	public Object next() {
		Node node = null;
		if (hasNext()){
			node = currNode;
			currId = node.getId();
			loadNext();
		} else {
			throw new RuntimeException("No more Node records available in this batch");
		}
		return node;
	}


	@Override
	public boolean hasNext() {
		return (currNode!=null);	
	}

	/**
	 * Collect OSF data from API using parameters defined
	 */
	private void loadBatch() {
		position = -1;
    	try {
    		nodes = tempGetNodeList(params);
    	} catch(Exception e){
    		log.error("Could not load list of records to iterate over, exiting.");
    		throw new RuntimeException(e);
    	}	
	}
	

	private void loadNext(){
		currNode = null;
		if (nodes == null) {
			loadBatch();
		}
		if (nodes.size()>0 && !isLastRow()){
			Node node = null;
			do {
				//load next
				position = position+1;
				node = nodes.get(position);
				if (!isTopAccessibleLevel(node)){
					node = null;
				}
			} while ((node==null)&&!isLastRow());
			currNode = node;			
		}
	}
	
	private boolean isLastRow(){
		return (position==(nodes.size()-1));
	}
	

	private boolean isTopAccessibleLevel(Node node){
		//check if we are at top level
		String root = node.getRoot();
		String rootId = OsfUtils.extractLastSubFolder(root);
		
		if (!rootId.equals(node.getId())){
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
				throw new RuntimeException("Could not validate Node accessibility");
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
