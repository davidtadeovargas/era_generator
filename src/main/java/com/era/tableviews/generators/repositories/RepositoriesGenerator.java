/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era.tableviews.generators.repositories;

import com.era.logger.LoggerUtility;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

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
                                
            final File File = new File(finalPath);
            if(File.exists()){
                LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "The file " + finalPath + " exists and will be deleted");
                File.delete();
                LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "The file " + finalPath + " was deleted");
            }

            LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "The file " + finalPath + " is being created");
            File.createNewFile();
            LoggerUtility.getSingleton().logInfo(RepositoriesGenerator.class, "The file " + finalPath + " was created");

            try ( //Write the class
                FileWriter writer = new FileWriter(File)) {
                final String content = this.getContentOfClas(simpleName);
                writer.write(content);
            }
        }
    }
    
    private String getContentOfClas(final String className){
        
        String classs = 
                "package com.era.repositories;\n\n" +
                "import com.era.models." + className + ";\n\n" +
                "public class " + className + "sRepository extends Repository {\n\n" +
                "   public " + className + "sRepository() {\n" +
                "        super(" + className + ".class);\n" +
                "    }\n" +
                "}";
        return classs;
    }
}
