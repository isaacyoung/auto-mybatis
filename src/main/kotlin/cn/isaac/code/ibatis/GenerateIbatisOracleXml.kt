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
 * create by isaac at 2018/1/11 20:18
 */
object GenerateIbatisOracleXml {

    fun code() {
        context.tables.forEach {
            val fullClass = "${config[pkg.model]}.${context.getShortClassName(it.name)}"
            val aliasName = context.getShortFieldName(it.name)

            val str = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE sqlMap  PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN" "http://ibatis.apache.org/dtd/sql-map-2.dtd">
<sqlMap namespace="$fullClass">

    <typeAlias alias="$aliasName" type="$fullClass" />

    <!-- 查询分页 -->
    ${queryPageList(it)}
    <!-- 查询分页 -->
	${queryPageCount(it)}
    <!-- 查询列表 -->
    ${queryList(it)}
	<!-- 新增 -->
	${insert(it)}
	<!-- 修改 -->
	${update(it)}
    <!-- 删除 -->
	${delete(it)}

</sqlMap>
"""

            val path = "${config[out.target]}${config[pkg.xml]}"
            File(path).mkdirs()
            File("$path${File.separator}${it.name.toUpperCase()}_SQLMAP.xml")
                .appendText(str, Charset.defaultCharset())
        }

    }

    fun queryPageList(table: TableContext): String {
        fun getQueryStr(): String {
            var q = ""
            table.columnsList?.forEachIndexed { index, columnResult ->
                q += "          t.${columnResult.name}      ${context.getFieldName(columnResult.name)}"
                if (index != table.columnsList!!.size - 1) {
                    q += ",\n"
                }
            }
            return q
        }

        fun getWhereStr(): String {
            var w = ""
            table.columnsList?.forEachIndexed { index, columnResult ->
                w += "          <isNotEmpty prepend=\"and\" property=\"${context.getFieldName(columnResult.name)}\"> t.${columnResult.name} = #${context.getFieldName(columnResult.name)}#</isNotEmpty>"
                if (index != table.columnsList!!.size - 1) {
                    w += "\n"
                }
            }
            return w
        }

        return """<select id="queryPageList" parameterClass="java.util.Map" resultClass="${context.getShortFieldName(table.name)}">
		<![CDATA[
		select * from (
		select rownum rn,t1.* from (
		select
${getQueryStr()}
		from ${table.name} t where 1=1
		]]>
		<dynamic>
${getWhereStr()}
		</dynamic>
		<![CDATA[
		order by t.${table.primaryKey} desc ) t1 ) t2 where t2.rn>=#startRow# and t2.rn<=#endRow#
		]]>
	</select>
"""
    }

    fun queryPageCount(table: TableContext): String {
        fun getWhereStr(): String {
            var w = ""
            table.columnsList?.forEachIndexed { index, columnResult ->
                w += "          <isNotEmpty prepend=\"and\" property=\"${context.getFieldName(columnResult.name)}\"> t.${columnResult.name} = #${context.getFieldName(columnResult.name)}#</isNotEmpty>"
                if (index != table.columnsList!!.size - 1) {
                    w += "\n"
                }
            }
            return w
        }

        return """<select id="queryPageCount" parameterClass="java.util.Map" resultClass="int">
		select count(*) from ${table.name} t where 1=1
		<dynamic>
${getWhereStr()}
		</dynamic>
	</select>
"""
    }

    fun queryList(table: TableContext): String {
        fun getQueryStr(): String {
            var q = ""
            table.columnsList?.forEachIndexed { index, columnResult ->
                q += "          t.${columnResult.name}      ${context.getFieldName(columnResult.name)}"
                if (index != table.columnsList!!.size - 1) {
                    q += ",\n"
                }
            }
            return q
        }

        fun getWhereStr(): String {
            var w = ""
            table.columnsList?.forEachIndexed { index, columnResult ->
                w += "          <isNotEmpty prepend=\"and\" property=\"${context.getFieldName(columnResult.name)}\"> t.${columnResult.name} = #${context.getFieldName(columnResult.name)}#</isNotEmpty>"
                if (index != table.columnsList!!.size - 1) {
                    w += "\n"
                }
            }
            return w
        }

        return """<select id="queryList" parameterClass="java.util.Map" resultClass="${context.getShortFieldName(table.name)}">
        <![CDATA[
		select
${getQueryStr()}
		from ${table.name} t where 1=1
		]]>
		<dynamic>
${getWhereStr()}
		</dynamic>
	</select>
"""
    }

    fun insert(table: TableContext): String {
        fun getColumns(): Array<String> {
            var names = "          "
            var values = "          "
            var temp = ""
            table.columnsList?.forEachIndexed { index, columnResult ->
                names += "${columnResult.name}"
                values += "#${context.getFieldName(columnResult.name)}#"
                temp += "#${context.getFieldName(columnResult.name)}#"
                if (index != table.columnsList!!.size - 1) {
                    names += ","
                    values += ","
                    temp += ","

                    if (temp.trim().length >= 80) {
                        names += "\n          "
                        values += "\n          "
                        temp = ""
                    }
                }
            }
            return arrayOf(names,values)
        }

        return """<insert id="insert" parameterClass="${context.getShortFieldName(table.name)}">
		<selectKey resultClass="int" keyProperty="${table.primaryKey}">
   			select SEQ_${table.name.toUpperCase()}.nextval from dual
		</selectKey>
		insert into ${table.name} (
${getColumns()[0]}
		) values (
${getColumns()[1]}
        )
	</insert>
"""
    }

    fun update(table: TableContext): String {
        fun getWhereStr(): String {
            var w = ""
            table.columnsList?.forEachIndexed { index, columnResult ->
                if (columnResult.name != table.primaryKey) {
                    w += "          <isNotEmpty prepend=\",\" property=\"${context.getFieldName(columnResult.name)}\"> t.${columnResult.name} = #${context.getFieldName(columnResult.name)}#</isNotEmpty>"
                    if (index != table.columnsList!!.size - 1) {
                        w += "\n"
                    }
                }
            }
            return w
        }

        return """<update id="update" parameterClass="${context.getShortFieldName(table.name)}">
		update ${table.name} t set t.${table.primaryKey} = #${context.getShortFieldName(table.primaryKey)}#
${getWhereStr()}
		where t.${table.primaryKey} = #${context.getShortFieldName(table.primaryKey)}#
	</update>
"""
    }

    fun delete(table: TableContext): String {
        fun getWhereStr(): String {
            var w = ""
            table.columnsList?.forEachIndexed { index, columnResult ->
                w += "          <isNotEmpty prepend=\"and\" property=\"${context.getFieldName(columnResult.name)}\"> t.${columnResult.name} = #${context.getFieldName(columnResult.name)}#</isNotEmpty>"
                if (index != table.columnsList!!.size - 1) {
                    w += "\n"
                }
            }
            return w
        }

        return """<delete id="delete" parameterClass="${context.getShortFieldName(table.name)}">
		delete from ${table.name} t where 1=1
${getWhereStr()}
	</delete>
"""
    }

}