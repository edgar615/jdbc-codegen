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
public class SysDict implements Persistable<Integer> {

	private static final long serialVersionUID = 1L;

	@NotNull(groups = {Default.class, Default.class})
	private int dictId;

	@NotNull(groups = {Default.class, Default.class})
	private int parentId = -1;

	@NotNull
	private int dictCode;

	@NotEmpty
	@Size(max=32)
	private String dictName;

	@NotNull
	private int sorted = 9999;

	public SysDict() {

	}

	@Override
	public Integer getId () {
		return this.dictId;
	}

	@Override
	public void setId(Integer id) {
		this.dictId = id;
	}

	public void setDictId(int dictId) {
		this.dictId = dictId;
	}

	public int getDictId() {
		return this.dictId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getParentId() {
		return this.parentId;
	}

	public void setDictCode(int dictCode) {
		this.dictCode = dictCode;
	}

	public int getDictCode() {
		return this.dictCode;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public String getDictName() {
		return this.dictName;
	}

	public void setSorted(int sorted) {
		this.sorted = sorted;
	}

	public int getSorted() {
		return this.sorted;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper("SysDict")
			.add("dictId", dictId)
			.add("parentId", parentId)
			.add("dictCode", dictCode)
			.add("dictName", dictName)
			.add("sorted", sorted)
			.toString();
	}

	/* START 写在START和END中间的代码不会被替换*/

	/* END 写在START和END中间的代码不会被替换*/

}