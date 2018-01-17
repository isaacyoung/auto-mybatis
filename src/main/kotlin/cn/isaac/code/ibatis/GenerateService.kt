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
object GenerateService {
    fun code() {
        context.tables.forEach {
            val imports = hashSetOf<String>()
            imports.add("java.util.List")
            imports.add("java.util.Map")
            imports.add("com.utility.page.Page")
            imports.add("${config[pkg.model]}.${context.getShortClassName(it.name)}")
            imports.add("com.lianlian.service.ServiceResult")


            val str = """package ${config[pkg.serv]};

${context.getImports(imports)}

/**
 *  ${it.comments}
 */
public interface I${context.getShortClassName(it.name)}Service {

    /**
     * 查询分页
     */
    ServiceResult<Page<${context.getShortClassName(it.name)}>> queryPage(Map<String, Object> map,Integer pageNo, Integer pageSize);

    /**
     * 查询列表
     */
    ServiceResult<List<${context.getShortClassName(it.name)}>> queryList(Map<String, Object> map);

    /**
     * 新增
     */
    ServiceResult<Boolean> insert(${context.getShortClassName(it.name)} entry);

    /**
     * 更新
     */
    ServiceResult<Boolean> update(${context.getShortClassName(it.name)} entry);

    /**
     * 删除
     */
    ServiceResult<Boolean> delete(${context.getShortClassName(it.name)} entry);

    /**
     * 单个查询
     */
    ${context.getShortClassName(it.name)} selectById(Integer id);

    /**
     * 单个删除
     */
    ServiceResult<Boolean> deleteById(Integer id);

}
"""

            val path = "${config[out.target]}${context.getPathFromPackage(config[pkg.serv])}"
            File(path).mkdirs()
            File("$path${File.separator}I${context.getShortClassName(it.name)}Service.java")
                    .appendText(str, Charset.defaultCharset())
        }
    }
}