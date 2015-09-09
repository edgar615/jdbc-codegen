package null;

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
public class Role implements Persistable<Integer> {

	private static final long serialVersionUID = 1L;

	@NotNull(groups = {Default.class, Default.class})
	private int roleId;

	@NotEmpty
	@Size(max=10)
	private String roleName = "-";

	public Role() {

	}

	@Override
	public Integer getId () {
		return this.roleId;
	}

	@Override
	public void setId(Integer id) {
		this.roleId = id;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getRoleId() {
		return this.roleId;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return this.roleName;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper("Role")
			.add("roleId", roleId)
			.add("roleName", roleName)
			.toString();
	}

	/* START 写在START和END中间的代码不会被替换*/

	/* END 写在START和END中间的代码不会被替换*/

}