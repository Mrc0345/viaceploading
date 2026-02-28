package com.marcus;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.*;
import java.util.Scanner;

public class Main {
    private static volatile boolean buscaFinalizada = false;

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Informe o CEP (apenas números): ");
        String cep = sc.nextLine();

        // Thread para a Animacao
        Thread tAnimacao = new Thread(() -> {
            String animacao = "|/-\\";
            int i = 0;
            while (!buscaFinalizada) {
                System.out.print("\rBuscando... " + animacao.charAt(i++ % animacao.length()));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        tAnimacao.start();

        // Execucao da busca (API ViaCEP)
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://viacep.com.br/ws/" + cep + "/json/"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    Endereco endereco = new Gson().fromJson(response, Endereco.class);
                    System.out.println("\nLogradouro: " + endereco.logradouro);
                    System.out.println("Bairro: " + endereco.bairro);
                    System.out.println("Localidade: " + endereco.localidade);
                    System.out.println("UF: " + endereco.uf);
                    System.out.println("CEP: " + endereco.cep);
                    buscaFinalizada = true;
                }).join();
    }
}