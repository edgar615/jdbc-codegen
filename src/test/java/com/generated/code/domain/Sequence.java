package com.generated.code.domain;

import com.edgar.core.repository.Persistable;
import javax.validation.constraints.Null;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.groups.Default;
import com.google.common.base.MoreObjects;

/**
 * This class is generated by Jdbc code generator.
 *
 * @author Jdbc Code Generator
 */
public class Sequence implements Persistable<Long> {

	private static final long serialVersionUID = 1L;

	@NotNull (groups = { Default.class })
	@Null (groups = { Default.class })
	private long id;

	@NotEmpty
	@Size (max = 1)
	private String stub;


	public Sequence () {

	}

	@Override
	public Long getId () {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public void setStub (String stub) {
		this.stub = stub;
	}

	public String getStub () {
		return this.stub;
	}

	@Override
	public String toString () {
		return MoreObjects.toStringHelper("Sequence")
			.add("id", id)
			.add("stub", stub)
			.toString();
	}

	/* START 写在START和END中间的代码不会被替换*/

	/* END 写在START和END中间的代码不会被替换*/

}