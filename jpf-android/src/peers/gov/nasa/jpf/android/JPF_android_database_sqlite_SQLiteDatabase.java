package gov.nasa.jpf.android;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;

public class JPF_android_database_sqlite_SQLiteDatabase  extends NativePeer {

  private static Map<String, List<ColumnDefinition>> tables = new HashMap<String, List<ColumnDefinition>>();

  private static CCJSqlParserManager pm = new CCJSqlParserManager();
  private static net.sf.jsqlparser.statement.Statement statement = null;

  @MJI
  public int parseSQL(MJIEnv env,int objectRef, int SQL) {

    try {
      statement = pm.parse(new StringReader(env.getStringObject(SQL)));
    } catch (JSQLParserException e) {
      System.out.println(e.getMessage());
      return -1;
    }
    /*
     * now you should use a class that implements StatementVisitor to decide
     * what to do
     * based on the kind of the statement, that is SELECT or INSERT etc. but
     * here we are only
     * interested in SELECTS
     */
    if (statement instanceof Select) {
      Select selectStatement = (Select) statement;
      //      TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
      //      List tableList = tablesNamesFinder.getTableList(selectStatement);
      //      for (Iterator iter = tableList.iterator(); iter.hasNext();) {
      //        System.out.println(iter.next());
      //      }

      //      List columnList = tablesNamesFinder.getColumns(selectStatement);
      //      if (columnList != null)
      //        for (Iterator iter = columnList.iterator(); iter.hasNext();) {
      //          System.out.println(iter.next());
      //        }
    } else if (statement instanceof CreateTable) {
      CreateTable create = (CreateTable) statement;
      tables.put(create.getTable().getWholeTableName(), create.getColumnDefinitions());
    }
    return (statement instanceof Select) ? 1 : 2;
  }

  @MJI
  public int getColumns(MJIEnv env, int objectRef, int tablename) {
    String tableName = env.getStringObject(tablename);
    List<ColumnDefinition> columns = tables.get(tableName);
    String[] arrColumns = new String[columns.size()];
    for (int i = 0; i < columns.size(); i++) {
      arrColumns[i] = columns.get(i).getColumnName();
    }

    return env.newStringArray(arrColumns);
  }
}
