package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "street_segments")
@NamedQueries({
	@NamedQuery(name = "StreetSegment.findByOriginEnd", query = "SELECT s FROM StreetSegment s WHERE s.origin = :origin AND s.end = :end")
	})
public class StreetSegment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToOne
	@JoinColumn(name = "origin", referencedColumnName = "id")
	private Position origin;

	@OneToOne
	@JoinColumn(name = "end", referencedColumnName = "id")
	private Position end;

	public StreetSegment() {
		super();
	}
	
	public StreetSegment(Position origin, Position end) {
		super();
		if (origin == null)
			throw new IllegalArgumentException("Origin cannot be null");
		this.origin = origin;
		if (end == null)
			throw new IllegalArgumentException("End cannot be null");
		this.end = end;
	}

	public Position getOrigin() {
		return origin;
	}

	public void setOrigin(Position origin) {
		if (origin == null)
			throw new IllegalArgumentException("Origin cannot be null");
		this.origin = origin;
	}

	public Position getEnd() {
		return end;
	}

	public void setEnd(Position end) {
		if (end == null)
			throw new IllegalArgumentException("End cannot be null");
		this.end = end;
	}

	public int getId() {
		return id;
	}

}
