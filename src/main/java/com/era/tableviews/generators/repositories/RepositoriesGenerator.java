/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era.tableviews.generators.repositories;

import com.era.logger.LoggerUtility;
import com.era.repositories.utils.HibernateUtil;
import com.era.utilities.UtilitiesFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import javax.persistence.Column;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

/**
 *
 * @author PC
 */
public class RepositoriesGenerator {
    
    final String classesPath = "../era_repositories/src/main/java/com/era/repositories";
    private static RepositoriesGenerator TableGenerator;
    
    
    public static RepositoriesGenerator getSingleton(){
        if(TableGenerator == null){
            TableGenerator = new RepositoriesGenerator();
        }
        return TableGenerator;
    }
    
    public void createClass(List<Class> tables) throws Exception {
        
        for(Class Class_: tables){
            
            final String simpleName = Class_.getSimpleName();
            final String fileName = simpleName + "sRepository.java";
            final String finalPath = classesPath + "/" + fileName;
            
            //Get the content of the new file
            final String contentNewFile = this.getContentOfClas(simpleName);
            
            final File File = new File(finalPath);
            boolean create = true;
            if(File.exists()){
                
                LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "The file " + finalPath + " exists");
                
                //Get content file
                final String contentFile = UtilitiesFactory.getSingleton().getFilesUtility().getFileContentToString(finalPath).replaceAll("\\s+", "");
                final String contentNewFileCompare = contentNewFile.replaceAll("\\s+", "");
                
                //Get the byes of the files
                final int contentFileBytes = contentFile.getBytes().length;
                final int contentNewFileCompareBytes = contentFile.getBytes().length;
                
                LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "Comparing original bytes: " + contentFileBytes + " with contentNewFileCompareBytes: " + contentNewFileCompareBytes);
                
                //If the files are identical delete it
                if(contentFile.compareTo(contentNewFileCompare)==0){                                        
                
                    LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "The file " + finalPath + " not cotains changes and will be deleted");
                    File.delete();
                    LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "The file " + finalPath + " was deleted");
                }
                else{
                    LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "The file " + finalPath + " cotains changes and skip deleting");
                    create = false;
                }
            }
            else{
                LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "The file " + finalPath + " not exists");
            }

            if(create){
                LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "The file " + finalPath + " is being created");
                File.createNewFile();
                LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "The file " + finalPath + " was created");
             
                //Write to the file
                try ( //Write the class
                    FileWriter writer = new FileWriter(File)) {                
                    writer.write(contentNewFile);
                }
            }
            
            //Create table header
            createTableHeaderFile(Class_,simpleName);                        

            //Create the validation exception
            createValidationExceptionFile(Class_, simpleName);
            
            //Create the validation
            createValidationFile(Class_, simpleName);            
        }
        
        //Create the table header factory file
        createTableHeaderFactoryFile();
        
        //Create the validation factory file
        createValidatorFactoryFile();
    }
    
    private void createValidatorFactoryFile() throws IOException{
        
        //Create the path
        final String className = "ValidatorFactory";
        final String path = "../era_easyretail/src/main/java/com/era/easyretail/validators";
        final String finalClassPath = path + "/" + className + ".java";
        
        //If the file exists delete it
        final File File = new File(finalClassPath);        
        if(File.exists()){
            File.delete();
        }
        
        //Create the file
        File.createNewFile();
        
        //Write to the file
        try ( //Write the class
            FileWriter writer = new FileWriter(File)) {                
            writer.write(getValidatorFactoryContent(path));
        }
    }
    private String getValidatorFactoryContent(final String tableHeaderPath){
                        
        final String className = "ValidatorFactory";
        
        final StringBuffer StringBuffer = new StringBuffer(
                                "package com.era.easyretail.validators;\n\n" +
                                "public class " + className + " {\n\n" +
                                "   private static " + className + " " + className + ";\n" +
                                "   public static " + className + " getSigleton(){\n" +
                                "       if(" + className + "==null){" + className + " = new " + className + "();}return " + className + ";\n" +
                                "   }\n\n");
                
        File[] files = new File(tableHeaderPath).listFiles();
        for(File file:files){
            
            final String fileName = file.getName().replace(".java", "");
            
            if(fileName.compareTo("ValidatorFactory")==0 || file.isDirectory() || fileName.compareTo("IValidate")==0){
                continue;
            }
            
            StringBuffer.append("   public " + fileName + " get" + fileName + "(){\n" +
                                "       return new " + fileName + "();\n" +
                                "   }\n\n");
        }
        
        StringBuffer.append("}");
        
        return StringBuffer.toString();
    }
    
    private void createValidationFile(final Class Class_, final String simpleName) throws Exception {
        
        //Get all the properties of that table
        final SessionFactory SessionFactory = HibernateUtil.getSingleton().getSessionFactoryLocal();
        final ClassMetadata classMetadata = SessionFactory.getClassMetadata(Class_);
        final String[] propertyNames = classMetadata.getPropertyNames();
        
        //Create the path
        final String className = simpleName + "sValidator";
        final String path = "../era_easyretail/src/main/java/com/era/easyretail/validators";
        final String finalClassPath = path + "/" + className + ".java";
        
        //Get file content
        final String fileContent = this.getValidationFileContent(Class_,simpleName,propertyNames);
        
        //If the file exists delete it
        final File File = new File(finalClassPath);        
        if(File.exists()){
            File.delete();
        }
        
        //Create the file
        File.createNewFile();
        
        //Write to the file
        try ( //Write the class
            FileWriter writer = new FileWriter(File)) {                
            writer.write(fileContent);
        }
    }
    private String getValidationFileContent(final Class Class_, final String simpleName, final String[] propertyNames) throws Exception {
                        
        final String className = simpleName + "sValidator";
        
        final StringBuffer StringBuffer = new StringBuffer(
                                "package com.era.easyretail.validators;\n\n" +
                                "import com.era.easyretail.validators.exceptions." + simpleName + "sValidatorsExceptions;\n" +
                                "import com.era.models." + simpleName + ";\n" +
                                "import com.era.repositories.RepositoryFactory;\n\n" +
                                "public class " + className + " extends IValidate{\n\n");
        for(String property:propertyNames){
            
            //Get field metadata
            final Field field = Class_.getDeclaredField(property);
            final Column Column = field.getAnnotation(Column.class);
            final boolean nullable = Column.nullable();
            final String propertyFirstMayus = property.substring(0, 1).toUpperCase() + property.substring(1);
            if(!nullable){
                if(property.compareTo("estac")==0 || property.compareTo("falt")==0 || property.compareTo("fmod")==0 || property.compareTo("nocaj")==0 || property.compareTo("sucu")==0){
                    continue;
                }                
                StringBuffer.append("   private String " + property + ";\n" +
                                    "   public void set" + propertyFirstMayus + "(String property){\n" + 
                                    "       this." + property + " = property;\n" +
                                    "   }\n\n");                
            }
        }
        StringBuffer.append("\n" +
                            "   @Override\n" +
                            "   public void validateInsert() throws Exception {\n\n");
        for(String property:propertyNames){
            
            //Get field metadata
            final Field field = Class_.getDeclaredField(property);
            field.setAccessible(true);
            final Column Column = field.getAnnotation(Column.class);
            final boolean nullable = Column.nullable();
            if(!nullable){
                
                if(property.compareTo("estac")==0 || property.compareTo("falt")==0 || property.compareTo("fmod")==0 || property.compareTo("nocaj")==0 || property.compareTo("sucu")==0){
                    continue;
                }
                
                final String propertyFirstMayus = property.substring(0, 1).toUpperCase() + property.substring(1);
                if(field.getType() == Integer.class){
                    
                }
                if(field.getType() == Boolean.class){
                    
                }
                if(field.getType() == Float.class){
                    
                }
                if(field.getType() == Double.class){
                    
                }
                else{
                    StringBuffer.append("       if(" + property + "==null || " + property + ".isEmpty()){\n" +
                                        "           throw new " + simpleName + "sValidatorsExceptions().get" + propertyFirstMayus + "Exception();\n" +
                                        "       }\n\n");
                }
            }
        }
        StringBuffer.append("" +                             
                            "       if(IInsertValidation!=null){\n" +
                            "            final boolean response = IInsertValidation.validate();\n" +
                            "            if(!response){\n" +
                            "                throw new " + simpleName + "sValidatorsExceptions().getCustomVaidationNotPassedException();\n" +
                            "            }                \n" +
                            "        }\n\n" +
                            "   }\n\n" + 
                            "   @Override\n" +
                            "   public void validateUpdate() throws Exception {\n\n" +                            
                            "       this.validateInsert();\n\n" +                            
                            "       if(IUpdateValidation!=null){\n" +
                            "           final boolean response = IUpdateValidation.validate();\n" +
                            "           if(!response){\n" +
                            "               throw new " + simpleName + "sValidatorsExceptions().getCustomVaidationNotPassedException();\n" +
                            "           }\n" +
                            "       }\n" +
                            "   }\n\n" +
                            "}");
        
                
        return StringBuffer.toString();
    }
    
    private void createValidationExceptionFile(final Class Class_, final String simpleName) throws Exception {
        
        //Get all the properties of that table
        final SessionFactory SessionFactory = HibernateUtil.getSingleton().getSessionFactoryLocal();
        final ClassMetadata classMetadata = SessionFactory.getClassMetadata(Class_);
        final String[] propertyNames = classMetadata.getPropertyNames();
        
        //Create the path
        final String className = simpleName + "sValidatorsExceptions";
        final String path = "../era_easyretail/src/main/java/com/era/easyretail/validators/exceptions";
        final String finalClassPath = path + "/" + className + ".java";                
        
        //Get file content
        final String fileContent = this.getValidationExceptionContent(Class_,simpleName,propertyNames);
        
        //If the file exists delete it
        final File File = new File(finalClassPath);        
        if(File.exists()){
            File.delete();
        }
        
        //Create the file
        File.createNewFile();
        
        //Write to the file
        try ( //Write the class
            FileWriter writer = new FileWriter(File)) {                
            writer.write(fileContent);
        }
    }
    private String getValidationExceptionContent(final Class Class_, final String simpleName, final String[] propertyNames) throws Exception {
                        
        final String className = simpleName + "sValidatorsExceptions";
        
        final StringBuffer StringBuffer = new StringBuffer(
                                "package com.era.easyretail.validators.exceptions;\n\n" +
                                "public class " + className + " {\n\n" +
                                "   private static " + className + " " + className + ";\n\n" +
                                "   public static " + className + " getSigleton(){\n" +
                                "       if(" + className + "==null){" + className + " = new " +  className + "();}return " + className + ";\n" +
                                "   }\n\n" +
                                "   public Exception getModelExistsException(){\n" + 
                                "       return new Exception(\"El registro ya existe\");\n" +
                                "   }\n\n" +
                                "   public Exception getCustomVaidationNotPassedException(){\n" +
                                "       return new Exception(\"El registro no paso las validaciones\");\n" +
                                "   }\n\n" +
                                "   public Exception getModelNotExistsException(){\n" +
                                "       return new Exception(\"El registro no existe\");\n" +
                                "   }\n\n" +
                                "   public Exception getCodeException(){\n" +
                                "       return new Exception(\"Falta espeficiar codigo\");\n" +
                                "   }\n\n");
        for(String property:propertyNames){
            
            //Get field metadata
            final Field field = Class_.getDeclaredField(property);
            final Column Column = field.getAnnotation(Column.class);
            final boolean nullable = Column.nullable();
            if(!nullable){
                if(property.compareTo("estac")==0 || property.compareTo("falt")==0 || property.compareTo("fmod")==0 || property.compareTo("nocaj")==0 || property.compareTo("sucu")==0 || property.compareTo("code")==0){
                    continue;
                }
                final String propertyFirstMayus = property.substring(0, 1).toUpperCase() + property.substring(1);            
                StringBuffer.append("   public Exception get" + propertyFirstMayus + "Exception(){\n" +
                                    "       return new Exception(\"Falta espeficiar " + propertyFirstMayus + "\");\n" +
                                    "   }\n\n");                
            }
        }
        StringBuffer.append("}");
        
        return StringBuffer.toString();
    }
    
    private void createTableHeaderFile(final Class Class_, final String simpleName) throws IOException{
        
        //Get all the properties of that table
        final SessionFactory SessionFactory = HibernateUtil.getSingleton().getSessionFactoryLocal();
        final ClassMetadata classMetadata = SessionFactory.getClassMetadata(Class_);
        final String[] propertyNames = classMetadata.getPropertyNames();
        
        //Get the content file
        final String contentFileTableHeader = this.getContentTableHeader(simpleName,propertyNames);            
        
        //Create the path
        final String className = simpleName + "sTableHeader";        
        final String tableHeaderPath = "../era_views/src/main/java/com/era/views/tables/headers";
        final String finalClassPath = tableHeaderPath + "/" + className + ".java";
        
        //If the file exists delete it
        final File File = new File(finalClassPath);        
        if(File.exists()){
            File.delete();
        }
        
        //Create the file
        File.createNewFile();
        
        //Write to the file
        try ( //Write the class
            FileWriter writer = new FileWriter(File)) {                
            writer.write(contentFileTableHeader);
        }
    }
    
    private void createTableHeaderFactoryFile() throws IOException{
        
        //Create the path
        final String className = "TableHeaderFactory";
        final String tableHeaderPath = "../era_views/src/main/java/com/era/views/tables/headers";
        final String finalClassPath = tableHeaderPath + "/" + className + ".java";
        
        //If the file exists delete it
        final File File = new File(finalClassPath);        
        if(File.exists()){
            File.delete();
        }
        
        //Create the file
        File.createNewFile();
        
        //Write to the file
        try ( //Write the class
            FileWriter writer = new FileWriter(File)) {                
            writer.write(getContentTableHeaderFactory(tableHeaderPath));
        }
    }
    private String getContentTableHeaderFactory(final String tableHeaderPath){
                        
        final String classTableHeaderFactoryName = "TableHeaderFactory";
        
        final StringBuffer StringBuffer = new StringBuffer(
                                "package com.era.views.tables.headers;\n\n" +
                                "public class " + classTableHeaderFactoryName + " {\n\n" +
                                "   private static " + classTableHeaderFactoryName + " " + classTableHeaderFactoryName + ";\n" +
                                "   public static " + classTableHeaderFactoryName + " getSigleton(){\n" +
                                "       if(TableHeaderFactory==null){TableHeaderFactory = new TableHeaderFactory();}return TableHeaderFactory;\n" +
                                "   }\n\n");
                
        File[] files = new File(tableHeaderPath).listFiles();
        for(File file:files){
            
            final String fileName = file.getName().replace(".java", "");
            
            if(fileName.compareTo("ColumnTable")==0 || file.isDirectory()){
                continue;
            }
            
            StringBuffer.append("   public " + fileName + " get" + fileName + "(){\n" +
                                "       final " + fileName + " " + fileName + " = new " + fileName + "();return " + fileName + ";\n" +
                                "   }\n\n");
        }
        
        StringBuffer.append("}");
        
        return StringBuffer.toString();
    }
    
    private String getContentTableHeader(final String className, String[] propertyNames){
        
        final String classTableHeaderName = className + "sTableHeader";
        
        final StringBuffer StringBuffer = new StringBuffer(
                                "package com.era.views.tables.headers;\n\n" +                                
                                "public class " + classTableHeaderName + " extends BaseTableHeader {\n\n" +
                                "private final ColumnTable ROWNUMBER = new ColumnTable(\"No\");\n" +
                                "   public ColumnTable getROWNUMBER() {\n" +
                                "       return this.ROWNUMBER;\n" +
                                "   }\n\n");
                
        for(String propertyName:propertyNames){
            final String columnaName = propertyName.toUpperCase();
            StringBuffer.append("   private final ColumnTable " + columnaName + " = new ColumnTable(\"" + columnaName + "\");\n" +
                                "   public ColumnTable get" + columnaName + "() {\n" +
                                "       return this." + columnaName + ";\n" +
                                "   }\n\n");
        }
        StringBuffer.append("}");
        
        return StringBuffer.toString();
    }
    
    private String getContentOfClas(final String className){
        
        String classs = 
                "package com.era.repositories;\n\n" +
                "import java.util.List;\n" +
                "import java.util.ArrayList;\n" +
                "import com.era.models." + className + ";\n\n" +
                "public class " + className + "sRepository extends Repository {\n\n" +
                "   public " + className + "sRepository() {\n" +
                "        super(" + className + ".class);\n" +
                "    }\n\n" +
                "   @Override\n" +
                "   final public List<" + className + "> getByLikeEncabezados(final String search) throws Exception{\n" +
                "        \n" +
                "       final List<String> likes = new ArrayList<>();\n" +
                "       likes.add(\"falt\");\n" +
                "       likes.add(\"fmod\");\n" +
                "       \n" +
                "       final List<" + className + "> records = (List<" + className + ">) this.getAllLike(likes, search);\n" +
                "       \n" +
                "       return records;\n" +
                "   }\n\n" +
                "}";
        return classs;
    }    
}
