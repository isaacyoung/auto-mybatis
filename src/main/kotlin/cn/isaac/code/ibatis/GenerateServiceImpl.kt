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
object GenerateServiceImpl {
    fun code() {
        context.tables.forEach {
            val imports = hashSetOf<String>()
            imports.add("java.util.Map")
            imports.add("com.utility.page.Page")
            imports.add("${config[pkg.model]}.${context.getShortClassName(it.name)}")
            imports.add("com.lianlian.service.ServiceResult")


            val str = """
package ${config[pkg.serv]}.impl;

${context.getImports(imports)}

/**
 *  ${it.comments}
 */
public class ${context.getShortClassName(it.name)}ServiceImpl implements I${context.getShortClassName(it.name)}Service{

    private I${context.getShortClassName(it.name)}Dao ${context.getShortFieldName(it.name)}Dao;

    public void set${context.getShortClassName(it.name)}Dao(I${context.getShortClassName(it.name)}Dao ${context.getShortFieldName(it.name)}Dao) {
        this.${context.getShortFieldName(it.name)}Dao = ${context.getShortFieldName(it.name)}Dao;
    }

     /**
     * 查询
     */
    @Override
    public ServiceResult<Page<${context.getShortClassName(it.name)}>> queryPage(Map<String, Object> map,Integer pageNo, Integer pageSize) {
        ServiceResult<Page<${context.getShortClassName(it.name)}>> result = new ServiceResult<Page<${context.getShortClassName(it.name)}>>();
        result.setResult(${context.getShortFieldName(it.name)}Dao.queryPage(map, pageNo, pageSize));
        return result;
    }

    /**
     * 新增
     */
    @Override
    public ServiceResult<Boolean> save(${context.getShortClassName(it.name)} entry) {
        ServiceResult<Boolean> result = new ServiceResult<Boolean>();
        ${context.getShortFieldName(it.name)}Dao.add(entry);
        result.setResult(true);
        return result;
    }

    /**
     * 更新
     */
    @Override
    public ServiceResult<Boolean> update(${context.getShortClassName(it.name)} entry) {
        ServiceResult<Boolean> result = new ServiceResult<Boolean>();
        ${context.getShortFieldName(it.name)}Dao.update(entry);
        result.setResult(true);
        return result;
    }

    /**
     * 删除
     */
    @Override
    public ServiceResult<Boolean> delete(${context.getShortClassName(it.name)} entry) {
        ServiceResult<Boolean> result = new ServiceResult<Boolean>();
        ${context.getShortFieldName(it.name)}Dao.delete(entry);
        result.setResult(true);
        return result;
    }

}
"""

            val path = "${config[out.target]}${context.getPathFromPackage(config[pkg.serv])}${File.separator}impl"
            File(path).mkdirs()
            File("$path${File.separator}${context.getShortClassName(it.name)}ServiceImpl.java")
                    .appendText(str, Charset.defaultCharset())
        }
    }
}