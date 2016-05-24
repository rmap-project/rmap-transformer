package info.rmapproject.transformer.model;



public enum TransformType {
	SHARE_API (RecordType.SHARE, "api"), 
	SHARE_LOCAL (RecordType.SHARE, "local"), 
	OSF_REGISTRATIONS_API (RecordType.OSF_REGISTRATION, "api"),
	OSF_USERS_API (RecordType.OSF_USER, "api"),
	OSF_NODES_API (RecordType.OSF_NODE, "api");
	
	private RecordType recordType;
	private String source;
	
	private TransformType(RecordType value, String source){
		this.recordType = value;
		this.source = source;
	}
	
	public RecordType recordType(){
		return this.recordType;
	}
	public String source(){
		return this.source;
	}
	
	public static TransformType getVal(String value, String source) {
        if (value != null) {
        	RecordType recordType = RecordType.forValue(value);
            for (TransformType type : TransformType.values()) {
                if (recordType.equals(type.recordType()) && source.equals(type.source())) {                	
                    return type;
                }
            }
        }
        return null;
    }
	
}
