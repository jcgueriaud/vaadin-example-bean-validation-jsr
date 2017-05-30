/**
 * 
 */
package org.test;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.test.validator.DateStartEndFields;

/**
 * @author jcgueriaud
 *
 */
@DateStartEndFields(startField="dateFrom",endField="dateTo")
public class Pojo {

	private String id;
	
	@NotNull
	@Size(min=2,max=8)
	private String value;
	@NotNull
	private LocalDate dateFrom;
	@NotNull
	private LocalDate dateTo;

	/**
	 * @param string
	 */
	public Pojo(String value) {
		this.value = value;
	}

	/**
	 * @param string
	 * @param string2
	 */
	public Pojo(String id, String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * 
	 */
	public Pojo() {
		// TODO Auto-generated constructor stub
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pojo other = (Pojo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}

	public LocalDate getDateTo() {
		return dateTo;
	}

	public void setDateTo(LocalDate dateTo) {
		this.dateTo = dateTo;
	}
	
	
}
