package com.example.database;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;


import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SQL_Connection {
    private Connection connection;
    private String query;
    private Statement statement;
    private ResultSet resultSet;
    public SQL_Connection() {
        try {
            // Load the JDBC driver.
            Class.forName("org.sqlite.JDBC");
            // Connect to the database.
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
            CreateRelationsTable();
            CreateRelationTable();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }
    public void CreateRelationsTable() throws SQLException {
        if (!Arrays.asList(getTableNames()).contains("RelationsTable")) {
            SDUpdate("CREATE TABLE RelationsTable (id INTEGER, TableName varchar(255), Rid INTEGER, RTableName varchar(255), Relation varchar(255), AddRelations varchar(255))");
        }
    }
    public void CreateRelationTable() throws SQLException {
        if (!Arrays.asList(getTableNames()).contains("RelationsTable")) {
            SDUpdate("CREATE TABLE RelationTable (id INTEGER, TableName varchar(255), Rid INTEGER, RTableName varchar(255), Relation varchar(255)");
        }
    }
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void DoQuery() throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);
    }
    public ResultSet SDQuery(String query) throws SQLException {
        this.query = query;
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);
        return resultSet;
    }
    public void DoUpdate() throws SQLException {
        statement = connection.createStatement();
        statement.executeUpdate(query);
    }
    public void SDUpdate(String query) throws SQLException {
        this.query = query;
        statement = connection.createStatement();
        statement.executeUpdate(query);
    }
    public ResultSet getResultSet() {
        return resultSet;
    }
    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }
    public ResultSet CreateResultSet(String query) throws SQLException {
        return connection.createStatement().executeQuery(query);
    }
    public PreparedStatement CreatePreparedStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }
    public String[] getColumnNames(String tableName) throws SQLException {
        ResultSet rs = SDQuery("SELECT * FROM " + tableName);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        String[] columnNames = new String[columnsNumber];
        for (int i = 1; i <= columnsNumber; i++) {
            columnNames[i - 1] = rsmd.getColumnName(i);
        }
        return columnNames;
    }
    public String[] getColumnsTypes(String tableName) throws SQLException {
        ResultSet rs = SDQuery("SELECT * FROM " + tableName);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        String[] columnTypes = new String[columnsNumber];
        for (int i = 1; i <= columnsNumber; i++) {
            columnTypes[i - 1] = rsmd.getColumnTypeName(i);
        }
        return columnTypes;
    }
    public String[] getUseableColumnsNames(String tableName) throws SQLException {
        String[] columnNames = getColumnNames(tableName);
        ArrayList<String> useableColumnsNames = new ArrayList<>();
        for (String columnName : columnNames) {
            if (!columnName.equals("id") && !columnName.equals("info") && !columnName.equals("Documents") && !columnName.equals("Photos") && !columnName.equals("Videos") && !columnName.equals("Audio")) {
                useableColumnsNames.add(columnName);
            }
        }
        String[] result = new String[useableColumnsNames.size()];
        for (int i = 0; i < useableColumnsNames.size(); i++) {
            result[i] = useableColumnsNames.get(i);
        }
        return result;
    }
    public String[] getUseableColumnsTypes(String tableName) throws SQLException {
        ResultSet rs = SDQuery(getUseableQuery(tableName));
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        String[] columnTypes = new String[columnsNumber];
        for (int i = 1; i <= columnsNumber; i++) {
            columnTypes[i - 1] = rsmd.getColumnTypeName(i);
        }
        return columnTypes;
    }
    public String getUseableQuery(String tableName) throws SQLException {
        String[] columnNames = getUseableColumnsNames(tableName);
        StringBuilder query = new StringBuilder("SELECT ");
        for (int i = 0; i < columnNames.length; i++) {
            query.append(columnNames[i]);
            if (i != columnNames.length - 1) {
                query.append(", ");
            }
        }
        query.append(" FROM ").append(tableName);
        return query.toString();
    }
    public String[] getTableNames() throws SQLException {
        ArrayList<String> tables = new ArrayList<>();
        ResultSet TablesSet = CreateResultSet("SELECT name FROM sqlite_master WHERE type='table'");
        while (TablesSet.next()) {
            tables.add(TablesSet.getString("name"));
        }
        String[] result = new String[tables.size()];
        for (int i = 0; i < tables.size(); i++) {
            result[i] = tables.get(i);
        }
        return result;
    }
    public String[] getUseableTableNames() throws SQLException {
        ArrayList<String> tables = new ArrayList<>();
        ResultSet TablesSet = CreateResultSet("SELECT name FROM sqlite_master WHERE type='table'");
        while (TablesSet.next()) {
            if (!TablesSet.getString("name").equals("RelationTable") && !TablesSet.getString("name").equals("RelationsTable") && !TablesSet.getString("name").equals("sqlite_sequence")) {
                tables.add(TablesSet.getString("name"));
            }
        }
        String[] result = new String[tables.size()];
        for (int i = 0; i < tables.size(); i++) {
            result[i] = tables.get(i);
        }
        return result;
    }
    public String[] SeparateJSONColumnToArray(String tableName,  String column, int rowID) throws SQLException {
        ResultSet resaultSet = CreateResultSet("SELECT "+ column +" FROM " + tableName + " WHERE id = " + rowID);
        ArrayList<String> data = new ArrayList<>();
        while (resaultSet.next()) {
            String[] temp = resaultSet.getString(column).split(",");
            for (int i = 0; i < temp.length; i++) {
                if (temp[i].startsWith("[")) {
                    temp[i] = temp[i].substring(1);
                }
                if (temp[i].endsWith("]")) {
                    temp[i] = temp[i].substring(0, temp[i].length() - 1);
                }
                if (temp[i].contains("\"")) {
                    temp[i] = temp[i].replace("\"", "");
                }
            }
            data.addAll(Arrays.asList(temp));
        }
        String[] result = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            result[i] = data.get(i);
        }
        return result;
    }
    public String[] getDataByID(int id, String tableName) throws SQLException {
       ResultSet resaultSet = CreateResultSet("SELECT * FROM " + tableName + " WHERE id = " + id);
       int columnsNumber = resaultSet.getMetaData().getColumnCount();
        String[] data = new String[columnsNumber];
        while (resaultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                data[i - 1] = resaultSet.getString(i);
            }
        }
        return data;
    }
    public String[] getUseableDataByID(int id, String tableName) throws SQLException {
       ResultSet rs = CreateResultSet(getUseableQuery(tableName) + " WHERE id = " + id);
         int columnsNumber = rs.getMetaData().getColumnCount();
          String[] data = new String[columnsNumber];
          while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                 data[i - 1] = rs.getString(i);
                }
          }
          return data;
    }
    public void RemoveFromJSON(String tableName, String column, int rowID, int indexOfValue) throws SQLException {
        String sb = "UPDATE " + tableName + " SET " + column + " = " +
                "json_remove(" + column + ", '$[" + indexOfValue + "]')" + " WHERE id = " + rowID;
        System.out.println(sb);
        SDUpdate(sb);
    }
    public void AddToJSON(String tableName, String column, int rowID, String value) throws SQLException {
        String sb = "UPDATE " + tableName + " SET " + column + " = " +
                "json_insert(" + column + ", '$[#]', '" + value + "')" + " WHERE id = " + rowID;
        System.out.println(sb);
        SDUpdate(sb);
    }
    public void UpdateJSON(String tableName, String column, int rowID, int indexOfValue, String value) throws SQLException {
        String sb = "UPDATE " + tableName + " SET " + column + " = " +
                "json_replace(" + column + ", '$[" + indexOfValue + "]', '" + value + "')" + " WHERE id = " + rowID;
        System.out.println(sb);
        SDUpdate(sb);
    }
public String getJSON_Export(String tableName) throws SQLException {
    StringBuilder sb = new StringBuilder();
    String[] column_names = getColumnNames(tableName);
    int columnCount = column_names.length;
    sb.append("SELECT ");
    sb.append("id").append(", ");
    for (int i = 1; i < columnCount; i++) {

        if (column_names[i].equals("info")) {
            sb.append("'info'").append(", ");
        }
        else {
            sb.append("json_extract(").append(column_names[i]).append(", '$[0]')").append(", ");
        }
    }
    sb.delete(sb.length() - 2, sb.length());
    sb.append(" FROM ").append(tableName);
    sb.append(" WHERE ");
    return sb.toString();
}
    public ResultSet Search(String tableName, String[] columnNames, String[] text) throws SQLException {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> listText = new ArrayList<>();
        ArrayList<String> listColumns = new ArrayList<>();
        for (int i = 0; i < text.length; i++) {
            if (text[i] != null && !text[i].equals("")) {
                listText.add(text[i]);
                listColumns.add(columnNames[i]);
            }
        }
    if (listText.size() == 0) {
            System.out.println(getJSON_Export(tableName).replace("WHERE", ""));
            return SDQuery(getJSON_Export(tableName).replace("WHERE", ""));
        }
        for (int i = 0; i < listColumns.size(); i++) {
            if (i != 0) {
                sb.append(" AND ");
            }
            sb.append(listColumns.get(i)).append(" LIKE '%").append(listText.get(i).toLowerCase()).append("%'");
            sb.append(" OR ");
            sb.append(listColumns.get(i)).append(" LIKE '%").append(listText.get(i).toUpperCase()).append("%'");
            sb.append(" OR ");
            sb.append(listColumns.get(i)).append(" LIKE '%").append(listText.get(i).substring(0, 1).toUpperCase()).append(listText.get(i).substring(1)).append("%'");

        }
        System.out.println(getJSON_Export(tableName) + sb.toString());
        return SDQuery(getJSON_Export(tableName) + sb.toString());
    }
    public void close() throws SQLException {
        connection.close();
    }
    public void open() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
    }

    public void CreateTable(String Table_Name, String[] Column_Names) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(Table_Name).append(" (");
        sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        for (int i = 0; i < Column_Names.length; i++) {
            sb.append(Column_Names[i]).append(" varchar(255)");
            if (i != Column_Names.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        SDUpdate(sb.toString());
        System.out.println(sb.toString());
    }
    public void DropTable(String Table_Name) throws SQLException {
        SDUpdate("DROP TABLE IF EXISTS " + Table_Name);
    }

    public void AddColumn(String tableName, String columnName) throws SQLException {
        SDUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " varchar(255)");
    }
    public void RemoveColumn(String tableName, String columnName) throws SQLException {
        SDUpdate("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
    }
    public void RenameColumn(String tableName, String oldColumnName, String newColumnName) throws SQLException {
        SDUpdate("ALTER TABLE " + tableName + " RENAME COLUMN " + oldColumnName + " TO " + newColumnName);
    }
    public void RenameTable(String oldTableName, String newTableName) throws SQLException {
        SDUpdate("ALTER TABLE " + oldTableName + " RENAME TO " + newTableName);
    }
    public void RemoveRow(String tableName, int rowID) throws SQLException {
        SDUpdate("DELETE FROM " + tableName + " WHERE id = " + rowID);
    }
public void DeleteRelation(String tableName, int rowID, RelationRow relationRow) throws SQLException {
    String query = "DELETE FROM RelationTable WHERE " +
            "id = '" + rowID + "' AND " +
            "TableName = '" + tableName + "' AND " +
            "Rid = '" + relationRow.getId() + "' AND " +
            "RTableName = '" + relationRow.getTableName() + "' AND " +
            "Relation = '" + relationRow.getRelation() + "'";
    System.out.println(query);
    SDUpdate(query);
    }
    public void InsertRow(String tableName, String[] columnsNames, String[] data) throws SQLException {
        int count = 0;
        if (data.length == 0) {
            return;
        }
        if (columnsNames.length == 0) {
            return;
        }
        for (int i = 0; i < data.length; i++) {
            if (Objects.equals(data[i], "") || Objects.equals(data[i], " ")) {
                count++;
            }
        }
        if (count == data.length) {
            return;
        }
        System.out.println(count);
        System.out.println(data.length);
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (");
        for (int i = 0; i < columnsNames.length; i++) {
            sb.append(columnsNames[i]);
            if (i != columnsNames.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(") VALUES (");
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            if (i != 0) {
                values.append(", ");
            }
            values.append("'[\"").append(data[i]).append("\"]'");
        }
        sb.append(values).append(")");
        System.out.println(sb.toString());
        SDUpdate(sb.toString());
    }
    public ArrayList<RelationRow> getDataFromRelationsTable(String tableName, int rowId) throws SQLException {
        ResultSet resultSet = SDQuery("SELECT * FROM RelationsTable WHERE TableName = '" + tableName + "'"+ " AND id = " + rowId);
        ArrayList<RelationRow> rdata = new ArrayList<>();
        while (resultSet.next()) {
            rdata.add(new RelationRow(resultSet.getString("RTableName"), resultSet.getInt("Rid"), resultSet.getString("Relation")));
            ResultSet rs = SDQuery(getJSON_Export(resultSet.getString("RTableName"))+" id = " + resultSet.getInt("Rid"));
            while (rs.next()) {
               String[] values = new String[rs.getMetaData().getColumnCount() - 1];
                for (int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
                    values[i - 1] = rs.getString(i + 1);
                }
                rdata.get(rdata.size() - 1).setValues(values);
            }
        }
        for (RelationRow relationRow : rdata
             ) {
            System.out.println(relationRow.print());

        }
        return rdata;
    }
    public ArrayList<RelationRow> getDataFromRelationsTableFor(String tableName, String RelatedTableName, int rowId) throws SQLException {
        ResultSet resultSet = SDQuery("SELECT * FROM RelationsTable WHERE TableName = '" + tableName + "'"+ " AND id = " + rowId + " AND RTableName = '" + RelatedTableName + "'");
        ArrayList<RelationRow> rdata = new ArrayList<>();
        while (resultSet.next()) {
            rdata.add(new RelationRow(resultSet.getString("RTableName"), resultSet.getInt("Rid"), resultSet.getString("Relation")));
            ResultSet rs = SDQuery(getJSON_Export(resultSet.getString("RTableName"))+" id = " + resultSet.getInt("Rid"));
            while (rs.next()) {
                String[] values = new String[rs.getMetaData().getColumnCount() - 1];
                for (int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
                    values[i - 1] = rs.getString(i + 1);
                }
                rdata.get(rdata.size() - 1).setValues(values);
            }
        }
        for (RelationRow relationRow : rdata
        ) {
            System.out.println(relationRow.print());

        }
        return rdata;
    }
    /////////////////////////////////////////////////////
    public ArrayList<RelationRow> getDataFromRelationTable(String tableName, int rowId) throws SQLException {
        ResultSet resultSet = SDQuery("SELECT * FROM RelationTable WHERE TableName = '" + tableName + "'"+ " AND id = " + rowId);
        ArrayList<RelationRow> rdata = new ArrayList<>();
        while (resultSet.next()) {
            rdata.add(new RelationRow(resultSet.getString("RTableName"), resultSet.getInt("Rid"), resultSet.getString("Relation")));
            ResultSet rs = SDQuery(getJSON_Export(resultSet.getString("RTableName"))+" id = " + resultSet.getInt("Rid"));
            while (rs.next()) {
                String[] values = new String[rs.getMetaData().getColumnCount() - 1];
                for (int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
                    values[i - 1] = rs.getString(i + 1);
                }
                rdata.get(rdata.size() - 1).setValues(values);
            }
        }
        for (RelationRow relationRow : rdata
        ) {
            System.out.println(relationRow.print());

        }
        return rdata;
    }
    public ArrayList<RelationRow> getDataFromRelationTableFor(String tableName, String RelatedTableName, int rowId) throws SQLException {
        ResultSet resultSet = SDQuery("SELECT * FROM RelationTable WHERE TableName = '" + tableName + "'"+ " AND id = " + rowId + " AND RTableName = '" + RelatedTableName + "'");
        ArrayList<RelationRow> rdata = new ArrayList<>();
        while (resultSet.next()) {
            rdata.add(new RelationRow(resultSet.getString("RTableName"), resultSet.getInt("Rid"), resultSet.getString("Relation")));
            ResultSet rs = SDQuery(getJSON_Export(resultSet.getString("RTableName"))+" id = " + resultSet.getInt("Rid"));
            while (rs.next()) {
                String[] values = new String[rs.getMetaData().getColumnCount() - 1];
                for (int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
                    values[i - 1] = rs.getString(i + 1);
                }
                rdata.get(rdata.size() - 1).setValues(values);
            }
        }
        for (RelationRow relationRow : rdata
        ) {
            System.out.println(relationRow.print());

        }
        return rdata;
    }


    public void addRelation(String rootTableName, int rowID, RelationRow relationRow) throws SQLException {
        String query = "INSERT INTO RelationTable (TableName, id, RTableName, Rid, Relation) VALUES ('" + rootTableName + "', " + rowID + ", '" + relationRow.getTableName() + "', " + relationRow.getId() + ", '" + relationRow.getRelation() + "')";
        SDUpdate(query);
        System.out.println(query);
    }
    }
