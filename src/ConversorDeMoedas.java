import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.Gson;
import java.util.Map;

public class ConversorDeMoedas {

    private static final String API_KEY = "bf262700ac1a4c02faa179d6";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    private static final Scanner scanner = new Scanner(System.in);
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        System.out.println("====== CONVERSOR DE MOEDAS ======");

        while (true) {
            System.out.println("\nEscolha uma conversão:");
            System.out.println("1. USD (Dólar) -> BRL (Real)");
            System.out.println("2. BRL (Real) -> USD (Dólar)");
            System.out.println("3. EUR (Euro) -> BRL (Real)");
            System.out.println("4. BRL (Real) -> EUR (Euro)");
            System.out.println("5. USD (Dólar) -> EUR (Euro)");
            System.out.println("6. EUR (Euro) -> USD (Dólar)");
            System.out.println("0. Sair");

            int opcao = scanner.nextInt();
            if (opcao == 0) {
                System.out.println("Encerrando o conversor.");
                break;
            }

            String from = "", to = "";
            switch (opcao) {
                case 1 -> { from = "USD"; to = "BRL"; }
                case 2 -> { from = "BRL"; to = "USD"; }
                case 3 -> { from = "EUR"; to = "BRL"; }
                case 4 -> { from = "BRL"; to = "EUR"; }
                case 5 -> { from = "USD"; to = "EUR"; }
                case 6 -> { from = "EUR"; to = "USD"; }
                default -> {
                    System.out.println("Opção inválida.");
                    continue;
                }
            }

            System.out.print("Digite o valor para conversão (" + from + "): ");
            double valor = scanner.nextDouble();

            double taxa = obterTaxaDeCambio(from, to);
            if (taxa == -1) {
                System.out.println("Erro ao obter taxa.");
            } else {
                double convertido = valor * taxa;
                System.out.printf("Resultado: %.2f %s = %.2f %s\n", valor, from, convertido, to);
            }
        }
    }

    private static double obterTaxaDeCambio(String from, String to) {
        try {
            URL url = new URL(BASE_URL + from);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");

            if (conexao.getResponseCode() != 200) {
                System.out.println("Erro na conexão com API.");
                return -1;
            }

            Reader reader = new InputStreamReader(conexao.getInputStream());
            ExchangeRateResponse resposta = gson.fromJson(reader, ExchangeRateResponse.class);
            conexao.disconnect();

            if (resposta != null && resposta.conversion_rates.containsKey(to)) {
                return resposta.conversion_rates.get(to);
            } else {
                System.out.println("Moeda destino não encontrada na resposta.");
                return -1;
            }

        } catch (Exception e) {
            System.out.println("Erro ao acessar a API: " + e.getMessage());
            return -1;
        }
    }

    static class ExchangeRateResponse {
        String base_code;
        Map<String, Double> conversion_rates;
    }
}