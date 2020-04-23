package br.ifmath.compiler.domain.expertsystem.polynomial.addandsub;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PolynomialAddAndSubRuleGroupSimilarTerms implements IRule {

    private final List<ExpandedQuadruple> expandedQuadruples;

    public PolynomialAddAndSubRuleGroupSimilarTerms() {
        this.expandedQuadruples = new ArrayList<>();
    }


    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isThereEquivalentTermsToJoin(sources.get(0).getOperationsFromLeft())
                || isThereEquivalentTermsToJoin(sources.get(0).getOperationsFromRight());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) {
        List<NumericValueVariable> termsAndValuesList = new ArrayList<>();
        expandedQuadruples.clear();
        expandedQuadruples.addAll(sources.get(0).getExpandedQuadruples());

        List<Step> steps = new ArrayList<>();

        int numbersSum = sumTerms(sources.get(0), sources.get(0).getLeft(), false, termsAndValuesList);
        sortNVVList(termsAndValuesList);
        ThreeAddressCode step;
        if (termsAndValuesList.isEmpty()) {
            ExpandedQuadruple newQuadruple = new ExpandedQuadruple("", String.valueOf(numbersSum), "", "T1", 0, 0);
            step = new ThreeAddressCode("T1", Arrays.asList(newQuadruple));
        } else {
            replaceExpandedQuadruples(sources.get(0), termsAndValuesList, numbersSum);
            clearUnusedQuadruple(sources.get(0));
            sources.get(0).setLeft(expandedQuadruples.get(0).getResult());
            step = new ThreeAddressCode(sources.get(0).getLeft(), sources.get(0).getExpandedQuadruples());
        }

        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Soma dos termos semelhantes."));

        return steps;
    }

    private void sortNVVList(List<NumericValueVariable> termsAndValuesList) {
        for (int i = 0; i < termsAndValuesList.size(); i++) {
            for (int j = 0; j < termsAndValuesList.size() - 1; j++) {
                String currentLabel = termsAndValuesList.get(j).getLabel();
                Integer currentValue = 1;
                if (currentLabel.contains("^"))
                    currentValue = Integer.parseInt(currentLabel.substring(currentLabel.indexOf("^") + 1));
                String nextLabel = termsAndValuesList.get(j + 1).getLabel();
                Integer nextValue = 1;
                if (nextLabel.contains("^"))
                    nextValue = Integer.parseInt(nextLabel.substring(nextLabel.indexOf("^") + 1));
                if (nextValue > currentValue) {
                    NumericValueVariable aux = termsAndValuesList.remove(j);
                    termsAndValuesList.add(j + 1, aux);
                }
            }
        }
    }

    private void clearUnusedQuadruple(ThreeAddressCode source) {
        int size = expandedQuadruples.size() - 1;
        for (int i = 1; i <= size; i++) {
            source.getExpandedQuadruples().remove(1);
        }
    }

    private void replaceExpandedQuadruples(ThreeAddressCode source, List<NumericValueVariable> termsAndValuesList, int numbersSum) {
        boolean hasOnlyOneItemOnList = false;
        if (termsAndValuesList.size() == 1)
            hasOnlyOneItemOnList = true;

        ExpandedQuadruple iterationQuadruple = null;
        int i = 0;
        while (!termsAndValuesList.isEmpty()) {
            iterationQuadruple = (i == 0 || i == 1) ? expandedQuadruples.get(0) : source.findQuadrupleByResult(iterationQuadruple.getArgument2());
            NumericValueVariable iterationNVV = termsAndValuesList.get(0);
            if (iterationNVV.getValue() != 0) {
                String nvvValue = String.valueOf(Math.abs(iterationNVV.getValue()));
                if (nvvValue.equals("1"))
                    nvvValue = "";
                if (iterationNVV.getValue() < 0) {
                    if (i % 2 == 0 && i == 0) {
                        ExpandedQuadruple newQuadruple = new ExpandedQuadruple("MINUS", nvvValue + iterationNVV.getLabel(), source.retrieveNextTemporary(), 0, 0);
                        source.getExpandedQuadruples().add(newQuadruple);
                        iterationQuadruple.setArgument1(newQuadruple.getResult());
                    } else {
                        if (numbersSum == 0) {
                            iterationQuadruple.setOperator("-");
                            iterationQuadruple.setArgument2(nvvValue + iterationNVV.getLabel());
                        } else {
                            iterationQuadruple.setOperator("-");
                            ExpandedQuadruple newQuadruple = new ExpandedQuadruple("+", nvvValue + iterationNVV.getLabel(), "", source.retrieveNextTemporary(), 0, 0);
                            source.getExpandedQuadruples().add(newQuadruple);
                            iterationQuadruple.setArgument2(newQuadruple.getResult());
                        }
                    }
                } else {
                    if (i % 2 == 0 && i == 0) {
                        iterationQuadruple.setArgument1(nvvValue + iterationNVV.getLabel());
                    } else {
                        if (numbersSum == 0) {
                            if (termsAndValuesList.size() > 1) {
                                iterationQuadruple.setOperator("+");
                                ExpandedQuadruple newQuadruple = new ExpandedQuadruple("+", nvvValue + iterationNVV.getLabel(), "", source.retrieveNextTemporary(), 0, 0);
                                source.getExpandedQuadruples().add(newQuadruple);
                                iterationQuadruple.setArgument2(newQuadruple.getResult());
                            } else {
                                iterationQuadruple.setOperator("+");
                                iterationQuadruple.setArgument2(nvvValue + iterationNVV.getLabel());
                            }
                        } else {
                            iterationQuadruple.setOperator("+");
                            ExpandedQuadruple newQuadruple = new ExpandedQuadruple("+", nvvValue + iterationNVV.getLabel(), "", source.retrieveNextTemporary(), 0, 0);
                            source.getExpandedQuadruples().add(newQuadruple);
                            iterationQuadruple.setArgument2(newQuadruple.getResult());
                        }
                    }
                }
            }

            termsAndValuesList.remove(0);
            i++;
        }

        if (numbersSum != 0) {
            ExpandedQuadruple quadruple;

            if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                quadruple = source.findQuadrupleByResult(iterationQuadruple.getArgument2());
            } else {
                quadruple = iterationQuadruple;
            }

            if (numbersSum < 0)
                quadruple.setOperator("-");
            else
                quadruple.setOperator("+");

            quadruple.setArgument2(String.valueOf(Math.abs(numbersSum)));

        } else {
            if (hasOnlyOneItemOnList) {
                iterationQuadruple.setOperator("");
                iterationQuadruple.setArgument2("");
            }
        }

    }

    private int sumTerms(ThreeAddressCode threeAddressCode, String param, boolean lastOperationIsMinus, List<
            NumericValueVariable> termsAndValuesList) {
        int sum = 0;
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);

            if (expandedQuadruple.isNegative()) {
                sum -= sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), false, termsAndValuesList);
            } else {
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument1(), lastOperationIsMinus, termsAndValuesList);
                sum += sumTerms(threeAddressCode, expandedQuadruple.getArgument2(), expandedQuadruple.isMinus(), termsAndValuesList);
            }
        } else {
            if (StringUtil.isVariable(param)) {
                String paramValue, paramVariable;
                if (StringUtil.match(param, RegexPattern.VARIABLE_AND_COEFICIENT.toString())) {
                    paramValue = param.substring(0, param.indexOf("^") - 1);
                    paramVariable = param.substring(param.indexOf("^") - 1);
                } else {
                    paramValue = StringUtil.removeNonNumericChars(param);
                    paramVariable = StringUtil.removeNumericChars(param);
                }
                int index = 0;
                int cont = 0;
                if (termsAndValuesList.isEmpty()) {
                    termsAndValuesList.add(new NumericValueVariable(paramVariable, 0));
                } else {
                    for (int i = 0; i < termsAndValuesList.size(); i++) {
                        if (!termsAndValuesList.get(i).getLabel().equals(paramVariable)) {
                            cont++;
                            if (cont == termsAndValuesList.size()) {
                                termsAndValuesList.add(new NumericValueVariable(paramVariable, 0));
                            }
                        } else {
                            index = i;
                        }
                    }
                }
                int newValue = (StringUtil.isEmpty(paramValue)) ? 1 : Integer.parseInt(paramValue);
                if (lastOperationIsMinus)
                    termsAndValuesList.get(index).setValue(termsAndValuesList.get(index).getValue() - newValue);
                else
                    termsAndValuesList.get(index).setValue(termsAndValuesList.get(index).getValue() + newValue);
            } else {
                if (lastOperationIsMinus)
                    sum -= Double.parseDouble(param.replace(",", "."));
                else
                    sum += Double.parseDouble(param.replace(",", "."));
            }
        }
        return sum;
    }


    private boolean isThereEquivalentTermsToJoin(List<ExpandedQuadruple> expandedQuadruples) {
        int variableAmount = 0;
        int numberAmount = 0;

        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (!expandedQuadruple.isPlusOrMinus() || expandedQuadruple.getLevel() != 0)
                return false;

            if (StringUtil.matchAny(expandedQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString()))
                numberAmount++;

            if (StringUtil.matchAny(expandedQuadruple.getArgument2(), RegexPattern.NATURAL_NUMBER.toString(), RegexPattern.DECIMAL_NUMBER.toString()))
                numberAmount++;

            if (StringUtil.isVariable(expandedQuadruple.getArgument1()))
                variableAmount++;

            if (StringUtil.isVariable(expandedQuadruple.getArgument2()))
                variableAmount++;
        }

        return (variableAmount > 1 || numberAmount > 1);
    }


}
