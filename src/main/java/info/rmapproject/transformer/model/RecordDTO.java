package info.rmapproject.transformer.model;


/** 
 * DTO class to transfer record, it's type, and id in a consistent manner.
 * @author khanson
 */
public class RecordDTO {
	
	/**
	 * ID unique to this data set
	 */
	private String id;
	/**
	 * Single record from data source
	 */
	private Object record;
	
	/**
	 * Type of data record
	 */
	private RecordType recordType;
	
	public RecordDTO(Object record, String id, RecordType recordType){
		if (record==null){
			throw new IllegalArgumentException("record cannot be null");
		}
		if (id==null){
			throw new IllegalArgumentException("id cannot be null");
		}
		this.id = id;
		this.record = record;
		this.recordType = recordType;
	}
	
	public String getId() {
		return id;
	}
	public Object getRecord() {
		return record;
	}

	public RecordType getRecordType() {
		return recordType;
	}

	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}
}
