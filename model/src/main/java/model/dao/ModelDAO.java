package model.dao;



public interface ModelDAO<ModelObject, UniqueKey> {
	public void add(ModelObject modelObject);

	public void delete(ModelObject modelObject);

	public void modify(ModelObject modelObjectOld, ModelObject modelObjectNew);

	public ModelObject find(UniqueKey uniqueKey);
}
