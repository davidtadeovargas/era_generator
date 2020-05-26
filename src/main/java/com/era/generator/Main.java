/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era.generator;

import com.era.logger.LoggerUtility;
import com.era.repositories.models.HibernateConfigModel;
import com.era.repositories.utils.HibernateUtil;
import com.era.tableviews.generators.repositories.RepositoriesGenerator;
import com.era.tableviews.generators.repositories.RepositoryFactoryGenerator;
import com.era.tableviews.generators.tables.TableGenerator;
import com.era.tableviews.generators.tables.TableModelGenerator;
import com.era.utilities.ConfigFileUtil;
import com.era.utilities.models.ConfigFileModel;
import java.util.List;
import org.hibernate.classic.Session;

/**
 *
 * @author PC
 */
public class Main {
   
    public static void main(String[] args) {
        
        try{
         
            if(ConfigFileUtil.getSingleton().configFileExists()){

                final String DB = "era_db_test";
                final ConfigFileModel ConfigFileModel = ConfigFileUtil.getSingleton().getConfigFileModel();

                final String instance = ConfigFileModel.getInstance();
                final String user = ConfigFileModel.getUser();
                final String password = ConfigFileModel.getPassword();
                final String db = DB;
                final String port = ConfigFileModel.getPort();

                final HibernateConfigModel HibernateConfigModel_ = new HibernateConfigModel();
                HibernateConfigModel_.setUser(user);        
                HibernateConfigModel_.setPassword(password);
                HibernateConfigModel_.setPort(Integer.valueOf(port));
                HibernateConfigModel_.setInstance(instance);
                HibernateConfigModel_.setDatabase(db);
                HibernateUtil.getSingleton().setHibernateConfigModelLocal(HibernateConfigModel_);
                HibernateUtil.getSingleton().loadDbLocal();
                
                //Get all the tables schemes
                final List<Class> tables = HibernateUtil.getSingleton().getAnnottatedClassesForLocal();                
                final Session Session = HibernateUtil.getSingleton().getSessionFactoryLocal().openSession();
                
                //Create the RepositoryFactory
                RepositoryFactoryGenerator.getSingleton().createClass(tables);
                
                //Create all the repositories
                RepositoriesGenerator.getSingleton().createClass(tables);
                
                //Create other stuffs
                for(Class Class_:tables){
                    
                    //Get table metadata
                    //final ClassMetadata ClassMetadata = HibernateSchemeUtil.getSingleton().getTableMetaData(HibernateUtil.getSingleton().getSession(), HibernateUtil.getSingleton().getClass());
                    
                    final String className = Class_.getSimpleName();
                    
                    //Create the tablemodels classes
                    TableModelGenerator.getSingleton().createClass(className);
                    
                    //Create the table classes
                    TableGenerator.getSingleton().createClass(className);
                }
                Session.close();
            }
            
        }catch (Exception ex) {
            
            LoggerUtility.getSingleton().logError(Main.class, ex);

            System.exit(-1);
        }
    }
}