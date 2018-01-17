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
            imports.add("java.util.List")
            imports.add("java.util.Map")
            imports.add("java.util.HashMap")
            imports.add("com.utility.page.Page")
            imports.add("org.springframework.transaction.annotation.Transactional")
            imports.add("${config[pkg.model]}.${context.getShortClassName(it.name)}")
            imports.add("com.lianlian.service.ServiceResult")


            val str = """package ${config[pkg.serv]}.impl;

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
     * 查询列表
     */
    @Override
    public ServiceResult<List<${context.getShortClassName(it.name)}>> queryList(Map<String, Object> map) {
        ServiceResult<List<${context.getShortClassName(it.name)}>> result = new ServiceResult<List<${context.getShortClassName(it.name)}>>();
        result.setResult(${context.getShortFieldName(it.name)}Dao.queryList(map));
        return result;
    }

    /**
     * 新增
     */
    @Transactional
    @Override
    public ServiceResult<Boolean> insert(${context.getShortClassName(it.name)} entry) {
        ServiceResult<Boolean> result = new ServiceResult<Boolean>();
        ${context.getShortFieldName(it.name)}Dao.insert(entry);
        result.setResult(true);
        return result;
    }

    /**
     * 更新
     */
    @Transactional
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
    @Transactional
    @Override
    public ServiceResult<Boolean> delete(${context.getShortClassName(it.name)} entry) {
        ServiceResult<Boolean> result = new ServiceResult<Boolean>();
        ${context.getShortFieldName(it.name)}Dao.delete(entry);
        result.setResult(true);
        return result;
    }

    /**
     * 单个查询
     */
    @Override
    public ${context.getShortClassName(it.name)} selectById(Integer id) {
        ServiceResult<${context.getShortClassName(it.name)}> result = new ServiceResult<${context.getShortClassName(it.name)}>();
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        List<${context.getShortClassName(it.name)}> list = ${context.getShortFieldName(it.name)}Dao.queryList(map);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 单个删除
     */
    @Transactional
    @Override
    public ServiceResult<Boolean> deleteById(Integer id) {
        ServiceResult<Boolean> result = new ServiceResult<Boolean>();
        ${context.getShortClassName(it.name)} entry = new ${context.getShortClassName(it.name)}();
        entry.setId(id);
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