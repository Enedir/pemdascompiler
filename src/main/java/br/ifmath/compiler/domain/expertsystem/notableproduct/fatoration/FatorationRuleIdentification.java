package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.commonfactorandgroup.FatorationRuleCommonFactorAndGroup;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.twobinomialproduct.FatorationRuleTwoBinomialProduct;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.domain.grammar.nonterminal.T;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FatorationRuleIdentification implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        String correctExplanation = identify();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(),
                "Identificação do tipo de fatoração a partir da equação inicial: " + correctExplanation));
        return steps;
    }

    private String identify() throws InvalidAlgebraicExpressionException {
        if (isPerfectSquareTrinomial(this.source)) {
            return "Trinômio quadrado perfeito.\n\nNote que a expressão é formada " +
                    "por três monômios em que o primeiro e o último termo são quadrados e o termo cental é o dobro do " +
                    "produto entre o priemiro termo e o segundo termo.";
        }

        if (isPerfectCube(this.source)) {
            return (isCubeDifference(this.source)) ? "Cubo perfeito (cubo da diferença)." : "Cubo perfeito (cubo da soma).";
        }

        if (isDifferenceOfTwoSquares(this.source)) {
            return "Diferença de dois quadrados.";
        }

        if (isTwoBinomialProduct(this.source)) {
            return "Trinômio do segundo grau.";
        }

        if (isGroupment(this.source)) {
            return "Agrupamento";
        }

        if (isCommonFactor(this.source.getRootQuadruple(), this.source)) {
            return "Fator comum em evidência.";
        }


        throw new InvalidAlgebraicExpressionException("Regra não identificada");
    }

    //<editor-fold desc="CommonFactor">

    public static boolean isCommonFactor(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source) {
        String argument = (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                ? source.findQuadrupleByResult(iterationQuadruple.getArgument1()).getArgument1() : iterationQuadruple.getArgument1();
        NumericValueVariable patternNVV = new NumericValueVariable(argument);
        return isThereAEqualPattern(iterationQuadruple, patternNVV.getLabel(), source);
    }

    private static boolean isThereAEqualPattern(ExpandedQuadruple iterationQuadruple, String pattern, ThreeAddressCode source) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return isThereAEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), pattern, source);
        }

        NumericValueVariable iterationArgumentNVV = new NumericValueVariable(iterationQuadruple.getArgument1());
        if (iterationArgumentNVV.getLabel().contains(pattern))
            return true;

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return isThereAEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), pattern, source);
        }

        iterationArgumentNVV = new NumericValueVariable(iterationQuadruple.getArgument2());
        return iterationArgumentNVV.getLabel().contains(pattern);
    }
    //</editor-fold>>

    //<editor-fold desc="Groupment">

    public static boolean isGroupment(ThreeAddressCode source) throws InvalidAlgebraicExpressionException {
        //TODO ver o que está fazendo
        ExpandedQuadruple root = source.getRootQuadruple();
        if (isCommonFactor(root, source)) {
            if (quadruplesCount(source.getRootQuadruple(), source) == 4) {

                ThreeAddressCode firstCouple = generateFirstCouple(source);

                ThreeAddressCode secondCouple = new ThreeAddressCode();
                List<ExpandedQuadruple> quadruples = new ArrayList<>();
                quadruples.add(source.getListLastQuadruple());
                secondCouple.setExpandedQuadruples(quadruples);
                secondCouple.setLeft(source.getListLastQuadruple().getResult());


                Couples couples = new Couples(firstCouple, secondCouple);
                return !couples.getFirstCoupleFactor().equals(couples.getSecondCoupleFactor()) &&
                        (couples.getFirstCoupleMultiplier().equals(couples.getSecondCoupleMultiplier()));

            }
        }

        return false;
    }


    private static ThreeAddressCode generateFirstCouple(ThreeAddressCode source) {
        String argument1 = source.getRootQuadruple().getArgument1();
        if (StringUtil.match(argument1, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            argument1 = source.findQuadrupleByResult(argument1).getArgument1();
        }
        String argument2 = source.findQuadrupleByResult(source.getRootQuadruple().getArgument2()).getArgument1();
        ExpandedQuadruple expandedQuadruple = new ExpandedQuadruple("+", argument1, argument2, "T1", 0, 0);

        List<ExpandedQuadruple> quadruples = new ArrayList<>();
        quadruples.add(expandedQuadruple);
        ThreeAddressCode newSource = new ThreeAddressCode();
        newSource.setExpandedQuadruples(quadruples);
        newSource.setLeft("T1");
        return newSource;

    }

    private static int quadruplesCount(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source) {
        int sum = 0;
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            sum += quadruplesCount(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), source);
        }

        sum++;
        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            sum += quadruplesCount(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), source);
        }

        sum++;
        return sum;
    }

    private static class Couples {
        private String firstCoupleFactor, firstCoupleMultiplier, secondCoupleFactor, secondCoupleMultiplier;

        public Couples(ThreeAddressCode firstCouple, ThreeAddressCode secondCouple) throws InvalidAlgebraicExpressionException {
            setCouples(firstCouple, true);
            setCouples(secondCouple, false);
        }

        private void setCouples(ThreeAddressCode couple, boolean isFirstCouple) throws InvalidAlgebraicExpressionException {
            FatorationRuleCommonFactorAndGroup commonFactor = new FatorationRuleCommonFactorAndGroup();
            ThreeAddressCode source = getResultSource(commonFactor, couple);
            if (isFirstCouple)
                this.firstCoupleFactor = source.getRootQuadruple().getArgument1();
            else
                this.secondCoupleFactor = source.getRootQuadruple().getArgument1();
            ExpandedQuadruple firstSourceRoot = couple.getRootQuadruple();
            if (StringUtil.match(firstSourceRoot.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                if (isFirstCouple)
                    this.firstCoupleMultiplier = getMultiplierFromQuadruple(firstSourceRoot.getArgument2(), source);
                else
                    this.secondCoupleMultiplier = getMultiplierFromQuadruple(firstSourceRoot.getArgument2(), source);
            }
        }

        private String getMultiplierFromQuadruple(String quadrupleResult, ThreeAddressCode source) {
            ExpandedQuadruple quadruple = source.findQuadrupleByResult(quadrupleResult);
            return quadruple.getArgument1() + " " + quadruple.getOperator() + " " + quadruple.getArgument2();
        }

        private ThreeAddressCode getResultSource(IRule rule, ThreeAddressCode ruleSource) throws InvalidAlgebraicExpressionException {
            return rule.handle(Collections.singletonList(ruleSource)).get(0).getSource().get(0);
        }

        public String getFirstCoupleFactor() {
            return firstCoupleFactor;
        }

        public String getFirstCoupleMultiplier() {
            return firstCoupleMultiplier;
        }

        public String getSecondCoupleFactor() {
            return secondCoupleFactor;
        }

        public String getSecondCoupleMultiplier() {
            return secondCoupleMultiplier;
        }

    }

    //</editor-fold>

    //<editor-fold desc="PerfectSquareTrinomial">
    public static boolean isPerfectSquareTrinomial(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        if (root.isPlus()) {
            if (isSquareReducibleTerm(root.getArgument1())) {
                if (StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple middleTermQuadruple = source.findQuadrupleByResult(root.getArgument2());
                    if (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                            RegexPattern.VARIABLE_WITH_COEFFICIENT.toString(), RegexPattern.NATURAL_NUMBER.toString())) {
                        NumericValueVariable middleTerm = new NumericValueVariable(middleTermQuadruple.getArgument1());
                        if (isSquareReducibleTerm(middleTermQuadruple.getArgument2())) {

                            NumericValueVariable firstTerm = new NumericValueVariable(root.getArgument1());
                            NumericValueVariable secondTerm = new NumericValueVariable(middleTermQuadruple.getArgument2());
                            //Variável com variável
                            if (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                    RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()) &&
                                    (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                            RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()))) {
                                if (firstTerm.getLabelVariable().equals(secondTerm.getLabelVariable())) {
                                    if ((firstTerm.getLabelPower() + secondTerm.getLabelPower()) / 2 == middleTerm.getLabelPower()) {
                                        if (firstTerm.getValue() == 1 && secondTerm.getValue() == 1 && middleTerm.getValue() == 2)
                                            return true;
                                        else if (firstTerm.getValue() != 1 && secondTerm.getValue() != 1) {
                                            return (((int) Math.sqrt(firstTerm.getValue()) * (int) Math.sqrt(secondTerm.getValue())) * 2) == middleTerm.getValue();
                                        } else {
                                            int value = (firstTerm.getValue() != 1) ? (int) Math.sqrt(firstTerm.getValue()) : (int) Math.sqrt(secondTerm.getValue());
                                            return (value * 2) == middleTerm.getValue();
                                        }
                                    }
                                }
                            }

                            //Número com número
                            if (StringUtil.match(root.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()) &&
                                    StringUtil.match(middleTermQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString())) {
                                return ((Math.round(Math.sqrt(firstTerm.getValue()) * Math.sqrt(secondTerm.getValue()))) * 2) == middleTerm.getValue();
                            }

                            //Variável com número
                            if (StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                    RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()) &&
                                    StringUtil.match(middleTermQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString())) {
                                int variableValue = (firstTerm.getValue() == 1) ? 1 : (int) Math.sqrt(firstTerm.getValue());
                                if (((variableValue * (int) Math.sqrt(secondTerm.getValue())) * 2) == middleTerm.getValue()) {
                                    return middleTerm.getLabelPower() == firstTerm.getLabelPower() / 2;
                                }
                            }

                            //Número com variável
                            if (StringUtil.match(root.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()) &&
                                    StringUtil.matchAny(middleTermQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                                            RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
                                int variableValue = (secondTerm.getValue() == 1) ? 1 : (int) Math.sqrt(secondTerm.getValue());
                                if (((variableValue * (int) Math.sqrt(firstTerm.getValue())) * 2) == middleTerm.getValue()) {
                                    return middleTerm.getLabelPower() == secondTerm.getLabelPower() / 2;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean isSquareReducibleTerm(String argument) {
        return StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()) &&
                (Math.sqrt(Integer.parseInt(argument)) % 1 == 0) ||
                (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_EXPONENT.toString()) &&
                        (new NumericValueVariable(argument).getLabelPower() % 2 == 0 &&
                                (Math.sqrt(new NumericValueVariable(argument).getValue()) % 1 == 0)));
    }
    //</editor-fold>>

    //<editor-fold desc="DifferenceOfTwoSquares">
    public static boolean isDifferenceOfTwoSquares(ThreeAddressCode source) {
        if (source.getExpandedQuadruples().size() == 1) {
            ExpandedQuadruple root = source.getRootQuadruple();
            if (root.isMinus()) {
                return isSquareReducibleTerm(root.getArgument1()) && isSquareReducibleTerm(root.getArgument2());
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="PerfectCube">

    public static boolean isPerfectCube(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        ExpandedQuadruple last = source.getLastQuadruple(root);
        ExpandedQuadruple middleQuadruple = source.findQuadrupleByResult(root.getArgument2());


        //Variável com variável
        if (isCubeReducibleVariableTerm(root.getArgument1()) && isCubeReducibleVariableTerm(last.getArgument2())) {
            NumericValueVariable firstTerm = new NumericValueVariable(root.getArgument1());
            NumericValueVariable lastTerm = new NumericValueVariable(last.getArgument2());
            if (isMiddleTermValid(firstTerm, lastTerm, middleQuadruple.getArgument1(), true)) {
                middleQuadruple = source.findQuadrupleByResult(middleQuadruple.getArgument2());
                return isMiddleTermValid(firstTerm, lastTerm, middleQuadruple.getArgument1(), false);
            }
        }

        //Número com número
        if (isCubeReducibleNumericTerm(root.getArgument1()) && isCubeReducibleNumericTerm(last.getArgument2())) {
            int firstValue = (int) Math.cbrt(Integer.parseInt(root.getArgument1()));
            int lastValue = (int) Math.cbrt(Integer.parseInt(last.getArgument2()));
            if (Integer.parseInt(middleQuadruple.getArgument1()) == 3 * (Math.pow(firstValue, 2) * lastValue)) {
                middleQuadruple = source.findQuadrupleByResult(middleQuadruple.getArgument2());
                return Integer.parseInt(middleQuadruple.getArgument1()) == 3 * (Math.pow(lastValue, 2) * firstValue);
            }
        }

        //Variável com número
        if (isCubeReducibleVariableTerm(root.getArgument1()) && isCubeReducibleNumericTerm(last.getArgument2())) {
            NumericValueVariable firstTerm = new NumericValueVariable(root.getArgument1());
            int lastValue = (int) Math.cbrt(Integer.parseInt(last.getArgument2()));
            if (isVariableAndNumberValid(firstTerm, lastValue, middleQuadruple, true, true)) {
                middleQuadruple = source.findQuadrupleByResult(middleQuadruple.getArgument2());
                return isVariableAndNumberValid(firstTerm, lastValue, middleQuadruple, true, false);
            }
        }

        //Número com variável
        if (isCubeReducibleNumericTerm(root.getArgument1()) && isCubeReducibleVariableTerm(last.getArgument2())) {
            NumericValueVariable lastTerm = new NumericValueVariable(last.getArgument2());
            int firstValue = (int) Math.cbrt(Integer.parseInt(root.getArgument1()));
            if (isVariableAndNumberValid(lastTerm, firstValue, middleQuadruple, false, true)) {
                middleQuadruple = source.findQuadrupleByResult(middleQuadruple.getArgument2());
                return isVariableAndNumberValid(lastTerm, firstValue, middleQuadruple, false, false);
            }
        }
        return false;
    }

    private static boolean isCubeReducibleNumericTerm(String argument) {
        return StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()) &&
                (Math.cbrt(Integer.parseInt(argument)) % 1 == 0);

    }

    private static boolean isCubeReducibleVariableTerm(String argument) {
        return StringUtil.match(argument, RegexPattern.VARIABLE_WITH_EXPONENT.toString()) &&
                (new NumericValueVariable(argument).getLabelPower() % 3 == 0 &&
                        (Math.cbrt(new NumericValueVariable(argument).getValue()) % 1 == 0));

    }


    private static boolean isMiddleTermValid(NumericValueVariable firstTerm, NumericValueVariable lastTerm, String middleValue, boolean isMiddleTerm1) {
        if (!isMiddleTerm1) {
            NumericValueVariable aux = firstTerm;
            firstTerm = lastTerm;
            lastTerm = aux;
        }

        String firstTermVariable = firstTerm.getLabelVariable();
        String lastTermVariable = lastTerm.getLabelVariable();

        if (middleValue.contains(firstTermVariable) && middleValue.contains(lastTermVariable)) {
            if (middleValue.charAt(middleValue.indexOf(firstTermVariable) + 1) == '^') {
                if (firstTerm.getLabelPower() % 3 == 0 && lastTerm.getLabelPower() % 3 == 0) {
                    int firstPower = firstTerm.getLabelPower() / 3;
                    int lastPower = lastTerm.getLabelPower() / 3;
                    if ((firstPower * 2) + lastPower == new NumericValueVariable(middleValue).getLabelPower()) {
                        if (firstTerm.getValue() == 1 && lastTerm.getValue() == 1 && middleValue.startsWith("3"))
                            return true;
                        else if (firstTerm.getValue() != 1 && lastTerm.getValue() != 1) {
                            int firstTermCbrt = (int) Math.cbrt(firstTerm.getValue());
                            int lastTermCbrt = (int) Math.cbrt(lastTerm.getValue());
                            return ((firstTermCbrt * lastTermCbrt) * 3) == middleValue.charAt(0);
                        } else {
                            int value = (firstTerm.getValue() != 1) ?
                                    (int) Math.cbrt(firstTerm.getValue()) :
                                    (int) Math.cbrt(lastTerm.getValue());

                            return (value * 3) == middleValue.charAt(0);
                        }
                    }
                }
            }
        }

        return false;
    }

    private static boolean isVariableAndNumberValid(NumericValueVariable variableTerm, int numberValue,
                                                    ExpandedQuadruple middleQuadruple, boolean isFirstTermAVariable,
                                                    boolean isMiddleTerm1) {
        int variableTermValue = (variableTerm.getValue() == 1) ?
                variableTerm.getValue() : (int) Math.cbrt(variableTerm.getValue());

        if ((isMiddleTerm1 && !isFirstTermAVariable) || (!isMiddleTerm1 && isFirstTermAVariable))
            numberValue = (int) Math.pow(numberValue, 2);
        else
            variableTermValue = (int) Math.pow(variableTermValue, 2);

        NumericValueVariable middleTermNVV = new NumericValueVariable(middleQuadruple.getArgument1());
        return middleTermNVV.getValue() == (3 * (variableTermValue * numberValue));
    }


    private static boolean isCubeDifference(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        if (root.isMinus()) {
            ExpandedQuadruple lastQuadruple = source.getLastQuadruple(root);
            return lastQuadruple.isMinus();
        }
        return false;
    }

    //</editor-fold>

    //<editor-fold desc="Two Binomial Product">
    public static boolean isTwoBinomialProduct(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();

        String rootArgument1 = root.getArgument1();

        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(rootArgument1);
            rootArgument1 = innerQuadruple.getArgument1();
        }

        if (StringUtil.match(rootArgument1, RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {

            NumericValueVariable argument = new NumericValueVariable(rootArgument1);
            if (argument.getValue() != 0 && argument.getLabelPower() == 2) {

                if (StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

                    ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(root.getArgument2());
                    if (StringUtil.match(innerQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_COEFFICIENT.toString())) {
                        argument = new NumericValueVariable(innerQuadruple.getArgument1());
                        if (argument.getValue() != 0 && argument.getLabelPower() == 1) {
                            if (StringUtil.match(innerQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString())) {
                                return !innerQuadruple.getArgument2().equals("0");
                            }
                        }

                    }
                }
            }
        }

        return false;
    }
    //</editor-fold>


}
