package br.com.idxtec.mdfe.dom;

import org.apache.axis2.transport.http.HttpTransportProperties;

public class Proxy {

    private final HttpTransportProperties.ProxyProperties proxyProperties;

    public Proxy(String ip, int porta, String usuario, String senha) {

        proxyProperties = new HttpTransportProperties.ProxyProperties();

        proxyProperties.setProxyName(ip);

        proxyProperties.setProxyPort(porta);

        proxyProperties.setUserName(usuario);

        proxyProperties.setPassWord(senha);
    }

    public String getProxyHostName(){
        return proxyProperties.getProxyHostName();
    }

    public String getProxyPort(){
        return String.valueOf(proxyProperties.getProxyPort());
    }

    public String getProxyUserName(){
        return proxyProperties.getUserName();
    }

    public String getProxyPassWord(){
        return proxyProperties.getPassWord();
    }
}
