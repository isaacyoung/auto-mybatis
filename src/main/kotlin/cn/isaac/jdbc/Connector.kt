package cn.isaac.jdbc

/**
 *
 * create by isaac at 2017/12/26 8:53
 */
interface Connector {

    fun getTables(tableName: String): List<TableResult>

    fun getColumns(tableName: String): List<ColumnResult>

}