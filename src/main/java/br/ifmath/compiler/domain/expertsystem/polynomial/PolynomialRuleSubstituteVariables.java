package br.ifmath.compiler.domain.expertsystem.polynomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolynomialRuleSubstituteVariables implements IRule {
    private List<NumericValueVariable> userInput = new ArrayList<>();


    private List<ExpandedQuadruple> expandedQuadruples;

    public PolynomialRuleSubstituteVariables() {
        this.expandedQuadruples = new ArrayList<>();
        // Valores para testes: switch_variable_y_to_3_scenery_one_with_success
        // e switch_multiple_variable_scenery_one_with_success
        this.userInput.add(new NumericValueVariable("a", 777));
        this.userInput.add(new NumericValueVariable("y", 3));
        this.userInput.add(new NumericValueVariable("z", 4));

//        Valor para teste sum_numbers_scenery_one_with_success
//        this.userInput.add(new NumericValueVariable("y", 4));
    }


    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return this.isThereVariablesToSubstitute(sources.get(0).getOperationsFromRight()); // pegando operacoes da direita pois nao haverá sinal de igualdade. EX: x=3y^2
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        expandedQuadruples.clear();

        List<Step> steps = new ArrayList<>();

        switchVariables(sources.get(0).getExpandedQuadruples());

        String right = generateParameter(sources.get(0), 1);

        //instanciando um novo ThreeAddressCode para ser adicionada a lista que contém o passo-a-passo
        //FIXME: primeiro parâmetro hardcoded devido a restrição do compilador
        ThreeAddressCode step = new ThreeAddressCode("x", sources.get(0).getComparison(), right, expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation(), "Substituindo os valores nas variáveis correspondentes."));

        return steps;
    }

    /**
     * Transforma o {@link ThreeAddressCode} em uma {@link String}
     * @param value código de três endereços a ser transformado
     * @param position valor da prioridade das operações
     * @return {@link String} que representa o {@link ThreeAddressCode}
     */
    private String generateParameter(ThreeAddressCode value, int position) {
        //variável que irá concatenar os valores das quádruplas
        String parameter = "";

        //variável contendo a lista de quádruplas com seus elementros reordenados
        List<ExpandedQuadruple> inverted = new ArrayList<>(value.getExpandedQuadruples());
        Collections.reverse(inverted);


        for (ExpandedQuadruple expanded : inverted) {
            //caso algum dos argumentos à direita da quádrupla seja uma variável temporária
            if (StringUtil.match(value.getRight(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                //procura dentro das quádruplas que possúi
                ExpandedQuadruple expandedQuadruple = value.findQuadrupleByResult(expanded.getResult());

                //verifica se a variável temporária está presente no segundo argumento e caso sim, a mesma
                //não é concatenada na variável de retorno
                if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    parameter += expandedQuadruple.getArgument1() + expandedQuadruple.getOperator();
                } else {
                    parameter += expandedQuadruple.getArgument1() + expandedQuadruple.getOperator() + expandedQuadruple.getArgument2();
                }

            } else {
                parameter += expanded.getArgument1() + expanded.getOperator() + expanded.getArgument2();
            }
        }
        return parameter;
    }

    /**
     * Altera todas as variáveis presentes no polinômio para seus valores
     * respectivos.
     *
     * @param expandedQuadruples Lista de quadruplas expandidadas para substituição
     */
    private void switchVariables(List<ExpandedQuadruple> expandedQuadruples) {

        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            replaceTemporaryVariables(expandedQuadruple);
        }
        userInput.remove(0);
        if (!userInput.isEmpty()) {
            switchVariables(expandedQuadruples);
        }
    }

    /**
     * Efetivamente substitui as variáveis contidas no polinômio
     *
     * @param expandedQuadruple uma quádrupla expecífica a ser analisada
     */
    private void replaceTemporaryVariables(ExpandedQuadruple expandedQuadruple) {
        //verifica se o usuário atribuiu valores as variáveis
        //FIXME: deve ser alterado quando forem utilizados valores reais do frontend
        if (!this.userInput.isEmpty()) {
            //caso o primeiro argumento não seja uma variável temporária
            if (!StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                //se o primeiro argumento da quadrupla for igual ao rótulo da variável,
                // irá substituir o rótulo por seu valor real
                if (expandedQuadruple.getArgument1().contains(this.userInput.get(0).getLabel())) {
                    expandedQuadruple.setArgument1(this.userInput.get(0).getValue().toString());
                }
            }
            //caso o segundo argumento não seja uma variável temporária
            if (!StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                //se o primeiro argumento da quadrupla for igual ao rótulo da variável,
                // irá substituir o rótulo por seu valor real
                if (expandedQuadruple.getArgument2().contains(this.userInput.get(0).getLabel())) {
                    expandedQuadruple.setArgument2(this.userInput.get(0).getValue().toString());
                }
            }
        }
    }

    /**
     * Verifica se há alguma variável a ser substituida dentro do polinômio
     * @param expandedQuadruples lista de quádruplas que representam o polinômio
     * @return true caso haja pelo menos uma variável a ser substituida
     */
    private boolean isThereVariablesToSubstitute(List<ExpandedQuadruple> expandedQuadruples) {
        //contador para verificar se existe uma variável a ser substituida
        int countEqualVariable = 0;

        //Verifica na lista de quadruplas se existe uma variavel a ser substituida
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {

            //verifica se tem um rótulo de variável igual ao que o usuário inseriu (mesma lógica para os demais)
            if (!userInput.isEmpty() && expandedQuadruple.getArgument1().contains(userInput.get(0).getLabel()) || expandedQuadruple.getArgument2().contains(userInput.get(0).getLabel())) {
                countEqualVariable++;
            }

            if (userInput.size() > 1) {
                if (expandedQuadruple.getArgument1().contains(userInput.get(1).getLabel()) || expandedQuadruple.getArgument2().contains(userInput.get(1).getLabel())) {
                    countEqualVariable++;
                }
            }

            if (userInput.size() > 2) {
                if (expandedQuadruple.getArgument1().contains(userInput.get(2).getLabel()) || expandedQuadruple.getArgument2().contains(userInput.get(2).getLabel())) {
                    countEqualVariable++;
                }
            }

            if (userInput.size() > 3) {
                if (expandedQuadruple.getArgument1().contains(userInput.get(3).getLabel()) || expandedQuadruple.getArgument2().contains(userInput.get(3).getLabel())) {
                    countEqualVariable++;
                }
            }

            if (userInput.size() > 4) {
                if (expandedQuadruple.getArgument1().contains(userInput.get(4).getLabel()) || expandedQuadruple.getArgument2().contains(userInput.get(4).getLabel())) {
                    countEqualVariable++;
                }
            }

        }

        return (countEqualVariable > 0);
    }

}
