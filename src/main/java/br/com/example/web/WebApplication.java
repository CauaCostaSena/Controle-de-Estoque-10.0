package br.com.example.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.awt.Desktop;
import java.net.URI;

// O scanBasePackages garante que o Spring ache todos os seus controllers e models
@SpringBootApplication(scanBasePackages = "br.com.example")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    // Este método é disparado automaticamente assim que o servidor termina de carregar
    @EventListener({ApplicationReadyEvent.class})
    public void abrirNavegadorAutomaticamente() {
        System.out.println("Servidor iniciado com sucesso! Abrindo o navegador...");
        
        // Coloque aqui a página inicial que você quer que abra (estou colocando a de cadastro)
        String url = "http://localhost:8085/index.html"; 

        try {
            // Tenta abrir pelo método padrão do Java
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Se o Java não conseguir, força a abertura pelo Windows
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
        } catch (Exception e) {
            System.err.println("Não foi possível abrir o navegador automaticamente.");
            e.printStackTrace();
        }
    }
}