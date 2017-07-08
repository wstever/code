package mybatisMapperFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class MapperFactory {

    private static Map<String, String> javaSqlTypeTransferMap;
    private static Properties          prop;
    private static String              folder;

    static {
        javaSqlTypeTransferMap = new HashMap<String, String>();
        javaSqlTypeTransferMap.put("int", "java.lang.Integer");
        javaSqlTypeTransferMap.put("varchar", "java.lang.String");
        javaSqlTypeTransferMap.put("char", "java.lang.String");
        javaSqlTypeTransferMap.put("decimal", "java.math.BigDecimal");
        javaSqlTypeTransferMap.put("datetime", "java.util.Date");
        javaSqlTypeTransferMap.put("timestamp", "java.util.Date");
        javaSqlTypeTransferMap.put("text", "java.lang.String");
        javaSqlTypeTransferMap.put("tinyint", "java.lang.Integer");
        javaSqlTypeTransferMap.put("longtext", "java.lang.Object");
        javaSqlTypeTransferMap.put("bigint", "java.lang.Long");
        javaSqlTypeTransferMap.put("mediumtext", "java.lang.String");
        javaSqlTypeTransferMap.put("smallint", "java.lang.Integer");
        prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("config/config.properties");
        try {
            prop.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File("MapperFacatory.java");
        String filePath = file.getAbsolutePath();
        int idx = filePath.lastIndexOf("/");
        folder = filePath.substring(0, idx + 1) + "src/items/";
    }

    public void mapper() {
        this.delGeneratedFiels();

        String url = prop.getProperty("url");
        String db = prop.getProperty("db");
        String user = prop.getProperty("user");
        String pass = prop.getProperty("pass");
        String basePackage = prop.getProperty("package.base");
        String daoReadWrite = prop.getProperty("dao.read.write");

        String entityPackage = basePackage + ".entity";
        String daoPackage = basePackage + ".dao." + daoReadWrite;
        String modelPackage = basePackage + ".model";
        String servicePackage = basePackage + ".service";
        String sessionFactoryBeanName = prop.getProperty("sql.session.factory");
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet dbNameRs = st.executeQuery(
                "SELECT distinct c.table_name FROM INFORMATION_SCHEMA.COLUMNS c where c.table_schema = '"
                                                 + db + "' ");
            List<String> includeTables = this.getIncludeTables(prop.getProperty("include.tables"));
            while (dbNameRs.next()) {
                if ((includeTables != null && includeTables.contains(dbNameRs.getString(1)))
                    || includeTables == null || "".equals(includeTables)) {
                    this.mapperTable(db, dbNameRs.getString(1), con, daoPackage, entityPackage,
                        sessionFactoryBeanName, modelPackage, servicePackage);
                }
            }

            dbNameRs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            try {
                if (con != null)
                    con.close();
            } catch (SQLException e1) {
            }
            e.printStackTrace();
        }
    }

    private void delGeneratedFiels() {
        File file = new File(folder);
        if (file.exists()) {
            File[] subs = file.listFiles();
            if (subs != null && subs.length > 0) {
                for (File sub : subs) {
                    sub.delete();
                }
            }
        }

    }

    private String upcaseFirstLetter(String source) {
        String upcase = source.substring(0, 1).toUpperCase();
        return upcase + source.substring(1);
    }

    private List<String> getIncludeTables(String property) {
        List<String> ret = null;
        if (property != null && property.length() > 0) {
            ret = Arrays.asList(property.split(","));
        }
        return ret;
    }

    private void mapperTable(String db, String tableName, Connection con, String namespacePac,
                             String entityPac, String sessionFactoryBeanName, String modelPackage,
                             String servicePackage) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT DISTINCT c.column_name, c.DATA_TYPE, c.column_comment,c.IS_NULLABLE,c.CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS c WHERE c.table_name = '"
                                           + tableName + "'");
            System.out.println(
                "========================" + tableName + "===============================");
            List<Column> columns = new ArrayList<Column>();
            while (rs.next()) {
                columns.add(this.getColumn(rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getInt(5)));
            }
            ResultSet keyRs = st
                .executeQuery("SELECT k.column_name FROM information_schema.table_constraints t "
                              + " JOIN information_schema.key_column_usage k "
                              + " USING(constraint_name,table_schema,table_name) "
                              + " WHERE t.constraint_type='PRIMARY KEY' " + " AND t.table_schema='"
                              + db + "'  AND t.table_name='" + tableName + "'");
            String keyColumn = null;
            if (keyRs.next()) {
                keyColumn = keyRs.getString(1).toLowerCase();
            }

            ResultSet tableCommentRs = st.executeQuery(
                "SELECT TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE table_schema='" + db
                                                       + "' AND table_name='" + tableName + "'");
            String tableComment = null;
            if (tableCommentRs.next()) {
                tableComment = tableCommentRs.getString(1);
            }

            Table table = this.createTable(namespacePac, entityPac, tableName, columns, keyColumn,
                tableComment, sessionFactoryBeanName, modelPackage, servicePackage);

            this.createMapperFile(table);
            this.createEntityFile(table);
            this.createDaoFile(table);
            this.createModelFile(table);
            this.createServiceFile(table);
            this.createServiceImplFile(table);
            this.createListHtmlImplFile(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createListHtmlImplFile(Table table) {
        try {
            String templateFileName = "htmlTemplate.ftl";
            Template template = this.getTemplateCfg().getTemplate(templateFileName);
            File xmlFile = new File(folder + table.getEntityName().toLowerCase() + ".ftl");
            Writer out = new FileWriter(xmlFile);
            template.process(table, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createServiceImplFile(Table table) {
        try {
            String templateFileName = "serviceImplTemplate.ftl";
            Template template = this.getTemplateCfg().getTemplate(templateFileName);
            File xmlFile = new File(folder + table.getServiceName() + "Impl.java");
            Writer out = new FileWriter(xmlFile);
            template.process(table, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createServiceFile(Table table) {
        try {
            String templateFileName = "serviceTemplate.ftl";
            Template template = this.getTemplateCfg().getTemplate(templateFileName);
            File xmlFile = new File(folder + "I" + table.getServiceName() + ".java");
            Writer out = new FileWriter(xmlFile);
            template.process(table, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createModelFile(Table table) {
        try {
            String templateFileName = "modelTemplate.ftl";
            Template template = this.getTemplateCfg().getTemplate(templateFileName);
            File xmlFile = new File(folder + table.getModelName() + ".java");
            Writer out = new FileWriter(xmlFile);
            template.process(table, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDaoFile(Table table) {
        try {
            String templateFileName = "daoTemplate.ftl";
            Template template = this.getTemplateCfg().getTemplate(templateFileName);
            File javaFile = new File(folder + table.getDaoName() + ".java");
            Writer out = new FileWriter(javaFile);
            template.process(table, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createEntityFile(Table table) {
        try {
            String templateFileName = "entityTemplate.ftl";
            Template template = this.getTemplateCfg().getTemplate(templateFileName);
            File javaFile = new File(folder + table.getEntityName() + ".java");
            Writer out = new FileWriter(javaFile);
            template.process(table, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Table createTable(String namespacePac, String entityPac, String tableName,
                              List<Column> columns, String keyColumn, String tableComment,
                              String sessionFactoryBeanName, String modelPackage,
                              String servicePackage) {
        Table table = new Table();
        table.setColumns(columns);
        if (keyColumn != null) {
            table.setKeyColumn(keyColumn);
        }

        String daoReadWrite = prop.getProperty("dao.read.write");
        String nameTail = "";
        if ("write".equals(daoReadWrite)) {
            nameTail = "Write";
        }
        table.setNamespacePackage(namespacePac);
        table.setDaoName(this.getBeanNameFromTalbeName(tableName) + nameTail + "Dao");
        table.setResultMapId(this.getBeanNameFromTalbeName(tableName) + "Result");
        table.setEntityPackage(entityPac);
        table.setEntityName(this.getBeanNameFromTalbeName(tableName));
        table.setTableName(tableName);
        table.setTableComment(tableComment);
        table.setSessionFactoryBeanName(sessionFactoryBeanName);
        table.setModelName(this.getBeanNameFromTalbeName(tableName) + "Model");
        table.setModelPackage(modelPackage);
        table.setServiceName(this.getBeanNameFromTalbeName(tableName) + "Service");
        table.setServicePackage(servicePackage);
        return table;
    }

    private Configuration getTemplateCfg() {
        try {
            // Initialize configuration;
            Configuration cfg = new Configuration();

            cfg.setDirectoryForTemplateLoading(new File(this.getTemplateLocation()));
            cfg.setTemplateUpdateDelay(0);
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            //Use beans wrapper (recommmended for most applications)
            cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
            cfg.setDefaultEncoding("UTF-8");
            //charset of the output
            cfg.setOutputEncoding("UTF-8");
            //default locale
            cfg.setLocale(Locale.US);
            return cfg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTemplateLocation() {
        try {
            File f = new File(this.getClass().getName());
            String path = f.getAbsolutePath();
            path = path.substring(0, path.lastIndexOf(File.separator)) + "/src/mybatisMapperFactory";
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createMapperFile(Table table) {
        try {
            String templateFileName = "mapperTemplate.ftl";
            Template template = this.getTemplateCfg().getTemplate(templateFileName);

            String daoReadWrite = prop.getProperty("dao.read.write");
            String nameTail = "";
            if ("write".equals(daoReadWrite)) {
                nameTail = "Write";
            }

            File mapperFile = new File(folder + this
                .getBeanNameFromTalbeName(table.getTableName() + nameTail + "Mapper.xml"));
            Writer out = new FileWriter(mapperFile);
            template.process(table, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Column getColumn(String dbColumn, String colType, String colComment, String isNullable,
                             Integer characterMaximumLength) {
        Column col = new Column();
        col.setDbColumn(this.getDbColumn(dbColumn));
        col.setBeanField(this.getBeanFieldFromDbColumn(dbColumn));
        if (javaSqlTypeTransferMap.get(colType) == null
            || "".equals(javaSqlTypeTransferMap.get(colType))) {
            System.out.println(
                "colType=----------------------------" + colType + "--------------" + dbColumn);
        }
        col.setColJavaType(javaSqlTypeTransferMap.get(colType));
        col.setColDbType(colType);
        col.setColComment(colComment);
        col.setIsNullable(isNullable);
        col.setCharacterMaximumLength(characterMaximumLength);
        return col;
    }

    private String getDbColumn(String dbColumn) {
        String ret = null;
        if (dbColumn.indexOf("_") != -1) {
            ret = dbColumn.toLowerCase();
        } else {
            ret = dbColumn;
        }
        return ret;
    }

    private String getBeanFieldFromDbColumn(String dbColumn) {
        System.out.println(dbColumn);
        String ret = null;
        if (dbColumn.indexOf("_") != -1) {
            String[] pieces = dbColumn.toLowerCase().split("_");
            StringBuffer sb = new StringBuffer(pieces[0]);
            if (pieces.length > 1) {
                for (int i = 1; i < pieces.length; i++) {
                    sb.append(String.valueOf(pieces[i].charAt(0)).toUpperCase())
                        .append(pieces[i].substring(1));
                }
            }
            ret = sb.toString();
        } else {
            ret = dbColumn;
        }
        return ret;
    }

    private String getBeanNameFromTalbeName(String tableName) {
        String[] pieces = tableName.split("_");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < pieces.length; i++) {
            sb.append(String.valueOf(pieces[i].charAt(0)).toUpperCase())
                .append(pieces[i].substring(1));
        }
        return sb.toString();
    }
}
