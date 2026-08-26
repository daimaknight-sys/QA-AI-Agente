package execution;

import crawler.WebCrawler;

public class TestRunner {

    public static void main(String[] args) {

        System.out.println("===== INICIANDO AGENTE QA =====");

        WebCrawler crawler = new WebCrawler();
        var links = crawler.crawl("https://secure.tata.com.uy/checkout?orderFormId=a99b079821c14a088ac702f585dd293e/#/profile");

        System.out.println("\n===== LINKS ENCONTRADOS =====");
        for (String link : links) {
            System.out.println(link);
        }
        System.out.println("-----------------------------");
        System.out.println("Total: " + links.size());

        System.out.println("\n===== AGENTE QA FINALIZADO =====");
    }
}