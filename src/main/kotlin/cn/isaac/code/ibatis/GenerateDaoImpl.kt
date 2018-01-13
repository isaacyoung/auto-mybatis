package cn.isaac.code.ibatis

import cn.isaac.config.config
import cn.isaac.config.out
import cn.isaac.config.pkg
import cn.isaac.context.context
import java.io.File
import java.nio.charset.Charset

/**
 *
 * create by isaac at 2018/1/12 20:18
 */
object GenerateDaoImpl {
    fun code() {
        context.tables.forEach {
            val imports = hashSetOf<String>()
            imports.add("java.util.Map")
            imports.add("com.utility.page.Page")
            imports.add("com.lianlian.base.dao.SqlMapBaseDAO")
            imports.add("${config[pkg.model]}.${context.getShortClassName(it.name)}")
            imports.add("${config[pkg.dao]}.I${context.getShortClassName(it.name)}Dao")


            val str = """
package ${config[pkg.dao]}.impl;

${context.getImports(imports)}

/**
 *  ${it.comments}
 */
public class ${context.getShortClassName(it.name)}DaoImpl extends SqlMapBaseDAO implements I${context.getShortClassName(it.name)}Dao {

    /**
     * 查询
     */
    @Override
    public Page<${context.getShortClassName(it.name)}> queryPage(Map<String, Object> map,Integer pageNo, Integer pageSize) {
        return this.getPaginationList(map,"${config[pkg.model]}.${context.getShortClassName(it.name)}.queryPageCount","${config[pkg.model]}.${context.getShortClassName(it.name)}.queryPageList", pageNo, pageSize);
    }

    /**
     * 新增
     */
    @Override
    public void save(${context.getShortClassName(it.name)} entry) {
        this.executeInsert("${config[pkg.model]}.${context.getShortClassName(it.name)}.save", entry);
    }

    /**
     * 更新
     */
    @Override
    public void update(${context.getShortClassName(it.name)} entry) {
        this.executeInsert("${config[pkg.model]}.${context.getShortClassName(it.name)}.update", entry);
    }

    /**
     * 删除
     */
    @Override
    public void delete(${context.getShortClassName(it.name)} entry) {
        this.executeInsert("${config[pkg.model]}.${context.getShortClassName(it.name)}.delete", entry);
    }

}
"""

            val path = "${config[out.target]}${context.getPathFromPackage(config[pkg.dao])}${File.separator}impl"
            File(path).mkdirs()
            File("$path${File.separator}${context.getShortClassName(it.name)}DaoImpl.java")
                    .appendText(str, Charset.defaultCharset())
        }
    }
}