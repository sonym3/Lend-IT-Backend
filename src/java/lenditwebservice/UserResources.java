/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lenditwebservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import net.sf.json.JSONObject;

/**
 * REST Web Service
 *
 * @author sonym
 */
@Path("user")
public class UserResources {
    JSONObject mainObject=new JSONObject();
    String level=null;
    String status=null;
    String sql;
    String sql2;
    Date today=new Date();
    long timeStamp=today.getTime();

    DatabaseConnection databaseConn=new DatabaseConnection();


    @Context
    private UriInfo context;

   
    @GET
    @Path("validate&{id}&{password}")
    @Produces("text/plain")
    public String checkCredentials(@PathParam("id") int id, 
            @PathParam("password") String password) {
        
        Connection conn = null;
        conn=  databaseConn.getConnection(conn);
        if(id==1234) level="admin";
        else level="user";
            try {
                sql = "SELECT PersonId,password FROM User WHERE PersonId=? and password=?";
                PreparedStatement stm = conn.prepareStatement(sql);
                stm.setInt(1,id);
                stm.setString(2,password);

                ResultSet rs=stm.executeQuery();
                Instant instant=Instant.now();
                long time=instant.getEpochSecond();
                if(rs.next() == false){
                    status="ERROR";   
                    mainObject.accumulate("Status", status);
                    mainObject.accumulate("Timestamp", time);
                }     
                
                else {
                    do{
                        status="OK";
                        mainObject.accumulate("Status", status);
                        mainObject.accumulate("Level", level);
                        mainObject.accumulate("Timestamp", time);
                    }while(rs.next());
                 databaseConn.closeConnection(conn,rs,stm);   
                }
            }catch (SQLException ex) {
            Logger.getLogger(UserResources.class.getName()).log(Level.SEVERE, null, ex);
        }
            return mainObject.toString();
    }
    
    
    
    
    @GET
    @Path("register&{id}&{name}&{email}&{password}&{mobile}&{sex}")
    @Produces("text/plain")
    public String registerUser(@PathParam("id") int id, 
            @PathParam("name") String name,@PathParam("email") String email,
            @PathParam("password") String password,@PathParam("mobile") String mobile,
            @PathParam("sex") String sex){
        
        Connection conn = null;
        conn=  databaseConn.getConnection(conn);
        int qRes=0,qRes2=0;
        
            try {
                sql = "insert into User values(?,?,?,?)";
                sql2= "insert into Student values (?,?,?)";
                PreparedStatement stm = conn.prepareStatement(sql);

                stm.setInt(1,id);
                stm.setString(2,name);
                stm.setString(3,email);
                stm.setString(4,password);
                PreparedStatement stm2 = conn.prepareStatement(sql2);
               
                stm2.setInt(1,id);
                stm2.setString(2,sex);
                stm2.setString(3,mobile);
                
                qRes=stm.executeUpdate();
                  qRes2=stm2.executeUpdate();
                  
                  if(qRes==1&&qRes2==1)
                  {
                   mainObject.accumulate("Status", "ok");
                    mainObject.accumulate("Timestamp", timeStamp);
                    mainObject.accumulate("Message", "Registered");

                  }

                databaseConn.closeConnection(conn,null,stm);
                
            }catch (SQLException ex) {
            Logger.getLogger(UserResources.class.getName()).log(Level.SEVERE, null, ex);
        }
            if(qRes!=1)
        {
            mainObject.clear();
        mainObject.accumulate("Status", "error");
        mainObject.accumulate("Timestamp", timeStamp);
        mainObject.accumulate("Message", "Not Registered");
       }
            return mainObject.toString();
    }
    
    
}
           

