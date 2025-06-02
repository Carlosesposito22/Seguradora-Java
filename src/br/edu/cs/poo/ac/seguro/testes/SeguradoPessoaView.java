package br.edu.cs.poo.ac.seguro.testes;

import br.edu.cs.poo.ac.seguro.entidades.Endereco;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoPessoaMediator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SeguradoPessoaView extends Application {

    private SeguradoPessoaMediator mediator = SeguradoPessoaMediator.getInstancia();

    private TextField txtCpf;
    private TextField txtNome;
    private TextField txtRenda;

    private TextField txtLogradouro;
    private TextField txtCep;
    private TextField txtNumero;
    private TextField txtComplemento;
    private TextField txtPais;
    private TextField txtEstado;
    private TextField txtCidade;

    private Button btnNovo;
    private Button btnBuscar;
    private Button btnIncluirAlterar;
    private Button btnExcluir;
    private Button btnCancelar;
    private Button btnLimpar;

    private boolean editando = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Cadastro Segurado Pessoa");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        // Linha 0
        grid.add(new Label("CPF:"), 0, 0);
        txtCpf = new TextField();
        txtCpf.setPromptText("Apenas números, 11 dígitos");
        grid.add(txtCpf, 1, 0);

        btnNovo = new Button("Novo");
        btnBuscar = new Button("Buscar");
        grid.add(btnNovo, 2, 0);
        grid.add(btnBuscar, 3, 0);

        // Linha 1
        grid.add(new Label("Nome:"), 0, 1);
        txtNome = new TextField();
        txtNome.setDisable(true);
        grid.add(txtNome, 1, 1, 3, 1);

        // Linha 2
        grid.add(new Label("Renda:"), 0, 2);
        txtRenda = new TextField();
        txtRenda.setDisable(true);
        grid.add(txtRenda, 1, 2);

        // Linha 3
        grid.add(new Label("Logradouro:"), 0, 3);
        txtLogradouro = new TextField();
        txtLogradouro.setDisable(true);
        grid.add(txtLogradouro, 1, 3, 3, 1);

        // Linha 4
        grid.add(new Label("CEP:"), 0, 4);
        txtCep = new TextField();
        txtCep.setDisable(true);
        grid.add(txtCep, 1, 4);

        grid.add(new Label("Número:"), 2, 4);
        txtNumero = new TextField();
        txtNumero.setDisable(true);
        grid.add(txtNumero, 3, 4);

        // Linha 5
        grid.add(new Label("Complemento:"), 0, 5);
        txtComplemento = new TextField();
        txtComplemento.setDisable(true);
        grid.add(txtComplemento, 1, 5, 3, 1);

        // Linha 6
        grid.add(new Label("País:"), 0, 6);
        txtPais = new TextField();
        txtPais.setDisable(true);
        grid.add(txtPais, 1, 6);

        grid.add(new Label("Estado:"), 2, 6);
        txtEstado = new TextField();
        txtEstado.setDisable(true);
        grid.add(txtEstado, 3, 6);

        // Linha 7
        grid.add(new Label("Cidade:"), 0, 7);
        txtCidade = new TextField();
        txtCidade.setDisable(true);
        grid.add(txtCidade, 1, 7);

        // Linha 8 - botões
        btnIncluirAlterar = new Button("Incluir");
        btnExcluir = new Button("Excluir");
        btnCancelar = new Button("Cancelar");
        btnLimpar = new Button("Limpar");

        btnIncluirAlterar.setDisable(true);
        btnExcluir.setDisable(true);
        btnCancelar.setDisable(true);

        grid.add(btnIncluirAlterar, 1, 8);
        grid.add(btnExcluir, 2, 8);
        grid.add(btnCancelar, 3, 8);
        grid.add(btnLimpar, 4, 8);

        // Eventos
        btnNovo.setOnAction(e -> iniciarNovo());
        btnBuscar.setOnAction(e -> buscarSegurado());
        btnIncluirAlterar.setOnAction(e -> incluirOuAlterar());
        btnExcluir.setOnAction(e -> excluirSegurado());
        btnCancelar.setOnAction(e -> cancelarEdicao());
        btnLimpar.setOnAction(e -> limparCampos());

        Scene scene = new Scene(grid, 700, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void iniciarNovo() {
        String cpf = txtCpf.getText().trim();
        if (cpf.isEmpty()) {
            alerta(Alert.AlertType.WARNING, "CPF deve ser preenchido para novo cadastro.");
            return;
        }
        SeguradoPessoa existente = mediator.buscarSeguradoPessoa(cpf);
        if (existente != null) {
            alerta(Alert.AlertType.INFORMATION, "Segurado Pessoa já existente com este CPF.");
            return;
        }
        editando = false;
        habilitarCampos(true);
        btnIncluirAlterar.setText("Incluir");
        btnIncluirAlterar.setDisable(false);
        btnCancelar.setDisable(false);
        btnExcluir.setDisable(true);
        btnNovo.setDisable(true);
        btnBuscar.setDisable(true);
        txtCpf.setDisable(true);
    }

    private void buscarSegurado() {
        String cpf = txtCpf.getText().trim();
        if (cpf.isEmpty()) {
            alerta(Alert.AlertType.WARNING, "Informe o CPF para buscar.");
            return;
        }
        SeguradoPessoa seg = mediator.buscarSeguradoPessoa(cpf);
        if (seg == null) {
            alerta(Alert.AlertType.INFORMATION, "Segurado Pessoa não encontrado.");
            return;
        }
        preencherCampos(seg);
        editando = true;
        habilitarCampos(true);
        btnIncluirAlterar.setText("Alterar");
        btnIncluirAlterar.setDisable(false);
        btnExcluir.setDisable(false);
        btnCancelar.setDisable(false);
        btnNovo.setDisable(true);
        btnBuscar.setDisable(true);
        txtCpf.setDisable(true);
    }

    private void incluirOuAlterar() {
        String cpf = txtCpf.getText().trim();
        String nome = txtNome.getText().trim();
        String rendaStr = txtRenda.getText().trim();

        String logradouro = txtLogradouro.getText().trim();
        String cep = txtCep.getText().trim();
        String numero = txtNumero.getText().trim();
        String complemento = txtComplemento.getText().trim();
        String pais = txtPais.getText().trim();
        String estado = txtEstado.getText().trim();
        String cidade = txtCidade.getText().trim();

        if (cpf.isEmpty() || nome.isEmpty() || rendaStr.isEmpty() || logradouro.isEmpty() || cep.isEmpty() || numero.isEmpty()
                || pais.isEmpty() || estado.isEmpty() || cidade.isEmpty()) {
            alerta(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios.");
            return;
        }

        double renda;
        try {
            renda = Double.parseDouble(rendaStr);
        } catch (NumberFormatException ex) {
            alerta(Alert.AlertType.ERROR, "Renda deve ser um número válido.");
            return;
        }

        Endereco endereco = new Endereco(logradouro, cep, numero, complemento, pais, estado, cidade);

        SeguradoPessoa seg = new SeguradoPessoa(nome, endereco, LocalDate.now(), BigDecimal.ZERO, cpf, renda);

        String msg;
        if (editando) {
            msg = mediator.alterarSeguradoPessoa(seg);
            if (msg != null) {
                alerta(Alert.AlertType.ERROR, msg);
                return;
            }
            alerta(Alert.AlertType.INFORMATION, "Alteração realizada com sucesso.");
        } else {
            msg = mediator.incluirSeguradoPessoa(seg);
            if (msg != null) {
                alerta(Alert.AlertType.ERROR, msg);
                return;
            }
            alerta(Alert.AlertType.INFORMATION, "Inclusão realizada com sucesso.");
        }
        limparCampos();
        resetarEstadoInicial();
    }

    private void excluirSegurado() {
        String cpf = txtCpf.getText().trim();
        if (cpf.isEmpty()) {
            alerta(Alert.AlertType.WARNING, "Informe o CPF para excluir.");
            return;
        }
        String msg = mediator.excluirSeguradoPessoa(cpf);
        if (msg != null) {
            alerta(Alert.AlertType.ERROR, msg);
            return;
        }
        alerta(Alert.AlertType.INFORMATION, "Exclusão realizada com sucesso.");
        limparCampos();
        resetarEstadoInicial();
    }

    private void cancelarEdicao() {
        limparCampos();
        resetarEstadoInicial();
    }

    private void limparCampos() {
        if (txtCpf.isDisable()) {
            // Só limpa campos editáveis
            txtNome.clear();
            txtRenda.clear();
            txtLogradouro.clear();
            txtCep.clear();
            txtNumero.clear();
            txtComplemento.clear();
            txtPais.clear();
            txtEstado.clear();
            txtCidade.clear();
        } else {
            // Limpa tudo
            txtCpf.clear();
            txtNome.clear();
            txtRenda.clear();
            txtLogradouro.clear();
            txtCep.clear();
            txtNumero.clear();
            txtComplemento.clear();
            txtPais.clear();
            txtEstado.clear();
            txtCidade.clear();
        }
    }

    private void preencherCampos(SeguradoPessoa seg) {
        txtCpf.setText(seg.getCpf());
        txtNome.setText(seg.getNome());
        txtRenda.setText(Double.toString(seg.getRenda()));

        Endereco e = seg.getEndereco();
        if (e != null) {
            txtLogradouro.setText(e.getLogradouro());
            txtCep.setText(e.getCep());
            txtNumero.setText(e.getNumero());
            txtComplemento.setText(e.getComplemento());
            txtPais.setText(e.getPais());
            txtEstado.setText(e.getEstado());
            txtCidade.setText(e.getCidade());
        } else {
            txtLogradouro.clear();
            txtCep.clear();
            txtNumero.clear();
            txtComplemento.clear();
            txtPais.clear();
            txtEstado.clear();
            txtCidade.clear();
        }
    }

    private void habilitarCampos(boolean habilitar) {
        txtNome.setDisable(!habilitar);
        txtRenda.setDisable(!habilitar);
        txtLogradouro.setDisable(!habilitar);
        txtCep.setDisable(!habilitar);
        txtNumero.setDisable(!habilitar);
        txtComplemento.setDisable(!habilitar);
        txtPais.setDisable(!habilitar);
        txtEstado.setDisable(!habilitar);
        txtCidade.setDisable(!habilitar);
    }

    private void resetarEstadoInicial() {
        editando = false;
        habilitarCampos(false);
        btnIncluirAlterar.setDisable(true);
        btnExcluir.setDisable(true);
        btnCancelar.setDisable(true);
        btnNovo.setDisable(false);
        btnBuscar.setDisable(false);
        txtCpf.setDisable(false);
    }

    private void alerta(Alert.AlertType tipo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Mensagem");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
