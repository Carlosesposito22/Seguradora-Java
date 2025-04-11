package br.edu.cs.poo.ac.seguro.testes;

import br.edu.cs.poo.ac.seguro.daos.ApoliceDAO;
import br.edu.cs.poo.ac.seguro.entidades.Apolice;

import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;
import br.edu.cs.poo.ac.seguro.entidades.Veiculo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TesteApoliceDAO extends TesteDAO {

    private ApoliceDAO dao = new ApoliceDAO();

    @Override
    protected Class getClasse() {
        return Apolice.class;
    }

    static {
        String sep = File.separator;
        File dir = new File("." + sep + Apolice.class.getSimpleName());
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private Veiculo veiculo = new Veiculo("JQK3B92",2005,null,null,null);



    @Test
    public void teste01() {

        String numero = "0";

        cadastro.incluir(new Apolice(veiculo,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO),numero);
        Apolice seg = dao.buscar(numero);
        Assertions.assertNotNull(seg);

    }

    @Test
    public void teste02() {

        String numero = "0";

        boolean ret = dao.alterar(new Apolice(veiculo, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        Assertions.assertFalse(ret);


    }

    @Test
    public void teste03(){

        String numero = "0";

        cadastro.incluir(new Apolice(veiculo,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO),numero);
        Apolice seg = dao.buscar(numero);
        boolean ret = dao.excluir(numero);
        Assertions.assertTrue(ret);





    }










    }







