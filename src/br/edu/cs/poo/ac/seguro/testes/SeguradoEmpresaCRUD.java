package br.edu.cs.poo.ac.seguro.testes;

import br.edu.cs.poo.ac.seguro.entidades.Endereco;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoEmpresaMediator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SeguradoEmpresaCRUD extends Application {

    private TextField tfCnpj, tfNome, tfFaturamento;
    private CheckBox cbLocadora;
    private TextField tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade;
    private Button btnIncluir, btnAlterar, btnExcluir, btnBuscar, btnLimpar;

    private SeguradoEmpresaMediator mediator = SeguradoEmpresaMediator.getInstancia();

    @Override
    public void start(Stage stage) {
        stage.setTitle("CRUD Segurado Empresa");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        int row = 0;

        grid.add(new Label("CNPJ:"), 0, row);
        tfCnpj = new TextField();
        tfCnpj.setPromptText("Somente números");
        grid.add(tfCnpj, 1, row++);

        grid.add(new Label("Nome:"), 0, row);
        tfNome = new TextField();
        grid.add(tfNome, 1, row++);

        grid.add(new Label("Faturamento:"), 0, row);
        tfFaturamento = new TextField();
        tfFaturamento.setPromptText("Ex: 100000.50");
        grid.add(tfFaturamento, 1, row++);

        cbLocadora = new CheckBox("É Locadora de Veículos");
        grid.add(cbLocadora, 1, row++);

        grid.add(new Label("Endereço (Logradouro):"), 0, row);
        tfLogradouro = new TextField();
        grid.add(tfLogradouro, 1, row++);

        grid.add(new Label("CEP:"), 0, row);
        tfCep = new TextField();
        tfCep.setPromptText("Ex: 12345678");
        grid.add(tfCep, 1, row++);

        grid.add(new Label("Número:"), 0, row);
        tfNumero = new TextField();
        grid.add(tfNumero, 1, row++);

        grid.add(new Label("Complemento:"), 0, row);
        tfComplemento = new TextField();
        grid.add(tfComplemento, 1, row++);

        grid.add(new Label("País:"), 0, row);
        tfPais = new TextField();
        grid.add(tfPais, 1, row++);

        grid.add(new Label("Estado:"), 0, row);
        tfEstado = new TextField();
        grid.add(tfEstado, 1, row++);

        grid.add(new Label("Cidade:"), 0, row);
        tfCidade = new TextField();
        grid.add(tfCidade, 1, row++);

        btnIncluir = new Button("Incluir");
        btnAlterar = new Button("Alterar");
        btnExcluir = new Button("Excluir");
        btnBuscar = new Button("Buscar");
        btnLimpar = new Button("Limpar");

        GridPane buttons = new GridPane();
        buttons.setHgap(10);
        buttons.add(btnIncluir, 0, 0);
        buttons.add(btnAlterar, 1, 0);
        buttons.add(btnExcluir, 2, 0);
        buttons.add(btnBuscar, 3, 0);
        buttons.add(btnLimpar, 4, 0);
        grid.add(buttons, 0, row, 2, 1);

        btnIncluir.setOnAction(e -> incluir());
        btnAlterar.setOnAction(e -> alterar());
        btnExcluir.setOnAction(e -> excluir());
        btnBuscar.setOnAction(e -> buscar());
        btnLimpar.setOnAction(e -> limparCampos());

        Scene scene = new Scene(grid, 450, 600);
        stage.setScene(scene);
        stage.show();
    }

    private SeguradoEmpresa getSeguradoFromFields() {
        try {
            String cnpj = tfCnpj.getText().trim();
            String nome = tfNome.getText().trim();
            double faturamento = Double.parseDouble(tfFaturamento.getText().trim());
            boolean ehLocadora = cbLocadora.isSelected();

            String logradouro = tfLogradouro.getText().trim();
            String cep = tfCep.getText().trim();
            String numero = tfNumero.getText().trim();
            String complemento = tfComplemento.getText().trim();
            String pais = tfPais.getText().trim();
            String estado = tfEstado.getText().trim();
            String cidade = tfCidade.getText().trim();

            if (cnpj.isEmpty() || nome.isEmpty() || logradouro.isEmpty() || cep.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Campos obrigatórios", "Preencha todos os campos obrigatórios.");
                return null;
            }

            Endereco endereco = new Endereco(logradouro, cep, numero, complemento, pais, estado, cidade);

            // Os dois campos extras que não estão no formulário:
            LocalDate dataAbertura = LocalDate.now(); // ou use um DatePicker se quiser permitir entrada
            BigDecimal bonus = BigDecimal.ZERO;

            return new SeguradoEmpresa(nome, endereco, dataAbertura, bonus, cnpj, faturamento, ehLocadora);

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Erro de Formato", "Verifique os campos numéricos.");
            return null;
        }
    }

    private void carregarSegurado(SeguradoEmpresa seg) {
        tfCnpj.setText(seg.getCnpj());
        tfNome.setText(seg.getNome());
        tfFaturamento.setText(String.valueOf(seg.getFaturamento()));
        cbLocadora.setSelected(seg.getEhLocadoraDeVeiculos());

        Endereco end = seg.getEndereco();
        tfLogradouro.setText(end.getLogradouro());
        tfCep.setText(end.getCep());
        tfNumero.setText(end.getNumero());
        tfComplemento.setText(end.getComplemento());
        tfPais.setText(end.getPais());
        tfEstado.setText(end.getEstado());
        tfCidade.setText(end.getCidade());
    }

    private void incluir() {
        SeguradoEmpresa seg = getSeguradoFromFields();
        if (seg == null) return;

        String msg = mediator.incluirSeguradoEmpresa(seg);
        if (msg == null) {
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Segurado incluído com sucesso!");
            limparCampos();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro na inclusão", msg);
        }
    }

    private void alterar() {
        SeguradoEmpresa seg = getSeguradoFromFields();
        if (seg == null) return;

        String msg = mediator.alterarSeguradoEmpresa(seg);
        if (msg == null) {
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Segurado alterado com sucesso!");
            limparCampos();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro na alteração", msg);
        }
    }

    private void excluir() {
        String cnpj = tfCnpj.getText().trim();
        if (cnpj.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Informe o CNPJ para excluir");
            return;
        }
        String msg = mediator.excluirSeguradoEmpresa(cnpj);
        if (msg == null) {
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Segurado excluído com sucesso!");
            limparCampos();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro na exclusão", msg);
        }
    }

    private void buscar() {
        String cnpj = tfCnpj.getText().trim();
        if (cnpj.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Informe o CNPJ para buscar");
            return;
        }
        SeguradoEmpresa seg = mediator.buscarSeguradoEmpresa(cnpj);
        if (seg == null) {
            showAlert(Alert.AlertType.INFORMATION, "Info", "Segurado não encontrado");
        } else {
            carregarSegurado(seg);
        }
    }

    private void limparCampos() {
        tfCnpj.clear();
        tfNome.clear();
        tfFaturamento.clear();
        cbLocadora.setSelected(false);
        tfLogradouro.clear();
        tfCep.clear();
        tfNumero.clear();
        tfComplemento.clear();
        tfPais.clear();
        tfEstado.clear();
        tfCidade.clear();
    }

    private void showAlert(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
