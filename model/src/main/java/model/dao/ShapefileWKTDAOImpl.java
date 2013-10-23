package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.ShapefileWKT;

@Stateless(name = "ShapefileWKTDAO")
public class ShapefileWKTDAOImpl implements ShapefileWKTDAO {
	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(ShapefileWKT modelObject) {
		String type = modelObject.getShapefileType();
		if (type == ShapefileWKT.CORNER || type == ShapefileWKT.BUS_NON_MAXIMAL
				|| type == ShapefileWKT.CORNER
				|| type == ShapefileWKT.BUS_MAXIMAL
				|| type == ShapefileWKT.BUS_STOP
				|| type == ShapefileWKT.ADDRESS 
				|| type == ShapefileWKT.STREET
				|| type == ShapefileWKT.CORNER)
			entityManager.persist(modelObject);
	}

	public void delete(ShapefileWKT modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(ShapefileWKT modelObjectOld, ShapefileWKT modelObjectNew) {
		// TODO Auto-generated method stub

	}

	public ShapefileWKT find(String shapefileType) {
		ShapefileWKT shapefileWKT;
		try {
			Query q = entityManager.createNamedQuery("ShapefileWKT.findByType");
			q.setParameter("shapefileType", shapefileType);
			shapefileWKT = (ShapefileWKT) q.getSingleResult();
		} catch (NoResultException e) {
			shapefileWKT = null;
		}
		return shapefileWKT;
	}

}
