package com.alibaba.druid.sql.parser;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

public class ExportTableAliasVisitor extends MySqlASTVisitorAdapter{
	private Map<String, SQLExpr> aliasMap = new HashMap<String, SQLExpr>();
    public boolean visit(SQLExpr x) {
        return true;
    }

    public Map<String, SQLExpr> getAliasMap() {
        return aliasMap;
    }
}
