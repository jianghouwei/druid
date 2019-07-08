package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlIntervalExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class SqlParserTest  extends TestCase {

    protected final static Log log= LogFactory.getLog(SqlParserTest.class);

    /**
     *
     * 只转换MySql 查询语句
     * @author jhw
     * @createDate 创建时间：2019年7月3日 下午2:29:11
     */
    public static String translateMySqlToSqlServer(String sql) {
        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);
        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);
        for(SQLStatement sqlStatement : stmtList){
            translateMySqlToSqlServer(sqlStatement);
            sqlStatement.accept(visitor);
        }
        String mysqlSql = out.toString();
        log.info(mysqlSql);
        return mysqlSql;

    }


    public static SQLStatement translateMySqlToSqlServer(SQLStatement sqlStatement){
        if(sqlStatement instanceof SQLSelectStatement){
            SQLSelectStatement sqlSelectStatement =(SQLSelectStatement) sqlStatement;
            SQLSelectQuery sqlSelectQuery  = sqlSelectStatement.getSelect().getQuery();
            if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
                // 非联合查询
                SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
                List<SQLSelectItem> selectItems = sqlSelectQueryBlock.getSelectList();
                for(SQLSelectItem selectItem : selectItems){
                    SQLExpr sqlExpr = selectItem.getExpr();
                    translateSQLExpr(sqlExpr);
                }
                // 获取where条件
                SQLExpr where = sqlSelectQueryBlock.getWhere();
                translateSQLExpr(where);
                // 获取分组
                SQLSelectGroupByClause groupBy = sqlSelectQueryBlock.getGroupBy();
                if(groupBy != null){
                    List<SQLExpr> groups = groupBy.getItems();
                    for(SQLExpr sqlExpr : groups){
                        translateSQLExpr(sqlExpr);
                    }
                    SQLExpr having = groupBy.getHaving();
                    if(having != null){
                        translateSQLExpr(having);
                    }
                }
                // 一般 排序是不会携带函数或者其他 但是我们这里就是有
            }else if(sqlSelectQuery instanceof SQLUnionQuery){
                // 联合查询
                //SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) sqlSelectQuery;
            }
        }
        return sqlStatement;
    }










    /**
     * SQL 字段解析
     *
     * @author jhw
     * @createDate 创建时间：2019年7月4日 上午8:27:58
     */
    public static SQLExpr translateSQLExpr(SQLExpr sqlExpr){

        if(sqlExpr instanceof SQLMethodInvokeExpr){
            SQLMethodInvokeExpr methodInvoke = (SQLMethodInvokeExpr) sqlExpr;
            List<SQLExpr> list  = methodInvoke.getParameters();
            if(list != null && !list.isEmpty()){
                translateSQLExprs(list);
            }
            replaceSqlExpr(methodInvoke);
        }else if(sqlExpr instanceof SQLIdentifierExpr){
            // SQLIdentifierExpr identifier = (SQLIdentifierExpr) sqlExpr;
        }else if(sqlExpr instanceof SQLPropertyExpr){
            // SQLPropertyExpr property = (SQLPropertyExpr) sqlExpr;
        }else if(sqlExpr instanceof SQLBinaryOpExpr){// 二元查询
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
            List<SQLExpr> list = SQLUtils.split(sqlBinaryOpExpr);
            if(list != null && !list.isEmpty()){
                translateSQLExprs(list);
            }
        }else if (sqlExpr instanceof SQLSubqueryTableSource){ //子查询

        }
        return sqlExpr;
    }




    /**
     * SQL 字段解析
     *
     * @author jhw
     * @createDate 创建时间：2019年7月4日 上午8:27:58
     */
    public static void translateSQLExprs(List<SQLExpr> sqlExprs){
        for(SQLExpr sqlExpr : sqlExprs){
            translateSQLExpr(sqlExpr) ;
        }
    }

    /**
     *  没有子函数才调用替换函数
     * @param methodInvoke
     * @return
     */
    public static SQLExpr replaceSqlExpr(SQLMethodInvokeExpr methodInvoke){
        //只解析一层
        String methodName = methodInvoke.getMethodName().toLowerCase();
        if("now".equals(methodName)){// 时间解析
            methodInvoke.setMethodName("GETDATE");
        }else if("date_sub".equals(methodName)){
            methodInvoke.setMethodName("DATEADD");
            List<SQLExpr> sqlExprs = methodInvoke.getParameters();
            MySqlIntervalExpr expr = (MySqlIntervalExpr) sqlExprs.get(1);
            String searchKey = expr.getUnit().name();
            SqlDateAddEnum subEnum = SqlDateAddEnum.getEnumByKey(searchKey.toLowerCase());
            if(subEnum != null){
                SQLIdentifierExpr arg0 = new SQLIdentifierExpr();
                arg0.setName(subEnum.getReplacKey());
                methodInvoke.addParameter(arg0);
                SQLIntegerExpr param1_1 = (SQLIntegerExpr) expr.getValue();
                SQLIntegerExpr arg1 = new SQLIntegerExpr();
                arg1.setNumber(-(param1_1.getNumber().intValue()));
                methodInvoke.addParameter(arg1);
                methodInvoke.addParameter(sqlExprs.get(0));
                methodInvoke.getParameters().remove(0);
                methodInvoke.getParameters().remove(0);
            }else{
                log.info( "date_add==========>" + "没有找到对应的转换格式");
            }
        }else if("date_add".equals(methodName)){
            methodInvoke.setMethodName("DATEADD");
            List<SQLExpr> sqlExprs = methodInvoke.getParameters();
            MySqlIntervalExpr expr = (MySqlIntervalExpr) sqlExprs.get(1);
            String searchKey = expr.getUnit().name();
            SqlDateAddEnum addEnum = SqlDateAddEnum.getEnumByKey(searchKey.toLowerCase());
            if(addEnum != null){
                SQLIdentifierExpr arg0 = new SQLIdentifierExpr();
                arg0.setName(addEnum.getReplacKey());
                methodInvoke.addParameter(arg0);
                methodInvoke.addParameter(expr.getValue());
                methodInvoke.addParameter(sqlExprs.get(0));
                methodInvoke.getParameters().remove(0);
                methodInvoke.getParameters().remove(0);
            }else{
                log.info( "date_add==========>" + "没有找到对应的转换格式");
            }

        }else if("date_format".equals(methodName)){
            List<SQLExpr> sqlExprs = methodInvoke.getParameters();
            SQLExpr sqlExpr =  sqlExprs.get(0);
            SQLCharExpr charExpr = (SQLCharExpr) sqlExprs.get(1);
            String key = charExpr.getText();
            SqlDateFormatEnum enumFormat = SqlDateFormatEnum.getEnumByKey(key);
            if(enumFormat == null){
                log.info( "date_format==========>" + "没有找到对应的转换格式");
            }else{
                String[] strs = enumFormat.getReplacKey().split("_");
                SQLMethodInvokeExpr param1 = new SQLMethodInvokeExpr(strs[0]);
                SQLIntegerExpr param1_1 = new SQLIntegerExpr(Integer.valueOf(strs[1]));
                param1.addParameter(param1_1);
                methodInvoke.addParameter(param1);
                methodInvoke.addParameter(sqlExpr);
                methodInvoke.addParameter(new SQLIntegerExpr(Integer.valueOf(strs[2])));
                methodInvoke.setMethodName("CONVERT");
                methodInvoke.getParameters().remove(0);
                methodInvoke.getParameters().remove(0);
            }

        }else if("ifnull".equals(methodName)){
            methodInvoke.setMethodName("isnull");
        }else if("CAST".equals(methodName)){
            methodInvoke.getParameters();
        }
        return methodInvoke;
    }




    public static String findSqlKeyWord(String sql) {
        return translateMySqlToSqlServer(sql);
    }

    public void test_lexer() throws Exception{

         String sql = "" ;
         findSqlKeyWord(sql);
//        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);
//        StringBuilder out = new StringBuilder();
//        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);
////        ExportTableAliasVisitor visitor = new ExportTableAliasVisitor();
//        for (SQLStatement stmt : stmtList) {
//            stmt.accept(visitor);
//        }
//        visitor.getParameters();
//        visitor.getAppender();

    }

    


}
