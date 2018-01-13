package cn.isaac.code.ibatis

import cn.isaac.config.config
import cn.isaac.config.out
import cn.isaac.config.pkg
import cn.isaac.context.TableContext
import cn.isaac.context.context
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

            val str = """
package ${config[pkg.model]};

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
            w += "    private String ${context.getShortFieldName(columnResult.name)};\n"
        }
        return w
    }

    fun getMethods(table: TableContext): String {
        var w = ""
        table.columnsList?.forEachIndexed { index, columnResult ->
            w += getGetMethod(columnResult.name)
            w += getSetMethod(columnResult.name)
        }
        return w
    }

    private fun getGetMethod(name: String): String {
        var w = ""
        w += "    public String get${context.getShortClassName(name)}() {\n"
        w += "        return ${context.getShortFieldName(name)};\n"
        w += "    }\n"
        return w
    }

    private fun getSetMethod(name: String): String {
        var w = ""
        w += "    public void set${context.getShortClassName(name)}(String _${context.getShortFieldName(name)}) {\n"
        w += "        this.${context.getShortFieldName(name)} = _${context.getShortFieldName(name)};\n"
        w += "    }\n"
        return w
    }

}