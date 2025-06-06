package br.edu.cs.poo.ac.seguro.testes;

import br.edu.cs.poo.ac.seguro.daos.SeguradoEmpresaDAO;
import br.edu.cs.poo.ac.seguro.daos.SeguradoPessoaDAO;
import br.edu.cs.poo.ac.seguro.mediators.DadosSinistro;
import br.edu.cs.poo.ac.seguro.mediators.SinistroMediator;
import br.edu.cs.poo.ac.seguro.daos.VeiculoDAO;
import br.edu.cs.poo.ac.seguro.daos.ApoliceDAO;
import br.edu.cs.poo.ac.seguro.entidades.*; // Importa todas as entidades necessárias
import br.edu.cs.poo.ac.seguro.excecoes.ExcecaoValidacaoDados;
import br.edu.cs.poo.ac.seguro.mediators.ValidadorCpfCnpj; // Importa ValidadorCpfCnpj
import br.edu.cs.poo.ac.seguro.mediators.StringUtils; // Importa StringUtils

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class TelaInclusaoSinistroFX extends Application {

    private SinistroMediator mediator;
    private SeguradoPessoaDAO seguradoPessoaDAO;
    private SeguradoEmpresaDAO seguradoEmpresaDAO;
    private VeiculoDAO veiculoDAO; // Necessário para buscar o veículo pela placa

    private TextField txtPlaca;
    private TextField txtDataHoraSinistro;
    private TextField txtCpfCnpjSegurado;
    private TextField txtUsuarioRegistro;
    private TextField txtValorSinistro;
    private ComboBox<TipoSinistro> cmbTipoSinistro;

    private Button btnBuscarSegurado;
    private Button btnIncluir;
    private Button btnLimpar;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    public TelaInclusaoSinistroFX() {
        this.mediator = SinistroMediator.getInstancia();
        this.seguradoPessoaDAO = new SeguradoPessoaDAO();
        this.seguradoEmpresaDAO = new SeguradoEmpresaDAO();
        this.veiculoDAO = new VeiculoDAO(); // Inicializa o VeiculoDAO
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Inclusão de Sinistro");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        initComponents();
        setupLayout(grid);
        addListeners();
        setupTabOrder();

        Scene scene = new Scene(grid, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initComponents() {
        txtPlaca = new TextField();
        txtPlaca.setPromptText("Ex: ABC1234");
        txtPlaca.setMaxWidth(120);

        txtDataHoraSinistro = new TextField();
        txtDataHoraSinistro.setPromptText("dd/MM/yyyy HH:mm:ss");
        setupDateTimeMask(txtDataHoraSinistro);

        txtCpfCnpjSegurado = new TextField();
        txtCpfCnpjSegurado.setPromptText("CPF ou CNPJ do Segurado");
        txtCpfCnpjSegurado.setMaxWidth(180);
        setupCpfCnpjMask(txtCpfCnpjSegurado);

        txtUsuarioRegistro = new TextField();
        txtUsuarioRegistro.setPromptText("Nome do Usuário/Segurado");
        txtUsuarioRegistro.setEditable(false);

        txtValorSinistro = new TextField();
        txtValorSinistro.setPromptText("Ex: 1234,56");
        setupCurrencyMask(txtValorSinistro);

        cmbTipoSinistro = new ComboBox<>();
        cmbTipoSinistro.getItems().addAll(
                Arrays.stream(TipoSinistro.values())
                        .sorted(Comparator.comparing(TipoSinistro::getNome))
                        .collect(java.util.stream.Collectors.toList())
        );
        cmbTipoSinistro.setConverter(new StringConverter<TipoSinistro>() {
            @Override
            public String toString(TipoSinistro tipo) {
                return tipo != null ? tipo.getNome() : "";
            }

            @Override
            public TipoSinistro fromString(String string) {
                return null;
            }
        });
        if (!cmbTipoSinistro.getItems().isEmpty()) {
            cmbTipoSinistro.getSelectionModel().selectFirst();
        }

        btnBuscarSegurado = new Button("Buscar Segurado");
        btnIncluir = new Button("Incluir");
        btnLimpar = new Button("Limpar");
    }

    private void setupLayout(GridPane grid) {
        int row = 0;
        grid.add(new Label("Placa:"), 0, row);
        grid.add(txtPlaca, 1, row);
        row++;

        grid.add(new Label("Data/Hora Sinistro:"), 0, row);
        grid.add(txtDataHoraSinistro, 1, row);
        row++;

        grid.add(new Label("CPF/CNPJ Segurado:"), 0, row);
        HBox hbCpfCnpj = new HBox(5);
        hbCpfCnpj.getChildren().addAll(txtCpfCnpjSegurado, btnBuscarSegurado);
        grid.add(hbCpfCnpj, 1, row);
        row++;

        grid.add(new Label("Usuário Registro:"), 0, row);
        grid.add(txtUsuarioRegistro, 1, row);
        row++;

        grid.add(new Label("Valor Sinistro:"), 0, row);
        grid.add(txtValorSinistro, 1, row);
        row++;

        grid.add(new Label("Tipo Sinistro:"), 0, row);
        grid.add(cmbTipoSinistro, 1, row);
        row++;

        HBox hbButtons = new HBox(10);
        hbButtons.setAlignment(Pos.BOTTOM_RIGHT);
        hbButtons.getChildren().addAll(btnIncluir, btnLimpar);
        grid.add(hbButtons, 1, row);
    }

    private void addListeners() {
        btnIncluir.setOnAction(e -> incluirSinistro());
        btnLimpar.setOnAction(e -> limparCampos());
        btnBuscarSegurado.setOnAction(e -> buscarNomeSegurado());

        txtCpfCnpjSegurado.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !txtCpfCnpjSegurado.getText().trim().isEmpty()) {
                buscarNomeSegurado();
            }
        });
    }

    private void setupTabOrder() {
        txtPlaca.setFocusTraversable(true);
        txtDataHoraSinistro.setFocusTraversable(true);
        txtCpfCnpjSegurado.setFocusTraversable(true);
        btnBuscarSegurado.setFocusTraversable(true);
        txtUsuarioRegistro.setFocusTraversable(true);
        txtValorSinistro.setFocusTraversable(true);
        cmbTipoSinistro.setFocusTraversable(true);
        btnIncluir.setFocusTraversable(true);
        btnLimpar.setFocusTraversable(true);
    }

    private void setupDateTimeMask(TextField textField) {
        final String format = "dd/MM/yyyy HH:mm:ss";
        Pattern pattern = Pattern.compile("[0-9/ :]*");
        UnaryOperator<Change> filter = c -> {
            if (pattern.matcher(c.getControlNewText()).matches()) {
                return c;
            } else {
                return null;
            }
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String text = textField.getText().trim();
                if (!text.isEmpty()) {
                    try {
                        LocalDateTime.parse(text, DATE_TIME_FORMATTER);
                        textField.setStyle("");
                    } catch (DateTimeParseException e) {
                        textField.setStyle("-fx-border-color: red;");
                        showAlert(Alert.AlertType.ERROR, "Erro de Formato", "Data/Hora do sinistro inválida. Use o formato " + format);
                    }
                } else {
                    textField.setStyle("");
                }
            }
        });
    }

    private void setupCurrencyMask(TextField textField) {
        Pattern pattern = Pattern.compile("[0-9.,]*");
        UnaryOperator<Change> filter = c -> {
            if (pattern.matcher(c.getControlNewText()).matches()) {
                return c;
            } else {
                return null;
            }
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String text = textField.getText().trim();
                if (!text.isEmpty()) {
                    try {
                        String cleanText = text.replace(".", "").replace(",", ".");
                        double value = Double.parseDouble(cleanText);
                        textField.setText(DECIMAL_FORMAT.format(value));
                        textField.setStyle("");
                    } catch (NumberFormatException e) {
                        textField.setStyle("-fx-border-color: red;");
                        showAlert(Alert.AlertType.ERROR, "Erro de Formato", "Valor do sinistro inválido. Use apenas números, vírgula para centavos e ponto para milhares (opcional).");
                    }
                } else {
                    textField.setStyle("");
                }
            }
        });
    }

    private void setupCpfCnpjMask(TextField textField) {
        Pattern pattern = Pattern.compile("\\d*");
        UnaryOperator<Change> filter = c -> {
            if (pattern.matcher(c.getControlNewText()).matches()) {
                if (c.getControlNewText().length() > 14) return null;
                return c;
            } else {
                return null;
            }
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String id = textField.getText().trim();
                if (!id.isEmpty()) {
                    boolean isValid = false;
                    if (id.length() == 11) {
                        isValid = ValidadorCpfCnpj.ehCpfValido(id);
                    } else if (id.length() == 14) {
                        isValid = ValidadorCpfCnpj.ehCnpjValido(id);
                    }

                    if (!isValid) {
                        textField.setStyle("-fx-border-color: red;");
                        // Não mostra alerta aqui para não ser redundante com a busca de nome
                    } else {
                        textField.setStyle("");
                    }
                } else {
                    textField.setStyle("");
                }
            }
        });
    }

    private void buscarNomeSegurado() {
        String idSegurado = txtCpfCnpjSegurado.getText().trim();
        txtUsuarioRegistro.clear();

        if (StringUtils.ehNuloOuBranco(idSegurado)) {
            showAlert(Alert.AlertType.WARNING, "Busca de Segurado", "Informe o CPF ou CNPJ do segurado.");
            txtCpfCnpjSegurado.requestFocus();
            txtCpfCnpjSegurado.setStyle("-fx-border-color: orange;"); // Indica que o campo precisa ser preenchido
            return;
        }

        String nomeSegurado = null;
        boolean idValido = false;

        if (idSegurado.length() == 11) {
            idValido = ValidadorCpfCnpj.ehCpfValido(idSegurado);
            if (idValido) {
                SeguradoPessoa seguradoPessoa = seguradoPessoaDAO.buscar(idSegurado);
                if (seguradoPessoa != null) {
                    nomeSegurado = seguradoPessoa.getNome();
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Busca de Segurado", "CPF não encontrado no cadastro de segurados.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Busca de Segurado", "CPF inválido.");
            }
        } else if (idSegurado.length() == 14) {
            idValido = ValidadorCpfCnpj.ehCnpjValido(idSegurado);
            if (idValido) {
                SeguradoEmpresa seguradoEmpresa = seguradoEmpresaDAO.buscar(idSegurado);
                if (seguradoEmpresa != null) {
                    nomeSegurado = seguradoEmpresa.getNome();
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Busca de Segurado", "CNPJ não encontrado no cadastro de segurados.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Busca de Segurado", "CNPJ inválido.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Busca de Segurado", "Formato de CPF/CNPJ inválido. Use 11 dígitos para CPF ou 14 para CNPJ.");
        }

        if (nomeSegurado != null) {
            txtUsuarioRegistro.setText(nomeSegurado);
            txtCpfCnpjSegurado.setStyle(""); // Limpa o estilo de erro se a busca foi bem-sucedida
        } else {
            txtUsuarioRegistro.clear();
            if (idValido) { // Se o ID é válido, mas não foi encontrado, marca o campo
                txtCpfCnpjSegurado.setStyle("-fx-border-color: red;");
            }
        }
    }

    private void incluirSinistro() {
        try {
            java.util.List<String> errosFormato = new java.util.ArrayList<>();

            // 1. Validação da Placa e busca do veículo
            String placa = txtPlaca.getText().trim();
            Pattern finalPlatePattern = Pattern.compile("[A-Z]{3}\\d{4}");
            Veiculo veiculoEncontrado = null; // Para armazenar o veículo encontrado

            if (placa.isEmpty() || !finalPlatePattern.matcher(placa).matches()) {
                txtPlaca.setStyle("-fx-border-color: red;");
                errosFormato.add("Placa inválida. Deve ter 3 letras maiúsculas seguidas de 4 números (Ex: ABC1234).");
            } else {
                txtPlaca.setStyle("");
                veiculoEncontrado = veiculoDAO.buscar(placa);
                if (veiculoEncontrado == null) {
                    txtPlaca.setStyle("-fx-border-color: red;");
                    errosFormato.add("Veículo com a placa '" + placa + "' não encontrado no cadastro.");
                } else {
                    txtPlaca.setStyle(""); // Limpa o estilo de erro se foi encontrado
                }
            }

            // 2. Validação do CPF/CNPJ do Segurado (campo de entrada)
            String cpfCnpjDigitado = txtCpfCnpjSegurado.getText().trim();
            if (StringUtils.ehNuloOuBranco(cpfCnpjDigitado)) {
                txtCpfCnpjSegurado.setStyle("-fx-border-color: red;");
                errosFormato.add("CPF/CNPJ do segurado é obrigatório.");
            } else {
                boolean idDigitadoValido = false;
                if (cpfCnpjDigitado.length() == 11) {
                    idDigitadoValido = ValidadorCpfCnpj.ehCpfValido(cpfCnpjDigitado);
                } else if (cpfCnpjDigitado.length() == 14) {
                    idDigitadoValido = ValidadorCpfCnpj.ehCnpjValido(cpfCnpjDigitado);
                }

                if (!idDigitadoValido) {
                    txtCpfCnpjSegurado.setStyle("-fx-border-color: red;");
                    errosFormato.add("CPF ou CNPJ do segurado inválido.");
                } else if (txtUsuarioRegistro.getText().trim().isEmpty()) {
                    // Se o CPF/CNPJ é válido mas o nome não foi carregado, provavelmente não existe no sistema
                    txtCpfCnpjSegurado.setStyle("-fx-border-color: red;");
                    errosFormato.add("Segurado não encontrado para o CPF/CNPJ informado. Utilize o botão 'Buscar Segurado'.");
                } else {
                    txtCpfCnpjSegurado.setStyle("");
                }
            }

            // 3. Validação do Usuário Registro (nome do segurado carregado)
            String usuarioRegistro = txtUsuarioRegistro.getText().trim();
            if (StringUtils.ehNuloOuBranco(usuarioRegistro)) {
                errosFormato.add("O nome do usuário/segurado (preenchido automaticamente pelo CPF/CNPJ) é obrigatório.");
            }


            // Validação de formato da Data/Hora do Sinistro
            LocalDateTime dataHoraSinistro = null;
            if (txtDataHoraSinistro.getText().trim().isEmpty()) {
                errosFormato.add("Data/Hora do sinistro é obrigatória.");
                txtDataHoraSinistro.setStyle("-fx-border-color: red;");
            } else {
                try {
                    dataHoraSinistro = LocalDateTime.parse(txtDataHoraSinistro.getText().trim(), DATE_TIME_FORMATTER);
                    txtDataHoraSinistro.setStyle("");
                } catch (DateTimeParseException e) {
                    txtDataHoraSinistro.setStyle("-fx-border-color: red;");
                    errosFormato.add("Data/Hora do sinistro inválida. Use o formato dd/MM/yyyy HH:mm:ss.");
                }
            }

            // Validação de formato do Valor do Sinistro
            BigDecimal valorSinistro = null;
            if (txtValorSinistro.getText().trim().isEmpty()) {
                errosFormato.add("Valor do sinistro é obrigatório.");
                txtValorSinistro.setStyle("-fx-border-color: red;");
            } else {
                try {
                    String cleanValor = txtValorSinistro.getText().trim().replace(".", "").replace(",", ".");
                    valorSinistro = new BigDecimal(cleanValor);
                    if (valorSinistro.compareTo(BigDecimal.ZERO) <= 0) {
                        errosFormato.add("Valor do sinistro deve ser maior que zero.");
                    }
                    txtValorSinistro.setStyle("");
                } catch (NumberFormatException e) {
                    txtValorSinistro.setStyle("-fx-border-color: red;");
                    errosFormato.add("Valor do sinistro inválido. Use apenas números, vírgula para centavos e ponto para milhares (opcional).");
                }
            }

            // Se houver erros de formato até aqui, exibe e interrompe
            if (!errosFormato.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erro de Entrada", "Corrija os campos com formato inválido:\n" + String.join("\n", errosFormato));
                return;
            }

            // --- NOVA VALIDAÇÃO: CPF/CNPJ do segurado da placa deve ser igual ao digitado ---
            if (veiculoEncontrado != null && veiculoEncontrado.getProprietario() != null) {
                String idSeguradoDaPlaca = null;
                if (veiculoEncontrado.getProprietario() instanceof SeguradoPessoa) {
                    idSeguradoDaPlaca = ((SeguradoPessoa) veiculoEncontrado.getProprietario()).getCpf();
                } else if (veiculoEncontrado.getProprietario() instanceof SeguradoEmpresa) {
                    idSeguradoDaPlaca = ((SeguradoEmpresa) veiculoEncontrado.getProprietario()).getCnpj();
                }

                if (idSeguradoDaPlaca != null && !idSeguradoDaPlaca.equals(cpfCnpjDigitado)) {
                    txtPlaca.setStyle("-fx-border-color: red;");
                    txtCpfCnpjSegurado.setStyle("-fx-border-color: red;");
                    showAlert(Alert.AlertType.ERROR, "Erro de Validação",
                            "O CPF/CNPJ (" + cpfCnpjDigitado + ") informado não corresponde ao segurado proprietário do veículo de placa " + placa + " (CPF/CNPJ do proprietário: " + idSeguradoDaPlaca + ").");
                    return;
                } else if (idSeguradoDaPlaca == null) {
                    // Isso não deveria acontecer se o veículo foi encontrado, mas é um fallback
                    errosFormato.add("Não foi possível determinar o segurado proprietário da placa " + placa + ".");
                }
            } else if (veiculoEncontrado != null && veiculoEncontrado.getProprietario() == null) {
                // Caso o veículo seja encontrado mas não tenha um segurado associado (erro de dados)
                errosFormato.add("O veículo com placa " + placa + " não possui um segurado associado no cadastro.");
            }


            // Se houver erros após a validação do proprietário, exibe e interrompe
            if (!errosFormato.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erro de Entrada", "Corrija os campos com formato inválido:\n" + String.join("\n", errosFormato));
                return;
            }


            // A partir daqui, os formatos básicos e a associação segurado-veículo foram validados.
            double valorSinistroDouble = (valorSinistro != null) ? valorSinistro.doubleValue() : 0.0;

            TipoSinistro tipoSinistroSelecionado = cmbTipoSinistro.getSelectionModel().getSelectedItem();
            int codigoTipoSinistro = (tipoSinistroSelecionado != null) ? tipoSinistroSelecionado.getCodigo() : 0;

            DadosSinistro dados = new DadosSinistro(placa, dataHoraSinistro, usuarioRegistro, valorSinistroDouble, codigoTipoSinistro);

            String numeroSinistro = mediator.incluirSinistro(dados, LocalDateTime.now());

            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Sinistro incluído com sucesso! Anote o número do sinistro: " + numeroSinistro);
            limparCampos();

        } catch (ExcecaoValidacaoDados e) {
            String mensagensErro = String.join("\n", e.getMensagens());
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", "Problemas na inclusão do sinistro:\n" + mensagensErro);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        txtPlaca.clear();
        txtDataHoraSinistro.clear();
        txtCpfCnpjSegurado.clear();
        txtUsuarioRegistro.clear();
        txtValorSinistro.clear();
        if (!cmbTipoSinistro.getItems().isEmpty()) {
            cmbTipoSinistro.getSelectionModel().selectFirst();
        }
        txtPlaca.setStyle("");
        txtDataHoraSinistro.setStyle("");
        txtCpfCnpjSegurado.setStyle("");
        txtUsuarioRegistro.setStyle("");
        txtValorSinistro.setStyle("");
        txtPlaca.requestFocus();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinWidth(600); // Ajuste da largura da caixa de diálogo
        alert.showAndWait();
    }

    public static void main(String[] args) {
        try {
            VeiculoDAO veiculoDAO = new VeiculoDAO();
            ApoliceDAO apoliceDAO = new ApoliceDAO();
            SeguradoPessoaDAO segPesDAO = new SeguradoPessoaDAO();
            SeguradoEmpresaDAO segEmpDAO = new SeguradoEmpresaDAO();

            // --- DADOS DE TESTE INCLUÍDOS NO DAO NO INÍCIO DA EXECUÇÃO ---

            // 1. Segurado Pessoa (CPF 11122233344)
            Endereco endTestePessoa = new Endereco("Rua da Programação", "50000-000", "123", "Casa A", "Brasil", "PE", "Recife");
            SeguradoPessoa pessoaTeste = new SeguradoPessoa(
                    "Fulano de Tal",
                    endTestePessoa,
                    LocalDate.of(1990, 1, 1),
                    new BigDecimal("250.50"),
                    "11122233344",
                    5000.00
            );
            if (segPesDAO.buscar(pessoaTeste.getIdUnico()) == null) {
                if (segPesDAO.incluir(pessoaTeste)) {
                    System.out.println("Segurado Pessoa 11122233344 incluído para teste.");
                } else {
                    System.out.println("Erro ao incluir Segurado Pessoa 11122233344.");
                }
            } else {
                System.out.println("Segurado Pessoa 11122233344 já existe.");
            }

            // 2. Segurado Empresa (CNPJ 11222333000144)
            Endereco endTesteEmpresa = new Endereco("Av. das Empresas", "01000-000", "1000", "Conj. 50", "Brasil", "SP", "São Paulo");
            SeguradoEmpresa empresaTeste = new SeguradoEmpresa(
                    "Empresa Alpha Ltda",
                    endTesteEmpresa,
                    LocalDate.of(2005, 1, 1),
                    new BigDecimal("1500.00"),
                    "11222333000144",
                    100000.00,
                    false
            );
            if (segEmpDAO.buscar(empresaTeste.getIdUnico()) == null) {
                if (segEmpDAO.incluir(empresaTeste)) {
                    System.out.println("Segurado Empresa 11222333000144 incluído para teste.");
                } else {
                    System.out.println("Erro ao incluir Segurado Empresa 11222333000144.");
                }
            } else {
                System.out.println("Segurado Empresa 11222333000144 já existe.");
            }

            // 3. Veículo associado ao Segurado Pessoa (placa ABC1234, segurado 11122233344)
            Veiculo veiculoExemploPessoa = new Veiculo("ABC1234", 2023, pessoaTeste, CategoriaVeiculo.BASICO);
            if (veiculoDAO.buscar(veiculoExemploPessoa.getIdUnico()) == null) {
                if (!veiculoDAO.incluir(veiculoExemploPessoa)) {
                    System.out.println("Erro ao incluir veículo ABC1234.");
                } else {
                    System.out.println("Veículo ABC1234 incluído para teste (proprietário: Fulano de Tal).");
                }
            } else {
                System.out.println("Veículo ABC1234 já existe.");
            }

            // 4. Veículo associado ao Segurado Empresa (placa XYZ5678, segurado 11222333000144)
            Veiculo veiculoExemploEmpresa = new Veiculo("XYZ5678", 2024, empresaTeste, CategoriaVeiculo.BASICO);
            if (veiculoDAO.buscar(veiculoExemploEmpresa.getIdUnico()) == null) {
                if (!veiculoDAO.incluir(veiculoExemploEmpresa)) {
                    System.out.println("Erro ao incluir veículo XYZ5678.");
                } else {
                    System.out.println("Veículo XYZ5678 incluído para teste (proprietário: Empresa Alpha Ltda).");
                }
            } else {
                System.out.println("Veículo XYZ5678 já existe.");
            }

            // 5. Apólice de exemplo (apólice APOLICE001, veiculo ABC1234)
            Apolice apoliceExemploPessoa = new Apolice(
                    "APOLICE001",
                    veiculoExemploPessoa,
                    new BigDecimal("500.00"),
                    new BigDecimal("1200.00"),
                    new BigDecimal("100000.00"),
                    LocalDate.of(2024, 1, 1)
            );
            if (apoliceDAO.buscar(apoliceExemploPessoa.getIdUnico()) == null) {
                if (!apoliceDAO.incluir(apoliceExemploPessoa)) {
                    System.out.println("Erro ao incluir apólice APOLICE001.");
                } else {
                    System.out.println("Apólice APOLICE001 incluída para teste.");
                }
            } else {
                System.out.println("Apólice APOLICE001 já existe.");
            }

            // 6. Apólice de exemplo (apólice APOLICE002, veiculo XYZ5678)
            Apolice apoliceExemploEmpresa = new Apolice(
                    "APOLICE002",
                    veiculoExemploEmpresa,
                    new BigDecimal("700.00"),
                    new BigDecimal("1500.00"),
                    new BigDecimal("200000.00"),
                    LocalDate.of(2024, 3, 1)
            );
            if (apoliceDAO.buscar(apoliceExemploEmpresa.getIdUnico()) == null) {
                if (!apoliceDAO.incluir(apoliceExemploEmpresa)) {
                    System.out.println("Erro ao incluir apólice APOLICE002.");
                } else {
                    System.out.println("Apólice APOLICE002 incluída para teste.");
                }
            } else {
                System.out.println("Apólice APOLICE002 já existe.");
            }

        } catch (RuntimeException e) {
            System.err.println("Erro na inicialização dos dados de teste: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro inesperado na inicialização: " + e.getMessage());
            e.printStackTrace();
        }

        launch(args);
    }
}