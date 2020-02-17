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
@Path("credentials")
public class Credentials {
    JSONObject mainObject1=new JSONObject();
    String status=null;
    String sql;
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
        
            try {
                sql = "SELECT student_id,password FROM credentials WHERE student_id=? and password=?";
                PreparedStatement stm = conn.prepareStatement(sql);
                stm.setInt(1,id);
                stm.setString(2,password);

                ResultSet rs=stm.executeQuery();
                Instant instant=Instant.now();
                long time=instant.getEpochSecond();
                if(rs.next() == false){
                    status="ERROR";   
                    mainObject1.accumulate("Status", status);
                    mainObject1.accumulate("Timestamp", time);
                }     
                
                else {
                    do{
                        status="OK";
                        mainObject1.accumulate("Status", status);
                        mainObject1.accumulate("Timestamp", time);
                        mainObject1.accumulate("username", rs.getString("student_id"));
                        mainObject1.accumulate("password",rs.getString("password"));
                    }while(rs.next());
                 databaseConn.closeConnection(conn,rs,stm);   
                }
            }catch (SQLException ex) {
            Logger.getLogger(Credentials.class.getName()).log(Level.SEVERE, null, ex);
        }
            return mainObject1.toString();
    }
}
           

