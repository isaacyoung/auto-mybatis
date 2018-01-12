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

            val str = """
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE sqlMap  PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN" "http://ibatis.apache.org/dtd/sql-map-2.dtd">
<sqlMap namespace="$fullClass">

    <typeAlias alias="$aliasName" type="$fullClass" />

    <!-- 查询 -->
    ${queryPageList(it)}

	<select id="queryPageCount" parameterClass="java.util.Map" resultClass="int">
		<![CDATA[
		select count(*) from TB_STAT_ORDER_DAILY t where 1=1
		]]>
		<dynamic>
			<isNotEmpty prepend="and" property="statDate"> t.STAT_DATE = #statDate#</isNotEmpty>
			<isNotEmpty prepend="and" property="orderCount"> t.ORDER_COUNT = #orderCount#</isNotEmpty>
			<isNotEmpty prepend="and" property="orderAmount"> t.ORDER_AMOUNT = #orderAmount#</isNotEmpty>
			<isNotEmpty prepend="and" property="orderAverage"> t.ORDER_AVERAGE = #orderAverage#</isNotEmpty>
			<isNotEmpty prepend="and" property="onlineSchools"> t.ONLINE_SCHOOLS = #onlineSchools#</isNotEmpty>
			<isNotEmpty prepend="and" property="onlineSchoolDay"> t.ONLINE_SCHOOL_DAY = #onlineSchoolDay#</isNotEmpty>
		</dynamic>
	</select>

	<!-- 新增 -->
	<insert id="save" parameterClass="orderDaily">
		<selectKey resultClass="int" keyProperty="id">
   			select SEQ_TB_STAT_ORDER_DAILY.nextval from dual
		</selectKey>
		insert into TB_STAT_ORDER_DAILY
		  (ID,STAT_DATE,ORDER_COUNT, ORDER_AMOUNT, ORDER_AVERAGE, ONLINE_SCHOOLS, ONLINE_SCHOOL_DAY)
		values
		  (#id#,#statDate#,#orderCount#, #orderAmount#, #orderAverage#, #onlineSchools#, #onlineSchoolDay#)
	</insert>

	<!-- 修改 -->
	<update id="update" parameterClass="orderDaily">
		update TB_STAT_ORDER_DAILY t set t.STAT_DATE = #statDate#
		<isNotEmpty prepend="," property="orderCount">t.ORDER_COUNT=#orderCount# </isNotEmpty>
		<isNotEmpty prepend="," property="orderAmount">t.ORDER_AMOUNT=#orderAmount# </isNotEmpty>
		<isNotEmpty prepend="," property="orderAverage">t.ORDER_AVERAGE=#orderAverage# </isNotEmpty>
		<isNotEmpty prepend="," property="onlineSchools">t.ONLINE_SCHOOLS=#onlineSchools# </isNotEmpty>
		<isNotEmpty prepend="," property="onlineSchoolDay">t.ONLINE_SCHOOL_DAY=#onlineSchoolDay# </isNotEmpty>
		where t.ID = #id#
	</update>

    <!-- 删除 -->
	<delete id="delete" parameterClass="java.util.Map">
		delete from tb_salary_detail where 1=1
		<isNotEmpty prepend="and" property="detailId">
			detail_id = #detailId#
		</isNotEmpty>
		<isNotEmpty prepend="and" property="companyCode">
			company_code = #companyCode#
		</isNotEmpty>
		<isNotEmpty prepend="and" property="batchNo">
			batch_no = #batchNo#
		</isNotEmpty>
	</delete>

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

        return """
    <select id="queryPageList" parameterClass="java.util.Map" resultClass="${context.getShortFieldName(table.name)}">
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
		order by t.STAT_DATE desc ) t1 ) t2 where t2.rn>=#startRow# and t2.rn<=#endRow#
		]]>
	 </select>
"""
    }


}