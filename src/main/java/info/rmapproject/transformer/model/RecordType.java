package info.rmapproject.transformer.model;


public enum RecordType {
	SHARE ("share"), 
	OSF_REGISTRATION ("osf_registration"),
	OSF_USER ("osf_user"),
	OSF_NODE ("osf_node");
	
	private String value;
	
	private RecordType(String value){
		this.value = value;
	}
	
	public String value(){
		return this.value;
	}
	
	public static RecordType forValue(String value) {
        if (value != null) {
        	value = value.toLowerCase();
            for (RecordType type : RecordType.values()) {
                if (value.equals(type.value())) {                	
                    return type;
                }
            }
        }
        return null;
    }
	
}
