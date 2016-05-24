package info.rmapproject.transformer.model;


public enum RecordType {
	SHARE ("SHARE"), 
	OSF_REGISTRATION ("OSF_REGISTRATION"),
	OSF_USER ("OSF_USER"),
	OSF_NODE ("OSF_NODE");
	
	private String value;
	
	private RecordType(String value){
		this.value = value;
	}
	
	public String value(){
		return this.value;
	}
	
	public static RecordType forValue(String value) {
        if (value != null) {
            for (RecordType type : RecordType.values()) {
                if (value.equals(type.value())) {                	
                    return type;
                }
            }
        }
        return null;
    }
	
}
