package cn.isaac.code.ibatis

import cn.isaac.config.config
import cn.isaac.config.out
import cn.isaac.config.pkg
import cn.isaac.context.TableContext
import cn.isaac.context.context
import cn.isaac.jdbc.ColumnResult
import java.io.File
import java.nio.charset.Charset

/**
 *
 * create by isaac at 2018/1/12 19:30
 */
object GeneratePojo {


    fun code() {
        context.tables.forEach {
            val imports = hashSetOf<String>()
            imports.add("com.lianlian.base.pojo.Entity")

            val str = """package ${config[pkg.model]};

${context.getImports(imports)}

/**
 *  ${it.comments}
 */
public class ${context.getShortClassName(it.name)} extends Entity {
    private static final long serialVersionUID = 1L;

${getFields(it)}

${getMethods(it)}

}
"""

            val path = "${config[out.target]}${context.getPathFromPackage(config[pkg.model])}"
            File(path).mkdirs()
            File("$path${File.separator}${context.getShortClassName(it.name)}.java")
                    .appendText(str, Charset.defaultCharset())
        }
    }

    private fun getFields(table: TableContext): String {
        var w = ""
        table.columnsList?.forEachIndexed { index, columnResult ->
            w += context.getComment(columnResult.comments)
            w += "    private ${context.getFieldClassType(columnResult.dataType,columnResult.dataLength)} ${context.getShortFieldName(columnResult.name)};\n"
        }
        return w
    }

    fun getMethods(table: TableContext): String {
        var w = ""
        table.columnsList?.forEachIndexed { index, columnResult ->
            w += getGetMethod(columnResult)
            w += getSetMethod(columnResult)
        }
        return w
    }

    private fun getGetMethod(column: ColumnResult): String {
        var w = ""
        w += "    public ${context.getFieldClassType(column.dataType,column.dataLength)} get${context.getShortClassName(column.name)}() {\n"
        w += "        return ${context.getShortFieldName(column.name)};\n"
        w += "    }\n"
        return w
    }

    private fun getSetMethod(column: ColumnResult): String {
        var w = ""
        w += "    public void set${context.getShortClassName(column.name)}(${context.getFieldClassType(column.dataType,column.dataLength)} _${context.getShortFieldName(column.name)}) {\n"
        w += "        this.${context.getShortFieldName(column.name)} = _${context.getShortFieldName(column.name)};\n"
        w += "    }\n"
        return w
    }

}