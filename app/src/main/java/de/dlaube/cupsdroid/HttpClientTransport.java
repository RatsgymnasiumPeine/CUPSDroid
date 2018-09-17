package de.dlaube.cupsdroid;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.hp.jipp.encoding.IppPacket;
import com.hp.jipp.trans.IppClientTransport;
import com.hp.jipp.trans.IppPacketData;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import android.util.Base64;


class HttpIppClientTransport implements IppClientTransport {

    private HttpURLConnection connection;
    private String username;
    private String password;

    public void setAuthenticationData(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    @NotNull
    public IppPacketData sendData(@NotNull URI uri, @NotNull IppPacketData request) throws IOException {
        URL url = new URL(uri.toString().replaceAll("^ipp", "http"));

        connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(6 * 1000);
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-type", "application/ipp");
        connection.setDoOutput(true);

        if( !username.isEmpty() && !password.isEmpty() ) {
            String userpass = username + ":" + password;
            String basicAuth = "Basic " + new String(Base64.encode(userpass.getBytes(), Base64.URL_SAFE));
            connection.setRequestProperty("Authorization", basicAuth);
        }

        // Copy IppPacket to the output stream
        try (OutputStream output = connection.getOutputStream()) {
            request.getPacket().write(new DataOutputStream(output));
            InputStream extraData = request.getData();
            if (extraData != null) {
                copy(extraData, output);
                extraData.close();
            }
        }

        // Read the response from the input stream
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();

        try (InputStream response = connection.getInputStream()) {
            copy(response, responseBytes);
        }

        // Parse it back into an IPP packet
        InputStream responseInput = new DataInputStream(new ByteArrayInputStream(responseBytes.toByteArray()));
        return new IppPacketData(IppPacket.read(responseInput));
    }

    private void copy(InputStream data, OutputStream output) throws IOException {
        byte[] buffer = new byte[8 * 1024];
        int readAmount = data.read(buffer);
        while (readAmount != -1) {
            output.write(buffer, 0, readAmount);
            readAmount = data.read(buffer);
        }
    }


    public int getResponseCode(){
        if(connection == null)
            return -1;
        try {
            return connection.getResponseCode();
        } catch (IOException e) {
            return -1;
        }
    }
}
