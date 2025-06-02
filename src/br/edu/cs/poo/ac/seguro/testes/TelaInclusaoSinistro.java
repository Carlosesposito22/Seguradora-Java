package br.edu.cs.poo.ac.seguro.testes;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateTimeStringConverter;
import br.edu.cs.poo.ac.seguro.mediators.DadosSinistro;
import br.edu.cs.poo.ac.seguro.mediators.SinistroMediator;
import br.edu.cs.poo.ac.seguro.entidades.TipoSinistro;
import br.edu.cs.poo.ac.seguro.excecoes.ExcecaoValidacaoDados;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class TelaInclusaoSinistro extends Application {

    private SinistroMediator mediator = SinistroMediator.getInstancia();
    private TextField placaField;
    private TextField dataHoraField;
    private TextField usuarioRegistroField;
    private TextField valorSinistroField;
    private ComboBox<String> tipoSinistroComboBox;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Inclusão de Sinistro");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Labels
        Label placaLabel = new Label("Placa:");
        Label dataHoraLabel = new Label("Data/Hora do Sinistro:");
        Label usuarioLabel = new Label("Usuário Registro:");
        Label valorLabel = new Label("Valor do Sinistro:");
        Label tipoLabel = new Label("Tipo de Sinistro:");

        // Campos de texto
        placaField = new TextField();
        placaField.setPromptText("AAA-0000");

        // Configura máscara para data/hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dataHoraField = new TextField();
        dataHoraField.setPromptText("dd/MM/aaaa HH:mm");

        usuarioRegistroField = new TextField();
        usuarioRegistroField.setPromptText("Nome do usuário");

        valorSinistroField = new TextField();
        valorSinistroField.setPromptText("0,00");

        // ComboBox para tipos de sinistro
        tipoSinistroComboBox = new ComboBox<>();
        // Ordena os tipos de sinistro por nome em ordem alfabética crescente
        Arrays.stream(TipoSinistro.values())
                .sorted(Comparator.comparing(TipoSinistro::getNome))
                .forEach(tipo -> tipoSinistroComboBox.getItems().add(tipo.getNome()));
        tipoSinistroComboBox.getSelectionModel().selectFirst(); // Seleciona o primeiro item

        // Botões
        Button incluirBtn = new Button("Incluir");
        incluirBtn.setOnAction(e -> incluirSinistro());

        Button limparBtn = new Button("Limpar");
        limparBtn.setOnAction(e -> limparCampos());

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.getChildren().addAll(incluirBtn, limparBtn);

        // Adiciona componentes ao grid
        grid.add(placaLabel, 0, 0);
        grid.add(placaField, 1, 0);
        grid.add(dataHoraLabel, 0, 1);
        grid.add(dataHoraField, 1, 1);
        grid.add(usuarioLabel, 0, 2);
        grid.add(usuarioRegistroField, 1, 2);
        grid.add(valorLabel, 0, 3);
        grid.add(valorSinistroField, 1, 3);
        grid.add(tipoLabel, 0, 4);
        grid.add(tipoSinistroComboBox, 1, 4);
        grid.add(btnBox, 0, 5, 2, 1);

        // Define ordem de tabulação
        placaField.setFocusTraversable(true);
        dataHoraField.setFocusTraversable(true);
        usuarioRegistroField.setFocusTraversable(true);
        valorSinistroField.setFocusTraversable(true);
        tipoSinistroComboBox.setFocusTraversable(true);
        incluirBtn.setFocusTraversable(true);
        limparBtn.setFocusTraversable(true);

        Scene scene = new Scene(grid, 400, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void incluirSinistro() {
        try {
            // Obtém os dados dos campos
            String placa = placaField.getText();

            // Converte a data/hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dataHora = LocalDateTime.parse(dataHoraField.getText(), formatter);

            String usuario = usuarioRegistroField.getText();
            double valor = Double.parseDouble(valorSinistroField.getText().replace(",", "."));

            // Obtém o código do tipo de sinistro selecionado
            String tipoSelecionado = tipoSinistroComboBox.getValue();
            int codigoTipo = Arrays.stream(TipoSinistro.values())
                    .filter(t -> t.getNome().equals(tipoSelecionado))
                    .findFirst()
                    .map(TipoSinistro::getCodigo)
                    .orElse(0);

            // Cria DadosSinistro e chama o mediator
            DadosSinistro dados = new DadosSinistro(placa, dataHora, usuario, valor, codigoTipo);
            String numeroSinistro = mediator.incluirSinistro(dados, LocalDateTime.now());

            // Mostra mensagem de sucesso
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Sinistro incluído com sucesso! Anote o número do sinistro: " + numeroSinistro);
            alert.showAndWait();

            limparCampos();

        } catch (ExcecaoValidacaoDados e) {
            // Mostra mensagens de erro de validação
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao incluir sinistro");

            // Concatena todas as mensagens de erro
            StringBuilder mensagens = new StringBuilder();
            for (String mensagem : e.getMensagens()) {
                mensagens.append("- ").append(mensagem).append("\n");
            }

            alert.setContentText(mensagens.toString());
            alert.showAndWait();
        } catch (Exception e) {
            // Mostra mensagem de erro genérico
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao incluir sinistro");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void limparCampos() {
        placaField.clear();
        dataHoraField.clear();
        usuarioRegistroField.clear();
        valorSinistroField.clear();
        tipoSinistroComboBox.getSelectionModel().selectFirst();
    }

    public static void main(String[] args) {
        launch(args);
    }
}