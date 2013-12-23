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
		if (type == ShapefileWKT.CORNER || type == ShapefileWKT.CONTROL_POINT
				|| type == ShapefileWKT.BUS_MAXIMAL
				|| type == ShapefileWKT.BUS_STOP
				|| type == ShapefileWKT.ADDRESS || type == ShapefileWKT.STREET
				|| type == ShapefileWKT.CORNER || type == ShapefileWKT.OBSTACLE)
			entityManager.persist(modelObject);
	}

	public void delete(ShapefileWKT modelObject) {
		entityManager.remove(modelObject);

	}

	public void modify(ShapefileWKT modelObjectOld, ShapefileWKT modelObjectNew) {
		if (modelObjectOld == null || modelObjectNew == null)
			throw new IllegalArgumentException("No nulls allowed");
		if (modelObjectOld.getId() != modelObjectNew.getId())
			throw new IllegalArgumentException("Different ids");
		entityManager.merge(modelObjectNew);
	}

	public ShapefileWKT find(String shapefileType) {
		ShapefileWKT shapefileWKT;
		try {
			Query q = entityManager.createNamedQuery("ShapefileWKT.findByType");
			q.setParameter("shapefileType", shapefileType);
			shapefileWKT = (ShapefileWKT) q.getSingleResult();
		} catch (NoResultException e) {
			if ("obstacle".equals(shapefileType)) {
				addObstacleWKT();
				return find("obstacle");
			}
			shapefileWKT = null;
		}
		return shapefileWKT;
	}

	private void addObstacleWKT() {
		ShapefileWKT shapefileWKT = new ShapefileWKT();
		shapefileWKT.setShapefileType(ShapefileWKT.OBSTACLE);
		shapefileWKT
				.setWkt("PROJCS[\"WGS 84 / UTM zone 21S\", "
						+ " GEOGCS[\"WGS 84\", "
						+ "  DATUM[\"WGS_1984\","
						+ "   SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], "
						+ "  TOWGS84[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], "
						+ " AUTHORITY[\"EPSG\",\"6326\"]], "
						+ "    PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], "
						+ "   UNIT[\"degree\", 0.017453292519943295], "
						+ "  AXIS[\"Longitude\", EAST], "
						+ " AXIS[\"Latitude\", NORTH], "
						+ " AUTHORITY[\"EPSG\",\"4326\"]], "
						+ "  PROJECTION[\"Transverse_Mercator\"], "
						+ "  PARAMETER[\"central_meridian\", -57.0],"
						+ " PARAMETER[\"latitude_of_origin\", 0.0], "
						+ "PARAMETER[\"scale_factor\", 0.9996], "
						+ "  PARAMETER[\"false_easting\", 500000.0],"
						+ " PARAMETER[\"false_northing\", 10000000.0], "
						+ " UNIT[\"m\", 1.0], " + "AXIS[\"x\", EAST], "
						+ "AXIS[\"y\", NORTH], "
						+ "AUTHORITY[\"EPSG\",\"32721\"]]");
		add(shapefileWKT);
	}

}
