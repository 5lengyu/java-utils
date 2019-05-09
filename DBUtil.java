import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MysqlTool
 */	
public class MysqlTool implements Serializable {
	/**
	 * This number is here for model snapshot storing purpose<br>
	 * It needs to be changed when this class gets changed
	 */ 
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(MysqlTool.class);
    public static int executeUpdate(Connection conn, String sql){
        PreparedStatement stmt = null;

        int updateCount;
        try {
            stmt = conn.prepareStatement(sql);
            updateCount = stmt.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException("Execute update statement:" + sql + ", has a failture.",e);
        }finally {
            close(stmt);
        }

        return updateCount;
    }


    public static List<Map<String, Object>> executeQuery(Connection conn, String sql){
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

        PreparedStatement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            ResultSetMetaData rsMeta = rs.getMetaData();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<String, Object>();

                for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
                    String columnName = rsMeta.getColumnLabel(i + 1);
                    Object value = rs.getObject(i + 1);
                    row.put(columnName, value);
                }

                rows.add(row);
            }
        } catch (Exception e){
            throw new RuntimeException("Query execute statement:" + sql + ", has failed.",e);
        }finally {
            MysqlTool.close(rs);
            MysqlTool.close(stmt);
        }

        return rows;
    }
    
    public static <T> T getAValue(Connection conn, String sql, Class<T> tClass) {
        PreparedStatement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            ResultSetMetaData rsMeta = rs.getMetaData();

            if(rs.next()) {
                T value = tClass.cast(rs.getObject(1));
                return value;
            }else {
            	return null;
            }
        } catch (SQLException e){
            throw new RuntimeException("Get a value execute statement:" + sql + ", has failed.",e);
        }finally {
            MysqlTool.close(rs);
            MysqlTool.close(stmt);
        }

    }
    
    public static String getSkuBand(Connection conn,int sku) {
    	String bandQuery="select BAND from sku_band where SKU_SEQ=" + sku;
    	return MysqlTool.getAValue(conn, bandQuery, String.class);
    }

    public static void execute(Connection conn, String sql){
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
            //System.out.println(sql);
        } catch (Exception e){
            throw new RuntimeException("Execute sql statement:" + sql + ", has failed.",e);
        }finally {
            MysqlTool.close(stmt);
        }
    }

    public static void close(PreparedStatement x) {
        if (x != null) {
            try {
                x.close();
            } catch (Exception var2) {
                log.debug("close statement error", var2);
            }

        }
    }

    public static void close(java.sql.ResultSet x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
            log.debug("close result set error", e);
        }
    }

}
