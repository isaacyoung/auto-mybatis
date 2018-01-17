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
object GenerateDao {
    fun code() {
        context.tables.forEach {
            val imports = hashSetOf<String>()
            imports.add("java.util.List")
            imports.add("java.util.Map")
            imports.add("com.utility.page.Page")
            imports.add("${config[pkg.model]}.${context.getShortClassName(it.name)}")


            val str = """package ${config[pkg.dao]};

${context.getImports(imports)}

/**
 *  ${it.comments}
 */
public interface I${context.getShortClassName(it.name)}Dao {

    /**
     * 查询分页
     */
    Page<${context.getShortClassName(it.name)}> queryPage(Map<String, Object> map,Integer pageNo, Integer pageSize);

    /**
     * 查询列表
     */
    List<${context.getShortClassName(it.name)}> queryList(Map<String, Object> map);

    /**
     * 新增
     */
    void insert(${context.getShortClassName(it.name)} entry);

    /**
     * 更新
     */
    void update(${context.getShortClassName(it.name)} entry);

    /**
     * 删除
     */
    void delete(${context.getShortClassName(it.name)} entry);

}
"""

            val path = "${config[out.target]}${context.getPathFromPackage(config[pkg.dao])}"
            File(path).mkdirs()
            File("$path${File.separator}I${context.getShortClassName(it.name)}Dao.java")
                    .appendText(str, Charset.defaultCharset())
        }
    }
}