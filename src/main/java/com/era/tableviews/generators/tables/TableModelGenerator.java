/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era.tableviews.generators.tables;

import com.era.logger.LoggerUtility;
import com.era.repositories.utils.HibernateUtil;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

/**
 *
 * @author PC
 */
public class TableModelGenerator {
    
    final String classesPath = "../era_views/src/main/java/com/era/views/tables/tablemodels";
    private static TableModelGenerator TableModelGenerator;
    
    
    public static TableModelGenerator getSingleton(){
        if(TableModelGenerator == null){
            TableModelGenerator = new TableModelGenerator();
        }
        return TableModelGenerator;
    }
    
    public void createClass(final Class Class_, final String name) throws Exception {
        
        final String newName = name.substring(0, 1).toUpperCase() + name.substring(1);
        final String finalClassName = newName + "TableModel.java";
        final String finalPath = classesPath + "/" + finalClassName;
        
        final File File = new File(finalPath);
        
        //First delete if exists
        if(File.exists()){
            LoggerUtility.getSingleton().logInfo(TableModelGenerator.class, "Table class " + finalPath + " exists");
            File.delete();
            LoggerUtility.getSingleton().logInfo(TableModelGenerator.class, "Table class " + finalPath + " deleted");
        }
        
        //Create it
        if (File.createNewFile())
        {
            LoggerUtility.getSingleton().logInfo(TableModelGenerator.class, "Table class " + finalPath + " created succesfully");
        } else {
            LoggerUtility.getSingleton().logInfo(TableModelGenerator.class, "Table class " + finalPath + " not created");
        }

        //Get all the properties of that table
        final SessionFactory SessionFactory = HibernateUtil.getSingleton().getSessionFactoryLocal();
        final ClassMetadata classMetadata = SessionFactory.getClassMetadata(Class_);
        final String[] propertyNames = classMetadata.getPropertyNames();
        
        try ( //Write the class
            FileWriter writer = new FileWriter(File)) {
            final String content = this.getContentOfClas(Class_, newName, propertyNames);
            writer.write(content);
        }        
    }
    
    private String getContentOfClas(final Class Class_, final String className, String[] propertyNames) throws Exception {
        
        final String tableClass = className + "TableModel";
        
        final StringBuffer StringBuffer = new StringBuffer(
                                    "package com.era.views.tables.tablemodels;\n\n" +
                                    "import com.era.views.tables.headers.ColumnTable;\n" +
                                    "import java.util.List;\n" +
                                    "import com.era.models." + className + ";\n" +
                                    "import com.era.views.tables.headers.TableHeaderFactory;\n" +                
                                    "import com.era.views.abstracttablesmodel.BaseAbstractTableModel;\n\n" +
                                    "public class " + tableClass + "  extends BaseAbstractTableModel {\n\n" +
                                    "   public " + tableClass + "(List<?> items, final List<ColumnTable> header){\n" +
                                    "       super(items,header);\n\n" +
                                    "       this.GetValueAt = (int rowIndex, int columnIndex, String valueColumn, final Object model) -> {\n\n" +
                                    "           final " + className + " " + className + " = (" + className + ") model;\n\n" +
                                    "           String returnValue = \"\";\n");
        StringBuffer.append(        "           if(valueColumn.compareTo(TableHeaderFactory.getSigleton().get" + className + "sTableHeader().getROWNUMBER().getValue())==0){\n" +
                                    "               returnValue = String.valueOf(rowIndex + 1);\n" +
                                    "            }\n");                                    
        for(String property:propertyNames){
            
            //Get field metadata
            final Field field = Class_.getDeclaredField(property);
            final Column Column = field.getAnnotation(Column.class);
            final boolean nullable = Column.nullable();
            
            final String allPropertyMayus = property.toUpperCase();
            final String propertyFirstMayus = property.substring(0, 1).toUpperCase() + property.substring(1);
            
            final String typeClassName = field.getType().getName();
            if(typeClassName.contains("boolean")){
                StringBuffer.append("           else if(valueColumn.compareTo(TableHeaderFactory.getSigleton().get" + className + "sTableHeader().get" + allPropertyMayus + "().getValue())==0){\n" +
                                    "               returnValue = " + className + ".is" + propertyFirstMayus + "()? \"Si\":\"No\";\n" +
                                    "           }\n");
            }                
            else if(field.getType() == Date.class){
                StringBuffer.append("           else if(valueColumn.compareTo(TableHeaderFactory.getSigleton().get" + className + "sTableHeader().get" + allPropertyMayus + "().getValue())==0){\n" +
                                    "               returnValue = " + className + ".get" + propertyFirstMayus + "().toString();\n" +
                                    "           }\n");
            }
            else if(field.getType() == Timestamp.class){
                StringBuffer.append("           else if(valueColumn.compareTo(TableHeaderFactory.getSigleton().get" + className + "sTableHeader().get" + allPropertyMayus + "().getValue())==0){\n" +
                                    "               returnValue = " + className + ".get" + propertyFirstMayus + "().toString();\n" +
                                    "           }\n");
            }            
            else if(typeClassName.toLowerCase().contains("float")){
                StringBuffer.append("           else if(valueColumn.compareTo(TableHeaderFactory.getSigleton().get" + className + "sTableHeader().get" + allPropertyMayus + "().getValue())==0){\n" +
                                    "               returnValue = String.valueOf(" + className + ".get" + propertyFirstMayus + "());\n" +
                                    "           }\n");
            }
            else if(typeClassName.toLowerCase().contains("double")){
                StringBuffer.append("           else if(valueColumn.compareTo(TableHeaderFactory.getSigleton().get" + className + "sTableHeader().get" + allPropertyMayus + "().getValue())==0){\n" +
                                    "               returnValue = String.valueOf(" + className + ".get" + propertyFirstMayus + "());\n" +
                                    "           }\n");
            }
            else if(typeClassName.toLowerCase().contains("int")){
                StringBuffer.append("           else if(valueColumn.compareTo(TableHeaderFactory.getSigleton().get" + className + "sTableHeader().get" + allPropertyMayus + "().getValue())==0){\n" +
                                    "               returnValue = String.valueOf(" + className + ".get" + propertyFirstMayus + "());\n" +
                                    "           }\n");
            }
            else if(field.getType() == BigDecimal.class){
                StringBuffer.append("           else if(valueColumn.compareTo(TableHeaderFactory.getSigleton().get" + className + "sTableHeader().get" + allPropertyMayus + "().getValue())==0){\n" +
                                    "               returnValue = " + className + ".get" + propertyFirstMayus + "().toString();\n" +
                                    "           }\n");
            }
            else {
                StringBuffer.append("           else if(valueColumn.compareTo(TableHeaderFactory.getSigleton().get" + className + "sTableHeader().get" + allPropertyMayus + "().getValue())==0){\n" +
                                    "               returnValue = " + className + ".get" + propertyFirstMayus + "();\n" +
                                    "           }\n");
            }
        }
        StringBuffer.append(        "           return returnValue;\n" +
                                    "       };\n" + 
                                    "   }\n" +
                                    "}");
        
        return StringBuffer.toString();
    }
}
