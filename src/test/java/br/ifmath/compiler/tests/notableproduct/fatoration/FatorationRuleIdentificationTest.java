package br.ifmath.compiler.tests.notableproduct.fatoration;

import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.*;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationExpertSystem;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class FatorationRuleIdentificationTest {
    private ICompiler compiler;
    private IExpertSystem expertSystem;
    private String stepOneResult1Expected;
    private String stepOneResult2Expected;
    private String stepOneResult3Expected;
    private String stepOneResult4Expected;
    private String stepOneResult5Expected;
    private String stepOneResult5PlusExpected;
    private String stepOneResult5MinusExpected;
    private String stepTwoResult1Expected;
    private String stepTwoResult2Expected;
    private String stepTwoResult3PlusExpected;
    private String stepTwoResult3MinusExpected;
    private String stepTwoResult4Expected;
    private String stepTwoResult5SumExpected;
    private String stepThreeResult3PlusExpected;
    private String stepThreeResult3MinusExpected;
    private String stepThreeResult4Expected;
    private String stepThreeResult5PlusExpected;
    private String stepThreeResult5MinusExpected;
    private String finalResultExplicationExpected;


    @Before
    public void setUp() {
        compiler = new Compiler();
        expertSystem = new FatorationExpertSystem();
        String stepOneBaseResult = "Identificação do tipo de fatoração a partir da equação inicial: ";
        stepOneResult1Expected = stepOneBaseResult + "Fator comum em evidência.";
        stepOneResult2Expected = stepOneBaseResult + "Quadrado da diferença de dois termos.";
        stepOneResult3Expected = stepOneBaseResult + "Trinômio quadrado perfeito.\n\nNote que a expressão é formada " +
                "por três monômios em que o primeiro e o último termo são quadrados e o termo cental é o dobro do " +
                "produto entre o priemiro termo e o segundo termo.";
        stepOneResult4Expected = stepOneBaseResult + "Diferença de dois quadrados.";
        stepOneResult5Expected = stepOneBaseResult + "Cubo perfeito ";
        stepOneResult5PlusExpected = stepOneResult5Expected + "(cubo da soma).";
        stepOneResult5MinusExpected = stepOneResult5Expected + "(cubo da diferença).";


        stepTwoResult1Expected = "Verificamos o elemento que temos em comum e colocamos em evidência.";
        stepTwoResult2Expected = "Removendo os parênteses dos polinômios.";

        stepTwoResult3PlusExpected = "Escrevemos a expressão no formato a^2 + 2 * a * b + b^2, identificando os " +
                "elementos que estão elevados ao quadrado e os respectivos produtos.";
        stepTwoResult3MinusExpected = "Escrevemos a expressão no formato a^2 + 2 * a * b - b^2, identificando os " +
                "elementos que estão elevados ao quadrado e os respectivos produtos.";
        stepThreeResult3PlusExpected = "Identificamos os elementos a e b e escrevemos o resultado como o quadrado da " +
                "soma, no formato (a + b)^2.";
        stepThreeResult3MinusExpected = "Identificamos os elementos a e b e escrevemos o resultado como o quadrado da " +
                "diferença, no formato (a - b)^2.";

        stepTwoResult4Expected = "Escrevemos a expressão no formato a^2 - b^2, identificando os elementos que estão " +
                "elevados ao quadrado.";
        stepThreeResult4Expected = "Escrevemos a expressão como o produto da soma pela diferença de dois termos.";

        stepTwoResult5SumExpected = "Escrevemos a expressão no formato a^3 + 3 * a^2 * b + 3 * a * b^2 + b^3, " +
                "identificando os elementos que estão elevados ao cubo, ao quadrado e os respectivos produtos.";
        stepThreeResult5PlusExpected = "Identificamos os elementos a e b e escrevemos o resultado como o quadrado da " +
                "soma, no formato (a + b)^3.";
        stepThreeResult5MinusExpected = "Identificamos os elementos a e b e escrevemos o resultado como o quadrado da " +
                "diferença, no formato (a - b)^3.";

        finalResultExplicationExpected = "Soma dos termos semelhantes.";
    }

    //<editor-fold desc="Common Factor">

    @Test()
    public void identify_simple_terms_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "2x + 5x";

        String lastStepValueExpected = "x * (2 + 5)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    @Test()
    public void identify_simple_terms_common_factor_scenery_two_with_success() {
        //Arrange
        String expression = "2a + 5a - 9a - a";

        String lastStepValueExpected = "a * (2 + 5 - 9 - 1)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    @Test()
    public void identify_number_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "2 + 4 - 8 - 12";

        String lastStepValueExpected = "2 * (1 + 2 - 4 - 6)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    @Test()
    public void identify_number_common_factor_scenery_two_with_success() {
        //Arrange
        String expression = "3x^3 + 3 - 6x - 27x^9";

        String lastStepValueExpected = "3 * (x^3 + 1 - 2x - 9x^9)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    @Test()
    public void identify_number_common_factor_scenery_three_with_success() {
        //Arrange
        String expression = "2x + 8x - 4";

        String lastStepValueExpected = "2 * (x + 4x - 2)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    @Test()
    public void identify_monomial_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "4x^2 + 2x - 12x^3 - 8x^4";

        String lastStepValueExpected = "2x * (2x + 1 - 6x^2 - 4x^3)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    @Test()
    public void identify_monomial_common_factor_scenery_two_with_success() {
        //Arrange
        String expression = "-3x - 9x - 24x^4 + 18x^2";

        String lastStepValueExpected = "3x * (-1 - 3 - 8x^3 + 6x)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    @Test()
    public void identify_monomial_common_factor_scenery_three_with_success() {
        //Arrange
        String expression = "-4x + 15x^6 - 28x^2 + 48x^3";

        String lastStepValueExpected = "x * (-4 + 15x^5 - 28x + 48x^2)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    @Test()
    public void identify_numbers_and_variables_common_factor_scenery_one_with_failure() {
        //Arrange
        String expression = "2x + 5x - 7";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(Exception.class));
        }
    }

    @Test()
    public void identify_numbers_and_variables_common_factor_scenery_two_with_failure() {
        //Arrange
        String expression = "2x^2 + 4x^4 - 7x + 8z";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(Exception.class));
        }
    }

    @Test()
    public void identify_monomial_with_exponent_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "-30z^4 + 16z^2 - 4z^8 + 10z^6";

        String lastStepValueExpected = "z^2 * (-30z^2 + 16 - 4z^6 + 10z^4)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    @Test()
    public void identify_monomial_with_exponent_common_factor_scenery_two_with_success() {
        //Arrange
        String expression = "a^7 + 5a^4 - 4a^8 + 15a^5";

        String lastStepValueExpected = "a^4 * (a^3 + 5 - 4a^4 + 15a)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    @Test()
    public void identify_multiple_patterns_common_factor_scenery_one_with_success() {
        //Arrange
        String expression = "3x^3 + 6x^6 - 3x - 27x^9";

        String lastStepValueExpected = "3x * (x^2 + 2x^5 - 1 - 9x^8)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }


    @Test()
    public void identify_multiple_patterns_common_factor_scenery_two_with_success() {
        //Arrange
        String expression = "8x^8 - 2x^2 - 4x^4 + 6x^6";

        String lastStepValueExpected = "2x^2 * (4x^6 - 1 - 2x^2 + 3x^4)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult1Expected, stepOne.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepTwoResult1Expected, finalStep.getReason());
    }

    //</editor-fold>

    //<editor-fold desc="Perfect Square Trinomial">
    @Test()
    public void identify_simple_perfect_square_trinomial_scenery_one_with_success() {
        //Arrange
        String expression = "x^2 + 6x + 9";

        String stepTwoValueExpected = "(x  ) ^ 2 + 2 * x * 3 + (3  ) ^ 2";
        String lastStepValueExpected = "(x + 3) ^ 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3PlusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3PlusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_simple_perfect_square_trinomial_scenery_two_with_success() {
        //Arrange
        String expression = "36 + 12x^3 + x^6";

        String stepTwoValueExpected = "(6  ) ^ 2 + 2 * 6 * x^3 + (x^3  ) ^ 2";
        String lastStepValueExpected = "(6 + x^3) ^ 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3PlusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3PlusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_only_numbers_perfect_square_trinomial_scenery_one_with_success() {
        //Arrange
        String expression = "25 + 40 + 16";

        String stepTwoValueExpected = "(5  ) ^ 2 + 2 * 5 * 4 + (4  ) ^ 2";
        String lastStepValueExpected = "(5 + 4) ^ 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3PlusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3PlusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_only_numbers_perfect_square_trinomial_scenery_two_with_success() {
        //Arrange
        String expression = "36 + 84 - 49";

        String stepTwoValueExpected = "(6  ) ^ 2 + 2 * 6 * 7 - (7  ) ^ 2";
        String lastStepValueExpected = "(6 - 7) ^ 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3MinusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3MinusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_only_variables_perfect_square_trinomial_scenery_one_with_success() {
        //Arrange
        String expression = "a^4 + 2a^3 - a^2";

        String stepTwoValueExpected = "(a^2  ) ^ 2 + 2 * a^2 * a - (a  ) ^ 2";
        String lastStepValueExpected = "(a^2 - a) ^ 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3MinusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3MinusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_only_variables_perfect_square_trinomial_scenery_two_with_success() {
        //Arrange
        String expression = "a^8 + 2a^10 - a^12";

        String stepTwoValueExpected = "(a^4  ) ^ 2 + 2 * a^4 * a^6 - (a^6  ) ^ 2";
        String lastStepValueExpected = "(a^4 - a^6) ^ 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3MinusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3MinusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_variables_and_numbers_perfect_square_trinomial_scenery_one_with_success() {
        //Arrange
        String expression = "4b^2 + 20b - 25";

        String stepTwoValueExpected = "(2b  ) ^ 2 + 2 * 2b * 5 - (5  ) ^ 2";
        String lastStepValueExpected = "(2b - 5) ^ 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3MinusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3MinusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_variables_and_numbers_perfect_square_trinomial_scenery_two_with_success() {
        //Arrange
        String expression = "64 + 64t^3 + 16t^6";

        String stepTwoValueExpected = "(8  ) ^ 2 + 2 * 8 * 4t^3 + (4t^3  ) ^ 2";
        String lastStepValueExpected = "(8 + 4t^3) ^ 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3PlusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3PlusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_variables_and_numbers_perfect_square_trinomial_scenery_three_with_success() {
        //Arrange
        String expression = "25g^8 + 40g^5 + 16g^2";

        String stepTwoValueExpected = "(5g^4  ) ^ 2 + 2 * 5g^4 * 4g + (4g  ) ^ 2";
        String lastStepValueExpected = "(5g^4 + 4g) ^ 2";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult3Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult3PlusExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult3PlusExpected, finalStep.getReason());
    }

    @Test()
    public void identify_numbers_perfect_square_trinomial_scenery_one_with_failure() {
        //Arrange
        String expression = "4 + 7 - 3";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(Exception.class));
        }
    }

    @Test()
    public void identify_numbers_perfect_square_trinomial_scenery_two_with_failure() {
        //Arrange
        String expression = "15 + 32 + 4";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(Exception.class));
        }
    }

    @Test()
    public void identify_numbers_and_variables_perfect_square_trinomial_scenery_one_with_failure() {
        //Arrange
        String expression = "2m^2 + 11m - 8";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(Exception.class));
        }
    }

    @Test()
    public void identify_numbers_and_variables_perfect_square_trinomial_scenery_two_with_failure() {
        //Arrange
        String expression = "16 + 16m - 4m^4";

        // Act
        try {
            compiler.analyse(expertSystem, AnswerType.BEST, expression);
            fail();
        } catch (Exception e) {
            // Assert
            assertThat(e, instanceOf(Exception.class));
        }
    }

    //</editor-fold>

    //<editor-fold desc="Difference of Two Squares">
    @Test()
    public void identify_simple_difference_of_two_squares_scenery_one_with_success() {
        //Arrange
        String expression = "25 - x^2";

        String stepTwoValueExpected = "(5  ) ^ 2 - (x  ) ^ 2";
        String lastStepValueExpected = "(5 + x) * (5 - x)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult4Expected, finalStep.getReason());
    }

    @Test()
    public void identify_only_numbers_difference_of_two_squares_scenery_one_with_success() {
        //Arrange
        String expression = "25 - 49";

        String stepTwoValueExpected = "(5  ) ^ 2 - (7  ) ^ 2";
        String lastStepValueExpected = "(5 + 7) * (5 - 7)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult4Expected, finalStep.getReason());
    }

    @Test()
    public void identify_only_variables_difference_of_two_squares_scenery_one_with_success() {
        //Arrange
        String expression = "x^6 - x^4";

        String stepTwoValueExpected = "(x^3  ) ^ 2 - (x^2  ) ^ 2";
        String lastStepValueExpected = "(x^3 + x^2) * (x^3 - x^2)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult4Expected, finalStep.getReason());
    }

    @Test()
    public void identify_only_variables_difference_of_two_squares_scenery_two_with_success() {
        //Arrange
        String expression = "x^10 - x^16";

        String stepTwoValueExpected = "(x^5  ) ^ 2 - (x^8  ) ^ 2";
        String lastStepValueExpected = "(x^5 + x^8) * (x^5 - x^8)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult4Expected, finalStep.getReason());
    }

    @Test()
    public void identify_numbers_and_variables_difference_of_two_squares_scenery_one_with_success() {
        //Arrange
        String expression = "4x^2 - 9x^6";

        String stepTwoValueExpected = "(2x  ) ^ 2 - (3x^3  ) ^ 2";
        String lastStepValueExpected = "(2x + 3x^3) * (2x - 3x^3)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult4Expected, finalStep.getReason());
    }

    @Test()
    public void identify_numbers_and_variables_difference_of_two_squares_scenery_two_with_success() {
        //Arrange
        String expression = "81p^12 - 25p^8";

        String stepTwoValueExpected = "(9p^6  ) ^ 2 - (5p^4  ) ^ 2";
        String lastStepValueExpected = "(9p^6 + 5p^4) * (9p^6 - 5p^4)";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult4Expected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult4Expected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult4Expected, finalStep.getReason());
    }
    //</editor-fold>

    @Test()
    public void identify_simple_terms_perfect_sum_cube_scenery_one_with_success() {
        //Arrange
        String expression = "x^3 + 6x^2 + 12x + 8";

        String stepTwoValueExpected = "(x  ) ^ 3 + 3 * (x)^2 * 2 + 3 * x * (2)^2 + (2  ) ^ 3";
        String lastStepValueExpected = "(x + 2) ^ 3";

        // Act
        IAnswer answer = null;
        try {
            answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Assert
        Step stepOne = answer.getSteps().get(answer.getSteps().size() - 3);
        Step stepTwo = answer.getSteps().get(answer.getSteps().size() - 2);
        Step finalStep = answer.getSteps().get(answer.getSteps().size() - 1);

        assertEquals(expression, stepOne.getMathExpression());
        assertEquals(stepOneResult5PlusExpected, stepOne.getReason());
        assertEquals(stepTwoValueExpected, stepTwo.getMathExpression());
        assertEquals(stepTwoResult5SumExpected, stepTwo.getReason());
        assertEquals(lastStepValueExpected, finalStep.getMathExpression());
        assertEquals(stepThreeResult5PlusExpected, finalStep.getReason());
    }

}
