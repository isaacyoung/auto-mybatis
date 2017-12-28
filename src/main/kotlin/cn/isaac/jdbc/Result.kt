package cn.isaac.jdbc

/**
 *
 * create by isaac at 2017/12/28 15:55
 */

data class TableResult(var name:String, var comments: String?)
data class ColumnResult(var tableName: String, var name: String, var dataType: String, var dataLength: Int, var comments: String?)
