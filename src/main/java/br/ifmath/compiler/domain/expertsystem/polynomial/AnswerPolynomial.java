package br.ifmath.compiler.domain.expertsystem.polynomial;

import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.Step;

import java.util.List;

public class AnswerPolynomial implements IAnswer {

    private String result;
    private final List<Step> steps;

    public AnswerPolynomial(List<Step> steps) {
        this.steps = steps;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public List<Step> getSteps() {
        return steps;
    }
}
