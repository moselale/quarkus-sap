package org.acme.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
// import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

@Path("/greeting")
public class GreetingResource {
    static JCoDestination destination;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws JCoException {
        System.out.println("runtimeclasspath: " + System.getProperty("java.class.path"));

        String ABAP_SYSTEM2 = "ABAP_SYSTEM2";
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "172.28.3.26");
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "11");
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "100");
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, "sviluppo");
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "pocredhat2020");
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "IT");
        createDataFile(ABAP_SYSTEM2, "jcoDestination", connectProperties);

        destination = JCoDestinationManager.getDestination(ABAP_SYSTEM2);
        if (destination == null)
            throw new RuntimeException("Destination not found.");
        JCoFunction function = destination.getRepository().getFunction("STFC_CONNECTION");
        if (function == null)
            throw new RuntimeException("STFC_CONNECTION not found in SAP.");

        try {
            function.execute(destination);
        } catch (AbapException e) {
            System.out.println(e);
        }

        System.out.println("STFC_CONNECTION finished:");
        System.out.println(" Echo: " + function.getExportParameterList().getString("ECHOTEXT"));
        System.out.println(" Response: " + function.getExportParameterList().getString("RESPTEXT"));

        return  "hello";
        // return function.getExportParameterList().getString("RESPTEXT");
    }

    static void createDataFile(String name, String suffix, Properties properties) {
        File cfg = new File(name + "." + suffix);
        if (!cfg.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(cfg, false);
                properties.store(fos, "for tests only !");
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
            }
        }
    }
}